package no.nav.tms.token.support.idporten.sidecar

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.*

object JwtBuilder {

    fun generateJwtString(issueTime: Date, expiryTime: Date, issuer: String, loginLevel: String, rsaKey: RSAKey): String {
        return JWTClaimsSet.Builder()
            .issuer(issuer)
            .issueTime(issueTime)
            .expirationTime(expiryTime)
            .claim("acr", loginLevel)
            .jwtID(UUID.randomUUID().toString())
            .build()
            .sign(rsaKey)
            .serialize()
    }
}

private fun JWTClaimsSet.sign(rsaKey: RSAKey): SignedJWT =
        SignedJWT(
                JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(rsaKey.keyID)
                        .type(JOSEObjectType.JWT).build(),
                this
        ).apply {
            sign(RSASSASigner(rsaKey.toPrivateKey()))
        }
