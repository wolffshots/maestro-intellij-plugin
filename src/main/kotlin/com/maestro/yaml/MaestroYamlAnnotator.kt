package com.maestro.yaml

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.regex.Pattern

class MaestroYamlAnnotator : Annotator {
    companion object {
        // Match ${...} with any content (expressions)
        private val EXPRESSION_PATTERN = Pattern.compile("\\$\\{[^}]+}")
        // Match simple variables ${VAR} - starts with letter/underscore, can contain letters, numbers, underscores, dots
        // Examples: ${VAR}, ${MY_VAR}, ${TEST_PRODUCT_NAME_1}, ${output.value}
        private val SIMPLE_VAR_PATTERN = Pattern.compile("\\$\\{[A-Za-z_][A-Za-z0-9_.]*}")
        // Match bad variables $VAR
        private val BAD_PATTERN = Pattern.compile("\\$[A-Za-z0-9_]+")
        // Match string literals inside expressions
        private val STRING_LITERAL_PATTERN = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1")
        
        val EXPRESSION_BACKGROUND = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_EXPRESSION",
            DefaultLanguageHighlighterColors.IDENTIFIER
        )
        
        val STRING_LITERAL = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_STRING_LITERAL",
            DefaultLanguageHighlighterColors.STRING
        )
        
        val OPERATOR = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        
        val PROPERTY_REFERENCE = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_PROPERTY_REFERENCE",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        
        val BAD_VARIABLE = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_BAD_VARIABLE",
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
        )
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = element.containingFile ?: return
        val filePath = file.virtualFile?.path ?: return
        
        // Only process files in maestro folders
        if (!filePath.contains("/maestro/")) {
            return
        }
        
        // Only process leaf elements to avoid duplicate annotations
        if (element.firstChild != null) {
            return
        }
        
        val text = element.text ?: return
        
        // Skip if text is too short to contain our patterns
        if (text.length < 2) {
            return
        }
        
        // Find all ${...} expressions and treat them all as code
        val expressionMatcher = EXPRESSION_PATTERN.matcher(text)
        while (expressionMatcher.find()) {
            val expressionText = expressionMatcher.group()
            val expressionStart = expressionMatcher.start()
            
            // All ${...} expressions are highlighted as code
            highlightExpression(expressionText, expressionStart, element, holder)
        }
        
        // Find bad variables ($VAR without braces)
        val badMatcher = BAD_PATTERN.matcher(text)
        while (badMatcher.find()) {
            val matchText = badMatcher.group()
            val matchStart = badMatcher.start()
            val matchEnd = badMatcher.end()
            
            // Skip if this is part of a ${} expression
            if (matchEnd < text.length && text[matchEnd] == '}') {
                continue
            }
            
            val startOffset = element.textRange.startOffset + matchStart
            val endOffset = element.textRange.startOffset + matchEnd
            
            holder.newAnnotation(HighlightSeverity.WARNING, "Use \${${matchText.substring(1)}} instead of $matchText")
                .range(TextRange(startOffset, endOffset))
                .textAttributes(BAD_VARIABLE)
                .create()
        }
    }
    
    private fun highlightExpression(
        expressionText: String,
        expressionStart: Int,
        element: PsiElement,
        holder: AnnotationHolder
    ) {
        // Highlight the entire expression background
        val startOffset = element.textRange.startOffset + expressionStart
        val endOffset = element.textRange.startOffset + expressionStart + expressionText.length
        
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(startOffset, endOffset))
            .textAttributes(EXPRESSION_BACKGROUND)
            .create()
        
        // Extract content between ${ and }
        val content = expressionText.substring(2, expressionText.length - 1)
        val contentStart = expressionStart + 2
        
        // Find and highlight string literals
        val stringMatcher = STRING_LITERAL_PATTERN.matcher(content)
        while (stringMatcher.find()) {
            val stringStart = contentStart + stringMatcher.start()
            val stringEnd = contentStart + stringMatcher.end()
            
            val stringStartOffset = element.textRange.startOffset + stringStart
            val stringEndOffset = element.textRange.startOffset + stringEnd
            
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(stringStartOffset, stringEndOffset))
                .textAttributes(STRING_LITERAL)
                .create()
        }
        
        // Highlight operators (+, -, *, /, etc.)
        val operators = listOf("+", "-", "*", "/", "==", "!=", "<=", ">=", "<", ">", "&&", "||")
        for (op in operators) {
            var index = content.indexOf(op)
            while (index != -1) {
                // Make sure it's not inside a string literal
                if (!isInsideStringLiteral(content, index)) {
                    val opStart = contentStart + index
                    val opEnd = opStart + op.length
                    
                    val opStartOffset = element.textRange.startOffset + opStart
                    val opEndOffset = element.textRange.startOffset + opEnd
                    
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(TextRange(opStartOffset, opEndOffset))
                        .textAttributes(OPERATOR)
                        .create()
                }
                index = content.indexOf(op, index + 1)
            }
        }
        
        // Highlight all identifiers (variables and property references)
        // Pattern: identifiers with or without dots (e.g., MY_VAR, output.strings.add)
        val identifierPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*\\b")
        val identifierMatcher = identifierPattern.matcher(content)
        while (identifierMatcher.find()) {
            // Make sure it's not inside a string literal
            if (!isInsideStringLiteral(content, identifierMatcher.start())) {
                val idStart = contentStart + identifierMatcher.start()
                val idEnd = contentStart + identifierMatcher.end()
                
                val idStartOffset = element.textRange.startOffset + idStart
                val idEndOffset = element.textRange.startOffset + idEnd
                
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(idStartOffset, idEndOffset))
                    .textAttributes(PROPERTY_REFERENCE)
                    .create()
            }
        }
    }
    
    private fun isInsideStringLiteral(content: String, position: Int): Boolean {
        val stringMatcher = STRING_LITERAL_PATTERN.matcher(content)
        while (stringMatcher.find()) {
            if (position >= stringMatcher.start() && position < stringMatcher.end()) {
                return true
            }
        }
        return false
    }
}

