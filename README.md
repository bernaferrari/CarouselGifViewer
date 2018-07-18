<p align="center"><img src="app/src/main/ic_launcher-web.png" alt="Carousel Gif Viewer" height="200px"></p>

Carousel Gif Viewer
===================

This app **efficiently** displays a list of GIFs in a carousel.

##### Motivation:
Displaying a list of GIFs is not something easy, it is:
 - Heavy;
 - Has a high chance of OOM error on low end devices;
 - Quality is really low;
 - There is a high data usage, a 200kb mp4 file easily becomes 2mb.

On the other hand, displaying them as MP4 is not something trivial. ExoPlayer usually only allows to play [around 2 to 4 videos simultaneously](https://github.com/google/ExoPlayer/issues/273).
Besides this, RecyclerView really wasn't made to work well with players, with some [recent tries](https://github.com/eneim/toro) successfully making around the limitations.

![GIF](assets/showcase.gif?raw=true)
<p align="center"><img src="assets/showcase.gif?raw=true" alt="Carousel Gif Viewer" height="200px"></p>

##### How it works:

After a lot of frustration, I came to the idea of this app (which is just an activity inside [my other app](https://play.google.com/store/apps/details?id=com.biblialibras.android]). There is a [DiscreteScrollView](https://github.com/yarolegovich/DiscreteScrollView) displaying the GIF preview (a jpg thumbnail), and a fixed [ExoMedia player](https://github.com/brianwernick/ExoMedia) at the center of the screen playing a looping mp4 file.
The trick is: the player doesn't go anywhere. When user scrolls, the player is hidden. As soon as the RecyclerView stops scrolling, the player is shown. Since it has the same size and corners as the images, everything works seamlessly.
It also helps that Gfycat, which is used in this sample, generates the image thumbnail based on the first frame, so the video always starts playing from the same place as the picture is.

Current use case is as a Brazilian Sign Language Dictionary on "Bíblia em Libras" (Bible in Brazilian Sign Language) app, under "Dicionário".
[Download it here.](https://play.google.com/store/apps/details?id=com.biblialibras.android])

## Screenshots

| Main Screen | Drawer |
|:-:|:-:|
| ![First](assets/main_screen.png?raw=true) | ![Sec](assets/drawer.png?raw=true) |


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
