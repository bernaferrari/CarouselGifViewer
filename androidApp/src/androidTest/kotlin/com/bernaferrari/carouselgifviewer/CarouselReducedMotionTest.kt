package com.bernaferrari.carouselgifviewer

import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarouselReducedMotionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>(
        effectContext = object : MotionDurationScale { override val scaleFactor = 0f },
    )

    @Test fun tapsAndFlingsSettleWithAnimationsDisabled() {
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("carouselScrubber").fetchSemanticsNodes().isNotEmpty() }
        val strip = compose.onNodeWithTag("carouselScrubber")
        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(0) }
        repeat(5) { compose.onNodeWithContentDescription("Next").performClick() }
        compose.waitForIdle()
        val selected = compose.onAllNodes(isSelected() and hasAnyAncestor(hasTestTag("carouselScrubber")))
        val node = selected.fetchSemanticsNodes().single()
        assertEquals(5, node.config[SemanticsProperties.CollectionItemInfo].columnIndex)
        strip.performTouchInput { swipeLeft(durationMillis = 120) }
        compose.waitForIdle()
        val settled = selected.fetchSemanticsNodes().single()
        val title = settled.config[SemanticsProperties.ContentDescription].single()
        compose.onNodeWithTag("carouselTitle").assertTextEquals(title)
        compose.onNodeWithContentDescription("Share").assertIsEnabled()
    }
}
