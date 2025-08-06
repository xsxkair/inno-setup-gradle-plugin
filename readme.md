## 自动构架java程序的安装包插件
### 功能
- 自动构架java程序的安装包
- 依赖于launch4j插件的输出文件，需先使用launch4j插件构建出可执行文件
### 用法
- 引入插件
```
buildscript {
    repositories {
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath 'com.github.xsxkair.inno-setup-gradle-plugin:InnoSetupPlugin:0.0.11'
    }
}
plugins {
    id 'java' //引入java插件，用于编译java代码
    id 'edu.sc.seis.launch4j' version '3.0.4' //引入launch4j插件，用于构建可执行文件
}
apply plugin: 'org.xsx.inno-setup-gradle-plugin'
```
- 配置插件
```

group = 'org.xsx'   //根据项目实际填写，与本插件无关
version = '1.1.0'     //根据项目实际填写，与本插件无关

launch4j {
    outfile = '测试.exe'                       // 输出文件名
    mainClassName = 'com.xsx.App'               //主类
    icon = "$projectDir/logo_nor.ico"           //图标
    bundledJrePath = "jre"                     // 指定使用exe同级目录jre环境
}

innoSetup {
    appName = "测试"                  // 应用名称
    appVersion = project.version        // 应用版本
    appId = "com.xsx.test"              // 应用ID，用于唯一标识应用，重复安装时依据此来覆盖就的安装程序
    appIcon = "$projectDir/logo_nor.ico"           // 应用图标
    srcExeFiltPath = "$buildDir/launch4j/测试.exe"           // 应用可执行文件路径，此次填写launch4j插件输出的可执行文件路径
    srcLibPath = "$buildDir/launch4j/lib"           // 应用库文件路径，此次填写launch4j插件输出的库文件路径
    srcJrePath = "D:\\ProgramFiles\\java\\jre_32_1.8.0_121"           // 应用JRE路径
    replaceSetup = "true"           // 是否替换旧的安装，true表示替换，false表示不替换
    appPublisher = "测试"           // 应用发布者
    appPublisherUrl = "https://www.baidu.com"           // 应用发布者URL
    isccPath = "D:\\ProgramFiles\\Inno Setup 6\\ISCC.exe" // inno setup编译器路径，测试版本6.0
}
```
- 执行任务
```
gradle launch4j
gradle innoSetup
```
- 安装包路径
```
$buildDir/inno-setup/测试.exe
```
