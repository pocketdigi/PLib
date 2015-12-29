PLib
=================
PLib是一个Android应用开发库，集成了流行的开源库，整合一些Util,可以帮助开发者更快开发应用.仅支持Android Studio,master分支是之前Eclipse的版本，即将删除。


整合开源库：
-------------
1.AndroidAnonations 3.3.2 <br />
2.OrmLite 4.48<br />
3.gson 2.3<br />
4.eventbus 2.2.1<br />
5.fresco 0.8.1 <br />

功能:
-------------
1.SharedPreference封装<br />
2.Toast封装<br />
3.Log封装<br />
4.其他一些常用Utils(md5,DES,日期处理,字符串处理,图片处理,网络判断,首次运行检测等)<br />


下步工作：
---------
准备将目前几个应用中使用的单Activity开发架构整合进来，一个应用只有一个Activity,界面基于Fragment实现.

使用方法：
-----
#####1、切到项目(仅支持Android Studio项目)根目录下,添加子模块<br />
git submodule add https://github.com/pocketdigi/PLib.git plib <br />
#####2、修改项目的build.gradle

```java
buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        //添加android-apt插件
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
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
def AAVersion = '3.3.2'
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile project(':plib')
    apt "org.androidannotations:androidannotations:$AAVersion"
    compile "org.androidannotations:androidannotations-api:$AAVersion"
    compile 'com.android.support:support-v4:21.0.2'
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