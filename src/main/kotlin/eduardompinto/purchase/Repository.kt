package eduardompinto.purchase

import eduardompinto.plugins.dbQuery
import eduardompinto.purchase.Purchase.Companion.toPurchase
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object Repository {
    suspend fun findPurchaseById(id: Int) =
        dbQuery {
            PurchaseTable.selectAll().where { PurchaseTable.id eq id }.firstOrNull()?.toPurchase()
        }

    suspend fun createPurchase(
        purchaseRequest: PurchaseRequest,
    ): Int {
        return dbQuery {
            PurchaseTable.insert {
                it[email] = purchaseRequest.email
                it[name] = purchaseRequest.name
                it[lastName] = purchaseRequest.lastName
                it[document] = purchaseRequest.document
                it[address] = purchaseRequest.address
                it[complement] = purchaseRequest.complement
                it[city] = purchaseRequest.city
                it[country] = purchaseRequest.country
                it[countryState] = purchaseRequest.countryState
                it[phone] = purchaseRequest.phone
                it[postCode] = purchaseRequest.postCode
                it[order] = purchaseRequest.basket.toOrder()
                it[discountCode] = purchaseRequest.discountCode
            }[PurchaseTable.id].value
        }
    }
}
