# Git Blame Status Bar Plugin

使用 AI 生成的一个 IntelliJ IDEA 插件，在状态栏显示当前光标行的 Git blame 信息。

git-blame-statusbar-plugin-1.0-SNAPSHOT.zip 支持版本 233 ~ 263

## 功能特性

- 实时显示光标所在行的 Git blame 信息
- 显示提交人、提交信息和提交时间
- 支持 IntelliJ IDEA 版本 231-263
- 自动检测 Git 版本控制的文件

## 构建和安装

1. 克隆项目：
```bash
git clone <repository-url>
cd git-blame-statusbar-plugin
```

2. 构建插件：
```bash
# 注意  build.gradle.kts 中 
#  localPath.set("C:\\Users\\admin\\IDEA")   
# idea的版本是 IU-233.15619.7
# sinceBuild.set("233")
# untilBuild.set("263.*")

./gradlew buildPlugin
```

3. 安装插件：
- 在 IntelliJ IDEA 中，打开 Settings > Plugins
- 点击齿轮图标，选择 "Install Plugin from Disk..."
- 选择 `build/distributions/git-blame-statusbar-plugin-1.0-SNAPSHOT.zip`

## 使用方法

安装插件后，当你在有 Git 版本控制的文件中移动光标时，状态栏会自动显示当前行的 Git blame 信息，格式为：

```
作者名 - 提交信息 - 提交时间
```

## 开发

- Java/Kotlin 17+
- IntelliJ IDEA 2025.3+
- Gradle 8.0+