# Maestro YAML Highlighter - IntelliJ Plugin

A simple IntelliJ IDEA / Android Studio plugin that provides syntax highlighting for Maestro YAML files.

## Features

- **Code-like highlighting** for ALL `${}` expressions:
  - Variables and identifiers: `${MY_VAR_1}`, `${APP_ID}` (highlighted like Kotlin properties)
  - Property references: `${output.strings.add}`, `${user.name}`
  - String literals: `"text"` or `'text'` inside expressions
  - Operators: `+`, `-`, `*`, `/`, `==`, `!=`, `<`, `>`, etc.
- **Examples:**
  - Simple: `${TEST_PRODUCT_NAME_1}` - identifier highlighted
  - Complex: `${output.strings.add + " 1 " + output.strings.item}` - full code highlighting
- **Red highlighting** for incorrect variable syntax: `$VARIABLE_NAME` (missing braces)
- **File detection** - Only applies to YAML files in `maestro` folders
- **Warnings** - Shows inline warnings for incorrect variable syntax

## Building the Plugin

### Prerequisites

- JDK 17 or higher
- Gradle (included via wrapper)

### Build Steps

1. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```
   
   On Windows:
   ```cmd
   gradlew.bat buildPlugin
   ```

2. The plugin JAR will be created at:
   ```
   build/distributions/maestro-yaml-highlighter-1.0.0.zip
   ```

## Installation

### From ZIP File

1. Build the plugin (see above)
2. Open Android Studio / IntelliJ IDEA
3. Go to **Settings/Preferences → Plugins**
4. Click the gear icon ⚙️ → **Install Plugin from Disk...**
5. Select the generated ZIP file from `build/distributions/`
6. Restart the IDE (optional)

### From Source (Development)

1. Run the Gradle task: `runIde`
2. This will launch a new IDE instance with the plugin installed

## Configuration

### Customizing Colors

1. Go to **Settings/Preferences → Editor → Color Scheme → Maestro YAML**
2. Customize the colors for:
   - **Maestro Good Variable (${VAR})** - Default: Green
   - **Maestro Bad Variable ($VAR)** - Default: Red/Error

## Usage

The plugin automatically activates for YAML files in folders named `maestro`.

### Example

Create a file: `maestro/test.yaml`

```yaml
appId: com.example.app
---
- launchApp:
    appId: ${APP_ID}  # Green - correct format
    
- tapOn:
    text: $BUTTON_TEXT  # Red - incorrect format (warning shown)
    
- inputText: ${USER_NAME}  # Green
- assertVisible: $ERROR_MSG  # Red
```

## Development

### Project Structure

```
├── build.gradle.kts          # Build configuration
├── settings.gradle.kts       # Gradle settings
├── gradle.properties         # Gradle properties
└── src/main/
    ├── kotlin/com/maestro/yaml/
    │   ├── MaestroYamlAnnotator.kt      # Main highlighting logic
    │   └── MaestroColorSettingsPage.kt  # Color settings UI
    └── resources/META-INF/
        └── plugin.xml         # Plugin descriptor
```

### Key Components

#### MaestroYamlAnnotator
- Scans YAML file content for variable patterns
- Only processes files with `/maestro/` in the path
- Applies highlighting and warnings

#### MaestroColorSettingsPage
- Provides color customization UI
- Shows example YAML with highlighting

### Testing

Run the plugin in a test IDE:
```bash
./gradlew runIde
```

## Troubleshooting

### Plugin Not Loading
- Ensure you're using IntelliJ IDEA 2023.2 or later
- Check that the YAML plugin is enabled
- Restart the IDE after installation

### No Highlighting
- Verify the file is in a `maestro` folder
- Check that the file has `.yaml` or `.yml` extension
- Ensure the YAML plugin is not disabled

### Build Errors
- Verify JDK 17+ is installed: `java -version`
- Clean and rebuild: `./gradlew clean buildPlugin`

## Compatibility

- **IntelliJ IDEA**: 2023.2+ (build 232+)
- **Android Studio**: Hedgehog (2023.1.1) and later
- **All future IDE versions**: No upper version limit
- **Requires**: YAML plugin (bundled with IDE)

## Contributing

This is a simple plugin for Maestro YAML highlighting. Feel free to extend it with additional features:
- Code completion for Maestro commands
- Validation of Maestro-specific syntax
- Quick fixes to convert `$VAR` to `${VAR}`
- Support for environment variable files

