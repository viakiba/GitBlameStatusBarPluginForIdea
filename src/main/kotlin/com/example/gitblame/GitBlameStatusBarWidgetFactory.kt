package com.example.gitblame

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class GitBlameStatusBarWidgetFactory : StatusBarWidgetFactory {
    
    override fun getId(): @NonNls String = GitBlameStatusBarWidget.WIDGET_ID
    
    override fun getDisplayName(): @Nls String = "Git Blame Info"
    
    override fun isAvailable(project: Project): Boolean = true
    
    override fun createWidget(project: Project): StatusBarWidget {
        return GitBlameStatusBarWidget(project)
    }
    
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
    
    override fun canBeEnabledOn(statusBar: com.intellij.openapi.wm.StatusBar): Boolean = true
}