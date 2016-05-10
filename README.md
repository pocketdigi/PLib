PLib
=================
PLib是一个Android应用开发库，集成了流行的开源库，整合一些Util,可以帮助开发者更快开发应用.仅支持Android Studio。因为在开发过程中用到SQlite的概率不算高，v1.5已移除ormLite。
同时由于Fresco的引入，Vollery的NetworkImageView已经成为鸡肋，1.5版开始直接使用官方版的volley.


整合开源库：
-------------
1.AndroidAnonations 4.0.0 <br />
2.gson 2.3<br />
3.eventbus 2.6.2<br />
4.fresco 0.8.1 <br />
5.Volley <br />

功能:
-------------
1.SharedPreference封装<br />
2.Toast封装<br />
3.Log封装<br />
4.其他一些常用Utils(md5,DES,日期处理,字符串处理,图片处理,网络判断,首次运行检测等)<br />

使用方法有两种:
--------

### 一、下载Demo模板项目：

[下载附件中的Demo项目，导入Android Studio,Rebuild Project,改包名，直接使用。](http://git.oschina.net/pocketdigi/PLib/attach_files)
Demo包含的内容:

#####  1、PageManager
Demo项目使用单Activity架构，UI使用Fragment展示，通过PageManger控制。
#####  2、Http接口数据获取,Android DataBinding框架使用
#####  3、友盟统计集成
#####  4、其他一些小功能

### 二、作为子模块加到现有项目

#####1、切到项目(仅支持Android Studio项目)根目录下,添加子模块<br />
git submodule add -b v1.5 https://git.oschina.net/pocketdigi/PLib.git plib <br />
#####2、修改项目的build.gradle

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.1.0'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
####3、修改项目的settings.gradle

```java
include ':app', ':plib'
```
将plib模块加进来
####4、修改app模块的build.gradle

```java
apply plugin: 'com.neenbedankt.android-apt'
def AAVersion = '4.0.0'
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile project(':plib')
    apt "org.androidannotations:androidannotations:$AAVersion"
    compile 'com.android.support:appcompat-v7:23.2.1'
    compile 'com.android.support:cardview-v7:23.2.1'
    compile 'com.android.support:recyclerview-v7:23.2.1'
}

apt {
    arguments {
        resourcePackageName android.defaultConfig.applicationId
        androidManifestFile variant.outputs[0].processResources.manifestFile
    }
}
```

完成后，可以使用标准的AndroidAnnotations注解开发。Application继承PApplication,Activity继承PActivity.<br />
###常用Util
**1.SharedPreference封装** <br/>
使用SharedPreference可以串联：<br />
PreferenceManager.getDefaultManager().putBoolean(KEY1,Value1).putString(KEY2,Value2).commit();
最后加Commit()方法即可保存，支持指定保存的文件<br />
**2.Toast封装**<br />
Toast封装后，支持在非UI线程直接调用：<br />
PToast.show(message);<br />
**3.Log封装**<br />
PLog类支持更多的参数类型，如Tag可以直接传入Object,会取object的类名作tag,Message支持基本类型.<br />
**其他**<br />
md5,DES,日期处理,字符串处理,图片处理,网络判断,首次运行检测等<br />