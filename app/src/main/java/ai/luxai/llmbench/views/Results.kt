package ai.luxai.llmbench.views

import ai.luxai.llmbench.R
import ai.luxai.llmbench.components.AccordionItem
import ai.luxai.llmbench.components.AccordionText
import ai.luxai.llmbench.components.AccordionTitle
import ai.luxai.llmbench.components.AlertCard
import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.components.LockScreenOrientation
import ai.luxai.llmbench.hooks.useRankingAddress
import ai.luxai.llmbench.state.BenchmarkResult
import ai.luxai.llmbench.utils.navigateToUrl
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultView(
    title: String,
    onBack: (() -> Unit)?,
    onContinue: () -> Unit,
    continueLabel: String,
    results: List<BenchmarkResult>
) {
    val localFocusManager = LocalFocusManager.current
    val context = LocalContext.current

    val rankingAddress = useRankingAddress()

    Scaffold(topBar = {
        AppTopBar(
            title = title,
            onBack = onBack
        )
    }, modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            localFocusManager.clearFocus()
        })
    }) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                if (rankingAddress.isValid) {
                    Button(onClick = { navigateToUrl(context, rankingAddress.address) }) {
                        Icon(
                            painter = painterResource(R.drawable.web),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = "See global ranking"
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                AccordionItem(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    titleContent = {
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 5.dp),
                            painter = painterResource(id = R.drawable.help_circle_outline),
                            contentDescription = "help icon",
                            tint = Color.White,
                        )
                        AccordionTitle(text = "Help")
                    }
                ) {
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "What is prefill?") }
                    ) {
                        AccordionText(
                            text = "Prefill tok/s measures how many tokens the model can process per second during the initial setup phase."
                        )
                    }
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "What is decode?") }
                    ) {
                        AccordionText(
                            text = "Decode tok/s measures how many tokens the model can generate per second during the decoding phase."
                        )
                    }
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "Why can't the tok/s values be measured?") }
                    ) {
                        AccordionText(
                            text = "When the response takes too long, the app assumes that the model is broken or has entered a loop and interrupts the response. In that case, the tok/s values are not measured."
                        )
                    }
                    AccordionText(text = "STD = Standard Deviation")
                }
                results.map {
                    Spacer(modifier = Modifier.height(30.dp))
                    ResultCard(
                        result = it,
                    )
                }
                if (results.isEmpty()) {
                    AlertCard(text = "No benchmarking has been done yet")
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(onClick = onContinue) {
                    Text(text = continueLabel)
                }
            }
        }
    }
}


@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    result: BenchmarkResult,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.8F)
            .clip(RoundedCornerShape(15.dp))
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(0.dp, 15.dp),
                text = result.llm_model.name,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        ResultTable(result = result)

        if (result.decode.median === null) {
            Row(
                modifier = Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "warning icon",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tok/s values not measured",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun ResultTable(result: BenchmarkResult) {

    @Composable
    fun TableCell(
        modifier: Modifier = Modifier,
        value: String,
        bold: Boolean = false,
        textAlign: TextAlign = TextAlign.Left
    ) {
        Text(
            modifier = modifier,
            text = value,
            fontSize = 12.sp,
            textAlign = textAlign,
            fontWeight = if (bold)
                FontWeight.Bold
            else
                FontWeight.Normal
        )
    }

    data class RowContent(
        val text: String,
        val textAlign: TextAlign = TextAlign.Left,
        val bold: Boolean = false
    )

    @Composable
    fun TableRow(
        modifier: Modifier = Modifier,
        content: List<RowContent>
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            for ((index, rowValue) in content.withIndex()) {
                TableCell(
                    modifier = Modifier
                        .weight(1f),
                    textAlign = rowValue.textAlign,
                    value = rowValue.text,
                    bold = rowValue.bold
                )
            }
        }
    }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(10.dp, 0.dp, 0.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (result.decode.average !== null) {
            TableRow(
                content = listOf(
                    RowContent(
                        "Decoder Tok/s",
                        bold = true,
                        textAlign = TextAlign.Center
                    ),
                    RowContent(
                        formatDouble(result.decode.average, " tok/s"),
                        bold = true,
                        textAlign = TextAlign.Center
                    ),
                )
            )
        }

        if(result.prefill.sum !== null) {
            TableRow(
                content = listOf(
                    RowContent(
                        "Prefill time sum",
                        bold = true,
                        textAlign = TextAlign.Center
                    ),
                    RowContent(
                        formatDouble(result.prefill.sum/1000F, "s"),
                        bold = true,
                        textAlign = TextAlign.Center
                    ),
                )
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
        TableRow(
            content = listOf(
                RowContent(""),
                RowContent("Average", bold = true),
                RowContent("STD", bold = true),
                RowContent("Peak", bold = true)
            )
        )
        /**
        TableRow(
            content = listOf(
                RowContent("GPU", bold = true),
                RowContent(formatInt(result.gpu.average, "%")),
                RowContent(formatInt(result.gpu.std, "%")),
                RowContent(formatInt(result.gpu.peak, "%"))
            )
        )
        **/
        TableRow(
            content = listOf(
                RowContent("Tok/s", bold = true),
                RowContent(formatDouble(result.decode.average, " tok/s")),
                RowContent(formatDouble(result.decode.std, " tok/s")),
                RowContent(formatDouble(result.decode.peak, " tok/s"))
            )
        )

        TableRow(
            content = listOf(
                RowContent("RAM", bold = true),
                RowContent(formatInt(result.ram.average, "MB")),
                RowContent(formatInt(result.ram.std, "MB")),
                RowContent(formatInt(result.ram.peak, "MB"))
            )
        )

    }
}


fun formatDouble(number: Number?, suffix: String): String {
    if (number == null) return "-"
    return String.format("%.1f", number) + suffix
}

fun formatInt(number: Number?, suffix: String): String {
    if (number == null) return "-"
    return "${number.toInt()}${suffix}"
}