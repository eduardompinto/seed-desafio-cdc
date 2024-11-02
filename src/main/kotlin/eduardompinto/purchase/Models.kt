package eduardompinto.purchase

import eduardompinto.country.state.CountryState
import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.isValidCNPJ
import eduardompinto.plugins.isValidCPF
import eduardompinto.plugins.isValidEmail
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import eduardompinto.country.state.Repository as CountryStateRepository

@Serializable
data class PurchaseRequest(
    @NotBlank val email: String,
    @NotBlank val name: String,
    @NotBlank val lastName: String,
    @NotBlank val document: String,
    @NotBlank val address: String,
    @NotBlank val complement: String,
    @NotBlank val city: String,
    @NotBlank val country: String,
    @NotBlank val countryState: String,
    @NotBlank val phone: String,
    @NotBlank val postCode: String,
) {
    fun validate(): ValidationResult {
        val violations = mutableListOf<String>()

        if (!isValidEmail(email)) {
            violations.add("Email has to be valid")
        }

        if (!isValidCPF(document) || !isValidCNPJ(document)) {
            violations.add("Document is not a valid CPF/CNPJ")
        }

        if (!CountryStateRepository.countryStateExists(CountryState(countryState, country))) {
            violations.add("Invalid Country state")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}

@Serializable
data class PartialPurchase(val purchaseRequest: PurchaseRequest)
