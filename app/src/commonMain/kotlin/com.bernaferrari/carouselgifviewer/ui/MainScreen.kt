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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
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
    onOpenBrowser: (String) -> Unit,
    onOpenSettings: () -> Unit
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
    var showInfoSheet by remember { mutableStateOf(false) }

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

    // Haptic feedback when centered item changes during scrubbing
    var lastHapticIndex by remember { mutableStateOf(-1) }
    
    // SCRUBBER drives PAGER: instant jump while scrubbing (only if pager isn't driving)
    LaunchedEffect(centeredScrubberIndex, isUserScrollingScrubber) {
        if (isUserScrollingScrubber && !pagerIsDriving && uiState.items.isNotEmpty()) {
            val target = centeredScrubberIndex.coerceIn(0, uiState.items.lastIndex)
            if (pagerState.currentPage != target) {
                pagerState.scrollToPage(target)
            }
            // Trigger haptic when centered item changes
            if (target != lastHapticIndex) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastHapticIndex = target
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
            launch { 
                // First scroll to bring item into view
                listState.scrollToItem(target)
                // Then animate to ensure it's centered (snap behavior will help)
                listState.animateScrollToItem(
                    index = target,
                    scrollOffset = 0
                )
            }
        }.invokeOnCompletion { pagerIsDriving = false }
        viewModel.setSelectedIndex(target)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Library",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.items.size} videos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Search
                    OutlinedTextField(
                        value = drawerQuery,
                        onValueChange = { drawerQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        singleLine = true,
                        placeholder = {
                            Text("Search")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
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
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = item.id == uiState.currentItem?.id
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val index = uiState.items.indexOfFirst { it.id == item.id }
                                        if (index >= 0) {
                                            navigateTo(index)
                                            scope.launch { drawerState.close() }
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    KmpAsyncImage(
                                        imageUrl = item.thumbnailUrl,
                                        contentDescription = item.title,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer 
                                           else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
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
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            
                            // Action toolbar overlaid at bottom of pager
                            val toolbarAlpha by animateFloatAsState(
                                targetValue = if (pagerState.isScrollInProgress || listState.isScrollInProgress) 0.4f else 1f,
                                animationSpec = tween(150),
                                label = "toolbarAlpha"
                            )
                            
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                                    .graphicsLayer { alpha = toolbarAlpha },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(0.dp),
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
                                            showInfoSheet = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.Info,
                                            contentDescription = "Info",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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

                        Spacer(modifier = Modifier.height(4.dp))

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
        
        // Info Bottom Sheet
        if (showInfoSheet) {
            val currentItem = uiState.currentItem
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = currentItem?.title ?: "No item selected",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Description
                    currentItem?.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Verse card
                    if (currentItem?.verseReference != null || currentItem?.verseText != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentItem.verseText?.let { verse ->
                                    Text(
                                        text = "\"$verse\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                currentItem.verseReference?.let { reference ->
                                    val annotatedText = buildAnnotatedString {
                                        pushStringAnnotation(tag = "BIBLE", annotation = reference)
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.primary,
                                                textDecoration = TextDecoration.Underline,
                                                fontWeight = FontWeight.Medium
                                            )
                                        ) {
                                            append(reference)
                                        }
                                        pop()
                                        append(" →")
                                    }
                                    
                                    ClickableText(
                                        text = annotatedText,
                                        style = MaterialTheme.typography.labelLarge,
                                        onClick = { offset ->
                                            annotatedText.getStringAnnotations(tag = "BIBLE", start = offset, end = offset)
                                                .firstOrNull()?.let { annotation ->
                                                    // Convert reference to Bible.com URL format
                                                    // e.g., "Genesis 1:1" -> "GEN.1.1"
                                                    val bibleComUrl = "https://www.bible.com/bible/1/${formatBibleReference(annotation.item)}"
                                                    onOpenBrowser(bibleComUrl)
                                                }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    // No info placeholder
                    if (currentItem?.description == null && currentItem?.verseReference == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No additional info available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to format Bible reference for Bible.com URL
private fun formatBibleReference(reference: String): String {
    // Simple conversion: "Genesis 1:1" -> "GEN.1.1"
    val bookAbbreviations = mapOf(
        "genesis" to "GEN", "exodus" to "EXO", "leviticus" to "LEV",
        "numbers" to "NUM", "deuteronomy" to "DEU", "joshua" to "JOS",
        "judges" to "JDG", "ruth" to "RUT", "1 samuel" to "1SA",
        "2 samuel" to "2SA", "1 kings" to "1KI", "2 kings" to "2KI",
        "psalms" to "PSA", "psalm" to "PSA", "proverbs" to "PRO",
        "matthew" to "MAT", "mark" to "MRK", "luke" to "LUK",
        "john" to "JHN", "acts" to "ACT", "romans" to "ROM",
        "revelation" to "REV"
    )
    
    val parts = reference.lowercase().split(" ", ":")
    if (parts.size < 2) return reference
    
    val book = bookAbbreviations[parts[0]] ?: parts[0].take(3).uppercase()
    val chapter = parts.getOrNull(1) ?: "1"
    val verse = parts.getOrNull(2) ?: "1"
    
    return "$book.$chapter.$verse"
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
