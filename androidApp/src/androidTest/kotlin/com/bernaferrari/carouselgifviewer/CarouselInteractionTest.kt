package com.bernaferrari.carouselgifviewer

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.Lifecycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarouselInteractionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun selectedThumbnail() = compose.onAllNodes(
        isSelected() and hasAnyAncestor(hasTestTag("carouselScrubber"))
    )

    private fun assertSelectionMatchesTitle() {
        compose.waitUntil(10_000) { selectedThumbnail().fetchSemanticsNodes().size == 1 }
        compose.waitForIdle()
        val node = selectedThumbnail().fetchSemanticsNodes().single()
        val title = node.config[SemanticsProperties.ContentDescription].single()
        compose.onNodeWithTag("carouselTitle").assertTextEquals(title)
    }

    @Test fun gesturesEndpointsRapidTapsKeyboardAndRecreationKeepOneConsistentSelection() {
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("carouselScrubber").fetchSemanticsNodes().isNotEmpty() }
        val strip = compose.onNodeWithTag("carouselScrubber")
        val count = strip.fetchSemanticsNode().config[SemanticsProperties.CollectionInfo].columnCount
        assertSelectionMatchesTitle()

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(0) }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Previous").assertDoesNotExist()
        strip.performTouchInput { swipeRight(durationMillis = 120) }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Previous").assertDoesNotExist()

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(count - 1) }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Next").assertDoesNotExist()
        strip.performTouchInput { swipeLeft(durationMillis = 120) }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Next").assertDoesNotExist()

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(5.coerceAtMost(count - 1)) }
        // Hold a drag open: item actions must not target the previously committed video.
        strip.performTouchInput { down(center); moveTo(centerLeft, delayMillis = 250) }
        compose.onNodeWithContentDescription("Share").assertIsNotEnabled()
        strip.performTouchInput { up() }
        strip.performTouchInput { swipeLeft(durationMillis = 100) }
        strip.performTouchInput { swipeRight(durationMillis = 160) }
        assertSelectionMatchesTitle()

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(0) }
        compose.onNodeWithContentDescription("Next").performTouchInput {
            repeat(5) { click(); advanceEventTime(20) }
        }
        assertSelectionMatchesTitle()

        selectedThumbnail()[0].performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        selectedThumbnail()[0].performKeyInput { pressKey(Key.MoveHome) }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Previous").assertDoesNotExist()

        var accepted = true
        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { accepted = it(-1) }
        assertFalse(accepted)
        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { accepted = it(count) }
        assertFalse(accepted)

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(6.coerceAtMost(count - 1)) }
        assertSelectionMatchesTitle()
        // A short scrub that returns to this item must retain an explicit pause.
        if (compose.onAllNodesWithContentDescription("Pause").fetchSemanticsNodes().isEmpty()) {
            compose.onNodeWithTag("videoSurface").performClick()
        }
        compose.onNodeWithContentDescription("Pause").performClick()
        compose.onNodeWithContentDescription("Play").assertExists()
        strip.performTouchInput {
            down(center)
            moveTo(center.copy(x = center.x - 20), delayMillis = 600)
            up()
        }
        assertSelectionMatchesTitle()
        compose.onNodeWithContentDescription("Play").assertExists()
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithContentDescription("Play").assertExists()

        val before = selectedThumbnail().fetchSemanticsNodes().single().config[SemanticsProperties.ContentDescription]
        compose.activityRule.scenario.recreate()
        assertSelectionMatchesTitle()
        val after = selectedThumbnail().fetchSemanticsNodes().single().config[SemanticsProperties.ContentDescription]
        assertEquals(before, after)
        compose.onNodeWithContentDescription("Play").assertExists()
    }
}
