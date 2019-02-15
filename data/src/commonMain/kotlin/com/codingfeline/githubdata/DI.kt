package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.Database
import com.codingfeline.githubdata.local.GitHubLocalGateway
import com.codingfeline.githubdata.local.GitHubLocalGatewayImpl
import com.codingfeline.githubdata.remote.GitHubAuthHeader
import com.codingfeline.githubdata.remote.GitHubRemoteGateway
import com.codingfeline.githubdata.remote.GitHubRemoteGatewayImpl
import com.codingfeline.githubdata.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

object Tags {
    const val UI_CONTEXT = "uicontext"
    const val BG_CONTEXT = "bgcontext"
}

val remoteModule = Kodein.Module(name = "remote") {
    bind<HttpClient>() with singleton {
        HttpClient {
            install(GitHubAuthHeader.Feature) {
                token = BuildKonfig.GITHUB_TOKEN
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer().apply {
                    setMapper(UserResponse::class, UserResponse.serializer())
                }
            }
        }
    }
    bind<GitHubRemoteGateway>() with singleton {
        GitHubRemoteGatewayImpl(instance())
    }
}

val localModule = Kodein.Module(name = "local") {
    bind<Database>() with singleton { Database(instance()) }
    bind<GitHubLocalGateway>() with singleton { GitHubLocalGatewayImpl(instance()) }
}

val dataModule = Kodein.Module(name = "data") {
    bind<GitHubRepository>() with singleton { GitHubRepositoryImpl(instance(), instance()) }
}
