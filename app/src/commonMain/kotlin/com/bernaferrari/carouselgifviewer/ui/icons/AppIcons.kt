/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bernaferrari.carouselgifviewer.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** App-owned vectors; only the icons used by the UI are included. */
internal object AppIcons {
    val ArrowBack: ImageVector by lazy {
        icon(
            "ArrowBack",
            "M20.0 11.0 H7.83 l5.59 -5.59 L12.0 4.0 l-8.0 8.0 l8.0 8.0 l1.41 -1.41 L7.83 13.0 H20.0 v-2.0 Z",
            autoMirror = true,
        )
    }

    val Check: ImageVector by lazy {
        icon(
            "Check",
            "M9.0 16.17 L4.83 12.0 l-1.42 1.41 L9.0 19.0 L21.0 7.0 l-1.41 -1.41 Z",
        )
    }

    val Close: ImageVector by lazy {
        icon(
            "Close",
            "M19.0 6.41 L17.59 5.0 L12.0 10.59 L6.41 5.0 L5.0 6.41 L10.59 12.0 L5.0 17.59 L6.41 19.0 L12.0 13.41 " +
                "L17.59 19.0 L19.0 17.59 L13.41 12.0 Z",
        )
    }

    val Delete: ImageVector by lazy {
        icon(
            "Delete",
            "M6.0 19.0 c0.0 1.1 0.9 2.0 2.0 2.0 h8.0 c1.1 0.0 2.0 -0.9 2.0 -2.0 V7.0 H6.0 v12.0 Z M19.0 4.0 h-3.5 " +
                "l-1.0 -1.0 h-5.0 l-1.0 1.0 H5.0 v2.0 h14.0 V4.0 Z",
        )
    }

    val Download: ImageVector by lazy {
        icon(
            "Download",
            "M5.0 20.0 h14.0 v-2.0 H5.0 V20.0 Z M19.0 9.0 h-4.0 V3.0 H9.0 v6.0 H5.0 l7.0 7.0 L19.0 9.0 Z",
        )
    }

    val DownloadDone: ImageVector by lazy {
        icon(
            "DownloadDone",
            "M20.13 5.41 l-1.41 -1.41 l-9.19 9.19 l-4.25 -4.24 l-1.41 1.41 l5.66 5.66 Z",
            "M5.0 18.0 h14.0 v2.0 h-14.0 Z",
        )
    }

    val Error: ImageVector by lazy {
        icon(
            "Error",
            "M12.0 2.0 C6.48 2.0 2.0 6.48 2.0 12.0 s4.48 10.0 10.0 10.0 s10.0 -4.48 10.0 -10.0 S17.52 2.0 12.0 " +
                "2.0 Z M13.0 17.0 h-2.0 v-2.0 h2.0 v2.0 Z M13.0 13.0 h-2.0 L11.0 7.0 h2.0 v6.0 Z",
        )
    }

    val Favorite: ImageVector by lazy {
        icon(
            "Favorite",
            "M12.0 21.35 l-1.45 -1.32 C5.4 15.36 2.0 12.28 2.0 8.5 C2.0 5.42 4.42 3.0 7.5 3.0 c1.74 0.0 3.41 0.81 " +
                "4.5 2.09 C13.09 3.81 14.76 3.0 16.5 3.0 C19.58 3.0 22.0 5.42 22.0 8.5 c0.0 3.78 -3.4 6.86 -8.55 " +
                "11.54 L12.0 21.35 Z",
        )
    }

    val FavoriteBorder: ImageVector by lazy {
        icon(
            "FavoriteBorder",
            "M16.5 3.0 c-1.74 0.0 -3.41 0.81 -4.5 2.09 C10.91 3.81 9.24 3.0 7.5 3.0 C4.42 3.0 2.0 5.42 2.0 8.5 " +
                "c0.0 3.78 3.4 6.86 8.55 11.54 L12.0 21.35 l1.45 -1.32 C18.6 15.36 22.0 12.28 22.0 8.5 C22.0 5.42 " +
                "19.58 3.0 16.5 3.0 Z M12.1 18.55 l-0.1 0.1 l-0.1 -0.1 C7.14 14.24 4.0 11.39 4.0 8.5 C4.0 6.5 5.5 5.0 " +
                "7.5 5.0 c1.54 0.0 3.04 0.99 3.57 2.36 h1.87 C13.46 5.99 14.96 5.0 16.5 5.0 c2.0 0.0 3.5 1.5 3.5 3.5 " +
                "c0.0 2.89 -3.14 5.74 -7.9 10.05 Z",
        )
    }

    val Info: ImageVector by lazy {
        icon(
            "Info",
            "M11.0 7.0 h2.0 v2.0 h-2.0 Z M11.0 11.0 h2.0 v6.0 h-2.0 Z M12.0 2.0 C6.48 2.0 2.0 6.48 2.0 12.0 s4.48 " +
                "10.0 10.0 10.0 s10.0 -4.48 10.0 -10.0 S17.52 2.0 12.0 2.0 Z M12.0 20.0 c-4.41 0.0 -8.0 -3.59 -8.0 " +
                "-8.0 s3.59 -8.0 8.0 -8.0 s8.0 3.59 8.0 8.0 s-3.59 8.0 -8.0 8.0 Z",
        )
    }

