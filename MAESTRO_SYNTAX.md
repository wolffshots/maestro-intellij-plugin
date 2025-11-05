# Maestro Testing Framework - Syntax Reference

This document provides a comprehensive reference for the Maestro testing framework's YAML syntax.

## Table of Contents

1. [Flow Configuration](#flow-configuration)
2. [Basic Commands](#basic-commands)
3. [Selectors](#selectors)
4. [Assertions](#assertions)
5. [Navigation & Gestures](#navigation--gestures)
6. [Text & Input](#text--input)
7. [Control Flow](#control-flow)
8. [JavaScript Integration](#javascript-integration)
9. [Advanced Features](#advanced-features)
10. [Configuration Files](#configuration-files)

---

## Flow Configuration

### Basic Flow Structure

```yaml
appId: com.example.app
name: Custom Flow name
tags:
  - nightly-build
  - pull-request
env:
  USERNAME: user@example.com
  PASSWORD: 123
jsEngine: graaljs  # or rhino (default)
androidWebViewHierarchy: devtools  # optional
---
# Commands go below the --- separator
- launchApp
- tapOn: "Login"
```

### Configuration Options

- `appId`: Package name (Android) or Bundle ID (iOS)
- `url`: For web testing
- `name`: Custom flow name for reporting
- `tags`: List of tags for filtering flows
- `env`: Environment variables (accessible via `${VAR_NAME}`)
- `jsEngine`: JavaScript engine (`graaljs` or `rhino`)
- `androidWebViewHierarchy`: Android WebView inspection mode
- `onFlowStart`: Commands to run before flow starts
- `onFlowComplete`: Commands to run after flow completes

### Flow Hooks

```yaml
appId: my.app
onFlowStart:
  - runFlow: setup.yaml
  - runScript: setup.js
  - clearState
onFlowComplete:
  - runFlow: teardown.yaml
  - runScript: teardown.js
---
- launchApp
```

---

## Basic Commands

### launchApp

Launch the application.

```yaml
# Simple launch
- launchApp

# Launch specific app
- launchApp: com.example.app

# Launch with options
- launchApp:
    appId: "com.example.app"
    clearState: true          # Clear app state
    clearKeychain: true       # Clear iOS keychain
    stopApp: false            # Don't stop before launch
    arguments:                # Pass launch arguments
      foo: "This is a string"
      isFooEnabled: false
      fooValue: 3.24
      fooInt: 3
    permissions:              # Configure permissions
      all: deny
      camera: allow
      location: allow
      notifications: unset
```

### stopApp

Stop the application.

```yaml
# Stop default app
- stopApp

# Stop specific app
- stopApp: com.example.app
```

### killApp

Kill the app process (for testing process death scenarios).

```yaml
- killApp
```

### clearState

Clear application state.

```yaml
- clearState
```

### clearKeychain

Clear iOS keychain data.

```yaml
- clearKeychain
```

---

## Selectors

Selectors are used to identify UI elements. They can be used in most commands like `tapOn`, `assertVisible`, etc.

### Shorthand Text Selector

```yaml
- tapOn: "Button Text"
```

### Basic Selector Properties

```yaml
- tapOn:
    text: "Button Text"          # Text content (supports regex)
    id: "button_id"              # Resource ID (Android) or accessibility ID (iOS) (supports regex)
    enabled: true                # Element must be enabled
    checked: true                # Element must be checked
    focused: true                # Element must be focused
    selected: true               # Element must be selected
    index: 0                     # 0-based index when multiple elements match
```

### Coordinate and Dimension Selectors

```yaml
- tapOn:
    point: 50%,50%              # Relative position (percentage)
    
- tapOn:
    point: 100,200              # Absolute coordinates (pixels)
    
- tapOn:
    text: "Link"
    point: 90%,50%              # Point within an element

- assertVisible:
    width: 100                  # Element width in pixels
    height: 50                  # Element height in pixels
    tolerance: 10               # Tolerance for width/height comparison
```

### Relative Position Selectors

Target elements based on their position relative to other elements:

```yaml
- tapOn:
    text: "Edit"
    below:                      # Element below another element
      text: "Profile Section"
      
- tapOn:
    id: "delete_button"
    above:                      # Element above another element
      text: "Footer"
    rightOf:                    # Element to the right of another
      text: "User Name"
    leftOf:                     # Element to the left of another
      id: "settings_icon"
```

### Hierarchy Selectors

Target elements based on parent-child relationships:

```yaml
# Direct child
- tapOn:
    text: "Accept"
    childOf:                    # Element that is a child of another
      id: "terms_container"

# Contains child
- assertVisible:
    id: "product_card"
    containsChild: "Add to Cart"  # Element containing a direct child

# Contains descendants
- assertVisible:
    id: "product_card"
    containsDescendants:        # Element containing all specified descendants
      - text: ".*\\$[0-9]+.*"
      - id: "product_image"
      - text: "In Stock"
```

### Regex Support

Both `text` and `id` selectors support regular expressions:

```yaml
- tapOn:
    id: ".*button.*"            # Regex pattern
    text: "^Submit.*"           # Text starting with "Submit"
    
- assertVisible: '.*brown fox.*'  # Partial text match
- assertVisible: '[0-9]{6}'       # OTP pattern
- assertVisible: 'Movies \[NEW\]' # Escaped special characters
```

### Index Selection

Select a specific element when multiple match:

```yaml
- tapOn:
    text: "Buy Now"
    index: 2                    # Select the 3rd matching element (0-based)
```

### Complex Selector Combinations

Combine multiple selector properties for precise targeting:

```yaml
- tapOn:
    text: "Submit"
    enabled: true
    below:
      text: "Terms and Conditions"
    index: 0
    
- assertVisible:
    id: "product_item"
    containsChild: ".*\\$[0-9]+\\..*"
    above:
      text: "Load More"
    width: 300
    tolerance: 20
```

### Platform-Specific Selectors

```yaml
# Android
- tapOn:
    id: "com.example:id/button"

# iOS
- tapOn:
    id: "buttonAccessibilityId"

# Flutter
- tapOn:
    id: "unlock_reward"  # Key or accessibility identifier

# Jetpack Compose
- tapOn: "Like"  # Text content
```

---

## Assertions

### assertVisible

Assert that an element is visible.

```yaml
# Simple assertion
- assertVisible: "My Button"

# With selector
- assertVisible:
    text: "My Button"
    enabled: true

# With optional flag
- assertVisible:
    text: "Summer sale"
    optional: true  # Won't fail flow if not found
```

### assertNotVisible

Assert that an element is not visible.

```yaml
- assertNotVisible: "Error Message"

- assertNotVisible:
    id: "error_banner"
```

### assertTrue

Assert that a JavaScript condition is true.

```yaml
- assertTrue: ${output.counter > 5}
- assertTrue: ${maestro.copiedText.length > 0}
```

### assertWithAI

Use AI to assert UI state (requires AI configuration).

```yaml
- assertWithAI:
    assertion: Login and password text fields are visible.

- assertWithAI:
    assertion: A two-factor authentication prompt, with space for 6 digits, is visible.
```

### assertNoDefectsWithAI

Use AI to detect visual defects (experimental).

```yaml
- assertNoDefectsWithAI
```

---

## Navigation & Gestures

### tapOn

Tap on an element.

```yaml
# Simple tap
- tapOn: "Button"

# Tap with selector
- tapOn:
    id: "button_id"
    
# Tap with advanced options
- tapOn:
    text: "Submit"
    retryTapIfNoChange: true     # Retry if UI doesn't change
    waitToSettleTimeoutMs: 500   # Wait time for UI to settle
    repeat: 3                    # Repeat tap 3 times
    delay: 500                   # Delay between repeated taps (ms)
    
# Tap at coordinates
- tapOn:
    point: 50%,50%              # Relative
- tapOn:
    point: 100,200              # Absolute

# Tap within element
- tapOn:
    text: "Link with click here"
    point: 90%,50%              # Tap at 90% width, 50% height within element
```

### doubleTapOn

Double tap on an element.

```yaml
# Simple double tap
- doubleTapOn: "Button"

# With selector
- doubleTapOn:
    id: "buttonId"
    
# With custom delay
- doubleTapOn:
    text: "Photo"
    delay: 150  # Delay between taps in ms (default: 100ms)
```

### longPressOn

Long press on an element.

```yaml
- longPressOn: "Item"

- longPressOn:
    id: "item_id"
```

### swipe

Perform a swipe gesture.

```yaml
# Directional swipe
- swipe:
    direction: LEFT  # LEFT, RIGHT, UP, DOWN
    label: "Swipe for onboarding"

# Swipe with coordinates
- swipe:
    start: 90%,50%   # Start at 90% width, 50% height
    end: 10%,50%     # End at 10% width, 50% height

# Swipe from element
- swipe:
    from:
      id: "feed_item"  # Start from center of element
    direction: UP
```

### scroll

Scroll the view.

```yaml
- scroll

- scroll:
    direction: DOWN  # DOWN, UP, LEFT, RIGHT
```

### scrollUntilVisible

Scroll until an element becomes visible.

```yaml
- scrollUntilVisible:
    element: "Item 6"
    direction: DOWN  # DOWN, UP, LEFT, RIGHT (default: DOWN)
    timeout: 50000   # milliseconds (default: 20000)
    speed: 40        # 0-100 (default: 40)
    visibilityPercentage: 100  # 0-100 (default: 100)
    centerElement: false       # true|false (default: false)

# Simple version
- scrollUntilVisible: "My text"

# With centering
- scrollUntilVisible:
    centerElement: true
    element:
      text: "Item 6"
```

### back

Navigate back.

```yaml
- back
```

### openLink

Open a URL or deep link.

```yaml
- openLink: https://example.com
- openLink: myapp://deep/link
```

---

## Text & Input

### inputText

Input text into a field.

```yaml
# Simple input
- inputText: "Hello World"

# With selector
- inputText:
    text: "Hello World"
    label: "Enter username"

# Using variables
- inputText: ${USERNAME}

# Using JavaScript expressions
- inputText: ${1 + 1}
- inputText: ${'Hello ' + MY_NAME}
```

### eraseText

Erase text from input field.

```yaml
- eraseText

# iOS workaround for flakiness
- longPressOn: "<input text id>"
- tapOn: 'Select All'
- eraseText
```

### copyTextFrom

Copy text from an element.

```yaml
- copyTextFrom:
    id: "confirmation_code"

# Access copied text in JavaScript
- copyTextFrom:
    id: "price"
- evalScript: ${output.priceValue = parseFloat(maestro.copiedText.replace('$', ''))}
```

### pasteText

Paste previously copied text.

```yaml
- copyTextFrom:
    id: "someId"
- tapOn:
    id: "searchFieldId"
- pasteText

# Paste with JavaScript manipulation
- inputText: ${'Pasted: ' + maestro.copiedText}
```

### pressKey

Press a keyboard key.

```yaml
- pressKey: Enter
- pressKey: Backspace
```

### hideKeyboard

Hide the on-screen keyboard.

```yaml
- hideKeyboard
```

---

## Control Flow

### runFlow

Run another flow file.

```yaml
# Simple
- runFlow: login.yaml

# With parameters
- runFlow:
    file: subflow.yaml
    env:
      USERNAME: user@example.com
      PASSWORD: 123

# Inline commands
- runFlow:
    env:
      INNER_ENV: Inner Parameter
    commands:
      - inputText: ${INNER_ENV}
      - tapOn: "Submit"

# Conditional execution
- runFlow:
    when:
      visible: "Login Required"
    file: login.yaml
```

### runScript

Run a JavaScript file.

```yaml
# Simple
- runScript: myScript.js

# With parameters
- runScript:
    file: script.js
    env:
      myParameter: 'Parameter'

# Conditional execution
- runScript:
    when:
      visible: "Setup Required"
    file: api-setup.js
    env:
      ENVIRONMENT: "staging"
```

### repeat

Repeat commands multiple times.

```yaml
# Fixed number of times
- repeat:
    times: 3
    commands:
      - tapOn: "Button"
      - scroll

# While condition (element-based)
- repeat:
    while:
      notVisible: "End of List"
    commands:
      - scroll

# While condition (JavaScript)
- evalScript: ${output.counter = 0}
- repeat:
    while:
      true: ${output.counter < 10}
    commands:
      - tapOn: "Increment"
      - evalScript: ${output.counter = output.counter + 1}
```

### retry

Retry commands on failure.

```yaml
- retry:
    maxRetries: 3
    commands:
      - tapOn:
          id: 'button-that-might-not-be-here-yet'
      - assertVisible: "Success"

# Retry entire subflow
- retry:
    maxRetries: 5
    file: flaky-checkout-flow.yaml
```

### extendedWaitUntil

Wait for a condition.

```yaml
# Wait for visibility
- extendedWaitUntil:
    visible: "Loading complete"
    timeout: 10000  # milliseconds
    label: "Wait for content to load"

# Wait for invisibility
- extendedWaitUntil:
    notVisible:
      id: "loading_spinner"
    timeout: 10000
```

### waitForAnimationToEnd

Wait for animations to complete.

```yaml
- waitForAnimationToEnd
```

---

## JavaScript Integration

### Expression Syntax

Use `${}` to embed JavaScript expressions:

```yaml
# Simple expressions
- inputText: ${1 + 1}                    # Inputs '2'
- inputText: ${'Hello ' + MY_NAME}       # String concatenation
- tapOn: ${MY_NAME}                      # Variable substitution

# Comparisons
- runFlow:
    when:
      true: ${price > 100}
    file: premium.yaml

# Property access
- inputText: ${output.result}
- tapOn: ${user.name}
```

### evalScript

Execute inline JavaScript.

```yaml
# Set output values
- evalScript: ${output.counter = 0}
- evalScript: ${output.myFlow = MY_NAME.toUpperCase()}

# Use output in commands
- inputText: ${output.myFlow}

# Complex logic
- copyTextFrom:
    id: "product_price"
- evalScript: ${output.priceValue = parseFloat(maestro.copiedText.replace('$', ''))}
- assertTrue: ${output.priceValue > 0}
```

### JavaScript Files

```javascript
// myScript.js
var uppercaseName = MY_NAME.toUpperCase()
output.myFlow = uppercaseName

// HTTP requests
const response = http.post(API_URL + '/users', {
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        email: username,
        password: 'test123'
    })
});

const userData = json(response.body);
output.generatedUsername = username;
output.userId = userData.id;
```

### Output Object

The `output` object persists values across commands:

```yaml
# Set in script
- runScript: myScript.js

# Access in flow
- inputText: ${output.result}

# Namespaced outputs
- runScript: myScript.js
- runScript: mySecondScript.js
- inputText: ${output.myScript.result}
- inputText: ${output.mySecondScript.result}
```

### Built-in JavaScript Objects

- `output`: Global output object for storing values
- `maestro.copiedText`: Access text from `copyTextFrom`
- `http`: Make HTTP requests
- `json()`: Parse JSON
- `console.log()`: Logging

---

## Advanced Features

### AI Features

```yaml
# Extract text with AI
- extractTextWithAI: CAPTCHA value
- inputText: ${aiOutput}

# Custom output variable
- extractTextWithAI:
    query: 'CAPTCHA value'
    outputVariable: 'theCaptchaValue'
- inputText: ${theCaptchaValue}
```

### Recording

```yaml
# Start recording
- startRecording: recording

# With options
- startRecording:
    path: 'logging_in'
    label: 'Begin collecting test evidence'
    optional: true

# Stop recording
- stopRecording
```

### Screenshots

```yaml
- takeScreenshot: MainScreen

- takeScreenshot:
    path: LoginScreen
```

### Device Configuration

```yaml
# Set location
- setLocation:
    latitude: 52.3599976
    longitude: 4.8830301

# Set orientation
- setOrientation: portrait  # or landscape

# Set airplane mode
- setAirplaneMode: true

# Toggle airplane mode
- toggleAirplaneMode
```

### Media

```yaml
# Add media files
- addMedia: path/to/image.jpg
```

### Common Command Arguments

These arguments work with most commands:

```yaml
- tapOn:
    id: "button"
    label: "Tap on Buy Now button"     # Custom name in output
    optional: true                     # Don't fail flow if command fails
```

---

## Configuration Files

### Workspace Configuration (config.yaml)

Located at `.maestro/config.yaml`:

```yaml
# Flow inclusion patterns
flows:
  - 'subFolder/*'

# Tag filtering
includeTags:
  - tagNameToInclude
excludeTags:
  - tagNameToExclude

# Execution order
executionOrder:
  continueOnFailure: false  # default is true
  flowsOrder:
    - flowA
    - flowB

# Output directory
testOutputDir: test_output_directory

# Cloud settings
baselineBranch: main
notifications:
  email:
    enabled: true
    recipients:
      - john@example.com

# Platform configuration
platform:
  ios:
    snapshotKeyHonorModalViews: false
    disableAnimations: true
  android:
    disableAnimations: true
```

### AI Configuration

Set environment variable for AI features:

```bash
export MAESTRO_CLOUD_API_KEY=your_api_key_here
```

---

## Parameters and Variables

### Environment Variables

```yaml
# Define in flow
env:
  USERNAME: user@example.com
  PASSWORD: 123

# Use in commands
- inputText: ${USERNAME}

# Pass from shell (prefix with MAESTRO_)
export MAESTRO_FOO=bar
```

```yaml
- tapOn: ${MAESTRO_FOO}
```

### Passing Parameters to Subflows

```yaml
- runFlow:
    file: subflow.yaml
    env:
      USERNAME: user@example.com
      PASSWORD: 123
```

---

## Tags

### Defining Tags

```yaml
appId: com.example.App
tags:
  - nightly-build
  - pull-request
---
- launchApp
```

### Using Tags

```bash
# Include specific tags
maestro test --include-tags=dev,pull-request

# Exclude specific tags
maestro test --exclude-tags=slow
```

---

## Best Practices

1. **Use descriptive labels** for better test output readability
2. **Leverage variables** for reusable values
3. **Use tags** to organize and filter test suites
4. **Implement retry logic** for flaky elements
5. **Use optional flags** for non-critical assertions
6. **Prefer GraalJS** over Rhino for better JavaScript support
7. **Modularize flows** with `runFlow` for reusability
8. **Use AI features** sparingly and only when necessary

---

## CLI Commands

```bash
# Run a flow
maestro test flow.yaml

# Run with parameters
maestro test -e USERNAME=user@example.com flow.yaml

# Run on cloud
maestro cloud --apiKey <key> --projectId <id> app.apk .maestro/

# With tags
maestro cloud --include-tags=dev,pr app.apk .maestro/

# Specify Android API level
maestro cloud --android-api-level 30 myapp.apk myflows/
```

---

## Example Complete Flow

```yaml
appId: com.example.shop
name: Checkout Flow
tags:
  - smoke-test
  - checkout
jsEngine: graaljs
env:
  API_URL: "https://api.staging.example.com"
  TEST_USER: "test@example.com"

onFlowStart:
  - runScript: api-helper.js
  - clearState

onFlowComplete:
  - takeScreenshot: final-state
  - runScript: cleanup.js

---
# Launch and login
- launchApp:
    clearState: true
    permissions:
      all: deny
      notifications: allow

- tapOn: "Login"
- inputText: ${TEST_USER}
- tapOn: "Password"
- inputText: ${password}
- tapOn: "Submit"
- assertVisible: "Welcome"

# Add item to cart
- tapOn: "Shop"
- scrollUntilVisible:
    element: "Product 1"
    centerElement: true
- tapOn: "Product 1"
- tapOn: "Add to Cart"
- assertVisible: "Added to cart"

# Checkout
- tapOn: "Cart"
- assertVisible:
    text: "Product 1"
    enabled: true
- tapOn: "Checkout"

# Complete purchase
- extendedWaitUntil:
    visible: "Order Confirmed"
    timeout: 10000
    label: "Wait for payment processing"

- takeScreenshot: order-complete
- assertVisible: "Order Confirmed"
```
