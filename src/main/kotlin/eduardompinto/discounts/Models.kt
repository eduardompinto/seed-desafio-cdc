package eduardompinto.discounts

import eduardompinto.plugins.BigDecimalSerializer
import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.ValidRequest
import eduardompinto.plugins.Validatable
import eduardompinto.purchase.PurchaseTable
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.math.BigDecimal
import java.time.LocalDateTime

data class DiscountVoucher(
    val id: Int,
    val code: String,
    val discountPercentage: BigDecimal,
    val expiresAt: LocalDateTime,
    private val usedOnPurchaseId: Int? = null,
) {
    init {
        require(code.isNotBlank()) { "code cannot be blank" }
        require(discountPercentage > BigDecimal.ZERO) { "discountPercentage must be greater than zero" }
        require(expiresAt.isAfter(LocalDateTime.now())) { "expiresAt must be in the future" }
        discountPercentage.setScale(2)
    }

    fun isValid() = LocalDateTime.now().isBefore(expiresAt) && usedOnPurchaseId == null

    companion object {
        fun ResultRow.toDiscountVoucher() =
            DiscountVoucher(
                id = this[DiscountVoucherTable.id].value,
                code = this[DiscountVoucherTable.code],
                discountPercentage = this[DiscountVoucherTable.discountPercentage],
                expiresAt = this[DiscountVoucherTable.expiresAt],
                usedOnPurchaseId = this.getOrNull(PurchaseTable.id)?.value,
            )
    }
}

@ValidRequest
@Serializable
data class DiscountVoucherRequest(
    @NotBlank val code: String,
    @Serializable(with = BigDecimalSerializer::class)
    val discountPercentage: BigDecimal,
    @NotBlank val expiresAtISO8601: String,
) : Validatable {
    override suspend fun validate(): ValidationResult {
        val violations =
            buildList {
                if (discountPercentage <= BigDecimal.ZERO) {
                    add("discountPercentage must be greater than zero")
                }
                runCatching {
                    LocalDateTime.parse(expiresAtISO8601)
                }.onSuccess {
                    if (it.isBefore(LocalDateTime.now())) {
                        add("expiresAt must be in the future")
                    }
                }.onFailure {
                    add("expiresAt must be in ISO8601 format")
                }
            }
        return if (violations.isNotEmpty()) {
            ValidationResult.Invalid(violations)
        } else {
            ValidationResult.Valid
        }
    }

}

object DiscountVoucherTable : IntIdTable() {
    val code = varchar("code", 50).uniqueIndex()
    val discountPercentage = decimal("discount_percentage", 2, 2)
    val expiresAt = datetime("expires_at")
}