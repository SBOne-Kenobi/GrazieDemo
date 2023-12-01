import ai.grazie.api.gateway.client.SuspendableAPIGatewayClient
import ai.grazie.client.common.SuspendableHTTPClient
import ai.grazie.client.ktor.GrazieKtorHTTPClient
import ai.grazie.model.auth.v5.AuthData
import ai.grazie.model.llm.chat.function.LLMFunctionParameters
import ai.grazie.model.llm.profile.OpenAIProfileIDs
import ai.grazie.model.llm.prompt.LLMPromptID
import kotlinx.coroutines.runBlocking

val jwtToken = "*.*"

suspend fun SuspendableAPIGatewayClient.simpleExample() =
    llm().v5().chat {
        prompt = LLMPromptID("my-prompt")
        profile = OpenAIProfileIDs.Chat.ChatGPT
        messages {
            system("Answer using only one code block without any extra text")
            user("How to add two nums in python?")
            assistant(
                """
                ```python
                
                def add(a, b):
                    return a + b
                
                ```
            """.trimIndent()
            )
            user("How to sort int array in java using quick and heap sort depending on array length?")
        }
    }

suspend fun SuspendableAPIGatewayClient.exampleWithFunctions() =
    llm().v5().chat {
        prompt = LLMPromptID("my-prompt")
        profile = OpenAIProfileIDs.Chat.ChatGPT
        messages {
            system("Answer using only one code block without any extra text")
            functions {
                function {
                    name = "generateCode"
                    description = "Generate code on the specific language that does described action"
                    parameters = LLMFunctionParameters.fromJsonString(
                        """
                            {
                                "type": "object",
                                "properties": {
                                    "language": {
                                        "type": "string",
                                        "description": "The programming language, e.g. python or java"
                                    },
                                    "action": {
                                        "type": "string",
                                        "description": "The action that the code should perform"
                                    }
                                },
                                "required": [
                                    "language",
                                    "action"
                                ]
                            }
                        """.trimIndent()
                    )
                }
            }
            function(
                "generateCode", """
                    {
                        "language": "python",
                        "action": "add two nums"
                    }
                """.trimIndent()
            )
            assistant(
                "generateCode", """
                    ```python
                    
                    def add(a, b):
                        return a + b
                    
                    ```
                """.trimIndent()
            )
            function(
                "generateCode", """
                    {
                        "language": "java",
                        "action": "sort int array using quick and heap sort depending on length of array"
                    }
                """.trimIndent()
            )
        }
    }

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
        println("Simple example")
        client.simpleExample().collect { print(it.content) }
        println()
        println("Example with functions")
        client.exampleWithFunctions().collect { print(it.content) }
    }
}