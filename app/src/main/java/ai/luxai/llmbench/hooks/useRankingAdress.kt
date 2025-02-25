package ai.luxai.llmbench.hooks

import ai.luxai.llmbench.BuildConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class RankingAddress(
    val address: String,
    val isValid: Boolean
)

@Composable
fun useRankingAddress(): RankingAddress {
    val rankingAddress = remember {
        BuildConfig.RANKING_ADDRESS
    }
    val rankingIsValid = remember {
        rankingAddress.startsWith("http")
    }

    return RankingAddress(
        address = rankingAddress,
        isValid = rankingIsValid
    )
}