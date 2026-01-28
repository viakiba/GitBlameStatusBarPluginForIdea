package com.example.gitblame

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepositoryManager
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object GitBlameService {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun getBlameInfo(project: Project, file: VirtualFile, lineNumber: Int): String? {
        try {
            // 检查文件是否在 Git 版本控制下
            if (!GitUtil.isUnderGit(file)) {
                return "Not in Git"
            }
            
            val repositoryManager = GitRepositoryManager.getInstance(project)
            val repository = repositoryManager.getRepositoryForFileQuick(file) ?: return "No Git Repository"
            
            // 创建 git blame 命令
            val handler = GitLineHandler(project, repository.root, GitCommand.BLAME)
            handler.addParameters("--porcelain")
            handler.addParameters("-L", "${lineNumber + 1},${lineNumber + 1}")
            
            // 获取相对于仓库根目录的文件路径
            val relativePath = repository.root.findFileByRelativePath(
                file.path.removePrefix(repository.root.path).removePrefix("/")
            )
            
            if (relativePath != null) {
                handler.addRelativeFiles(setOf(relativePath))
            } else {
                return "File not found in repository"
            }
            
            // 执行 git blame 命令
            val result = Git.getInstance().runCommand(handler)
            
            if (result.success() && result.output.isNotEmpty()) {
                return parseBlameOutput(result.output)
            } else {
                return "Git blame failed: ${result.errorOutputAsJoinedString}"
            }
            
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }
    
    private fun parseBlameOutput(output: List<String>): String? {
        if (output.isEmpty()) return "No blame info"
        
        var author: String? = null
        var authorTime: Long? = null
        var summary: String? = null
        var commitHash: String? = null
        
        for (line in output) {
            when {
                line.startsWith("author ") -> {
                    author = line.substring(7).trim()
                }
                line.startsWith("author-time ") -> {
                    authorTime = line.substring(12).trim().toLongOrNull()
                }
                line.startsWith("summary ") -> {
                    summary = line.substring(8).trim()
                }
                line.matches(Regex("^[a-f0-9]{40}.*")) -> {
                    // 提取 commit hash (前40个字符)
                    commitHash = line.substring(0, 40)
                }
            }
        }
        
        return if (author != null && summary != null) {
            val timeStr = if (authorTime != null) {
                try {
                    val instant = Instant.ofEpochSecond(authorTime)
                    dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
                } catch (e: Exception) {
                    "Unknown time"
                }
            } else {
                "Unknown time"
            }
            
            val shortHash = commitHash?.substring(0, 8) ?: "????????"
            "$author - $summary - $timeStr [$shortHash]"
        } else {
            "Unable to parse blame info"
        }
    }
}