package com.example.gitblame

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class GitBlameStatusBarWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation, CaretListener, FileEditorManagerListener {
    
    companion object {
        const val WIDGET_ID = "GitBlameStatusBar"
    }
    
    private var currentBlameInfo: String = "Git Blame Ready"
    private var currentEditor: Editor? = null
    private var statusBar: com.intellij.openapi.wm.StatusBar? = null
    
    override fun ID(): String = WIDGET_ID
    
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this
    
    override fun getText(): String = currentBlameInfo
    
    override fun getTooltipText(): String? = "Git Blame 信息显示"
    
    override fun getClickConsumer(): Consumer<MouseEvent>? = null
    
    override fun getAlignment(): Float = 0.0f
    
    override fun install(statusBar: com.intellij.openapi.wm.StatusBar) {
        this.statusBar = statusBar
        
        // 监听文件编辑器变化
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this)
        
        // 获取当前活动编辑器
        val fileEditorManager = FileEditorManager.getInstance(project)
        val selectedEditor = fileEditorManager.selectedTextEditor
        if (selectedEditor != null) {
            setCurrentEditor(selectedEditor)
        }
        
        updateBlameInfo()
    }
    
    override fun dispose() {
        currentEditor?.caretModel?.removeCaretListener(this)
        currentEditor = null
        statusBar = null
    }
    
    override fun fileOpened(source: FileEditorManager, file: com.intellij.openapi.vfs.VirtualFile) {
        val editor = source.selectedTextEditor
        if (editor != null) {
            setCurrentEditor(editor)
        }
    }
    
    override fun selectionChanged(event: FileEditorManagerEvent) {
        val editor = event.manager.selectedTextEditor
        if (editor != null) {
            setCurrentEditor(editor)
        }
    }
    
    private fun setCurrentEditor(editor: Editor) {
        // 移除旧编辑器的监听器
        currentEditor?.caretModel?.removeCaretListener(this)
        
        // 设置新编辑器
        currentEditor = editor
        currentEditor?.caretModel?.addCaretListener(this)
        
        updateBlameInfo()
    }
    
    override fun caretPositionChanged(event: CaretEvent) {
        updateBlameInfo()
    }
    
    private fun updateBlameInfo() {
        ApplicationManager.getApplication().invokeLater {
            val editor = currentEditor
            if (editor == null) {
                currentBlameInfo = "No Editor"
                statusBar?.updateWidget(ID())
                return@invokeLater
            }
            
            val virtualFile = editor.virtualFile
            if (virtualFile == null) {
                currentBlameInfo = "No File"
                statusBar?.updateWidget(ID())
                return@invokeLater
            }
            
            val currentLine = editor.caretModel.logicalPosition.line
            
            // 在后台线程执行 Git 操作
            ApplicationManager.getApplication().executeOnPooledThread {
                try {
                    val blameInfo = GitBlameService.getBlameInfo(project, virtualFile, currentLine)
                    
                    // 回到 UI 线程更新界面
                    ApplicationManager.getApplication().invokeLater {
                        currentBlameInfo = blameInfo ?: "Git: Line ${currentLine + 1}"
                        statusBar?.updateWidget(ID())
                    }
                } catch (e: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        currentBlameInfo = "Git Error: ${e.message}"
                        statusBar?.updateWidget(ID())
                    }
                }
            }
        }
    }
}