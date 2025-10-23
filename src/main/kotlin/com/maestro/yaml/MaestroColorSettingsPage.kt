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
            AttributesDescriptor("Expression Background", MaestroYamlAnnotator.EXPRESSION_BACKGROUND),
            AttributesDescriptor("Variable/Identifier (in expressions)", MaestroYamlAnnotator.PROPERTY_REFERENCE),
            AttributesDescriptor("String Literal (in expressions)", MaestroYamlAnnotator.STRING_LITERAL),
            AttributesDescriptor("Operator (in expressions)", MaestroYamlAnnotator.OPERATOR),
            AttributesDescriptor("Bad Variable (\$VAR)", MaestroYamlAnnotator.BAD_VARIABLE)
        )
    }

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = YAMLSyntaxHighlighter()

    override fun getDemoText(): String = """
        # Maestro YAML Example
        appId: com.example.app
        ---
        # All ${'$'}{} expressions are code-like:
        - launchApp:
            appId: <expr>${'$'}{<var>APP_ID</var>}</expr>
            
        - inputText: <expr>${'$'}{<var>USER_NAME</var>}</expr>
        
        - assertVisible:
            text: <expr>${'$'}{<var>output.strings.add</var> <op>+</op> <str>" 1 "</str> <op>+</op> <var>output.strings.item</var>}</expr>
            
        - tapOn:
            text: <expr>${'$'}{<str>"Hello "</str> <op>+</op> <var>user.name</var> <op>+</op> <str>"!"</str>}</expr>
            
        - runScript:
            when: <expr>${'$'}{<var>count.value</var> <op>></op> <str>"5"</str>}</expr>
            
        - inputText:
            text: <expr>${'$'}{<var>user.firstName</var> <op>+</op> <str>" "</str> <op>+</op> <var>user.lastName</var>}</expr>
            
        - setVariable:
            name: <expr>${'$'}{<var>MY_VAR_1</var>}</expr>
        
        # Incorrect usage (red - will show warning):
        bad_vars:
          - <bad>${'$'}VAR1</bad>
          - <bad>${'$'}MISSING_BRACES</bad>
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "expr" to MaestroYamlAnnotator.EXPRESSION_BACKGROUND,
        "var" to MaestroYamlAnnotator.PROPERTY_REFERENCE,
        "str" to MaestroYamlAnnotator.STRING_LITERAL,
        "op" to MaestroYamlAnnotator.OPERATOR,
        "bad" to MaestroYamlAnnotator.BAD_VARIABLE
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Maestro YAML"
}

