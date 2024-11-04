package eduardompinto.discounts

import eduardompinto.discounts.DiscountVoucher.Companion.toDiscountVoucher
import eduardompinto.plugins.dbQuery
import eduardompinto.purchase.PurchaseTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

object Repository {

    suspend fun createDiscountVoucher(req: DiscountVoucherRequest) =
        dbQuery {
            DiscountVoucherTable.insert {
                it[code] = req.code
                it[discountPercentage] = req.discountPercentage
                it[expiresAt] = LocalDateTime.parse(req.expiresAtISO8601)
            }[DiscountVoucherTable.id].value
        }

    suspend fun findDiscountVoucher(code: String) =
        dbQuery {
            addLogger(StdOutSqlLogger)
            DiscountVoucherTable.join(
                PurchaseTable,
                JoinType.LEFT,
                DiscountVoucherTable.code,
                PurchaseTable.discountCode,
            ).selectAll().where { DiscountVoucherTable.code eq code }.firstOrNull()?.toDiscountVoucher()
        }


}