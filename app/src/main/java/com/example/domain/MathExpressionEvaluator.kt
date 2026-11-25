package com.example.domain

import kotlin.math.*

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}

/**
 * Recursive-descent scientific expression evaluator supporting:
 * - +, -, × (*), ÷ (/), ^, postfix %
 * - Implicit multiplication: 2π, 2(3+4), (2)(3), 5sin(30)
 * - Trig & inverse trig (DEG or RAD), hyperbolic, ln, log, sqrt (√), abs
 * - Constants: π (pi), e, ANS
 */
object MathExpressionEvaluator {

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Identifier(val name: String) : Token()
        data class Operator(val symbol: Char) : Token()
        data object LeftParen : Token()
        data object RightParen : Token()
        data object Percent : Token()
    }

    fun evaluate(
        expression: String,
        isDegreeMode: Boolean = true,
        lastAnswer: Double = 0.0
    ): EvalResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) return EvalResult.Success(0.0, "0")

        return try {
            val tokens = tokenize(trimmed)
            if (tokens.isEmpty()) return EvalResult.Success(0.0, "0")
            val parser = Parser(tokens, isDegreeMode, lastAnswer)
            val value = parser.parseExpression()
            if (!parser.isAtEnd()) {
                return EvalResult.Error("خطای نحوی")
            }
            if (value.isNaN() || value.isInfinite()) {
                return EvalResult.Error("خارج از محدوده محاسبه")
            }
            EvalResult.Success(value, formatNumber(value))
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: "خطای نحوی")
        } catch (_: ArithmeticException) {
            EvalResult.Error("تقسیم بر صفر")
        } catch (_: Exception) {
            EvalResult.Error("عبارت نامعتبر")
        }
    }

    fun formatNumber(value: Double): String {
        if (value == 0.0) return "0"
        val cleaned = if (abs(value) < 1e-12) 0.0 else value
        if (cleaned == 0.0) return "0"
        val absVal = abs(cleaned)
        return if (absVal >= 1e10 || (absVal > 0 && absVal < 1e-6)) {
            String.format(java.util.Locale.US, "%.6e", cleaned)
                .replace(Regex("0+e"), "e")
                .replace(".e", "e")
        } else {
            val formatted = String.format(java.util.Locale.US, "%.8f", cleaned)
            formatted.trimEnd('0').trimEnd('.')
        }
    }

    private fun tokenize(input: String): List<Token> {
        val normalized = input
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
            .replace('٫', '.')
            .replace("√", "sqrt")
            .replace("π", "pi")

        val rawTokens = mutableListOf<Token>()
        var i = 0
        while (i < normalized.length) {
            val c = normalized[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val start = i
                    var hasDot = c == '.'
                    i++
                    while (i < normalized.length) {
                        val ch = normalized[i]
                        if (ch.isDigit()) {
                            i++
                        } else if (ch == '.' && !hasDot) {
                            hasDot = true
                            i++
                        } else if ((ch == 'e' || ch == 'E') && i + 1 < normalized.length &&
                            (normalized[i + 1].isDigit() || normalized[i + 1] == '+' || normalized[i + 1] == '-')
                        ) {
                            i++
                            if (i < normalized.length && (normalized[i] == '+' || normalized[i] == '-')) i++
                        } else {
                            break
                        }
                    }
                    val numStr = normalized.substring(start, i)
                    val number = numStr.toDoubleOrNull()
                        ?: throw IllegalArgumentException("عدد نامعتبر")
                    rawTokens.add(Token.Number(number))
                }
                c.isLetter() -> {
                    val start = i
                    while (i < normalized.length && (normalized[i].isLetter() || normalized[i].isDigit())) {
                        i++
                    }
                    rawTokens.add(Token.Identifier(normalized.substring(start, i).lowercase()))
                }
                c == '(' -> {
                    rawTokens.add(Token.LeftParen)
                    i++
                }
                c == ')' -> {
                    rawTokens.add(Token.RightParen)
                    i++
                }
                c == '%' -> {
                    rawTokens.add(Token.Percent)
                    i++
                }
                c in charArrayOf('+', '-', '*', '/', '^') -> {
                    rawTokens.add(Token.Operator(c))
                    i++
                }
                else -> throw IllegalArgumentException("کاراکتر نامعتبر")
            }
        }

        // Insert implicit multiplication tokens where appropriate
        val result = mutableListOf<Token>()
        for (idx in rawTokens.indices) {
            val current = rawTokens[idx]
            if (idx > 0) {
                val prev = rawTokens[idx - 1]
                val prevProducesValue = prev is Token.Number ||
                    prev is Token.RightParen ||
                    prev is Token.Percent ||
                    (prev is Token.Identifier && isConstant(prev.name))
                val currentStartsFactor = current is Token.Number ||
                    current is Token.LeftParen ||
                    current is Token.Identifier
                if (prevProducesValue && currentStartsFactor) {
                    result.add(Token.Operator('*'))
                }
            }
            result.add(current)
        }
        return result
    }

    private fun isConstant(name: String): Boolean = name == "pi" || name == "e" || name == "ans"

    private class Parser(
        private val tokens: List<Token>,
        private val isDegreeMode: Boolean,
        private val lastAnswer: Double
    ) {
        private var pos = 0

        fun isAtEnd(): Boolean = pos >= tokens.size

        private fun peek(): Token? = tokens.getOrNull(pos)

        private fun consume(): Token = tokens[pos++]

        fun parseExpression(): Double {
            var left = parseTerm()
            while (true) {
                val op = (peek() as? Token.Operator)?.symbol
                if (op == '+' || op == '-') {
                    consume()
                    val right = parseTerm()
                    left = if (op == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseTerm(): Double {
            var left = parseUnary()
            while (true) {
                val op = (peek() as? Token.Operator)?.symbol
                if (op == '*' || op == '/') {
                    consume()
                    val right = parseUnary()
                    left = if (op == '*') {
                        left * right
                    } else {
                        if (abs(right) < 1e-15) throw ArithmeticException("Divide by zero")
                        left / right
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun parseUnary(): Double {
            val op = (peek() as? Token.Operator)?.symbol
            if (op == '+' || op == '-') {
                consume()
                val operand = parseUnary()
                return if (op == '-') -operand else operand
            }
            return parsePower()
        }

        private fun parsePower(): Double {
            val base = parsePostfix()
            val op = (peek() as? Token.Operator)?.symbol
            if (op == '^') {
                consume()
                val exponent = parseUnary()
                return base.pow(exponent)
            }
            return base
        }

        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (peek() is Token.Percent) {
                consume()
                value /= 100.0
            }
            return value
        }

        private fun parsePrimary(): Double {
            return when (val token = peek()) {
                is Token.Number -> {
                    consume()
                    token.value
                }
                is Token.LeftParen -> {
                    consume()
                    val value = parseExpression()
                    if (peek() !is Token.RightParen) {
                        throw IllegalArgumentException("پرانتز بسته نشده")
                    }
                    consume()
                    value
                }
                is Token.Identifier -> {
                    consume()
                    when (token.name) {
                        "pi" -> Math.PI
                        "e" -> Math.E
                        "ans" -> lastAnswer
                        else -> {
                            val arg = if (peek() is Token.LeftParen) {
                                consume()
                                val inside = parseExpression()
                                if (peek() !is Token.RightParen) {
                                    throw IllegalArgumentException("پرانتز بسته نشده")
                                }
                                consume()
                                inside
                            } else {
                                parseUnary()
                            }
                            applyFunction(token.name, arg)
                        }
                    }
                }
                else -> throw IllegalArgumentException("خطای نحوی")
            }
        }

        private fun applyFunction(name: String, arg: Double): Double {
            val rad = if (isDegreeMode) Math.toRadians(arg) else arg
            return when (name) {
                "sin" -> sin(rad)
                "cos" -> cos(rad)
                "tan" -> {
                    val c = cos(rad)
                    if (abs(c) < 1e-12) throw IllegalArgumentException("تانژانت تعریف نشده")
                    tan(rad)
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("خارج از دامنه [-1,1]")
                    val res = asin(arg)
                    if (isDegreeMode) Math.toDegrees(res) else res
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("خارج از دامنه [-1,1]")
                    val res = acos(arg)
                    if (isDegreeMode) Math.toDegrees(res) else res
                }
                "atan" -> {
                    val res = atan(arg)
                    if (isDegreeMode) Math.toDegrees(res) else res
                }
                "ln" -> {
                    if (arg <= 0.0) throw IllegalArgumentException("لگاریتم عدد غیرمثبت")
                    ln(arg)
                }
                "log" -> {
                    if (arg <= 0.0) throw IllegalArgumentException("لگاریتم عدد غیرمثبت")
                    log10(arg)
                }
                "sqrt" -> {
                    if (arg < 0.0) throw IllegalArgumentException("ریشه دوم عدد منفی")
                    sqrt(arg)
                }
                "abs" -> abs(arg)
                "exp" -> exp(arg)
                else -> throw IllegalArgumentException("تابع ناشناخته: $name")
            }
        }
    }
}