    val KeyboardArrowLeft: ImageVector by lazy {
        icon(
            "KeyboardArrowLeft",
            "M15.41 16.59 L10.83 12.0 l4.58 -4.59 L14.0 6.0 l-6.0 6.0 l6.0 6.0 l1.41 -1.41 Z",
            autoMirror = true,
        )
    }

    val KeyboardArrowRight: ImageVector by lazy {
        icon(
            "KeyboardArrowRight",
            "M8.59 16.59 L13.17 12.0 L8.59 7.41 L10.0 6.0 l6.0 6.0 l-6.0 6.0 l-1.41 -1.41 Z",
            autoMirror = true,
        )
    }

    val Menu: ImageVector by lazy {
        icon(
            "Menu",
            "M3.0 18.0 h18.0 v-2.0 L3.0 16.0 v2.0 Z M3.0 13.0 h18.0 v-2.0 L3.0 11.0 v2.0 Z M3.0 6.0 v2.0 h18.0 " +
                "L21.0 6.0 L3.0 6.0 Z",
        )
    }

    val Pause: ImageVector by lazy {
        icon(
            "Pause",
            "M6.0 19.0 h4.0 L10.0 5.0 L6.0 5.0 v14.0 Z M14.0 5.0 v14.0 h4.0 L18.0 5.0 h-4.0 Z",
        )
    }

    val PlayArrow: ImageVector by lazy {
        icon(
            "PlayArrow",
            "M8.0 5.0 v14.0 l11.0 -7.0 Z",
        )
    }

    val Refresh: ImageVector by lazy {
        icon(
            "Refresh",
            "M17.65 6.35 C16.2 4.9 14.21 4.0 12.0 4.0 c-4.42 0.0 -7.99 3.58 -7.99 8.0 s3.57 8.0 7.99 8.0 c3.73 " +
                "0.0 6.84 -2.55 7.73 -6.0 h-2.08 c-0.82 2.33 -3.04 4.0 -5.65 4.0 c-3.31 0.0 -6.0 -2.69 -6.0 -6.0 " +
                "s2.69 -6.0 6.0 -6.0 c1.66 0.0 3.14 0.69 4.22 1.78 L13.0 11.0 h7.0 V4.0 l-2.35 2.35 Z",
        )
    }

    val Search: ImageVector by lazy {
        icon(
            "Search",
            "M15.5 14.0 h-0.79 l-0.28 -0.27 C15.41 12.59 16.0 11.11 16.0 9.5 C16.0 5.91 13.09 3.0 9.5 3.0 S3.0 " +
                "5.91 3.0 9.5 S5.91 16.0 9.5 16.0 c1.61 0.0 3.09 -0.59 4.23 -1.57 l0.27 0.28 v0.79 l5.0 4.99 L20.49 " +
                "19.0 l-4.99 -5.0 Z M9.5 14.0 C7.01 14.0 5.0 11.99 5.0 9.5 S7.01 5.0 9.5 5.0 S14.0 7.01 14.0 9.5 " +
                "S11.99 14.0 9.5 14.0 Z",
        )
    }

    val SearchOff: ImageVector by lazy {
        icon(
            "SearchOff",
            "M15.5 14.0 h-0.79 l-0.28 -0.27 C15.41 12.59 16.0 11.11 16.0 9.5 C16.0 5.91 13.09 3.0 9.5 3.0 C6.08 " +
                "3.0 3.28 5.64 3.03 9.0 h2.02 C5.3 6.75 7.18 5.0 9.5 5.0 C11.99 5.0 14.0 7.01 14.0 9.5 S11.99 14.0 " +
                "9.5 14.0 c-0.17 0.0 -0.33 -0.03 -0.5 -0.05 v2.02 C9.17 15.99 9.33 16.0 9.5 16.0 c1.61 0.0 3.09 -0.59 " +
                "4.23 -1.57 L14.0 14.71 v0.79 l5.0 4.99 L20.49 19.0 L15.5 14.0 Z",
            "M6.47 10.82 l-2.47 2.47 l-2.47 -2.47 l-0.71 0.71 l2.47 2.47 l-2.47 2.47 l0.71 0.71 l2.47 -2.47 l2.47 " +
                "2.47 l0.71 -0.71 l-2.47 -2.47 l2.47 -2.47 Z",
        )
    }

