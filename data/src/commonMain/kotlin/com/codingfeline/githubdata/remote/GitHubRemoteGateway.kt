package com.codingfeline.githubdata.remote

import com.codingfeline.githubdata.api.RepositoriesDocument
import com.codingfeline.githubdata.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.JsonObject

const val TOKEN = ""

interface GitHubRemoteGateway {
    suspend fun fetchUserRepository(login: String): UserResponse?
}

class GitHubRemoteGatewayImpl : GitHubRemoteGateway {

    private val endpoint = Url("https://api.github.com/graphql")

    private val client: HttpClient = HttpClient {
        install(GitHubAuthHeader.Feature) {
            token = TOKEN
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(UserResponse::class, UserResponse.serializer())
            }
        }
    }

    override suspend fun fetchUserRepository(login: String): UserResponse? {
        val rawResult = client.post<JsonObject>(endpoint) {
            body = RepositoriesDocument.RepositoriesQuery.requestBody(
                RepositoriesDocument.RepositoriesQuery.Variables(login)
            )
        }

        println("result: $rawResult")

        if (rawResult.containsKey("data")) {
            if (rawResult["data"].isNull) {
                return null
            } else {
                println("rawData.data: ${rawResult["data"]}")
                return null
            }
        } else {
            return null
        }
    }

}

class GitHubAuthHeader(val token: String) {

    class Config {
        var token: String? = null
    }

    private fun intercept(context: PipelineContext<Unit, HttpRequest>) {
        context.context.headers
    }

    companion object Feature : HttpClientFeature<Config, GitHubAuthHeader> {
        override val key: AttributeKey<GitHubAuthHeader> = AttributeKey("GitHubAuthHeader")

        override fun prepare(block: Config.() -> Unit): GitHubAuthHeader {
            return GitHubAuthHeader(Config().apply(block).token ?: "github.no.user")
        }

        override fun install(feature: GitHubAuthHeader, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
                context.accept(ContentType.Application.Json)

                context.headers {
                    append("Authorization", "bearer ${feature.token}")
                }
                proceedWith(payload)
            }
        }


    }
}