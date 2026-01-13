package com.bernaferrari.carouselgifviewer.ui


import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.ui.components.AnimatedIconButton
import com.bernaferrari.carouselgifviewer.ui.components.GlassmorphicSurface
import com.bernaferrari.carouselgifviewer.ui.components.LoadingSpinner
import com.bernaferrari.carouselgifviewer.ui.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedIndex,
        pageCount = { uiState.items.size }
    )
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var drawerQuery by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val centeredScrubberIndex by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf 0
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - viewportCenter)
            }?.index ?: 0
        }
    }

    val isUserScrollingScrubber by remember {
        derivedStateOf { listState.isScrollInProgress }
    }
    
    // Track who is driving the sync to prevent feedback loops
    var pagerIsDriving by remember { mutableStateOf(false) }

    // SCRUBBER drives PAGER: instant jump while scrubbing (only if pager isn't driving)
    LaunchedEffect(centeredScrubberIndex, isUserScrollingScrubber) {
        if (isUserScrollingScrubber && !pagerIsDriving && uiState.items.isNotEmpty()) {
            val target = centeredScrubberIndex.coerceIn(0, uiState.items.lastIndex)
            if (pagerState.currentPage != target) {
                pagerState.scrollToPage(target)
            }
        }
    }

    // PAGER drives SCRUBBER: smooth animation while swiping
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress && uiState.items.isNotEmpty()) {
            pagerIsDriving = true
            val target = pagerState.currentPage.coerceIn(0, uiState.items.lastIndex)
            listState.animateScrollToItem(target)
        } else {
            pagerIsDriving = false
        }
    }

    // Update selectedIndex when pager settles (for UI display)
    LaunchedEffect(pagerState.settledPage) {
        if (uiState.items.isNotEmpty()) {
            viewModel.setSelectedIndex(pagerState.settledPage.coerceIn(0, uiState.items.lastIndex))
        }
    }

    // External navigation (arrows, drawer): animate both
    fun navigateTo(index: Int) {
        if (uiState.items.isEmpty()) return
        val target = index.coerceIn(0, uiState.items.lastIndex)
        pagerIsDriving = true
        scope.launch {
            launch { pagerState.animateScrollToPage(target) }
            launch { listState.animateScrollToItem(target) }
        }.invokeOnCompletion { pagerIsDriving = false }
        viewModel.setSelectedIndex(target)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(340.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.primaryGradient)
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "Videos",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${uiState.items.size} items available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = drawerQuery,
                        onValueChange = { drawerQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                "Search videos...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val filteredItems = if (drawerQuery.isBlank()) {
                        uiState.items
                    } else {
                        val q = drawerQuery.lowercase()
                        uiState.items.filter { it.title.lowercase().contains(q) }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = item.id == uiState.currentItem?.id
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = item.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                icon = {
                                    Card(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (isSelected) 4.dp else 1.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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
                                        navigateTo(index)
                                        scope.launch { drawerState.close() }
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoadingSpinner(modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading videos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Main pager area - each page is its own card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                pageSpacing = 4.dp
                            ) { page ->
                                val item = uiState.items.getOrNull(page) ?: return@HorizontalPager
                                
                                // Calculate scale based on distance from current page
                                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                val scale = 0.85f + (1f - abs(pageOffset).coerceIn(0f, 1f)) * 0.15f

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = 0.5f + (1f - abs(pageOffset).coerceIn(0f, 1f)) * 0.5f
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        shape = RoundedCornerShape(24.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (abs(pageOffset) < 0.5f) 12.dp else 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(24.dp))
                                        ) {
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
                                }
                            }

                            // Navigation buttons
                            if (pagerState.currentPage > 0) {
                                AnimatedIconButton(
                                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigateTo(pagerState.currentPage - 1)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 12.dp),
                                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            }
                            if (pagerState.currentPage < uiState.items.lastIndex) {
                                AnimatedIconButton(
                                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigateTo(pagerState.currentPage + 1)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 12.dp),
                                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            }

                            // Title overlay during scrubbing
                            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = isUserScrollingScrubber,
                                    enter = fadeIn(tween(150)),
                                    exit = fadeOut(tween(200))
                                ) {
                                    val previewTitle = uiState.items.getOrNull(centeredScrubberIndex)?.title
                                    if (!previewTitle.isNullOrBlank()) {
                                        GlassmorphicSurface(
                                            modifier = Modifier
                                                .padding(top = 20.dp)
                                                .width(180.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f)
                                        ) {
                                            Text(
                                                text = previewTitle,
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                                    .fillMaxWidth(),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Action toolbar below the video
                        val toolbarAlpha by animateFloatAsState(
                            targetValue = if (pagerState.isScrollInProgress || listState.isScrollInProgress) 0.4f else 1f,
                            animationSpec = tween(150),
                            label = "toolbarAlpha"
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .graphicsLayer { alpha = toolbarAlpha },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassmorphicSurface(
                                modifier = Modifier,
                                shape = RoundedCornerShape(24.dp),
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            // TODO: Toggle favorite
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            uiState.currentItem?.let { item ->
                                                onShareItem(item.title, item.shareUrl)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom scrubber
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                        )
                                    )
                                )
                        ) {
                            val itemSize = 80.dp
                            val sidePadding = if (maxWidth > itemSize) {
                                (maxWidth - itemSize) / 2
                            } else {
                                0.dp
                            }

                            LazyRow(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(horizontal = sidePadding),
                                verticalAlignment = Alignment.Bottom,
                                flingBehavior = rememberSnapFlingBehavior(
                                    lazyListState = listState,
                                    snapPosition = SnapPosition.Center
                                )
                            ) {
                                itemsIndexed(uiState.items) { index, item ->
                                    ScrubberItem(
                                        item = item,
                                        listState = listState,
                                        itemIndex = index,
                                        baseSize = itemSize,
                                        extraSize = 20.dp,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navigateTo(index)
                                        }
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
private fun ScrubberItem(
    item: GifItem,
    listState: androidx.compose.foundation.lazy.LazyListState,
    itemIndex: Int,
    baseSize: androidx.compose.ui.unit.Dp,
    extraSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    // Calculate progress based on distance from center (0 = far, 1 = centered)
    val progress by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
            if (itemInfo == null) {
                0f
            } else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                val itemCenter = itemInfo.offset + itemInfo.size / 2f
                val distance = abs(itemCenter - viewportCenter)
                val maxDistance = itemInfo.size * 1.5f
                (1f - (distance / maxDistance).coerceIn(0f, 1f))
            }
        }
    }

    // Lerp size: baseSize when far, baseSize + extraSize when centered
    val currentSize = baseSize + (extraSize * progress)
    val isCenter = progress > 0.7f

    Box(
        modifier = Modifier
            .size(currentSize)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        KmpAsyncImage(
            imageUrl = item.thumbnailUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize()
        )
        
        // Title overlay at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        // Border inside when centered
        if (isCenter) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}
