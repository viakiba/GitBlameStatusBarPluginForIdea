# 构建说明

## 插件已创建完成

插件的主要组件：

1. **GitBlameStatusBarWidget.kt** - 主要的状态栏组件
2. **GitBlameStatusBarWidgetFactory.kt** - 组件工厂
3. **GitBlameService.kt** - Git blame 信息获取服务
4. **GitBlameEditorListener.kt** - 编辑器事件监听器

## 功能特性

- 支持 IntelliJ IDEA 版本 231-263 (对应 2023.3 到 2026.3)
- 实时显示光标行的 Git blame 信息
- 显示格式：作者 - 提交信息 - 提交时间
- 自动检测 Git 版本控制文件

## 构建命令

```bash
# Windows
.\gradlew.bat buildPlugin

# 构建完成后，插件文件位于：
# build/distributions/git-blame-statusbar-plugin-1.0-SNAPSHOT.zip
```

## 安装方法

1. 在 IntelliJ IDEA 中打开 Settings > Plugins
2. 点击齿轮图标，选择 "Install Plugin from Disk..."
3. 选择构建生成的 zip 文件
4. 重启 IDE

插件安装后会自动在状态栏显示 Git blame 信息。