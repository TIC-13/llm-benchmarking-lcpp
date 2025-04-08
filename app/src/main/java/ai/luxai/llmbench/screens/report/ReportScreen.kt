package ai.luxai.llmbench.screens.report

import ai.luxai.llmbench.api.usePostRequest
import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.Message
import ai.luxai.llmbench.state.Role
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ReportBody(
    val type: String,
    val user_description: String,
    val conversation: String,
    val model: String,
)

@Composable
fun ReportScreen(
    viewModel: LLMViewModel,
    navController: NavController
) {

    val context = LocalContext.current

    val report = viewModel.report.collectAsState()
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var reportText by remember { mutableStateOf("") }

    fun exit() {
        navController.popBackStack("home", false)
    }

    fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun sendSuccess() {
        showToast("Sent report successfully")
        exit()
    }

    fun sendError() {
        showToast("Send report failed")
    }

    val (isLoading, sendPost) = usePostRequest<ReportBody>(
        endpoint = "report",
        onSuccess = { sendSuccess() },
        onError = { sendError() }
    )

    fun sendReport() {
        val reportValue = report.value ?:
        throw Exception("No model has been selected to report")

        val selectedOptionValue = selectedOption

        if(selectedOptionValue == null) {
            showToast("Select the reason for the report")
            return
        }

        sendPost(ReportBody(
            type = selectedOptionValue,
            user_description = reportText,
            conversation = parseConversation(reportValue.messages),
            model = reportValue.model
        ))
    }

    BackHandler { exit() }

    Scaffold(topBar = {
        AppTopBar(
            title = "Report conversation",
            onBack = { exit() }
        )
    }) { paddingValues ->
        AppBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    ReportSection{
                        Text(text = "Reason for reporting:", color = Color.White, fontWeight = FontWeight.Medium)
                        Column {
                            listOf(
                                "Inappropriate content" to "inappropriate",
                                "Conversation is broken" to "broken",
                                "Other" to "other"
                            ).forEach { (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .clickable { selectedOption = value },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedOption == value,
                                        onClick = { selectedOption = value }
                                    )
                                    Text(text = label, color = Color.White, fontWeight = FontWeight.Light)
                                }
                            }
                        }
                    }

                    ReportSection {
                        Text(
                            text = "Additional details:",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth(1f),
                            value = reportText,
                            onValueChange = { reportText = it },
                            placeholder = { Text("Describe the issue...") },
                            minLines = 3,
                            maxLines = 3,
                        )
                    }

                    ReportSection {
                        Button(
                            onClick = { sendReport() },
                            modifier = Modifier.padding(top = 16.dp),
                            enabled = !isLoading
                        ) {
                            Text("Send Report")
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportSection(
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

fun parseConversation(messages: List<Message>): String {
    return messages.joinToString(separator = "") {
        if (it.role === Role.APP)
            "${"<app>"}${it.text}${"</app>"}"
        else
            "${"<user>"}${it.text}${"</user>"}"
    }
}