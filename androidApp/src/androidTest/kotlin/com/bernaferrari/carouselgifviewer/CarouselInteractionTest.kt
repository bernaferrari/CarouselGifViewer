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
        val center = compose.onNodeWithTag("carouselScrubber").fetchSemanticsNode().boundsInRoot.center.x
        org.junit.Assert.assertEquals("Selected thumbnail must be centered", center, node.boundsInRoot.center.x, 1f)
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
        compose.mainClock.autoAdvance = false
        try {
            strip.performTouchInput { swipeLeft(durationMillis = 100) }
            compose.mainClock.advanceTimeBy(64)
            strip.performTouchInput { swipeRight(durationMillis = 160) }
            compose.mainClock.advanceTimeBy(64)
            compose.onNodeWithContentDescription("Next").performClick()
        } finally {
            compose.mainClock.autoAdvance = true
        }
        assertSelectionMatchesTitle()

        strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(0) }
        compose.onNodeWithContentDescription("Next").performTouchInput {
            repeat(5) { click(); advanceEventTime(20) }
        }
        assertSelectionMatchesTitle()
        assertEquals("6 of $count", selectedThumbnail().fetchSemanticsNodes().single().config[SemanticsProperties.StateDescription])

        // A real key event exits Android touch mode before focus is requested.
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_TAB)
        compose.waitForIdle()
        selectedThumbnail()[0].performSemanticsAction(SemanticsActions.RequestFocus) {
            org.junit.Assert.assertTrue("Selected thumbnail must accept keyboard focus", it())
        }
        selectedThumbnail()[0].assertIsFocused()
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
    @Test
    @androidx.test.filters.SdkSuppress(minSdkVersion = 33)
    fun rtlAndLandscapePreserveDirectionAndSelection() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val locales = context.getSystemService(android.app.LocaleManager::class.java)
        val original = locales.applicationLocales
        try {
            compose.runOnUiThread { locales.applicationLocales = android.os.LocaleList.forLanguageTags("ar") }
            compose.waitUntil(10_000) {
                runCatching {
                    compose.activity.resources.configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL
                }.getOrDefault(false)
            }
            val strip = compose.onNodeWithTag("carouselScrubber")
            compose.waitUntil(10_000) { compose.onAllNodesWithTag("carouselScrubber").fetchSemanticsNodes().isNotEmpty() }
            org.junit.Assert.assertTrue(strip.fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange].reverseScrolling)
            strip.performSemanticsAction(SemanticsActions.ScrollToIndex) { it(10) }
            assertSelectionMatchesTitle()
            strip.performTouchInput { swipeRight(durationMillis = 350) }
            assertSelectionMatchesTitle()
            val index = selectedThumbnail().fetchSemanticsNodes().single()
                .config[SemanticsProperties.CollectionItemInfo].columnIndex
            org.junit.Assert.assertTrue("A rightward RTL swipe must move forward", index > 10)
            val before = selectedThumbnail().fetchSemanticsNodes().single().config[SemanticsProperties.ContentDescription]
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            compose.waitUntil(10_000) {
                runCatching {
                    compose.activity.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
                }.getOrDefault(false)
            }
            assertSelectionMatchesTitle()
            assertEquals(before, selectedThumbnail().fetchSemanticsNodes().single().config[SemanticsProperties.ContentDescription])
            // Configuration changes precede the system rotation animation finishing.
            android.os.SystemClock.sleep(750)
            val bitmap = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            java.io.File(context.cacheDir, "carousel-rtl-landscape.png").outputStream().use {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
            }
            bitmap.recycle()
        } finally {
            compose.runOnUiThread { locales.applicationLocales = original }
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

}
