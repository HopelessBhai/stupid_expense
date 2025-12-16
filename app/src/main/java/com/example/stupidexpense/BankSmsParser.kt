object BankSmsParser {

    private val BANK_SENDER_KEYWORDS = listOf(
        "HDFC", "ICICI", "SBI", "AXIS", "KOTAK",
        "PNB", "IDFC", "YES", "INDUS", "BOB", "DCBANK"
    )

    private val DEBIT_KEYWORDS = listOf(
        "debit", "debited", "spent", "paid", "withdrawn", "purchase"
    )

    // private val CREDIT_KEYWORDS = listOf(
    //     "credit", "credited", "received", "refund", "reversal"
    // )

    // INR / Rs / ₹ amount extractor
    private val AMOUNT_REGEX =
        Regex("""(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)

    data class ParsedTransaction(
        val amount: Double,
        val type: TransactionType
    )

    enum class TransactionType { DEBIT, CREDIT }

    fun parse(sender: String?, body: String): ParsedTransaction? {
        if (sender == null) return null

        // 1. Sender check
        if (BANK_SENDER_KEYWORDS.none { sender.contains(it, ignoreCase = true) }) {
            return null
        }

        val text = body.lowercase()

        // 2. Transaction type
        val isDebit = DEBIT_KEYWORDS.any { text.contains(it) }
        // val isCredit = CREDIT_KEYWORDS.any { text.contains(it) }

        if (!isDebit) return null

        // Log.d("BankSmsParser", "isDebit: $isDebit")

        // 3. Amount extraction
        val match = AMOUNT_REGEX.find(body) ?: return null
        val amount = match.groupValues[1]
            .replace(",", "")
            .toDoubleOrNull() ?: return null

        val type = TransactionType.DEBIT

        return ParsedTransaction(amount, type)
    }
}
