PLib
=================
PLib是一个Android应用开发库，集成了流行的开源库，整合一些Util,可以帮助开发者更快开发应用.仅支持Android Studio。v1.6 开始移除Volley,网络请求使用OKHttp封装，简单高效。

整合开源库：
-------------
1.AndroidAnonations <br />
2.gson <br />
3.eventbus<br />
4.fresco<br />
5.OKHttp3 <br />

功能:
-------------
1.OKHttp3封装，可以很容易用一两行代码实现post,get请求，上传下载文件 <br />
2.SharedPreference封装,一行代码实现保存读取配置<br />
3.Toast封装<br />
4.Log封装<br />
5.其他一些常用Utils(md5,DES,日期处理,字符串处理,图片处理,网络判断,首次运行检测,版本号比较等)<br />
6.CarouselViewPager 轮播幻灯片<br />
7.BoldTextView 加粗的TextView
...

使用方法有两种:
--------

### 一、下载Demo模板项目：

[参考Demo项目，导入Android Studio,Rebuild Project,改包名，直接使用。](http://git.oschina.net/pocketdigi/PLibDemo)
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

完成后，可以使用标准的AndroidAnnotations注解开发。Application继承PApplication,Activity继承PFragmentActivity.<br />

###网络请求
####v1.6开始不再使用volley,而是通过封装OKHttp实现网络请求.

**GET:**
```java
PRequest<String> request=new PRequest<>("http://git.oschina.net/pocketdigi/PLib/raw/master/README.md", listener,String.class);
PHttp.getInstance().addRequest(request);
```
listener 是
```java
PResponseListener<String>
```
的实例,如果PResponseListener的泛型是String,则直接不处理返回字符串，如果是其他类型，则通过GSON转换成对象。

**POST:**
```java
    public static void post(PResponseListener<String> listener) {
        PRequest<String> request=new PRequest<>(PRequest.POST, API_PREFIX+"add", listener,String.class);
        request.addParam("p1","value1");
        request.addParam("p2","value2");
        PHttp.getInstance().addRequest(request);
    }
```
**POST Json**
```java
   public static void postObject(Person person,PResponseListener<String> listener) {
        PRequest<String> request=new PRequest<>(PRequest.POST,  API_PREFIX+"add", listener,String.class);
        request.setPostObject(person);
        PHttp.getInstance().addRequest(request);
    }
```
通过Gson将对象转成json放在request body里发送
**上传**
```java
    public static PUploadRequest upload(String filePath,UploadListener<String> listener) {
        PUploadRequest<String> request=new PUploadRequest<>(API_PREFIX+"upload","file",filePath, listener,String.class);
        PHttp.getInstance().addRequest(request);
        return request;
    }
```
**下载**
```java
    public static PDownFileRequest downloadFile(String url, String savePath, DownProgressListener listener) {
        PDownFileRequest pDownFileRequest = new PDownFileRequest(url, savePath,listener);
        PHttp.getInstance().addRequest(pDownFileRequest);
        return pDownFileRequest;
    }
```


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