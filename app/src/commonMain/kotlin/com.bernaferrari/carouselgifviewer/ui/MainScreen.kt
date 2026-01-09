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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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

enum class SyncSource { Bottom, Top, External }

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var bottomSelectedIndex by remember { mutableStateOf(0) }
    var drawerQuery by remember { mutableStateOf("") }
    val density = LocalDensity.current
    var lastSyncSource by remember { mutableStateOf<SyncSource?>(null) }
    val syncToIndex: (Int, SyncSource) -> Unit = sync@{ targetIndex, source ->
        if (uiState.items.isEmpty()) return@sync
        val clamped = targetIndex.coerceIn(0, uiState.items.lastIndex)
        lastSyncSource = source
        scope.launch {
            if (pagerState.currentPage != clamped) {
                pagerState.animateScrollToPage(clamped)
            }
            if (listState.firstVisibleItemIndex != clamped) {
                listState.animateScrollToItem(clamped)
            }
        }
    }

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
    LaunchedEffect(listState, pagerState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (!scrolling && uiState.items.isNotEmpty()) {
                    val layoutInfo = listState.layoutInfo
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                    val centeredIndex = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                        abs((item.offset + item.size / 2) - viewportCenter)
                    }?.index ?: bottomSelectedIndex
                    bottomSelectedIndex = centeredIndex
                    lastSyncSource = SyncSource.Bottom
                    scope.launch {
                        if (pagerState.currentPage != centeredIndex) {
                            pagerState.animateScrollToPage(centeredIndex)
                        }
                    }
                }
            }
    }
    LaunchedEffect(pagerState, listState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (!scrolling && uiState.items.isNotEmpty()) {
                    if (lastSyncSource == SyncSource.Bottom) {
                        lastSyncSource = null
                        return@collect
                    }
                    lastSyncSource = SyncSource.Top
                    scope.launch {
                        val target = pagerState.currentPage.coerceIn(0, uiState.items.lastIndex)
                        if (listState.firstVisibleItemIndex != target) {
                            listState.animateScrollToItem(target)
                        }
                    }
                }
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Text(text = "All videos", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "Pick one to jump to it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = drawerQuery,
                        onValueChange = { drawerQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val filteredItems = if (drawerQuery.isBlank()) {
                        uiState.items
                    } else {
                        val q = drawerQuery.lowercase()
                        uiState.items.filter { it.title.lowercase().contains(q) }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = item.id == uiState.currentItem?.id
                            NavigationDrawerItem(
                                label = {
                                    Text(text = item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                },
                                icon = {
                                    Card(
                                        modifier = Modifier
                                            .width(44.dp)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        KmpAsyncImage(
                                            imageUrl = item.thumbnailUrl,
                                            contentDescription = item.title,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                },
                                selected = isSelected,
                                onClick = {
                                    val index = uiState.items.indexOfFirst { it.id == item.id }
                                    if (index >= 0) {
                                        syncToIndex(index, SyncSource.External)
                                        scope.launch { drawerState.close() }
                                    }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }
    ) {
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onOpenBrowser("https://gfycat.com/about") }
                        ) { Icon(Icons.Default.Info, contentDescription = "Info") }
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .aspectRatio(1f)
                                .align(Alignment.Center),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
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
                                if (pagerState.currentPage > 0) {
                                    FilledIconButton(
                                        onClick = {
                                            val target = (pagerState.currentPage - 1).coerceAtLeast(0)
                                            syncToIndex(target, SyncSource.External)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(start = 12.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = "Previous"
                                        )
                                    }
                                }
                                if (pagerState.currentPage < uiState.items.lastIndex) {
                                    FilledIconButton(
                                        onClick = {
                                            val target = (pagerState.currentPage + 1)
                                                .coerceAtMost(uiState.items.lastIndex.coerceAtLeast(0))
                                            syncToIndex(target, SyncSource.External)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 12.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = "Next"
                                        )
                                    }
                                }
                            }
                        }
                        if (listState.isScrollInProgress) {
                            val previewTitle = uiState.items.getOrNull(bottomSelectedIndex)?.title
                            if (!previewTitle.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 16.dp)
                                        .width(140.dp)
                                        .height(140.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = previewTitle,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
