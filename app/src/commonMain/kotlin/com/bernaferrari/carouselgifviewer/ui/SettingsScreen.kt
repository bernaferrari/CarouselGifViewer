package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.icons.AppIcons
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import com.bernaferrari.carouselgifviewer.data.ShaderType
import com.bernaferrari.carouselgifviewer.data.ThemeMode
import com.bernaferrari.carouselgifviewer.ui.components.*

val accentGradients = listOf(
    ColorGroup(Color(0xFF9A67EA), Color(0xFF7E57C2)),
    ColorGroup(Color(0xFF448AFF), Color(0xFF2979FF)),
    ColorGroup(Color(0xFF26A69A), Color(0xFF00897B)),
    ColorGroup(Color(0xFF66BB6A), Color(0xFF43A047)),
    ColorGroup(Color(0xFFFF7043), Color(0xFFF4511E)),
    ColorGroup(Color(0xFFEC407A), Color(0xFFD81B60)),
    ColorGroup(Color(0xFFEF5350), Color(0xFFE53935)),
    ColorGroup(Color(0xFFFFCA28), Color(0xFFFFB300)),
)

private const val APP_VERSION = "1.0.0"
private const val MAX_CACHE_MB = 100L

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onClearCache: () -> Unit,
    onClearDownloads: () -> Unit = {},
    onOpenUrl: (String) -> Unit,
    currentCacheSizeMb: Float = 45f,
    downloadedCount: Int = 0,
    downloadedSizeMb: Float = 0f,
    showTopBar: Boolean = true
) {
    val settings by viewModel.settings.collectAsState()
    val selectedTheme = settings.themeMode
    val selectedAccentIndex = settings.accentColorIndex
    val selectedAccentGroup = accentGradients.getOrElse(selectedAccentIndex) { accentGradients[0] }
    val selectedAccentColor = selectedAccentGroup.first
    val selectedShaderType = settings.shaderType
    
    Scaffold(
        topBar = {
            if (showTopBar) TopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            AppIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // APPEARANCE CARD (Merged Theme + Color)
            item {
                SectionLabel("APPEARANCE")
            }
            
            item {
                AppearanceCard(
                    selectedTheme = selectedTheme,
                    onThemeSelected = { viewModel.setThemeMode(it) },
                    selectedAccentIndex = selectedAccentIndex,
                    onAccentSelected = { viewModel.setAccentColorIndex(it) },
                    accentColor = selectedAccentColor
                )
            }
            
            // VISUAL EFFECTS
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionLabel("VISUAL EFFECTS")
            }
            
            item {
                PremiumCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Background Effect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            "Choose an animated background effect",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        ShaderTypeSelector(
                            selectedType = selectedShaderType,
                            onTypeSelected = { viewModel.setShaderType(it) },
                            accentColor = selectedAccentColor
                        )
                    }
                }
            }
            
            // STORAGE
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionLabel("STORAGE")
            }
            
            item {
                PremiumCard {
                    Column {
                        CacheStorageRow(
                            currentCacheMb = currentCacheSizeMb,
                            maxCacheMb = MAX_CACHE_MB,
                            onClearCache = onClearCache
                        )
                        if (downloadedCount > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                            DownloadsStorageRow(
                                downloadedCount = downloadedCount,
                                downloadedSizeMb = downloadedSizeMb,
                                onClearDownloads = onClearDownloads
                            )
                        }
                    }
                }
            }
            
            // ABOUT
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionLabel("ABOUT")
            }
            
            item {
                PremiumCard {
                    Column {
                        CompactLinkItem(
                            title = "Project Website",
                            onClick = { onOpenUrl("https://github.com/bernaferrari") }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                        CompactLinkItem(
                            title = "YouTube Channel",
                            onClick = { onOpenUrl("https://youtube.com/@bernaferrari") }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                        CompactLinkItem(
                            title = "Report an Issue",
                            onClick = { onOpenUrl("https://github.com/bernaferrari/CarouselGifViewer/issues") }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                FooterSection()
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun PremiumCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun AppearanceCard(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    selectedAccentIndex: Int,
    onAccentSelected: (Int) -> Unit,
    accentColor: Color
) {
    val selectedAccentGroup = accentGradients.getOrElse(selectedAccentIndex) { accentGradients[0] }
    
    PremiumCard {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                ThemeModeSelector(
                    selectedTheme = selectedTheme,
                    onThemeSelected = onThemeSelected,
                    accentColor = accentColor
                )
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            
            // Accent Color Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Accent Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp)
                )
                ColorPickerList(
                    currentColors = selectedAccentGroup,
                    gradientList = accentGradients,
                    onColorSelected = { newGroup ->
                        val index = accentGradients.indexOf(newGroup)
                        if (index != -1) onAccentSelected(index)
                    }
                )
            }
        }
    }
}

data class ThemeOption(
    val mode: ThemeMode,
    val label: String,
    val emoji: String
)

@Composable
private fun ThemeModeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    accentColor: Color
) {
    val themeOptions = listOf(
        ThemeOption(ThemeMode.Light, "Light", "☀️"),
        ThemeOption(ThemeMode.Dark, "Dark", "🌙"),
        ThemeOption(ThemeMode.System, "System", "📱"),
        ThemeOption(ThemeMode.MaterialYou, "You", "🎨")
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        themeOptions.forEach { option ->
            ThemeOptionCard(
                option = option,
                isSelected = selectedTheme == option.mode,
                accentColor = accentColor,
                onClick = { onThemeSelected(option.mode) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    option: ThemeOption,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeScale"
    )
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 16.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeCorner"
    )
    
    val backgroundColor = if (isSelected) {
        accentColor.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    
    val borderColor = if (isSelected) accentColor else Color.Transparent
    
    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .border(2.dp, borderColor, RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                option.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Text(
            option.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class ShaderOption(
    val type: ShaderType,
    val name: String,
    val gradient: List<Color>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShaderTypeSelector(
    selectedType: ShaderType,
    onTypeSelected: (ShaderType) -> Unit,
    accentColor: Color
) {
    val basicOptions = listOf(
        ShaderOption(ShaderType.Aurora, "Aurora", listOf(Color(0xFF00C6FF), Color(0xFF0072FF))),
        ShaderOption(ShaderType.LavaLamp, "Lava Lamp", listOf(Color(0xFFFFADF0), Color(0xFF4339F2))),
        ShaderOption(ShaderType.Waves, "Waves", listOf(Color(0xFF89F7FE), Color(0xFF66A6FF))),
        ShaderOption(ShaderType.Nebula, "Nebula", listOf(Color(0xFFFF00CC), Color(0xFF333399))),
        ShaderOption(ShaderType.Candy, "Candy", listOf(Color(0xFFEE9CA7), Color(0xFFFFDDE1))),
        ShaderOption(ShaderType.Orbs, "Orbs", listOf(Color(0xFF4facfe), Color(0xFF00f2fe))),
        ShaderOption(ShaderType.Vortex, "Vortex", listOf(Color(0xFF00c6ff), Color(0xFF0072ff))),
        ShaderOption(ShaderType.Wormhole, "Wormhole", listOf(Color(0xFF000000), Color(0xFF434343))),
        ShaderOption(ShaderType.Silk, "Silk", listOf(Color(0xFFFAACA8), Color(0xFFDDD6F3))),
        ShaderOption(ShaderType.Mosaic, "Mosaic", listOf(Color(0xFFFFEFBA), Color(0xFFFFFFFF))),
        ShaderOption(ShaderType.Plasma, "Plasma", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
        ShaderOption(ShaderType.Stardust, "Stardust", listOf(Color(0xFF654ea3), Color(0xFFeaafc8))),
        ShaderOption(ShaderType.Prism, "Prism", listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3))),
    )

    val creativeOptions = listOf(
        ShaderOption(ShaderType.AuroraAlt, "Aurora (Alt)", listOf(Color(0xFF00C6FF), Color(0xFF0072FF))),
        ShaderOption(ShaderType.LavaLampAlt, "Lava Lamp (Alt)", listOf(Color(0xFFFFADF0), Color(0xFF4339F2))),
        ShaderOption(ShaderType.NebulaAlt, "Nebula (Alt)", listOf(Color(0xFFFF00CC), Color(0xFF333399))),
        ShaderOption(ShaderType.WavesAlt, "Waves (Alt)", listOf(Color(0xFF89F7FE), Color(0xFF66A6FF))),
        ShaderOption(ShaderType.SilkAlt, "Silk (Alt)", listOf(Color(0xFFFAACA8), Color(0xFFDDD6F3))),
        ShaderOption(ShaderType.Dreamy, "Dreamy", listOf(Color(0xFF88CCFF), Color(0xFFFF99CC))),
        ShaderOption(ShaderType.Starlight, "Starlight", listOf(Color(0xFFFFDD44), Color(0xFF66DDFF))),
        ShaderOption(ShaderType.Transit, "Transit", listOf(Color(0xFFFF9900), Color(0xFF003366))),
        ShaderOption(ShaderType.HexGrid, "Hex Grid", listOf(Color(0xFFFF0000), Color(0xFF0000FF))),
        ShaderOption(ShaderType.GoldenRings, "Golden Rings", listOf(Color(0xFFCC9900), Color(0xFF333300))),
        ShaderOption(ShaderType.Ether, "Ether", listOf(Color(0xFFCCCCCC), Color(0xFF001133))),
        ShaderOption(ShaderType.Inferno, "Inferno", listOf(Color(0xFF330000), Color(0xFFFF3300))),
        ShaderOption(ShaderType.Cinders, "Cinders", listOf(Color(0xFF000066), Color(0xFF00CCFF))),
        ShaderOption(ShaderType.InkBlot, "Ink Blot", listOf(Color(0xFFCCCCCC), Color(0xFFDDDDDD))),
        ShaderOption(ShaderType.Monochrome, "Monochrome", listOf(Color(0xFF000000), Color(0xFFFFFFFF))),
        ShaderOption(ShaderType.Hellfire, "Hellfire", listOf(Color(0xFFFF3300), Color(0xFF330000))),
        ShaderOption(ShaderType.Scanline, "Scanline", listOf(Color(0xFF00FF00), Color(0xFF003300))),
        ShaderOption(ShaderType.Radar, "Radar", listOf(Color(0xFF0066CC), Color(0xFF001133))),
        ShaderOption(ShaderType.Chrome, "Chrome", listOf(Color(0xFFFFFFFF), Color(0xFFCCCCCC))),
        ShaderOption(ShaderType.Zen, "Zen", listOf(Color(0xFF66CCCC), Color(0xFFCC66CC))),
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Basic Collection
        Text(
            "Basic Collection",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            basicOptions.forEach { option ->
                ShaderOptionCard(
                    option = option,
                    isSelected = selectedType == option.type,
                    accentColor = accentColor,
                    onClick = { onTypeSelected(option.type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Creative Collection
        Text(
            "Creative Collection",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            creativeOptions.forEach { option ->
                ShaderOptionCard(
                    option = option,
                    isSelected = selectedType == option.type,
                    accentColor = accentColor,
                    onClick = { onTypeSelected(option.type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ShaderOptionCard(
    option: ShaderOption,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "shaderScale"
    )
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 14.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "shaderCorner"
    )
    
    val borderColor = if (isSelected) accentColor else Color.Transparent
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(200),
        label = "shaderBgColor"
    )
    
    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(if (isSelected) 2.dp else 0.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Live shader preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(option.gradient)) // Fallback/Underlay
                .applyShader(option.type, isSystemInDarkTheme())
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        AppIcons.Check,
                        contentDescription = "Selected",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Text(
            option.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CacheStorageRow(
    currentCacheMb: Float,
    maxCacheMb: Long,
    onClearCache: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClearCache)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Cache Storage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${currentCacheMb.toInt()}MB / ${maxCacheMb}MB used",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { (currentCacheMb / maxCacheMb).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(140.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (currentCacheMb > maxCacheMb * 0.8f) Color(0xFFEF5350) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                strokeCap = StrokeCap.Round
            )
        }
        
        IconButton(
            onClick = onClearCache,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                AppIcons.Delete,
                contentDescription = "Clear Cache",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DownloadsStorageRow(
    downloadedCount: Int,
    downloadedSizeMb: Float,
    onClearDownloads: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClearDownloads)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Offline Downloads",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "$downloadedCount videos • ${downloadedSizeMb.toInt()}MB",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = onClearDownloads,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                AppIcons.Delete,
                contentDescription = "Clear Downloads",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CompactLinkItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Icon(
            AppIcons.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .alpha(0.5f)
                .graphicsLayer { rotationZ = 180f }
        )
    }
}

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "Version $APP_VERSION",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            "Designed with love",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}


