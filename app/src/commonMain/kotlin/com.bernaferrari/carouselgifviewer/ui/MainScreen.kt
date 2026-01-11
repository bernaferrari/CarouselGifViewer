package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.ui.components.AnimatedIconButton
import com.bernaferrari.carouselgifviewer.ui.components.GlassmorphicSurface
import com.bernaferrari.carouselgifviewer.ui.components.PremiumLoadingIndicator
import com.bernaferrari.carouselgifviewer.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Single source of truth for navigation events
 */
private sealed class NavigationEvent {
    data class FromPager(val index: Int) : NavigationEvent()
    data class FromScrubber(val index: Int) : NavigationEvent()
    data class External(val index: Int) : NavigationEvent()
}

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
    var drawerQuery by remember { mutableStateOf("") }
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    
    // Track if user is actively scrolling (for title overlay visibility)
    var isUserScrollingScrubber by remember { mutableStateOf(false) }
    // Suppress overlay for external navigation (drawer selection)
    var suppressOverlay by remember { mutableStateOf(false) }
    // Track which component initiated the scroll to prevent loops
    var scrollSource by remember { mutableStateOf<String?>(null) }
    
    // Compute the visually centered item from the scrubber
    val centeredScrubberIndex by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - viewportCenter)
            }?.index ?: 0
        }
    }
    
    // Navigate to index from external source (drawer, buttons) - animate both
    val navigateToIndex: (Int, Boolean) -> Unit = { targetIndex, fromExternal ->
        val clamped = targetIndex.coerceIn(0, uiState.items.lastIndex.coerceAtLeast(0))
        if (fromExternal) {
            suppressOverlay = true
        }
        scrollSource = "external"
        scope.launch {
            launch { pagerState.animateScrollToPage(clamped) }
            launch { listState.animateScrollToItem(clamped) }
        }
    }

    // Sync pager page changes to ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { index ->
                viewModel.setSelectedIndex(index)
            }
    }
    
    // GALLERY-STYLE SYNC: When user scrolls scrubber, jump pager INSTANTLY (no animation)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress to centeredScrubberIndex }
            .collect { (isScrolling, centerIndex) ->
                isUserScrollingScrubber = isScrolling
                
                if (isScrolling && scrollSource != "pager" && uiState.items.isNotEmpty()) {
                    scrollSource = "scrubber"
                    // INSTANT jump - no animation, like Apple Photos
                    val target = centerIndex.coerceIn(0, uiState.items.lastIndex)
                    if (pagerState.currentPage != target) {
                        pagerState.scrollToPage(target)
                    }
                }
                
                if (!isScrolling) {
                    suppressOverlay = false
                    scrollSource = null
                }
            }
    }
    
    // GALLERY-STYLE SYNC: When user scrolls pager, scrubber slides along in real-time
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .collect { (currentPage, isScrolling) ->
                if (scrollSource != "scrubber" && uiState.items.isNotEmpty()) {
                    if (isScrolling) {
                        scrollSource = "pager"
                    }
                    // Scroll scrubber to match pager (instant, follows along)
                    val target = currentPage.coerceIn(0, uiState.items.lastIndex)
                    listState.scrollToItem(target)
                }
                
                if (!isScrolling && scrollSource == "pager") {
                    scrollSource = null
                }
            }
    }
    
    // Reset suppress overlay after short delay
    LaunchedEffect(suppressOverlay) {
        if (suppressOverlay) {
            delay(800)
            suppressOverlay = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(340.dp),
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    // Gradient header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
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
                    
                    // Search field
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
                                        shape = RoundedCornerShape(12.dp),
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
                                        navigateToIndex(index, true)
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
                    actions = {
                        IconButton(
                            onClick = { onOpenBrowser("https://gfycat.com/about") }
                        ) { 
                            Icon(
                                Icons.Default.Info, 
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        }
                        IconButton(
                            onClick = {
                                uiState.currentItem?.let { item ->
                                    onShareItem(item.title, item.shareUrl)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Share, 
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary
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
                    // Premium loading state
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PremiumLoadingIndicator(
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading videos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Main video card area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            // Premium video card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .aspectRatio(1f)
                                    .align(Alignment.Center),
                                shape = RoundedCornerShape(28.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 12.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(28.dp))
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
                                    
                                    // Navigation buttons
                                    if (pagerState.currentPage > 0) {
                                        AnimatedIconButton(
                                            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = "Previous",
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                val target = (pagerState.currentPage - 1).coerceAtLeast(0)
                                                navigateToIndex(target, true)
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
                                                val target = (pagerState.currentPage + 1)
                                                    .coerceAtMost(uiState.items.lastIndex.coerceAtLeast(0))
                                                navigateToIndex(target, true)
                                            },
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 12.dp),
                                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                            
                            // Glassmorphic title overlay during scrubbing
                            androidx.compose.animation.AnimatedVisibility(
                                visible = isUserScrollingScrubber && !suppressOverlay,
                                enter = fadeIn(tween(150)) + scaleIn(
                                    initialScale = 0.85f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                ),
                                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f),
                                modifier = Modifier.align(Alignment.TopCenter)
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

                        // Bottom scrubber with glassmorphic background
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                        )
                                    )
                                )
                        ) {
                            val itemHeight = 96.dp
                            val selectedHeight = 108.dp
                            val maxItemWidth = 96.dp
                            val sidePadding = if (maxWidth > maxItemWidth) {
                                (maxWidth - maxItemWidth) / 2
                            } else {
                                0.dp
                            }
                            
                            LazyRow(
                                state = listState,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(horizontal = sidePadding),
                                flingBehavior = rememberSnapFlingBehavior(
                                    lazyListState = listState,
                                    snapPosition = SnapPosition.Center
                                )
                            ) {
                                itemsIndexed(uiState.items) { index, item ->
                                    EnhancedCarouselItem(
                                        item = item,
                                        isSelected = index == centeredScrubberIndex,
                                        isPagerSelected = index == pagerState.currentPage,
                                        onClick = {
                                            // Apple Photos style: tap = instant jump
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                scrollSource = "external"
                                                listState.scrollToItem(index)
                                                pagerState.scrollToPage(index)
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
                                        spacingPx = with(density) { 10.dp.toPx() }
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
private fun EnhancedCarouselItem(
    item: GifItem,
    isSelected: Boolean,
    isPagerSelected: Boolean,
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
    
    val animatedWidth by animateDpAsState(
        targetValue = targetWidth, 
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "carouselWidth"
    )
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "carouselHeight"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(200),
        label = "carouselElevation"
    )
    
    val containerColor by animateColorAsState(
        targetValue = when {
            isPagerSelected -> MaterialTheme.colorScheme.primaryContainer
            isSelected -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(200),
        label = "carouselContainerColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isPagerSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(200),
        label = "carouselBorderColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    Box(
        modifier = modifier
            .width(animatedWidth)
            .height(selectedHeight)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .then(
                    if (isPagerSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    KmpAsyncImage(
                        imageUrl = item.thumbnailUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (isSelected || isPagerSelected) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isPagerSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isPagerSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }
        }
    }
}
