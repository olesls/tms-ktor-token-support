package no.nav.tms.token.support.idporten.sidecar.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.auth.*

data class IdPortenTokenPrincipal(
        val accessToken: DecodedJWT
) : Principal

