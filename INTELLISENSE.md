# Intellisense / Code Completion Guide

This document explains how to use the intelligent code completion (intellisense) features in the Maestro YAML Highlighter plugin.

## Overview

The plugin provides context-aware code completion that suggests appropriate commands, properties, and values based on your cursor position in the YAML file.

## Activation

Code completion is automatically activated when you:
- Type in a Maestro YAML file (files must be in a `maestro` folder)
- Press `Ctrl+Space` (Windows/Linux) or `⌃Space` (macOS) to manually trigger completion

## Completion Types

### 1. Top-Level Configuration

When typing before the `---` separator, you'll get suggestions for flow configuration keys:

```yaml
appId: com.example.app
name: My Test Flow
tags:
  - smoke
env:
  USERNAME: test@example.com
jsEngine: graaljs
---
```

**Available completions:**
- `appId` - Package name (Android) or Bundle ID (iOS)
- `url` - URL for web testing
- `name` - Custom flow name for reporting
- `tags` - List of tags for filtering flows
- `env` - Environment variables
- `jsEngine` - JavaScript engine (graaljs or rhino)
- `androidWebViewHierarchy` - Android WebView inspection mode
- `onFlowStart` - Commands to run before flow starts
- `onFlowComplete` - Commands to run after flow completes

### 2. Command Suggestions

After the `---` separator, you'll get all available Maestro commands. The plugin will **automatically insert `- `** if you haven't typed it yet:

```yaml
---
- launchApp
- tapOn: "Login"
- inputText: "username"
- assertVisible: "Welcome"
```

**Smart prefix insertion:**
- Type at start of line → plugin adds `- ` prefix automatically
- Type `- ` manually → plugin completes the command name
- Type `-` alone → plugin adds space and completes command

**Available commands include:**
- App control: `launchApp`, `stopApp`, `killApp`, `clearState`, `clearKeychain`
- Interactions: `tapOn`, `doubleTapOn`, `longPressOn`, `swipe`, `scroll`
- Assertions: `assertVisible`, `assertNotVisible`, `assertTrue`, `assertWithAI`
- Input: `inputText`, `eraseText`, `copyTextFrom`, `pasteText`, `pressKey`
- Navigation: `back`, `openLink`, `scrollUntilVisible`
- Control flow: `runFlow`, `runScript`, `repeat`, `retry`, `extendedWaitUntil`
- Advanced: `evalScript`, `extractTextWithAI`, `takeScreenshot`, `setLocation`
- And many more...

### 3. Command Properties

When defining properties for a command, you'll get context-appropriate suggestions:

#### Common Properties (available for most commands)
```yaml
- tapOn:
    id: "button"
    label: "Tap the submit button"  # Custom name in output
    optional: true                  # Don't fail if not found
```

#### Selector Properties
For commands that interact with UI elements (`tapOn`, `assertVisible`, `copyTextFrom`, etc.):

**Basic Selectors:**
```yaml
- tapOn:
    text: "Button Text"    # Text content (supports regex)
    id: "button_id"        # Resource ID or accessibility ID (supports regex)
    enabled: true          # Element must be enabled
    checked: false         # Element must not be checked
    focused: true          # Element must be focused
    selected: false        # Element must not be selected
    index: 0               # 0-based index when multiple elements match
```

**Coordinate & Dimension Selectors:**
```yaml
- tapOn:
    point: 50%,50%         # Relative position (percentage)
    point: 100,200         # Absolute coordinates (pixels)
    width: 100             # Element width in pixels
    height: 50             # Element height in pixels
    tolerance: 10          # Tolerance for width/height comparison
```

**Relative Position Selectors:**
```yaml
- tapOn:
    text: "Edit"
    below:                 # Element below another element
      text: "Profile"
    above:                 # Element above another element
      id: "footer"
    leftOf:                # Element to the left of another
      text: "Settings"
    rightOf:               # Element to the right of another
      id: "icon"
```

**Hierarchy Selectors:**
```yaml
- tapOn:
    text: "Accept"
    childOf:               # Element that is a child of another
      id: "container"
    containsChild:         # Element containing a direct child
      text: "Button"
    containsDescendants:   # Element containing all specified descendants
      - text: "Price"
      - id: "image"
```

#### Command-Specific Properties

**launchApp:**
```yaml
- launchApp:
    appId: "com.example.app"
    clearState: true
    clearKeychain: true
    stopApp: false
    permissions:
      camera: allow
      location: deny
    arguments:
      key: "value"
```

**scrollUntilVisible:**
```yaml
- scrollUntilVisible:
    element: "Item Text"
    direction: DOWN           # UP, DOWN, LEFT, RIGHT
    timeout: 50000           # milliseconds
    speed: 40                # 0-100
    visibilityPercentage: 100 # 0-100
    centerElement: true      # true/false
```

**repeat:**
```yaml
- repeat:
    times: 3              # Number of iterations
    commands:
      - tapOn: "Next"
```

