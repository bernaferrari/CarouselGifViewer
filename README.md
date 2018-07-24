<p align="center"><img src="assets/github_icon.png" alt="Carousel Gif Viewer" height="200px"></p>

Carousel Gif Viewer
===================

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0b20b4208805461eac30075f81ae0ca9)](https://www.codacy.com/app/bernaferrari/CarouselGifViewer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bernaferrari/CarouselGifViewer&amp;utm_campaign=Badge_Grade)

This app **efficiently** displays a list of GIFs in a carousel.

### Motivation:
Displaying a list of GIFs is not something easy, it is:
 - Heavy;
 - Has a high chance of OOM error on low-end devices;
 - Quality is really low;
 - There is a high data usage, a video file can easily get 10x bigger.

On the other hand, displaying them as looping video (mp4) is not something trivial. ExoPlayer usually only allows to play [2 to 4 videos simultaneously](https://github.com/google/ExoPlayer/issues/273).
Besides this, RecyclerView really wasn't made to work well with players, with some [recent tries](https://github.com/eneim/toro) successfully making around some of the limitations.

<p align="center"><img src="assets/showcase.gif?raw=true" alt="Carousel Gif Viewer"></p>

### How it works:

After a lot of frustration, I came to the idea of this app (which is just an activity inside [my other app](https://play.google.com/store/apps/details?id=com.biblialibras.android])). There is a [DiscreteScrollView](https://github.com/yarolegovich/DiscreteScrollView) (which is a RecyclerView with a custom layoutManager) displaying the GIF preview (a jpg thumbnail), and a fixed [ExoMedia player](https://github.com/brianwernick/ExoMedia) at the center of the screen playing a looping mp4 file.
The trick is: the player doesn't go anywhere. When user scrolls, the player is hidden. As soon as the RecyclerView stops scrolling, the player is shown. Since it has the same size and corners as the images, everything works seamlessly.
It also helps that Gfycat, which is used in this sample, generates the image thumbnail based on the first frame, so the video always starts playing from the same place as the picture is.

Current use case for this app is as a Brazilian Sign Language Dictionary on "*Bíblia em Libras*" (Bible in Brazilian Sign Language) app, under "*Dicionário*".
[Download it here.](https://play.google.com/store/apps/details?id=com.biblialibras.android])

## Screenshots

| Main Screen | Drawer |
|:-:|:-:|
| ![First](assets/main_screen.png?raw=true) | ![Sec](assets/drawer.png?raw=true) |

### Side note
I tested the most popular GIF hosting sites, and Gfycat has been the best by far!
- Tenor won't allow editing, removing, renaming or changing tags after a GIF is uploaded (unless you contact support).
- Giphy is a mess, uploaded gifs won't show on search unless you are verified (and it is not easy to become verified); main channel page is a mess.
- Gfycat allows to create albums and sort GIFs; support is great and fast; API just works; view count is great; GIFs with random names instead of random ids is also great.

### Reporting Issues

Issues and Pull Requests are welcome.
You can report [here](https://github.com/bernaferrari/CarouselGifViewer/issues).

License
-------

Copyright 2018 Bernardo Ferrari.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
