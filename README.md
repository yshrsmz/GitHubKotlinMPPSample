# GitHubKNSample

Sample Project of Kotlin Multiplatform(Android/iOS).

It fetches viewer's user info and his repositories.

Currently experimenting with MVI-ish architecture using Kotlin Coroutines.

## Libraries

- Kodein(for DI)
- Ktor(for Networking)
- kgql(for GraphQL)
- SQLDelight(for local cache)
- BuildKonfig(to embed github api token in the code)


## Setup

1. get GitHub API token. needs user scope.
2. create `secret.properties` at the project root directory with following content

```properties
GITHUB_API_TOKEN=PLACE_YOUR_TOKEN
```