**runFlow:**
```yaml
- runFlow:
    file: subflow.yaml
    env:
      PARAM: "value"
    when:
      visible: "Login Required"
```

### 4. Value Suggestions

The plugin provides smart value suggestions for specific properties:

#### Directions
```yaml
- swipe:
    direction: # Suggests: UP, DOWN, LEFT, RIGHT
```

#### Orientations
```yaml
- setOrientation: # Suggests: portrait, landscape
```

#### Boolean Values
```yaml
- tapOn:
    enabled: # Suggests: true, false
```

#### Permission Values
```yaml
- launchApp:
    permissions:
      camera: # Suggests: allow, deny, unset
```

#### JavaScript Engines
```yaml
jsEngine: # Suggests: graaljs, rhino
```

#### Key Names (for pressKey)
```yaml
- pressKey: # Suggests: Enter, Backspace, Delete, Escape, Tab, etc.
```

## Usage Tips

### 1. Explore Available Options

When you're not sure what options are available for a command:
1. Type the command name
2. Press `Enter` to create it
3. Press `Ctrl+Space` / `⌃Space` to see available properties

### 2. Use Descriptions

Each completion item has a description shown in the completion popup. Read these to understand what each option does.

### 3. Auto-Insert Colons

For configuration keys, the plugin automatically inserts `: ` after the key name, positioning your cursor ready for the value.

### 4. Context Awareness

The plugin understands YAML structure and only shows relevant suggestions:
- Configuration keys before `---`
- Commands after `---`
- Properties inside commands
- Values for specific property types

## Examples

### Complete Flow with Intellisense

Here's how you might write a flow using intellisense:

**Method 1: Auto-insert dash prefix**
1. Type `appId:` → auto-completes with `: `
2. Enter your app ID
3. Type `---` and press `Enter`
4. Start typing `laun` directly → triggers completion showing `launchApp`
5. Press `Enter` → automatically inserts `- launchApp` (note the dash is added!)
6. Press `:` and `Enter`, then `Ctrl+Space` → shows launchApp properties
7. Select `clearState` → auto-suggests `true` or `false`

**Method 2: Manual dash prefix**
1. Type `appId:` → auto-completes with `: `
2. Enter your app ID
3. Type `---` and press `Enter`
4. Type `- ` then start typing `laun` → shows `launchApp`
5. Press `Enter` → completes to `launchApp`
6. Continue as above

Result:
```yaml
appId: com.example.app
---
- launchApp:
    clearState: true
- tapOn:
    text: "Login"
    optional: true
- inputText: ${USERNAME}
- assertVisible:
    text: "Welcome"
    enabled: true
```

### Quick Command Addition

1. After `---`, type `- tap` → shows `tapOn` as top suggestion
2. Press `Enter` → inserts `tapOn`
3. Type `:` and press `Enter`
4. Press `Ctrl+Space` → shows selector properties and common properties
5. Select `id` and enter the element ID

## Keyboard Shortcuts

| Action | Windows/Linux | macOS |
|--------|---------------|-------|
| Trigger completion | `Ctrl+Space` | `⌃Space` |
| Accept suggestion | `Enter` or `Tab` | `Enter` or `Tab` |
| Close completion popup | `Esc` | `Esc` |
| Navigate suggestions | `↑` / `↓` | `↑` / `↓` |

## Troubleshooting

### Completions Not Showing

**Problem:** Intellisense doesn't appear when typing

**Solutions:**
1. Ensure the file is in a folder named `maestro` (e.g., `.maestro/` or `maestro/`)
2. Ensure the file has a `.yaml` or `.yml` extension
3. Try manually triggering with `Ctrl+Space` / `⌃Space`
4. Restart the IDE if the plugin was just installed

### Wrong Suggestions

**Problem:** Getting suggestions that don't make sense

**Solutions:**
1. Check your YAML indentation (must be valid YAML)
2. Ensure you're in the right context (before/after `---`)
3. The plugin detects context based on structure - fix any YAML syntax errors

### Completion Popup Disappears

**Problem:** Completion popup closes immediately

**Solutions:**
1. Continue typing - IntelliJ filters completions as you type
2. Press `Ctrl+Space` / `⌃Space` again to reopen
3. Check for YAML syntax errors that might be confusing the parser

## Advanced Usage

### Combining with YAML Features

The plugin works alongside IntelliJ's built-in YAML support:
- Use YAML schema validation for general YAML syntax
- Use Maestro intellisense for Maestro-specific commands
- Both features work together seamlessly

### Custom Workflows

You can create snippets and live templates for frequently used patterns:
1. Go to Settings → Editor → Live Templates
2. Create templates for common Maestro patterns
3. Combine with intellisense for maximum productivity

## Related Documentation

- [MAESTRO_SYNTAX.md](MAESTRO_SYNTAX.md) - Complete Maestro syntax reference
- [EXPRESSION_HIGHLIGHTING.md](EXPRESSION_HIGHLIGHTING.md) - Variable highlighting details
- [README.md](README.md) - General plugin information
