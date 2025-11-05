# Maestro Selector Quick Reference

This guide provides a quick reference for all selectors supported by the Maestro IntelliJ plugin.

## Basic Selectors

| Selector | Description | Example |
|----------|-------------|---------|
| `text` | Text content (supports regex) | `text: "Login"` |
| `id` | Resource ID (Android) or accessibility ID (iOS) | `id: "button_submit"` |
| `enabled` | Element enabled state | `enabled: true` |
| `checked` | Element checked state | `checked: true` |
| `focused` | Element focused state | `focused: true` |
| `selected` | Element selected state | `selected: true` |
| `index` | 0-based index when multiple match | `index: 2` |

## Coordinate & Dimension Selectors

| Selector | Description | Example |
|----------|-------------|---------|
| `point` | Tap position (relative or absolute) | `point: 50%,50%` or `point: 100,200` |
| `width` | Element width in pixels | `width: 100` |
| `height` | Element height in pixels | `height: 50` |
| `tolerance` | Tolerance for width/height comparison | `tolerance: 10` |

## Relative Position Selectors

| Selector | Description | Example |
|----------|-------------|---------|
| `below` | Element below another element | `below: {text: "Header"}` |
| `above` | Element above another element | `above: {id: "footer"}` |
| `leftOf` | Element to the left of another | `leftOf: {text: "Icon"}` |
| `rightOf` | Element to the right of another | `rightOf: {id: "label"}` |

## Hierarchy Selectors

| Selector | Description | Example |
|----------|-------------|---------|
| `childOf` | Element that is a child of another | `childOf: {id: "container"}` |
| `containsChild` | Element containing a direct child | `containsChild: "Button Text"` |
| `containsDescendants` | Element containing all descendants | See example below |

## Examples

### Basic Selection
```yaml
- tapOn:
    text: "Login Button"
    enabled: true
    index: 0
```

### Coordinate Selection
```yaml
# Relative position
- tapOn:
    point: 50%,50%

# Absolute coordinates
- tapOn:
    point: 100,200

# Point within element
- tapOn:
    text: "Click here"
    point: 90%,50%
```

### Relative Position
```yaml
- tapOn:
    text: "Edit"
    below:
      text: "User Profile"
    rightOf:
      id: "avatar"
```

### Hierarchy
```yaml
# Child of specific container
- tapOn:
    text: "Accept"
    childOf:
      id: "terms_container"

# Contains specific child
- assertVisible:
    id: "product_card"
    containsChild: "Add to Cart"

# Contains multiple descendants
- assertVisible:
    id: "product_card"
    containsDescendants:
      - text: ".*\\$[0-9]+.*"
      - id: "product_image"
      - text: "In Stock"
```

### Regex Patterns
```yaml
- assertVisible:
    text: ".*welcome.*"        # Contains "welcome"
    
- tapOn:
    id: "^button_.*"           # Starts with "button_"
    
- assertVisible: '[0-9]{6}'    # 6-digit code (e.g., OTP)
```

### Complex Combinations
```yaml
- tapOn:
    text: "Delete"
    enabled: true
    below:
      text: "Item Name"
    rightOf:
      id: "checkbox"
    index: 0
```

## Command-Specific Properties

### tapOn / longPressOn
```yaml
- tapOn:
    text: "Button"
    retryTapIfNoChange: true
    waitToSettleTimeoutMs: 500
    repeat: 3
    delay: 500
```

### doubleTapOn
```yaml
- doubleTapOn:
    id: "photo"
    delay: 150  # ms between taps
```

### swipe
```yaml
# Direction
- swipe:
    direction: LEFT

# Coordinates
- swipe:
    start: 90%,50%
    end: 10%,50%

# From element
- swipe:
    from:
      id: "feed_item"
    direction: UP
```

## Tips

1. **Combine selectors** for more precise targeting
2. **Use regex** for flexible text matching
3. **Use index** when multiple elements match
4. **Use relative positions** when element IDs are unavailable
5. **Use hierarchy** for complex UI structures
6. **Test coordinates** on different screen sizes with percentages

## IntelliSense Support

All these selectors are supported by the plugin's IntelliSense:
- Type a command (e.g., `tapOn:`)
- Press `Ctrl+Space` (Windows/Linux) or `⌃Space` (macOS)
- Browse through available selector properties with descriptions
- Select and auto-complete

## See Also

- [MAESTRO_SYNTAX.md](MAESTRO_SYNTAX.md) - Complete syntax reference
- [INTELLISENSE.md](INTELLISENSE.md) - IntelliSense usage guide
