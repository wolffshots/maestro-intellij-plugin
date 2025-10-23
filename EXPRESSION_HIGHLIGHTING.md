# Expression Highlighting Feature

## Overview

The plugin treats ALL content inside `${}` blocks as code, providing syntax highlighting for variables, property references, string literals, and operators.

## Features

### Code-Like Highlighting for All ${} Expressions

**ALL** `${}` expressions are highlighted with code-like syntax:
- **Expression background**: The entire `${}` block
- **String literals**: Text in `"..."` or `'...'`
- **Operators**: `+`, `-`, `*`, `/`, `==`, `!=`, `<`, `>`, `<=`, `>=`, `&&`, `||`
- **Property references**: Dotted identifiers like `output.strings.add`, `user.name` (highlighted like Kotlin properties)

```yaml
# Simple variables (identifiers highlighted like code)
appId: ${APP_ID}
name: ${MY_VAR_1}
product: ${TEST_PRODUCT_NAME_1}

# Property access
value: ${output.result}
userName: ${user.name}

# String concatenation
text: ${output.strings.add + " 1 " + output.strings.item}
message: ${"Hello " + user.name + "!"}

# Comparisons
when: ${count > "5"}
condition: ${status == "active"}

# Complex logic
result: ${value * 2 + " items"}
```

### Bad Variables (Warnings)
**Pattern:** `$VARIABLE_NAME` (without braces)

Variables without braces are highlighted in red with a warning message.

```yaml
# ❌ Wrong - will show warning
bad: $MISSING_BRACES

# ✅ Correct
good: ${WITH_BRACES}
```

## Color Customization

You can customize the colors in **Settings → Editor → Color Scheme → Maestro YAML**:

1. **Expression Background** - Default: Identifier color (the `${}` block)
2. **Variable/Identifier (in expressions)** - Default: Instance field color (like Kotlin properties)
3. **String Literal (in expressions)** - Default: String color
4. **Operator (in expressions)** - Default: Operator color
5. **Bad Variable ($VAR)** - Default: Red/Error color

## Examples

### String Concatenation
```yaml
- assertVisible:
    text: ${greeting + " " + userName + "!"}
```

### Arithmetic Operations
```yaml
- runScript:
    value: ${count * 2 + " items"}
```

### Comparisons
```yaml
- runFlow:
    when: ${price > "100"}
    file: premium.yaml
```

### Property Access
```yaml
- tapOn:
    text: ${output.strings.add + " 1 " + output.strings.item}
```

### Mixed Expressions
```yaml
- inputText: ${"User: " + user.firstName + " " + user.lastName}
```

## Technical Details

### Pattern Matching

The annotator uses regex patterns to identify:
- **Expressions**: `\$\{[^}]+}` - Matches any `${}` block
- **Simple variables**: `\$\{[A-Za-z0-9_.]+}` - Only letters, numbers, dots, underscores
- **String literals**: `(["'])(?:(?=(\\?))\2.)*?\1` - Matches quoted strings with escape support
- **Bad variables**: `\$[A-Za-z0-9_]+` - Dollar sign without braces

### Highlighting Logic

1. **First pass**: Identify all `${}` blocks
2. **Classification**: Determine if simple variable or complex expression
3. **Simple variables**: Highlight entire block as string
4. **Complex expressions**:
   - Highlight expression background
   - Find and highlight string literals
   - Find and highlight operators (avoiding those inside strings)
5. **Bad variables**: Flag `$VAR` patterns with warnings

### Supported Operators

- **Arithmetic**: `+`, `-`, `*`, `/`
- **Comparison**: `==`, `!=`, `<`, `>`, `<=`, `>=`
- **Logical**: `&&`, `||`

## Building the Plugin

To rebuild the plugin with these new features:

```bash
./gradlew clean buildPlugin
```

The updated plugin will be at: `build/distributions/maestro-yaml-highlighter-1.0.0.zip`

## Compatibility

This feature works with all IDE versions supported by the plugin (IntelliJ IDEA 2023.2+, Android Studio Hedgehog+).

