import ai.grazie.api.gateway.client.SuspendableAPIGatewayClient
import ai.grazie.client.common.SuspendableHTTPClient
import ai.grazie.client.ktor.GrazieKtorHTTPClient
import ai.grazie.model.auth.v5.AuthData
import ai.grazie.model.llm.profile.OpenAIProfileIDs
import ai.grazie.model.llm.prompt.LLMPromptID
import kotlinx.coroutines.runBlocking

val jwtToken = "*.*"

fun main() {
    val httpClient = SuspendableHTTPClient.WithV5(
        GrazieKtorHTTPClient.Client.Default,
        AuthData(jwtToken)
    )
    val client = SuspendableAPIGatewayClient(
        serverUrl = "https://api.app.stgn.grazie.aws.intellij.net",
        httpClient
    )
    runBlocking {
        val result = client.llm().v5().chat {
            prompt = LLMPromptID("my-prompt")
            profile = OpenAIProfileIDs.Chat.ChatGPT
            messages {
                system("Answer using only one code block without any extra text")
                user("How to add two nums in python")
                assistant("""
                    ```
                    def add(a, b):
                        return a + b
                    
                """.trimIndent())
                user("How to compute gca in python?")
            }
        }

        result.collect {
            print(it.content)
        }
    }
}