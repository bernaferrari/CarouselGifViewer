package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.carouselgifviewer.data.GifItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onShareItem: (String, String) -> Unit,
    onOpenBrowser: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedIndex,
        pageCount = { uiState.items.size }
    )
    val scope = rememberCoroutineScope()
    var bottomSelectedIndex by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    abs((item.offset + item.size / 2) - viewportCenter)
                }?.index ?: 0
            }
            .distinctUntilChanged()
            .collect { index ->
                bottomSelectedIndex = index
            }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { index ->
                viewModel.setSelectedIndex(index)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.currentItem?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onOpenBrowser("https://gfycat.com/about") }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            uiState.currentItem?.let { item ->
                                onShareItem(item.title, item.shareUrl)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val item = uiState.items.getOrNull(page) ?: return@HorizontalPager
                            if (page == pagerState.currentPage) {
                                KmpVideoPlayer(
                                    videoUrl = item.videoUrl,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                KmpAsyncImage(
                                    imageUrl = item.thumbnailUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        val itemHeight = 100.dp
                        val selectedHeight = 110.dp
                        val maxItemWidth = 100.dp
                        val sidePadding = if (maxWidth > maxItemWidth) {
                            (maxWidth - maxItemWidth) / 2
                        } else {
                            0.dp
                        }
                        LazyRow(
                            state = listState,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = sidePadding),
                            flingBehavior = rememberSnapFlingBehavior(
                                lazyListState = listState,
                                snapPosition = SnapPosition.Center
                            )
                        ) {
                            itemsIndexed(uiState.items) { index, item ->
                                CarouselItem(
                                    item = item,
                                    isSelected = index == bottomSelectedIndex,
                                    onClick = {
                                        scope.launch {
                                            listState.animateScrollToItem(index)
                                        }
                                    },
                                    modifier = Modifier,
                                    listState = listState,
                                    itemIndex = index,
                                    itemHeight = itemHeight,
                                    maxWidth = maxItemWidth,
                                    minWidth = 72.dp,
                                    selectedHeight = selectedHeight,
                                    baseHeight = itemHeight,
                                    spacingPx = with(density) { 8.dp.toPx() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarouselItem(
    item: GifItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState,
    itemIndex: Int,
    itemHeight: androidx.compose.ui.unit.Dp,
    maxWidth: androidx.compose.ui.unit.Dp,
    minWidth: androidx.compose.ui.unit.Dp,
    selectedHeight: androidx.compose.ui.unit.Dp,
    baseHeight: androidx.compose.ui.unit.Dp,
    spacingPx: Float
) {
    val scaleProgress = remember(listState, itemIndex) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
            if (itemInfo == null) {
                0f
            } else {
                val itemCenter = itemInfo.offset + itemInfo.size / 2
                val distance = kotlin.math.abs(itemCenter - viewportCenter).toFloat()
                val maxDistance = (itemInfo.size + spacingPx).coerceAtLeast(1f)
                (1f - (distance / maxDistance).coerceIn(0f, 1f))
            }
        }
    }
    val targetWidth = minWidth + (maxWidth - minWidth) * scaleProgress.value
    val targetHeight = baseHeight + (selectedHeight - baseHeight) * scaleProgress.value
    val animatedWidth = animateDpAsState(targetValue = targetWidth, label = "carouselWidth")
    val animatedHeight = animateDpAsState(targetValue = targetHeight, label = "carouselHeight")

    Box(
        modifier = modifier
            .width(animatedWidth.value)
            .height(selectedHeight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight.value),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KmpAsyncImage(
                    imageUrl = item.thumbnailUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
