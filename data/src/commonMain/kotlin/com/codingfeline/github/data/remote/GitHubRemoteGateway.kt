package com.codingfeline.github.data.remote

import com.codingfeline.github.data.remote.response.UserResponse
import com.codingfeline.github.platform.checkIfFrozen
import com.codingfeline.kgql.core.KgqlError
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal


interface GitHubRemoteGateway {
    suspend fun fetchViewerRepository(): KgqlResponse<UserResponse>
}

class GitHubRemoteGatewayImpl(
    @ThreadLocal private val client: HttpClient
) : GitHubRemoteGateway {

    private val endpoint = Url("https://api.github.com/graphql")

    override suspend fun fetchViewerRepository(): KgqlResponse<UserResponse> {
        checkIfFrozen("client", client)
        checkIfFrozen("GitHubRemoteRepositoryImpl", this)

        val rawResult = client.post<String>(endpoint) {
            body = RepositoriesDocument.Query.requestBody()
        }

        val jsonResult = Json.plain.parseJson(rawResult).jsonObject

        if (jsonResult.containsKey("data") && !jsonResult["data"].isNull) {
            println("rawData.data: ${jsonResult["data"].jsonObject["viewer"]}")
            return KgqlResponse(
                data = Json.nonstrict.fromJson(UserResponse.serializer(), jsonResult["data"].jsonObject["viewer"])
            )
        } else {
            return KgqlResponse(
                errors = Json.nonstrict.fromJson(KgqlError.serializer().list, jsonResult["errors"])
            )
        }
    }

}

data class KgqlResponse<T>(
    override val data: T? = null,
    override val errors: List<KgqlError>? = null
) : com.codingfeline.kgql.core.KgqlResponse<T>

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
