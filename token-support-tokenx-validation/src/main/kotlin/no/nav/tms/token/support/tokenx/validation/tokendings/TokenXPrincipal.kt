package no.nav.tms.token.support.tokenx.validation.tokendings

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.auth.*

data class TokenXPrincipal(val decodedJWT: DecodedJWT) : Principal
