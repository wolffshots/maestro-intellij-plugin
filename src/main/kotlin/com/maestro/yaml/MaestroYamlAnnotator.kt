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
        private val GOOD_PATTERN = Pattern.compile("\\$\\{[A-Za-z0-9_]+}")
        private val BAD_PATTERN = Pattern.compile("\\$[A-Za-z0-9_]+")
        
        val GOOD_VARIABLE = TextAttributesKey.createTextAttributesKey(
            "MAESTRO_GOOD_VARIABLE",
            DefaultLanguageHighlighterColors.STRING
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
        
        val text = element.text ?: return
        
        // Find good variables first (${VAR})
        val goodMatcher = GOOD_PATTERN.matcher(text)
        while (goodMatcher.find()) {
            val startOffset = element.textRange.startOffset + goodMatcher.start()
            val endOffset = element.textRange.startOffset + goodMatcher.end()
            
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(startOffset, endOffset))
                .textAttributes(GOOD_VARIABLE)
                .create()
        }
        
        // Find bad variables ($VAR without braces)
        val badMatcher = BAD_PATTERN.matcher(text)
        while (badMatcher.find()) {
            val matchText = badMatcher.group()
            
            // Skip if this is actually part of a good variable (${VAR})
            val matchStart = badMatcher.start()
            val matchEnd = badMatcher.end()
            
            // Check if there's a closing brace after this match
            if (matchEnd < text.length && text[matchEnd] == '}') {
                continue
            }
            
            val startOffset = element.textRange.startOffset + matchStart
            val endOffset = element.textRange.startOffset + matchEnd
            
            holder.newAnnotation(HighlightSeverity.WARNING, "Use \${$matchText} instead of $matchText")
                .range(TextRange(startOffset, endOffset))
                .textAttributes(BAD_VARIABLE)
                .create()
        }
    }
}

