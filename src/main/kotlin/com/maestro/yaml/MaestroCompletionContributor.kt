package com.maestro.yaml

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.psi.YAMLKeyValue

class MaestroCompletionContributor : CompletionContributor() {
    
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(YAMLLanguage.INSTANCE),
            MaestroCompletionProvider()
        )
    }
    
    private class MaestroCompletionProvider : CompletionProvider<CompletionParameters>() {
        
        companion object {
            // Top-level configuration keys
            private val CONFIG_KEYS = mapOf(
                "appId" to "Package name (Android) or Bundle ID (iOS)",
                "url" to "URL for web testing",
                "name" to "Custom flow name for reporting",
                "tags" to "List of tags for filtering flows",
                "env" to "Environment variables",
                "jsEngine" to "JavaScript engine (graaljs or rhino)",
                "androidWebViewHierarchy" to "Android WebView inspection mode",
                "onFlowStart" to "Commands to run before flow starts",
                "onFlowComplete" to "Commands to run after flow completes"
            )
            
            // All Maestro commands
            private val COMMANDS = mapOf(
                "launchApp" to "Launch the application",
                "stopApp" to "Stop the application",
                "killApp" to "Kill the app process",
                "clearState" to "Clear application state",
                "clearKeychain" to "Clear iOS keychain data",
                "tapOn" to "Tap on an element",
                "doubleTapOn" to "Double tap on an element",
                "longPressOn" to "Long press on an element",
                "assertVisible" to "Assert that an element is visible",
                "assertNotVisible" to "Assert that an element is not visible",
                "assertTrue" to "Assert that a JavaScript condition is true",
                "assertWithAI" to "Use AI to assert UI state",
                "assertNoDefectsWithAI" to "Use AI to detect visual defects",
                "inputText" to "Input text into a field",
                "eraseText" to "Erase text from input field",
                "copyTextFrom" to "Copy text from an element",
                "pasteText" to "Paste previously copied text",
                "pressKey" to "Press a keyboard key",
                "hideKeyboard" to "Hide the on-screen keyboard",
                "swipe" to "Perform a swipe gesture",
                "scroll" to "Scroll the view",
                "scrollUntilVisible" to "Scroll until an element becomes visible",
                "back" to "Navigate back",
                "openLink" to "Open a URL or deep link",
                "runFlow" to "Run another flow file",
                "runScript" to "Run a JavaScript file",
                "repeat" to "Repeat commands multiple times",
                "retry" to "Retry commands on failure",
                "extendedWaitUntil" to "Wait for a condition",
                "waitForAnimationToEnd" to "Wait for animations to complete",
                "evalScript" to "Execute inline JavaScript",
                "extractTextWithAI" to "Extract text using AI",
                "startRecording" to "Start screen recording",
                "stopRecording" to "Stop screen recording",
                "takeScreenshot" to "Take a screenshot",
                "setLocation" to "Set mock geolocation",
                "setOrientation" to "Set device screen orientation",
                "setAirplaneMode" to "Set airplane mode",
                "toggleAirplaneMode" to "Toggle airplane mode",
                "addMedia" to "Add media files to the device",
                "travel" to "Navigate to a specific point on screen"
            )
            
            // Common selector properties
            private val SELECTOR_PROPERTIES = mapOf(
                "text" to "Text content of the element (supports regex)",
                "id" to "Resource ID (Android) or accessibility ID (iOS) (supports regex)",
                "enabled" to "Whether the element is enabled",
                "checked" to "Whether the element is checked",
                "focused" to "Whether the element is focused",
                "selected" to "Whether the element is selected",
                "index" to "0-based index to select among multiple matching elements",
                "point" to "Tap at relative position (50%,50%) or absolute coordinates (100,200)",
                "width" to "Element width in pixels",
                "height" to "Element height in pixels",
                "tolerance" to "Tolerance for width/height comparison",
                "below" to "Element below another element with given selector",
                "above" to "Element above another element with given selector",
                "leftOf" to "Element to the left of another element",
                "rightOf" to "Element to the right of another element",
                "containsChild" to "Element containing a direct child with given selector",
                "childOf" to "Element that is a child of another element",
                "containsDescendants" to "Element containing all specified descendant elements"
            )
            
            // launchApp properties
            private val LAUNCH_APP_PROPERTIES = mapOf(
                "appId" to "Package name (Android) or Bundle ID (iOS)",
                "clearState" to "Clear app state before launch",
                "clearKeychain" to "Clear iOS keychain before launch",
                "stopApp" to "Stop app before launching",
                "permissions" to "Configure app permissions",
                "arguments" to "Launch arguments to pass to app"
            )
            
            // Common command properties
            private val COMMON_PROPERTIES = mapOf(
                "label" to "Custom name in test output",
                "optional" to "Don't fail flow if command fails"
            )
            
            // Direction values
            private val DIRECTIONS = listOf("UP", "DOWN", "LEFT", "RIGHT")
            
            // Orientation values
            private val ORIENTATIONS = listOf("portrait", "landscape")
            
            // Permission values
            private val PERMISSION_VALUES = listOf("allow", "deny", "unset")
            
            // JS Engine values
            private val JS_ENGINES = listOf("graaljs", "rhino")
            
            // Key names for pressKey
            private val KEY_NAMES = listOf(
                "Enter", "Backspace", "Delete", "Escape", "Tab", "Space",
                "Home", "End", "PageUp", "PageDown",
                "ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"
            )
            
            // repeat properties
            private val REPEAT_PROPERTIES = mapOf(
                "times" to "Number of times to repeat",
                "while" to "Condition to repeat while true",
                "commands" to "Commands to repeat"
            )
            
            // retry properties
            private val RETRY_PROPERTIES = mapOf(
                "maxRetries" to "Maximum number of retry attempts",
                "commands" to "Commands to retry",
                "file" to "Flow file to retry"
            )
            
            // runFlow properties
            private val RUN_FLOW_PROPERTIES = mapOf(
                "file" to "Path to flow file",
                "env" to "Environment variables to pass",
                "commands" to "Inline commands to execute",
                "when" to "Condition for execution"
            )
            
            // runScript properties
            private val RUN_SCRIPT_PROPERTIES = mapOf(
                "file" to "Path to JavaScript file",
                "env" to "Environment variables to pass",
                "when" to "Condition for execution"
            )
            
            // extendedWaitUntil properties
            private val WAIT_UNTIL_PROPERTIES = mapOf(
                "visible" to "Wait until element is visible",
                "notVisible" to "Wait until element is not visible",
                "timeout" to "Timeout in milliseconds"
            )
            
            // scrollUntilVisible properties
            private val SCROLL_UNTIL_PROPERTIES = mapOf(
                "element" to "Element to scroll to",
                "direction" to "Scroll direction (UP, DOWN, LEFT, RIGHT)",
                "timeout" to "Timeout in milliseconds",
                "speed" to "Scroll speed (0-100)",
                "visibilityPercentage" to "Required visibility percentage (0-100)",
                "centerElement" to "Center element in viewport"
            )
            
            // swipe properties
            private val SWIPE_PROPERTIES = mapOf(
                "direction" to "Swipe direction (UP, DOWN, LEFT, RIGHT)",
                "start" to "Start coordinates (e.g., 50%,50% or 100,200)",
                "end" to "End coordinates (e.g., 10%,50% or 50,300)",
                "from" to "Element to swipe from (any selector)"
            )
            
            // doubleTapOn specific properties
            private val DOUBLE_TAP_PROPERTIES = mapOf(
                "delay" to "Delay in milliseconds between the two taps (default: 100ms)"
            )
            
            // scroll properties
            private val SCROLL_PROPERTIES = mapOf(
                "direction" to "Scroll direction (UP, DOWN, LEFT, RIGHT)"
            )
            
            // tapOn specific properties
            private val TAP_ON_PROPERTIES = mapOf(
                "retryTapIfNoChange" to "Retry tap if UI doesn't change",
                "waitToSettleTimeoutMs" to "Time in milliseconds to wait for UI to settle after tap",
                "repeat" to "Number of times to repeat the tap",
                "delay" to "Delay in milliseconds between repeated taps"
            )
            
            // setLocation properties
            private val SET_LOCATION_PROPERTIES = mapOf(
                "latitude" to "Latitude coordinate",
                "longitude" to "Longitude coordinate"
            )
            
            // recording properties
            private val RECORDING_PROPERTIES = mapOf(
                "path" to "Recording file path"
            )
            
            // extractTextWithAI properties
            private val EXTRACT_AI_PROPERTIES = mapOf(
                "query" to "Text extraction query",
                "outputVariable" to "Variable name for extracted text"
            )
            
            // assertWithAI properties
            private val ASSERT_AI_PROPERTIES = mapOf(
                "assertion" to "AI assertion description"
            )
        }
        
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {
            val position = parameters.position
            val file = position.containingFile
            val filePath = file.virtualFile?.path ?: return
            
            // Only provide completions for files in maestro folders
            if (!filePath.contains("/maestro/")) {
                return
            }
            
            // Get the YAML key value pair context
            val keyValue = findParentKeyValue(position)
            val parentKey = keyValue?.keyText
            
            // Determine context and provide appropriate completions
            when {
                // Top-level configuration (before ---) or root level
                isTopLevelContext(position) -> {
                    addConfigKeyCompletions(result)
                }
                
                // Command context (after --- or in command list)
                isCommandContext(position) -> {
                    addCommandCompletions(result)
                }
                
                // Inside a specific command
                parentKey != null -> {
                    addCommandPropertyCompletions(parentKey, result)
                }
                
                // Property value context
                isPropertyValueContext(position) -> {
                    val key = getPropertyKey(position)
                    addPropertyValueCompletions(key, result)
                }
            }
        }
        
        private fun findParentKeyValue(element: com.intellij.psi.PsiElement): YAMLKeyValue? {
            var current = element.parent
            while (current != null) {
                if (current is YAMLKeyValue) {
                    return current
                }
                current = current.parent
            }
            return null
        }
        
        private fun isTopLevelContext(element: com.intellij.psi.PsiElement): Boolean {
            val text = element.containingFile.text
            val offset = element.textOffset
            val beforeText = text.substring(0, offset)
            return !beforeText.contains("---")
        }
        
        private fun isCommandContext(element: com.intellij.psi.PsiElement): Boolean {
            val text = element.containingFile.text
            val offset = element.textOffset
            val beforeText = text.substring(0, offset)
            
            // Check if we're after --- separator
            if (!beforeText.contains("---")) {
                return false
            }
            
            val lines = beforeText.split("\n")
            val currentLine = lines.lastOrNull() ?: ""
            val trimmedLine = currentLine.trim()
            
            // Don't suggest commands if we're already in a property (has colon)
            if (currentLine.contains(":")) {
                return false
            }
            
            // Offer command completions if:
            // 1. Line starts with "- " (already typing command after dash)
            // 2. Line starts with "-" only (typed dash, need space and command)
            // 3. At start of new line at root level (need to add "- " prefix)
            return when {
                trimmedLine.startsWith("- ") -> true
                trimmedLine == "-" -> true
                trimmedLine.isEmpty() -> {
                    // Check if we're at root indentation level (0-2 spaces)
                    val indent = currentLine.takeWhile { it == ' ' }.length
                    indent <= 2
                }
                else -> false
            }
        }
        
        private fun isPropertyValueContext(element: com.intellij.psi.PsiElement): Boolean {
            val keyValue = findParentKeyValue(element)
            return keyValue != null && keyValue.value?.textContains(element.text.firstOrNull() ?: ' ') == true
        }
        
        private fun getPropertyKey(element: com.intellij.psi.PsiElement): String? {
            return findParentKeyValue(element)?.keyText
        }
        
        private fun addConfigKeyCompletions(result: CompletionResultSet) {
            CONFIG_KEYS.forEach { (key, description) ->
                result.addElement(
                    LookupElementBuilder.create(key)
                        .withTypeText(description)
                        .withInsertHandler { context, _ ->
                            context.document.insertString(context.tailOffset, ": ")
                            context.editor.caretModel.moveToOffset(context.tailOffset)
                        }
                )
            }
        }
        
        private fun addCommandCompletions(result: CompletionResultSet) {
            COMMANDS.forEach { (command, description) ->
                result.addElement(
                    LookupElementBuilder.create(command)
                        .withTypeText(description)
                        .bold()
                        .withInsertHandler { context, _ ->
                            val document = context.document
                            val lineNumber = document.getLineNumber(context.startOffset)
                            val lineStartOffset = document.getLineStartOffset(lineNumber)
                            val beforeCursorText = document.getText(
                                com.intellij.openapi.util.TextRange(lineStartOffset, context.startOffset)
                            )
                            
                            val trimmedLine = beforeCursorText.trim()
                            
                            // If the line doesn't start with "- ", we need to add it
                            if (!trimmedLine.startsWith("-")) {
                                // Find where to insert (after any leading whitespace)
                                val leadingWhitespace = beforeCursorText.takeWhile { it.isWhitespace() }
                                val insertOffset = lineStartOffset + leadingWhitespace.length
                                
                                // Delete any existing text that was typed
                                val textToDelete = beforeCursorText.substring(leadingWhitespace.length)
                                if (textToDelete.isNotEmpty()) {
                                    document.deleteString(insertOffset, context.startOffset)
                                }
                                
                                // Insert "- " + command
                                document.insertString(insertOffset, "- ")
                            }
                        }
                )
            }
        }
        
        private fun addCommandPropertyCompletions(commandName: String, result: CompletionResultSet) {
            // Add common properties
            COMMON_PROPERTIES.forEach { (key, description) ->
                result.addElement(
                    LookupElementBuilder.create(key)
                        .withTypeText(description)
                )
            }
            
            // Add selector properties for commands that use selectors
            val selectorCommands = setOf(
                "tapOn", "doubleTapOn", "longPressOn",
                "assertVisible", "assertNotVisible", "copyTextFrom"
            )
            if (selectorCommands.contains(commandName)) {
                SELECTOR_PROPERTIES.forEach { (key, description) ->
                    result.addElement(
                        LookupElementBuilder.create(key)
                            .withTypeText(description)
                    )
                }
            }
            
            // Add command-specific properties
            when (commandName) {
                "launchApp" -> addMapCompletions(result, LAUNCH_APP_PROPERTIES)
                "tapOn", "longPressOn" -> addMapCompletions(result, TAP_ON_PROPERTIES)
                "doubleTapOn" -> {
                    addMapCompletions(result, TAP_ON_PROPERTIES)
                    addMapCompletions(result, DOUBLE_TAP_PROPERTIES)
                }
                "repeat" -> addMapCompletions(result, REPEAT_PROPERTIES)
                "retry" -> addMapCompletions(result, RETRY_PROPERTIES)
                "runFlow" -> addMapCompletions(result, RUN_FLOW_PROPERTIES)
                "runScript" -> addMapCompletions(result, RUN_SCRIPT_PROPERTIES)
                "extendedWaitUntil" -> addMapCompletions(result, WAIT_UNTIL_PROPERTIES)
                "scrollUntilVisible" -> addMapCompletions(result, SCROLL_UNTIL_PROPERTIES)
                "swipe" -> addMapCompletions(result, SWIPE_PROPERTIES)
                "scroll" -> addMapCompletions(result, SCROLL_PROPERTIES)
                "setLocation" -> addMapCompletions(result, SET_LOCATION_PROPERTIES)
                "startRecording", "stopRecording" -> addMapCompletions(result, RECORDING_PROPERTIES)
                "extractTextWithAI" -> addMapCompletions(result, EXTRACT_AI_PROPERTIES)
                "assertWithAI" -> addMapCompletions(result, ASSERT_AI_PROPERTIES)
            }
        }
        
        private fun addPropertyValueCompletions(propertyKey: String?, result: CompletionResultSet) {
            when (propertyKey) {
                "direction" -> {
                    DIRECTIONS.forEach { direction ->
                        result.addElement(LookupElementBuilder.create(direction))
                    }
                }
                "setOrientation" -> {
                    ORIENTATIONS.forEach { orientation ->
                        result.addElement(LookupElementBuilder.create(orientation))
                    }
                }
                "jsEngine" -> {
                    JS_ENGINES.forEach { engine ->
                        result.addElement(LookupElementBuilder.create(engine))
                    }
                }
                "pressKey" -> {
                    KEY_NAMES.forEach { key ->
                        result.addElement(LookupElementBuilder.create(key))
                    }
                }
                "enabled", "checked", "focused", "selected", 
                "clearState", "clearKeychain", "stopApp", 
                "optional", "retryTapIfNoChange", "centerElement" -> {
                    result.addElement(LookupElementBuilder.create("true"))
                    result.addElement(LookupElementBuilder.create("false"))
                }
                "all", "camera", "location", "notifications", 
                "medialibrary" -> {
                    PERMISSION_VALUES.forEach { value ->
                        result.addElement(LookupElementBuilder.create(value))
                    }
                }
            }
        }
        
        private fun addMapCompletions(result: CompletionResultSet, map: Map<String, String>) {
            map.forEach { (key, description) ->
                result.addElement(
                    LookupElementBuilder.create(key)
                        .withTypeText(description)
                )
            }
        }
    }
}
