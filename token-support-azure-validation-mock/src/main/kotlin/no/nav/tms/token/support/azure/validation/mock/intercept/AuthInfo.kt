package no.nav.tms.token.support.azure.validation.mock.intercept

internal data class AuthInfo(
    val alwaysAuthenticated: Boolean,
    val azureJwt: String?
)
