package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Short, wide windows keep the video and filmstrip alongside one another, without cropping either. */
@Composable
internal fun CarouselLayout(
    modifier: Modifier = Modifier,
    media: @Composable () -> Unit,
    strip: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        if (maxHeight < 300.dp && maxWidth >= 600.dp) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1.2f).fillMaxSize()) { media() }
                Box(Modifier.weight(1f).padding(end = 16.dp)) { strip() }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f).fillMaxWidth()) { media() }
                Spacer(Modifier.height(4.dp))
                strip()
            }
        }
    }
}
