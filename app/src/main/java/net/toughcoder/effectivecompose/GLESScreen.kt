package net.toughcoder.effectivecompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GLESScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onBack: () -> Unit
) {
    Box(modifier = modifier.clickable { onBack() })
}