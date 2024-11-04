package eduardompinto.commons

@JvmInline
value class Email(val value: String) {

    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(isValid(value)) { "Invalid email" }
    }

    companion object {

        // AI generated code
        private val emailRegex =
            "^(?=.{1,256})(?=.{1,64}@.{1,255}$)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$".toRegex(
                RegexOption.IGNORE_CASE,
            )

        fun isValid(email: String): Boolean {
            return email.matches(emailRegex)
        }

        fun String.asEmail(): Email {
            return Email(this)
        }
    }

}

sealed class Document {

    abstract val value: String

    data class CPF(override val value: String) : Document() {
        companion object {
            /**
             * AI generated code, I didn't wanted to implement this
             */
            fun isValid(candidate: String): Boolean {
                val normalizedCandidate = candidate.replace("[^0-9]".toRegex(), "").substring(0, 11)
                if (normalizedCandidate.length != 11 || normalizedCandidate.all { it == normalizedCandidate[0] }) {
                    return false
                }

                fun calculateDigit(digits: String): Int {
                    val sum =
                        digits.mapIndexed { index, char ->
                            val multiplier = 11 - index
                            char.toString().toInt() * multiplier
                        }.sum()
                    val remainder = sum % 11
                    return if (remainder < 2) 0 else 11 - remainder
                }

                val firstDigit = calculateDigit(normalizedCandidate.substring(0, 9))
                if (firstDigit != normalizedCandidate[9].toString().toInt()) {
                    return false
                }

                val secondDigit = calculateDigit("${normalizedCandidate.substring(0, 9)}$firstDigit")
                return secondDigit == normalizedCandidate[10].toString().toInt()
            }
        }
    }

    data class CNPJ(override val value: String) : Document() {
        companion object {
            fun isValid(candidate: String): Boolean {
                if (candidate.length != 14) return false

                val normalizedCandidate = candidate.replace("[^0-9]".toRegex(), "")

                fun calculateDigit(position: Int): Int {
                    val sum =
                        (0 until 12).sumOf { i ->
                            normalizedCandidate[i + position].toString().toInt() * (13 - i)
                        }
                    return 11 - (sum % 11) % 11
                }

                val firstDigit = calculateDigit(0)
                if (firstDigit != normalizedCandidate[12].toString().toInt()) {
                    return false
                }

                val secondDigit = calculateDigit(1)
                return secondDigit == normalizedCandidate[13].toString().toInt()
            }
        }
    }

    data class Invalid(override val value: String) : Document()

    companion object {
        fun String.asDocument(): Document {
            return when {
                CPF.isValid(this) -> CPF(this)
                CNPJ.isValid(this) -> CNPJ(this)
                else -> Invalid(this)
            }
        }
    }

}
