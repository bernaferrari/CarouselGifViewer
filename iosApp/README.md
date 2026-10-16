# iOS app

Open `Carousel.xcodeproj`, select the Carousel scheme and an Apple Silicon simulator,
then Run. For a physical device, select your signing team in Xcode first. Xcode 26+
is required to build with Liquid Glass styling; older iOS versions use their native
navigation appearance. The deployment target is iOS 18.5, matching the current Skiko binary.

The build phase links the shared Kotlin framework automatically. Both Kotlin iOS
targets are ARM64; Intel simulators are not supported.

Navigation follows [JetBrains' native navigation guidance](https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html):
`UINavigationController` owns the stack, bar and interactive back gesture, while a
separate `ComposeUIViewController` renders each screen. Android keeps Navigation 3.
There are no extra glass or icon dependencies. System bars also honor the user's
Reduce Transparency and Reduce Motion settings.

`project.yml` is the source of the Xcode project. After changing it, regenerate with
`xcodegen generate --spec iosApp/project.yml` from the repository root.

The Carousel scheme includes UI tests covering repeated Settings pushes, back,
interactive back cancellation and completed edge swipes. Run them with Product → Test,
or `xcodebuild -project iosApp/Carousel.xcodeproj -scheme Carousel -destination
'platform=iOS Simulator,id=YOUR_SIMULATOR_ID' test`. Use a dedicated test simulator.
