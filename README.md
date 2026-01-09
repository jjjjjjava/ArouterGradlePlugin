# ArouterGradlePlugin

## 简介

本插件可实现AGP7.4+和AGP8下[ARouter](https://github.com/alibaba/ARouter)框架自动化插桩，并且支持java21版本。使用方法和`com.alibaba:arouter-register` 完全一致，无缝替换；

## 更新说明

2026/01/06，更新

- 说明：[issue 5](https://github.com/JailedBird/ArouterGradlePlugin/issues/5) 修复，解决多个Transform插件编译错误，并在demo工程中更新AGP8.1示例；
- 根因：宿主工程中低版本AGP存在bug，导致多个issue5中的多插件输出目录相同，产生写入冲突；
- 解决：宿主工程升级到AGP8.1+, 多个transform的产物生成目录会根据taskName自动区分，完成修复；
- 特别说明：仅需更新宿主工程AGP，无需更新此插件；



最新版本1.0.5, 修复和优化

- debug阶段支持禁用插桩，同时优化debug名称匹配，支持Debug这种，而非单纯的debug
- ASM7->ASM9
- 新增混淆规则配置 （需要手动配置）

为了不影响篇幅，请到最底部查阅详细内容；



## 导入方法

插件发布在 [ArouterPlugin](https://jitpack.io/#jjjjjjava/ArouterGradlePlugin) ，点开即可查阅最全面的插件导入方式；

**Koltin**

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```kotlin
plugins {
    id("io.github.jjjjjjava.ARouterPlugin") version "1.0.5"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.github.jjjjjjava.ArouterGradlePlugin:arouter-gradle-plugin:v1.0.5")
    }
}

apply(plugin = "io.github.jjjjjjava.ARouterPlugin")
```



## 插桩代码

遍历注解处理器生成的路由信息，然后在loadRouterMap函数中插桩

```
override fun visitInsn(opcode: Int) {
    // generate code before return
    if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {
        targetList?.forEach { scanSetting ->
            scanSetting.classList.forEach { name ->
                val className = name.replace("/", ".")
                mv.visitLdcInsn(className)// 类名
                
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    ScanSetting.GENERATE_TO_CLASS_NAME,
                    ScanSetting.REGISTER_METHOD_NAME,
                    "(Ljava/lang/String;)V",
                    false
                )
            }
        }
    }
    super.visitInsn(opcode)
}
```

插桩后字节码如下：

```
.method public static loadRouterMap()V
    .registers 1
    
    .line 63
    const/4 v0, 0x0
    sput-boolean v0, Lcom/alibaba/android/arouter/core/LogisticsCenter;->registerByPlugin:Z

    .line 68
    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Root$$app"
    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V
    
    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Root$$arouterapi"
    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V
    
    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Providers$$app"
    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V
    
    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi"
    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V

    return-void
.end method
```



## 更新说明

### 1.0.5 更新内容

1、 解决debug阶段编译慢的问题；宿主模块启用如下配置，可避免在debug的变体下进行插桩，此时会通过Arouter原生遍历dex寻找路由文件，首次启动慢，之后会保存在SP中，速度应该还可以接受；缺点是路由表表更，需要清除应用数据才能生效；默认关闭，推荐开启（毕竟大项目编译非常慢）；

```
arouter_config {
    disableTransformWhenDebugBuild = true
}
```

2、 Java21 环境支持

3、修复无法正确识别debug大小写，同时添加完全禁用Transform的配置

## 参考文献

- https://docs.gradle.org/

- https://github.com/android/gradle-recipes

- [你的插件想适配Transform Action? 可能还早了点](https://juejin.cn/post/7190196880469393463)

- [Transform API 废弃了，路由插件怎么办？](https://juejin.cn/post/7222091234100330554)

## 最后

本项目实现，参考自 [JailedBird](https://github.com/JailedBird/ArouterGradlePlugin) 的项目，但是其项目不支持java21的环境，因此拉取下来修改
