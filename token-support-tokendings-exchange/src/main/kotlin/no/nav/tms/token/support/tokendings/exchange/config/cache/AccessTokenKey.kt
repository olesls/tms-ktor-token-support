package no.nav.tms.token.support.tokendings.exchange.config.cache

internal data class AccessTokenKey(
        val subject: String,
        val targetApp: String
)
