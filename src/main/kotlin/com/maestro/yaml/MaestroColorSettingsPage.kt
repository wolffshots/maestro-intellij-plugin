package com.maestro.yaml

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.yaml.YAMLSyntaxHighlighter
import javax.swing.Icon

class MaestroColorSettingsPage : ColorSettingsPage {
    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Maestro Good Variable (\${VAR})", MaestroYamlAnnotator.GOOD_VARIABLE),
            AttributesDescriptor("Maestro Bad Variable (\$VAR)", MaestroYamlAnnotator.BAD_VARIABLE)
        )
    }

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = YAMLSyntaxHighlighter()

    override fun getDemoText(): String = """
        # Maestro YAML Example
        appId: com.example.app
        ---
        - launchApp:
            appId: <good>${'$'}{APP_ID}</good>
            
        - tapOn:
            text: <bad>${'$'}BUTTON_TEXT</bad>
            
        - inputText: <good>${'$'}{USER_NAME}</good>
        - assertVisible: <bad>${'$'}ERROR_MSG</bad>
        
        # Correct usage (green):
        env:
          APP_ID: <good>${'$'}{MY_APP_ID}</good>
          USER: <good>${'$'}{USERNAME}</good>
          
        # Incorrect usage (red - will show warning):
        bad_vars:
          - <bad>${'$'}VAR1</bad>
          - <bad>${'$'}VAR2</bad>
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "good" to MaestroYamlAnnotator.GOOD_VARIABLE,
        "bad" to MaestroYamlAnnotator.BAD_VARIABLE
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Maestro YAML"
}

