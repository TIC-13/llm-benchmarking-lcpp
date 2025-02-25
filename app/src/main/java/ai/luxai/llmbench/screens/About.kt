package ai.luxai.llmbench.screens

import ai.luxai.llmbench.components.AccordionItem
import ai.luxai.llmbench.components.AccordionText
import ai.luxai.llmbench.components.AccordionTitle
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.components.PressableLink
import ai.luxai.llmbench.utils.navigateToUrl
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val textSectionModifier = Modifier.padding(15.dp, 30.dp, 15.dp, 15.dp)
val linkModifier = Modifier.padding(15.dp, 10.dp, 0.dp, 0.dp)

@Composable
fun AboutScreen(
    navController: NavController
) {

    val context = LocalContext.current

    Scaffold(topBar = {
        AppTopBar(
            title = "About",
            onBack = { navController.popBackStack() }
        )
    }) { paddingValues ->
        HomeScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {

                TextSection(
                    modifier = textSectionModifier,
                    title = "About the app",
                    textColor = Color.White,
                    content = "This app performs benchmarking of LLMs (Large Language Models) running natively on Android devices. The main metrics captured include tokens per second (in Prefil and Decode phases), along with GPU and RAM consumption. The app uses the MLC LLM engine to compile and execute the models.\n" +
                            "\n" +
                            "More information about MLC LLM: llm.mlc.ai"
                )

                AccordionItem(
                    titleContent = { AccordionTitle(text = "How the Benchmarks Work") }
                ) {
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "GPU Benchmarking") }
                    ) {
                        AccordionText(text = "GPU usage is monitored through specific system files: /sys/kernel/gpu/gpu_busy for Mali GPUs and /sys/class/kgsl/kgsl-3d0/gpubusy for Adreno GPUs.")
                    }
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "RAM Benchmarking") }
                    ) {
                        AccordionText(text = "To measure RAM usage, the app uses the PSS (Proportional Set Size) value, which accounts for both the memory used solely by the process and the memory shared with other processes, but only in proportion to the number of processes using it.")
                    }
                    AccordionItem(
                        titleContent = { AccordionTitle(text = "Why doesn't the app measure CPU usage?") }
                    ) {
                        AccordionText(text = "Since Android 8.0, the operating system has restricted access to the /proc file during the app's runtime. This file was used, among other things, to obtain the CPU usage percentage. Due to these restrictions, it is not possible to capture the CPU usage percentage in the release version of the app, only in the debug version.")
                    }
                }

                TextSection(
                    modifier = textSectionModifier,
                    title = "About Lux.AI",
                    textColor = Color.White,
                    content = "Lux.AI is a project developed at the Center for Informatics at UFPE, as part of the PPI (Priority Programs and Projects of the IT Law), with support from the Ministry of Science, Technology, Innovations, and Communications, through the IT Law (Law No. 8.248/91) and the SOFTEX Program.\n" +
                            "\n" +
                            "Lux.AI’s main areas of study and development include:\n" +
                            "\n" +
                            "1. Computational Photography\n" +
                            "2. Artificial Intelligence\n" +
                            "3. Image Quality Analysis\n" +
                            "4. Performance Analysis of Heterogeneous Systems\n" +
                            "\n" +
                            "Lux.AI develops mobile applications with AI-based functionalities and applies techniques such as training, fine-tuning, pruning, and other performance optimization methods for AI models. Additionally, it offers consulting and personalized services to the industry, covering everything from AI model training and fine-tuning to performance improvement on restrictive hardware.",
                    titleIcon = Icons.Default.Camera,
                    componentAfterTitle = {
                        PressableLink(
                            modifier = linkModifier,
                            text = "Lux.AI's website",
                            onPress = { navigateToUrl(context, "https://luxai.cin.ufpe.br") },
                        )
                    }
                )
                TextSection(
                    modifier = textSectionModifier,
                    title = "See licences",
                    titleIcon = Icons.AutoMirrored.Filled.LibraryBooks,
                    content = "",
                    componentAfterTitle = {
                        PressableLink(
                            modifier = linkModifier,
                            text = "Licenses",
                            onPress = { navController.navigate("licenses") }
                        )
                    }
                )

            }
        }
    }
}

@Composable
fun TextSection(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    titleIcon: ImageVector? = null,
    iconTint: Color = Color.White,
    componentAfterTitle: @Composable() (ColumnScope.() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(titleIcon !== null){
                Icon(
                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp),
                    imageVector = titleIcon,
                    tint = iconTint,
                    contentDescription = "LuxAI Icon"
                )
            }
            Text(
                text = title,
                color = textColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (componentAfterTitle != null) {
            componentAfterTitle()
        }

        Text(
            modifier = Modifier.padding(15.dp),
            text = content,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}