    val Settings: ImageVector by lazy {
        icon(
            "Settings",
            "M19.14 12.94 c0.04 -0.3 0.06 -0.61 0.06 -0.94 c0.0 -0.32 -0.02 -0.64 -0.07 -0.94 l2.03 -1.58 c0.18 " +
                "-0.14 0.23 -0.41 0.12 -0.61 l-1.92 -3.32 c-0.12 -0.22 -0.37 -0.29 -0.59 -0.22 l-2.39 0.96 c-0.5 " +
                "-0.38 -1.03 -0.7 -1.62 -0.94 L14.4 2.81 c-0.04 -0.24 -0.24 -0.41 -0.48 -0.41 h-3.84 c-0.24 0.0 -0.43 " +
                "0.17 -0.47 0.41 L9.25 5.35 C8.66 5.59 8.12 5.92 7.63 6.29 L5.24 5.33 c-0.22 -0.08 -0.47 0.0 -0.59 " +
                "0.22 L2.74 8.87 C2.62 9.08 2.66 9.34 2.86 9.48 l2.03 1.58 C4.84 11.36 4.8 11.69 4.8 12.0 s0.02 0.64 " +
                "0.07 0.94 l-2.03 1.58 c-0.18 0.14 -0.23 0.41 -0.12 0.61 l1.92 3.32 c0.12 0.22 0.37 0.29 0.59 0.22 " +
                "l2.39 -0.96 c0.5 0.38 1.03 0.7 1.62 0.94 l0.36 2.54 c0.05 0.24 0.24 0.41 0.48 0.41 h3.84 c0.24 0.0 " +
                "0.44 -0.17 0.47 -0.41 l0.36 -2.54 c0.59 -0.24 1.13 -0.56 1.62 -0.94 l2.39 0.96 c0.22 0.08 0.47 0.0 " +
                "0.59 -0.22 l1.92 -3.32 c0.12 -0.22 0.07 -0.47 -0.12 -0.61 L19.14 12.94 Z M12.0 15.6 c-1.98 0.0 -3.6 " +
                "-1.62 -3.6 -3.6 s1.62 -3.6 3.6 -3.6 s3.6 1.62 3.6 3.6 S13.98 15.6 12.0 15.6 Z",
        )
    }

    val Share: ImageVector by lazy {
        icon(
            "Share",
            "M18.0 16.08 c-0.76 0.0 -1.44 0.3 -1.96 0.77 L8.91 12.7 c0.05 -0.23 0.09 -0.46 0.09 -0.7 s-0.04 -0.47 " +
                "-0.09 -0.7 l7.05 -4.11 c0.54 0.5 1.25 0.81 2.04 0.81 c1.66 0.0 3.0 -1.34 3.0 -3.0 s-1.34 -3.0 -3.0 " +
                "-3.0 s-3.0 1.34 -3.0 3.0 c0.0 0.24 0.04 0.47 0.09 0.7 L8.04 9.81 C7.5 9.31 6.79 9.0 6.0 9.0 c-1.66 " +
                "0.0 -3.0 1.34 -3.0 3.0 s1.34 3.0 3.0 3.0 c0.79 0.0 1.5 -0.31 2.04 -0.81 l7.12 4.16 c-0.05 0.21 -0.08 " +
                "0.43 -0.08 0.65 c0.0 1.61 1.31 2.92 2.92 2.92 c1.61 0.0 2.92 -1.31 2.92 -2.92 s-1.31 -2.92 -2.92 " +
                "-2.92 Z",
        )
    }

    val VolumeOff: ImageVector by lazy {
        icon(
            "VolumeOff",
            "M16.5 12.0 c0.0 -1.77 -1.02 -3.29 -2.5 -4.03 v2.21 l2.45 2.45 c0.03 -0.2 0.05 -0.41 0.05 -0.63 Z " +
                "M19.0 12.0 c0.0 0.94 -0.2 1.82 -0.54 2.64 l1.51 1.51 C20.63 14.91 21.0 13.5 21.0 12.0 c0.0 -4.28 " +
                "-2.99 -7.86 -7.0 -8.77 v2.06 c2.89 0.86 5.0 3.54 5.0 6.71 Z M4.27 3.0 L3.0 4.27 L7.73 9.0 L3.0 9.0 " +
                "v6.0 h4.0 l5.0 5.0 v-6.73 l4.25 4.25 c-0.67 0.52 -1.42 0.93 -2.25 1.18 v2.06 c1.38 -0.31 2.63 -0.95 " +
                "3.69 -1.81 L19.73 21.0 L21.0 19.73 l-9.0 -9.0 L4.27 3.0 Z M12.0 4.0 L9.91 6.09 L12.0 8.18 L12.0 4.0 " +
                "Z",
        )
    }

    val VolumeUp: ImageVector by lazy {
        icon(
            "VolumeUp",
            "M3.0 9.0 v6.0 h4.0 l5.0 5.0 L12.0 4.0 L7.0 9.0 L3.0 9.0 Z M16.5 12.0 c0.0 -1.77 -1.02 -3.29 -2.5 " +
                "-4.03 v8.05 c1.48 -0.73 2.5 -2.25 2.5 -4.02 Z M14.0 3.23 v2.06 c2.89 0.86 5.0 3.54 5.0 6.71 s-2.11 " +
                "5.85 -5.0 6.71 v2.06 c4.01 -0.91 7.0 -4.49 7.0 -8.77 s-2.99 -7.86 -7.0 -8.77 Z",
        )
    }

    private fun icon(
        name: String,
        vararg paths: String,
        autoMirror: Boolean = false,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror,
    ).apply {
        paths.forEach { data ->
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                fill = SolidColor(Color.Black),
            )
        }
    }.build()
}
