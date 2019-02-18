package com.codingfeline.github.data

import com.codingfeline.github.BuildKonfig
import com.codingfeline.github.Modules
import com.codingfeline.github.data.local.Database
import com.codingfeline.github.data.local.GitHubLocalGateway
import com.codingfeline.github.data.local.GitHubLocalGatewayImpl
import com.codingfeline.github.data.remote.GitHubAuthHeader
import com.codingfeline.github.data.remote.GitHubRemoteGateway
import com.codingfeline.github.data.remote.GitHubRemoteGatewayImpl
import com.codingfeline.github.data.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton


val remoteModule = Kodein.Module(name = Modules.REMOTE) {
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

val localModule = Kodein.Module(name = Modules.LOCAL) {
    bind<Database>() with singleton { Database(instance()) }
    bind<GitHubLocalGateway>() with singleton { GitHubLocalGatewayImpl(instance()) }
}

val dataModule = Kodein.Module(name = Modules.DATA) {
    bind<GitHubRepository>() with singleton { GitHubRepositoryImpl(instance(), instance()) }
}
