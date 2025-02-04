package no.nav.tms.token.support.idporten.sidecar.authentication

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.nimbusds.jose.jwk.RSAKey
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import no.nav.tms.token.support.idporten.sidecar.JwkBuilder
import no.nav.tms.token.support.idporten.sidecar.JwtBuilder
import org.amshove.kluent.`should not throw`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import java.time.Instant
import java.time.temporal.ChronoUnit.HOURS
import java.util.*


internal class TokenVerifierTest {
    private val jwk = JwkBuilder.generateJwk()
    private val issuer = "issuer"
    private val level4 = "Level4"

    private val now = Instant.now()
    private val hourFromNow = now.plus(1, HOURS)

    private val jwkProvider: JwkProvider = mockk()

    @AfterEach
    fun cleanUp() {
        clearMocks(jwkProvider)
    }

    @Test
    fun `Should accept valid token`() {
        val verifier = TokenVerifier(
            jwkProvider = jwkProvider,
            issuer = issuer,
            minLoginLevel = 4
        )

        val token = JwtBuilder.generateJwtString(
            issueTime = now.toDate(),
            expiryTime = hourFromNow.toDate(),
            issuer = issuer,
            loginLevel = level4,
            rsaKey = jwk
        )

        every { jwkProvider.get(any()) } returns jwk.toJwk()

        invoking {
            verifier.verifyAccessToken(token)
        } `should not throw` Exception::class
    }

    @Test
    fun `Should not accept token with invalid issuer`() {
        val verifier = TokenVerifier(
            jwkProvider = jwkProvider,
            issuer = issuer,
            minLoginLevel = 4
        )


        val token = JwtBuilder.generateJwtString(
            issueTime = now.toDate(),
            expiryTime = hourFromNow.toDate(),
            issuer = "invalid",
            loginLevel = level4,
            rsaKey = jwk
        )

        every { jwkProvider.get(any()) } returns jwk.toJwk()

        invoking {
            verifier.verifyAccessToken(token)
        } `should throw` Exception::class
    }

    @Test
    fun `Should not accept expired token`() {
        val verifier = TokenVerifier(
            jwkProvider = jwkProvider,
            issuer = issuer,
            minLoginLevel = 4
        )


        val token = JwtBuilder.generateJwtString(
            issueTime = now.minus(2, HOURS).toDate(),
            expiryTime = now.minus(1, HOURS).toDate(),
            issuer = issuer,
            loginLevel = level4,
            rsaKey = jwk
        )

        every { jwkProvider.get(any()) } returns jwk.toJwk()

        invoking {
            verifier.verifyAccessToken(token)
        } `should throw` Exception::class
    }

    @Test
    fun `Should not accept token with too low login level`() {
        val verifier = TokenVerifier(
            jwkProvider = jwkProvider,
            issuer = issuer,
            minLoginLevel = 4
        )


        val token = JwtBuilder.generateJwtString(
            issueTime = now.toDate(),
            expiryTime = hourFromNow.toDate(),
            issuer = issuer,
            loginLevel = "Level3",
            rsaKey = jwk
        )

        every { jwkProvider.get(any()) } returns jwk.toJwk()

        invoking {
            verifier.verifyAccessToken(token)
        } `should throw` Exception::class
    }
}

private fun Instant.toDate() = Date.from(this)

private fun RSAKey.toJwk() = Jwk.fromValues(
    listOf(
        "kid" to this.keyID,
        "kty" to this.keyType.toString(),
        "use" to this.keyUse.toString(),
        "e" to this.publicExponent.toString(),
        "n" to this.modulus.toString()
    ).toMap()
)
