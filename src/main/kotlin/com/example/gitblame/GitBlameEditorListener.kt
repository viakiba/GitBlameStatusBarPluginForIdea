package com.example.gitblame

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener

class GitBlameEditorListener : EditorFactoryListener {
    
    override fun editorCreated(event: EditorFactoryEvent) {
        // 编辑器创建时的处理 - 现在由 Widget 直接处理
    }
    
    override fun editorReleased(event: EditorFactoryEvent) {
        // 编辑器释放时的处理
    }
}