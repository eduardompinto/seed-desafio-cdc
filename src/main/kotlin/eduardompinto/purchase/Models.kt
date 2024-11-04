package eduardompinto.purchase

import eduardompinto.book.Repository
import eduardompinto.commons.Document
import eduardompinto.commons.Document.Companion.asDocument
import eduardompinto.commons.Email
import eduardompinto.commons.Email.Companion.asEmail
import eduardompinto.country.Country
import eduardompinto.country.state.CountryState
import eduardompinto.discounts.DiscountVoucher
import eduardompinto.discounts.DiscountVoucherTable
import eduardompinto.discounts.Repository.findDiscountVoucher
import eduardompinto.plugins.BigDecimalSerializer
import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.ValidRequest
import eduardompinto.plugins.Validatable
import eduardompinto.purchase.Purchase.Order
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.json.jsonb
import java.math.BigDecimal
import eduardompinto.country.Repository as CountryRepository
import eduardompinto.country.state.Repository as CountryStateRepository

data class Purchase(
    val id: Int,
    val email: Email,
    val name: String,
    val lastName: String,
    val document: Document,
    val address: String,
    val complement: String,
    val city: String,
    val country: String,
    val countryState: String?,
    val phone: String,
    val postCode: String,
    val order: Order,
) {
    @Serializable
    data class Order(
        @Serializable(with = BigDecimalSerializer::class)
        val total: BigDecimal,
        val items: List<Item>,
        val discount: Discount? = null,
    ) {

        fun applyDiscount(discountVoucher: DiscountVoucher): Order {
            return when {
                discountVoucher.isValid() -> {
                    val discountValue = total.times(discountVoucher.discountPercentage)
                    val newTotal = total.minus(discountValue)
                    copy(
                        discount = Discount(discountValue, discountVoucher.discountPercentage),
                        total = newTotal
                    )
                }

                else -> this
            }
        }

        @Serializable
        data class Item(
            val bookId: Int,
            val quantity: Int,
        )

        @Serializable
        data class Discount(
            @Serializable(with = BigDecimalSerializer::class)
            val discountValue: BigDecimal,
            @Serializable(with = BigDecimalSerializer::class)
            val discountPercentage: BigDecimal
        )
    }

    companion object {
        fun ResultRow.toPurchase() =
            Purchase(
                id = this[PurchaseTable.id].value,
                email = this[PurchaseTable.email].asEmail(),
                name = this[PurchaseTable.name],
                lastName = this[PurchaseTable.lastName],
                document = this[PurchaseTable.document].asDocument(),
                address = this[PurchaseTable.address],
                complement = this[PurchaseTable.complement],
                city = this[PurchaseTable.city],
                country = this[PurchaseTable.country],
                countryState = this[PurchaseTable.countryState],
                phone = this[PurchaseTable.phone],
                postCode = this[PurchaseTable.postCode],
                order = this[PurchaseTable.order],
            )

        // TODO
        fun PurchaseRequest.toPurchase(request: PurchaseRequest) = Purchase(
            id = -1,
            email = request.email.asEmail(),
            name = request.name,
            lastName = request.lastName,
            document = request.document.asDocument(),
            address = request.address,
            complement = request.complement,
            city = request.city,
            country = request.country,
            countryState = request.countryState,
            phone = request.phone,
            postCode = request.postCode,
            order = request.basket.toOrder(),
        )
    }
}

