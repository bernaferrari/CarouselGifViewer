import XCTest

final class NavigationTests: XCTestCase {
    func testNativeSettingsPushPopAndInteractiveCancellation() {
        let app = XCUIApplication()
        app.launch()
        let settingsButton = app.navigationBars.buttons["Settings"]
        XCTAssertTrue(settingsButton.waitForExistence(timeout: 30))
        XCTAssertTrue(app.navigationBars.staticTexts["1.Maria Madalena"].waitForExistence(timeout: 10))
        let carouselImage = XCTAttachment(screenshot: app.screenshot())
        carouselImage.name = "Native carousel navigation"
        carouselImage.lifetime = .keepAlways
        add(carouselImage)
        for attempt in 0..<3 {
            settingsButton.tap()
            let settingsBar = app.navigationBars["Settings"]
            XCTAssertTrue(settingsBar.waitForExistence(timeout: 5))
            XCTAssertEqual(app.navigationBars.count, 1)
            if attempt == 0 {
                let image = XCTAttachment(screenshot: app.screenshot())
                image.name = "Native Settings navigation"
                image.lifetime = .keepAlways
                add(image)
            }
            // An incomplete edge swipe should cancel and keep Settings visible.
            let start = app.coordinate(withNormalizedOffset: CGVector(dx: 0.005, dy: 0.5))
            let shortEnd = app.coordinate(withNormalizedOffset: CGVector(dx: 0.13, dy: 0.5))
            start.press(forDuration: 0.1, thenDragTo: shortEnd, withVelocity: .slow, thenHoldForDuration: 0.5)
            XCTAssertTrue(settingsBar.waitForExistence(timeout: 5))
            settingsBar.buttons["Carousel"].tap()
            XCTAssertTrue(settingsButton.waitForExistence(timeout: 5))
        }
        settingsButton.tap()
        XCTAssertTrue(app.navigationBars["Settings"].waitForExistence(timeout: 5))
        let start = app.coordinate(withNormalizedOffset: CGVector(dx: 0.005, dy: 0.5))
        let end = app.coordinate(withNormalizedOffset: CGVector(dx: 0.95, dy: 0.5))
        start.press(forDuration: 0.1, thenDragTo: end)
        XCTAssertTrue(settingsButton.waitForExistence(timeout: 5))
    }
}
