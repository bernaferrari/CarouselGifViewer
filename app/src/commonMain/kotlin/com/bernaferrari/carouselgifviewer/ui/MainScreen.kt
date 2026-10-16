package com.bernaferrari.carouselgifviewer.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.icons.AppIcons
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.scrollBy
import androidx.compose.ui.semantics.scrollToIndex
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.data.ShaderType
import com.bernaferrari.carouselgifviewer.ui.components.AnimatedIconButton
import com.bernaferrari.carouselgifviewer.ui.components.GlassmorphicSurface
import com.bernaferrari.carouselgifviewer.ui.components.LoadingSpinner
import com.bernaferrari.carouselgifviewer.ui.components.ShaderBackground
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.time.TimeSource

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onShareItem: (String, String) -> Unit,
    onOpenBrowser: (String) -> Unit,
    onOpenSettings: () -> Unit,
    shaderEffectEnabled: Boolean = false,
    shaderType: ShaderType = ShaderType.Zen,
    nativeTopBar: (@Composable (title: String, onOpenMenu: (() -> Unit)?) -> Unit)? = null
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
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var showInfoSheet by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val navigation = remember(pagerState, scope) { CarouselNavigation(pagerState, scope) }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val itemStepPx = with(density) { 106.dp.toPx() }
    val itemIds = remember(uiState.items) { uiState.items.map { it.id } }
    var savedSelectionId by rememberSaveable { mutableStateOf<String?>(null) }
    var committedIndex by remember { mutableStateOf(-1) }
    var carouselReady by remember { mutableStateOf(false) }
    val stripScrollState = remember(pagerState, itemStepPx) {
        ScaledScrollableState(pagerState) {
            (pagerState.layoutInfo.pageSize + pagerState.layoutInfo.pageSpacing).toFloat() / itemStepPx
        }
    }
    val stripSnapLayout = remember(pagerState, itemStepPx, density) {
        CarouselSnapLayout(
            position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
            pageCount = { pagerState.pageCount },
            itemStep = { itemStepPx },
            minimumFlingVelocity = with(density) { 400.dp.toPx() },
        )
    }
    val stripFlingBehavior = rememberSnapFlingBehavior(stripSnapLayout)

    // Close keyboard when drawer closes
    LaunchedEffect(drawerState.isOpen) {
        if (!drawerState.isOpen) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    // Restore by identity, and reconcile inserts/removals before accepting gestures.
    LaunchedEffect(itemIds, uiState.isLoading) {
        carouselReady = false
        navigation.cancel()
        if (uiState.isLoading) return@LaunchedEffect
        val target = resolveCarouselSelection(itemIds, savedSelectionId, uiState.selectedIndex)
            ?: return@LaunchedEffect
        pagerState.scrollToPage(target)
        listState.scrollToItem(target)
        committedIndex = target
        savedSelectionId = itemIds[target]
        viewModel.setSelectedIndex(target)
        carouselReady = true
    }

    // The strip is a projection of the pager. It has no independent gesture or fling to fight.
    LaunchedEffect(pagerState, listState, itemStepPx) {
        snapshotFlow {
            if (carouselReady) pagerState.currentPage else null
        }.collectLatest { position ->
            if (position != null && pagerState.pageCount > 0) {
                listState.scrollToItem(position)

            }
        }
    }

    LaunchedEffect(pagerState) {
        var lastPage = pagerState.currentPage
        var lastTick = TimeSource.Monotonic.markNow()
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Skip the initial tick and rate-limit fast flings instead of buzzing on every frame.
            if (carouselReady && pagerState.isScrollInProgress && page != lastPage &&
                lastTick.elapsedNow().inWholeMilliseconds >= 60
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastTick = TimeSource.Monotonic.markNow()
            }
            lastPage = page
        }
    }

    LaunchedEffect(pagerState, itemIds) {
        snapshotFlow {
            if (carouselReady && !pagerState.isScrollInProgress && navigation.requestedPage == null) {
                pagerState.settledPage
            } else null
        }.collect { page ->
            if (page != null && page in itemIds.indices) {
                committedIndex = page
                savedSelectionId = itemIds[page]
                viewModel.setSelectedIndex(page)
            }
        }
    }

    LaunchedEffect(uiState.selectedIndex) {
        if (carouselReady && uiState.selectedIndex != committedIndex) {
            navigation.moveTo(uiState.selectedIndex, animated = false)
        }
    }

    fun navigateTo(index: Int) {
        if (carouselReady) navigation.moveTo(index)
    }

    // Drawer content composable (shared between modal and permanent)
    @Composable
    fun DrawerContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier.fillMaxHeight()) {
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

            OutlinedTextField(
                value = drawerQuery,
                onValueChange = { drawerQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        AppIcons.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (drawerQuery.isNotEmpty()) {
                        IconButton(onClick = { drawerQuery = "" }) {
                            Icon(
                                AppIcons.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                            // Show favorite indicator
                            if (item.isFavorite) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(16.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        AppIcons.Favorite,
                                        contentDescription = null,
                                        modifier = Modifier.size(10.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        Text(
                            text = highlightSearchText(item.title, drawerQuery),
                            style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.ContentOrLtr),
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

    // Main content composable
    @Composable
    fun MainContent(showMenuButton: Boolean = true) {
        Scaffold(
            topBar = {
                if (nativeTopBar != null) {
                    nativeTopBar(
                        uiState.items.getOrNull(pagerState.currentPage)?.title.orEmpty(),
                        if (showMenuButton) ({ scope.launch { drawerState.open() }; Unit }) else null
                    )
                } else TopAppBar(
                    title = {
                        Text(
                            text = uiState.items.getOrNull(pagerState.currentPage)?.title ?: "",
                            modifier = Modifier.testTag("carouselTitle"),
                            style = MaterialTheme.typography.titleLarge.copy(textDirection = TextDirection.ContentOrLtr),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        if (showMenuButton) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    AppIcons.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                AppIcons.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    // Error state
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                AppIcons.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Something went wrong",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            FilledTonalButton(
                                onClick = { viewModel.clearError() }
                            ) {
                                Icon(
                                    AppIcons.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again")
                            }
                        }
                    }
                    // Loading state
                    uiState.isLoading -> {
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
                    }
                    // Empty state (no search results)
                    uiState.items.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                AppIcons.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No videos found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Check your connection and try again",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // Content
                    else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Category filter chips row
                        val categories = uiState.availableCategories
                        if (categories.size > 2) { // Only show if there are actual categories (beyond All & Favorites)
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    FilterChip(
                                        selected = uiState.activeCategoryFilter.id == category.id,
                                        onClick = { viewModel.setCategoryFilter(category) },
                                        label = { Text(category.displayName) },
                                        leadingIcon = {
                                            when {
                                                category.id == "all" -> Icon(
                                                    AppIcons.Search,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                category.id == "favorites" -> Icon(
                                                    AppIcons.Favorite,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }

                        // Legacy filter chip row (for favorites compatibility)
                        AnimatedVisibility(
                            visible = uiState.activeFilter != FilterCategory.All && categories.size <= 2,
                            enter = slideInVertically { -it } + fadeIn(),
                            exit = slideOutVertically { -it } + fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { viewModel.setActiveFilter(FilterCategory.All) },
                                    label = {
                                        Text(
                                            when (uiState.activeFilter) {
                                                FilterCategory.Favorites -> "Favorites"
                                                else -> ""
                                            }
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            AppIcons.Favorite,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            AppIcons.Close,
                                            contentDescription = "Clear filter",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        CarouselLayout(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            media = {
                        // Main pager area
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                key = { page -> uiState.items[page].id },
                                userScrollEnabled = carouselReady,
                                modifier = Modifier.fillMaxSize().padding(bottom = 64.dp).testTag("carouselPager"),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                pageSpacing = 4.dp
                            ) { page ->
                                val item = uiState.items.getOrNull(page) ?: return@HorizontalPager

                                val isCentered by remember(pagerState, page) {
                                    derivedStateOf {
                                        abs((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction) < 0.5f
                                    }
                                }

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                            val proximity = 1f - abs(pageOffset).coerceIn(0f, 1f)
                                            val scale = 0.85f + proximity * 0.15f
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = 0.5f + proximity * 0.5f
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier.size(minOf(maxWidth, maxHeight)),
                                        shape = RoundedCornerShape(24.dp),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (isCentered) 12.dp else 4.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(24.dp))
                                        ) {
                                            if (page == pagerState.settledPage && carouselReady &&
                                                !pagerState.isScrollInProgress && navigation.requestedPage == null
                                            ) {
                                                KmpVideoPlayer(
                                                    videoUrl = item.videoUrl,
                                                    modifier = Modifier.fillMaxSize(),
                                                    playbackSpeed = uiState.playbackSpeed,
                                                    posterUrl = item.thumbnailUrl,
                                                    playbackEnabled = !pagerState.isScrollInProgress,
                                                    preferences = viewModel.playbackPreferences(item.id),
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
                                    icon = AppIcons.KeyboardArrowLeft,
                                    contentDescription = "Previous",
                                    enabled = carouselReady,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigation.moveBy(-1)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 12.dp, bottom = 64.dp),
                                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                )
                            }
                            if (pagerState.currentPage < uiState.items.lastIndex) {
                                AnimatedIconButton(
                                    icon = AppIcons.KeyboardArrowRight,
                                    contentDescription = "Next",
                                    enabled = carouselReady,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigation.moveBy(1)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 12.dp, bottom = 64.dp),
                                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                )
                            }



                            // Actions always address the committed, visible item, never a passing preview.
                            val actionsEnabled = carouselReady && !pagerState.isScrollInProgress &&
                                navigation.requestedPage == null &&
                                uiState.currentItem?.id == uiState.items.getOrNull(pagerState.currentPage)?.id
                            val toolbarAlpha by animateFloatAsState(
                                targetValue = if (actionsEnabled) 1f else 0.4f,
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
                                    val currentItem = uiState.currentItem
                                    val isFavorite = currentItem?.isFavorite == true

                                    val favoriteScale by animateFloatAsState(
                                        targetValue = if (isFavorite) 1.1f else 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "favoriteScale"
                                    )

                                    IconButton(
                                        enabled = actionsEnabled,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            currentItem?.let { viewModel.toggleFavorite(it.id) }
                                        }
                                    ) {
                                        Icon(
                                            if (isFavorite) AppIcons.Favorite else AppIcons.FavoriteBorder,
                                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.graphicsLayer {
                                                scaleX = favoriteScale
                                                scaleY = favoriteScale
                                            }
                                        )
                                    }

                                    // Download button
                                    val videoId = currentItem?.id
                                    val isDownloaded = videoId != null && videoId in uiState.downloadedIds
                                    val downloadProgress = videoId?.let { uiState.downloadProgress[it] }
                                    val isDownloading = downloadProgress != null && !downloadProgress.isComplete

                                    Box(
                                        modifier = Modifier.size(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isDownloading -> {
                                                // Show progress indicator
                                                CircularProgressIndicator(
                                                    progress = { downloadProgress.progress },
                                                    modifier = Modifier.size(24.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    strokeWidth = 2.dp
                                                )
                                                // Cancel button overlay
                                                IconButton(
                                                    enabled = actionsEnabled,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        videoId?.let { viewModel.cancelDownload(it) }
                                                    },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        AppIcons.Close,
                                                        contentDescription = "Cancel download",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                            isDownloaded -> {
                                                IconButton(
                                                    enabled = actionsEnabled,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        videoId?.let { viewModel.deleteDownload(it) }
                                                    }
                                                ) {
                                                    Icon(
                                                        AppIcons.DownloadDone,
                                                        contentDescription = "Downloaded - tap to remove",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            else -> {
                                                IconButton(
                                                    enabled = actionsEnabled,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        viewModel.downloadCurrentVideo()
                                                    }
                                                ) {
                                                    Icon(
                                                        AppIcons.Download,
                                                        contentDescription = "Download for offline",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    IconButton(
                                        enabled = actionsEnabled,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showInfoSheet = true
                                        }
                                    ) {
                                        Icon(
                                            AppIcons.Info,
                                            contentDescription = "Info",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    // Speed control
                                    val speedOptions = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
                                    val currentSpeedIndex = speedOptions.indexOfFirst { it == uiState.playbackSpeed }.coerceAtLeast(0)
                                    
                                    IconButton(
                                        enabled = actionsEnabled,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            val nextIndex = (currentSpeedIndex + 1) % speedOptions.size
                                            viewModel.setPlaybackSpeed(speedOptions[nextIndex])
                                        }
                                    ) {
                                        Text(
                                            text = if (uiState.playbackSpeed == 1f) "1x" else "${uiState.playbackSpeed}x",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = if (uiState.playbackSpeed == 1f) 
                                                MaterialTheme.colorScheme.onSurfaceVariant 
                                            else 
                                                MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    IconButton(
                                        enabled = actionsEnabled,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            uiState.currentItem?.let { item ->
                                                onShareItem(item.title, item.shareUrl)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            AppIcons.Share,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                            },
                            strip = {
                        // Bottom scrubber
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clipToBounds()
                        ) {
                            val itemSize = 100.dp
                            // Render one extra slot at each edge so fractional translation never reveals a gap.
                            val renderWidth = maxWidth + 212.dp
                            val sidePadding = if (renderWidth > itemSize) {
                                (renderWidth - itemSize) / 2
                            } else {
                                0.dp
                            }

                            LazyRow(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .requiredWidth(renderWidth)
                                    .testTag("carouselScrubber")
                                    .onPreviewKeyEvent { event ->
                                        if (event.type != KeyEventType.KeyDown || !carouselReady) false
                                        else when (event.key) {
                                            Key.DirectionRight, Key.DirectionLeft -> {
                                                val forward = (event.key == Key.DirectionRight) !=
                                                    (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl)
                                                navigation.moveBy(if (forward) 1 else -1, animated = false)
                                                true
                                            }
                                            Key.MoveHome -> { navigation.moveTo(0, animated = false); true }
                                            Key.MoveEnd -> { navigation.moveTo(uiState.items.lastIndex, animated = false); true }
                                            else -> false
                                        }
                                    }
                                    .scrollable(
                                        state = stripScrollState,
                                        orientation = Orientation.Horizontal,
                                        enabled = carouselReady && uiState.items.size > 1,
                                        reverseDirection = ScrollableDefaults.reverseDirection(
                                            layoutDirection, Orientation.Horizontal, false
                                        ),
                                        flingBehavior = stripFlingBehavior,
                                    )
                                    .semantics {
                                        collectionInfo = CollectionInfo(1, uiState.items.size)
                                        horizontalScrollAxisRange = ScrollAxisRange(
                                            value = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
                                            maxValue = { (pagerState.pageCount - 1).coerceAtLeast(0).toFloat() },
                                            reverseScrolling = layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl,
                                        )
                                        scrollToIndex { index ->
                                            if (index in uiState.items.indices && carouselReady) {
                                                navigation.moveTo(index, animated = false)
                                                true
                                            } else false
                                        }
                                        scrollBy { x, _ ->
                                            if (x != 0f && carouselReady) {
                                                navigation.moveBy(if (x > 0) 1 else -1, animated = false)
                                                true
                                            } else false
                                        }
                                    }
                                    .graphicsLayer {
                                        val direction = if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) 1f else -1f
                                        translationX = direction * pagerState.currentPageOffsetFraction * itemStepPx
                                    },
                                userScrollEnabled = false,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(horizontal = sidePadding),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                itemsIndexed(uiState.items, key = { _, item -> item.id }) { index, item ->
                                    ScrubberItem(
                                        item = item,
                                        pagerState = pagerState,
                                        itemIndex = index,
                                        itemCount = uiState.items.size,
                                        isSelected = pagerState.currentPage == index,
                                        onFocus = {
                                            if (navigation.requestedPage == null) {
                                                navigation.moveTo(index, animated = false)
                                            }
                                        },
                                        baseSize = 80.dp,
                                        extraSize = 20.dp,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navigateTo(index)
                                        }
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .size(itemSize)
                                    .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                    .clearAndSetSemantics {}
                            )
                        }
                            },
                        )
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
                    Text(
                        text = currentItem?.title ?: "No item selected",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    currentItem?.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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
                                    val bibleComUrl = "https://www.bible.com/bible/1/${formatBibleReference(reference)}"
                                    val annotatedText = buildAnnotatedString {
                                        withLink(
                                            LinkAnnotation.Clickable(
                                                tag = "BIBLE",
                                                linkInteractionListener = { onOpenBrowser(bibleComUrl) }
                                            )
                                        ) {
                                            append(reference)
                                        }
                                        append(" →")
                                    }

                                    Text(
                                        text = annotatedText,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }

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

    // Adaptive layout based on screen width
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 840.dp && maxHeight >= 480.dp

        // Shader background layer
        ShaderBackground(
            modifier = Modifier.fillMaxSize(),
            enabled = shaderEffectEnabled,
            shaderType = shaderType
        )

        // Surface background - reduced opacity to let shader show through
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = if (shaderEffectEnabled) 0f else 1f))
        )

        if (isTablet) {
            // Permanent drawer for tablets
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(320.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        DrawerContent()
                    }
                }
            ) {
                MainContent(showMenuButton = false)
            }
        } else {
            // Modal drawer for phones
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(320.dp),
                        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        DrawerContent()
                    }
                }
            ) {
                MainContent(showMenuButton = true)
            }
        }
    }
}

@Composable
private fun highlightSearchText(text: String, query: String): AnnotatedString {
    if (query.isBlank()) {
        return AnnotatedString(text)
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    return buildAnnotatedString {
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()
        var currentIndex = 0

        while (currentIndex < text.length) {
            val matchIndex = lowerText.indexOf(lowerQuery, currentIndex)
            if (matchIndex == -1) {
                append(text.substring(currentIndex))
                break
            }

            // Append text before match
            if (matchIndex > currentIndex) {
                append(text.substring(currentIndex, matchIndex))
            }

            // Append highlighted match
            withStyle(
                SpanStyle(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(matchIndex, matchIndex + query.length))
            }

            currentIndex = matchIndex + query.length
        }
    }
}

private fun formatBibleReference(reference: String): String {
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
    pagerState: androidx.compose.foundation.pager.PagerState,
    itemIndex: Int,
    itemCount: Int,
    isSelected: Boolean,
    onFocus: () -> Unit,
    baseSize: androidx.compose.ui.unit.Dp,
    extraSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val progress = remember(pagerState, itemIndex) {
        derivedStateOf {
            val distance = abs(itemIndex - (pagerState.currentPage + pagerState.currentPageOffsetFraction))
            1f - (distance / 1.5f).coerceIn(0f, 1f)
        }
    }

    // Keep the row geometry fixed; magnification only updates the render layer.
    val maxSize = baseSize + extraSize
    Box(
        modifier = Modifier
            .size(maxSize)
            .graphicsLayer {
                val scale = (baseSize.value + extraSize.value * progress.value) / maxSize.value
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clip(RoundedCornerShape(12.dp))
            .onFocusChanged { if (it.isFocused && !isSelected) onFocus() }
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = item.title
                stateDescription = "${itemIndex + 1} of $itemCount"
                collectionItemInfo = CollectionItemInfo(0, 1, itemIndex, 1)
            }
    ) {
        KmpAsyncImage(
            imageUrl = item.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Favorite indicator
        if (item.isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(14.dp)
                    .background(
                        MaterialTheme.colorScheme.error,
                        RoundedCornerShape(3.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    AppIcons.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                    tint = Color.White
                )
            }
        }

        // Title overlay at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall.copy(textDirection = TextDirection.ContentOrLtr),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().clearAndSetSemantics {},
                textAlign = TextAlign.Center
            )
        }

    }
}