@Serializable
@ValidRequest
data class PurchaseRequest(
    @NotBlank val email: String,
    @NotBlank val name: String,
    @NotBlank val lastName: String,
    @NotBlank val document: String,
    @NotBlank val address: String,
    @NotBlank val complement: String,
    @NotBlank val city: String,
    @NotBlank val country: String,
    val countryState: String,
    @NotBlank val phone: String,
    @NotBlank val postCode: String,
    val basket: BasketRequest,
    val discountCode: String? = null,
) : Validatable {
    @Serializable
    data class BasketRequest(
        @Serializable(with = BigDecimalSerializer::class)
        val total: BigDecimal,
        val items: List<ItemRequest>,
    ) {
        fun toOrder() = Order(
            total = total,
            items = items.map { Order.Item(it.bookId, it.quantity) },
        )

        @Serializable
        data class ItemRequest(
            val bookId: Int,
            val quantity: Int,
        )
    }

    override suspend fun validate(): ValidationResult {
        val violations = mutableListOf<String>()

        if (!Email.isValid(email)) {
            violations.add("Email has to be valid")
        }

        if (document.asDocument() is Document.Invalid) {
            violations.add("Document is not a valid CPF/CNPJ")
        }

        val countryId = CountryRepository.findCountryId(Country(country))

        if (countryId == null) {
            violations.add("Country doesn't exist")
        } else if (CountryRepository.countryHasStates(countryId)) {
            if (countryState.isBlank()) {
                violations.add("State is required for country: $country")
            } else if (!CountryStateRepository.countryStateExists(CountryState(countryState, country))) {
                violations.add("Invalid country state")
            }
        }

        if (basket.total < BigDecimal.ZERO) {
            violations.add("Basket has to be positive")
        }

        if (basket.items.isEmpty()) {
            violations.add("Empty basket")
        }

        if (basket.items.any { it.quantity < 0 }) {
            violations.add("Item quantity has to be positive")
        }

        val booksId = basket.items.map { it.bookId }.toSet()
        val booksIdAndPrice = Repository.findBookIdAndPrice(booksId)
        val validBooksId = booksIdAndPrice.keys

        if (!validBooksId.containsAll(booksId)) {
            violations.add("Invalid book id ${validBooksId - booksId}")
        } else {
            val serverSideTotal =
                basket.items.sumOf {
                    booksIdAndPrice[it.bookId]!!.times(it.quantity.toBigDecimal())
                }

            if (basket.total.compareTo(serverSideTotal) != 0) {
                violations.add("Invalid total")
            }
        }

        discountCode?.let {
            when (val discountVoucher = findDiscountVoucher(it)) {
                null -> violations.add("Discount code doesn't exist")
                else -> if (!discountVoucher.isValid()) violations.add("Discount code is not valid")
            }
        }


        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}


@Serializable
data class PurchaseResponse(
    val id: Int,
    val email: String,
    val name: String,
    val lastName: String,
    val document: String,
    val address: String,
    val complement: String,
    val city: String,
    val country: String,
    val countryState: String?,
    val phone: String,
    val postCode: String,
    val basket: OrderResponse,
) {
    @Serializable
    data class OrderResponse(
        @Serializable(with = BigDecimalSerializer::class)
        val total: BigDecimal,
        val items: List<ItemResponse>,
    ) {
        @Serializable
        data class ItemResponse(
            val bookId: Int,
            val quantity: Int,
        )
    }

    constructor(purchase: Purchase) : this(
        id = purchase.id,
        email = purchase.email.value,
        name = purchase.name,
        lastName = purchase.lastName,
        document = purchase.document.value,
        address = purchase.address,
        complement = purchase.complement,
        city = purchase.city,
        country = purchase.country,
        countryState = purchase.countryState,
        phone = purchase.phone,
        postCode = purchase.postCode,
        basket = OrderResponse(
            total = purchase.order.total,
            items = purchase.order.items.map { OrderResponse.ItemResponse(it.bookId, it.quantity) },
        ),
    )
}


object PurchaseTable : IntIdTable() {
    val email = varchar("email", 255).index()
    val name = varchar("name", 50).index()
    val lastName = varchar("last_name", 100)
    val document = varchar("document_number", 14)
    val address = varchar("address", 100)
    val complement = varchar("complement", 100)
    val city = varchar("city", 20)
    val country = varchar("country", 20)
    val countryState = varchar("state", 20)
    val phone = varchar("phone", 20)
    val postCode = varchar("postal_code", 20)
    val order = jsonb("order", Json::encodeToString, { Json.decodeFromString<Order>(it) })
    val discountCode = reference("discount_code", DiscountVoucherTable.code).nullable()
}
