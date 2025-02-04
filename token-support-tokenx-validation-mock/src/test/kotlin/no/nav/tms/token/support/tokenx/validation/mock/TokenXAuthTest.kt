package no.nav.tms.token.support.tokenx.validation.mock

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import no.nav.tms.token.support.tokenx.validation.*
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class TokenXAuthTest {

    private val userPid = "12345"

    @Test
    fun `Should respond with status 401 if alwaysAuthenticated is false`() = testApplication {

        application {
            testApi {
                installTokenXAuthMock {
                    alwaysAuthenticated = false
                }
            }
        }

        val response = client.get("/test")

        response.status `should be equal to` HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should respond ok if alwaysAuthenticated is true and principal info is defined`() = testApplication {

        application {
            testApi {
                installTokenXAuthMock {
                    alwaysAuthenticated = true
                    staticUserPid = userPid
                    staticSecurityLevel = SecurityLevel.LEVEL_4
                }
            }
        }

        val response = client.get("/test")

        response.status `should be equal to` HttpStatusCode.OK
        response.body<String>() `should be equal to` userPid
    }

    @Test
    fun `Should enable setting authenticator as default`() = testApplication {

        application {
            testApiWithDefault {
                installTokenXAuthMock {
                    setAsDefault = true
                    alwaysAuthenticated = false
                }
            }
        }

        val response = client.get("/test")

        response.status `should be equal to` HttpStatusCode.Unauthorized
    }

    private fun Application.testApi(authConfig: Application.() -> Unit) {

        authConfig()

        routing {
            authenticate(TokenXAuthenticator.name) {
                get("/test") {
                    val user = TokenXUserFactory.createTokenXUser(call)
                    call.respondText(user.ident)
                }
            }
        }
    }

    private fun Application.testApiWithDefault(authConfig: Application.() -> Unit) {

        authConfig()

        routing {
            authenticate {
                get("/test") {
                    val user = TokenXUserFactory.createTokenXUser(call)
                    call.respondText(user.ident)
                }
            }
        }
    }
}

