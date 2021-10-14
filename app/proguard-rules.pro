# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Java\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#=========================== 基本指令区 start ===========================
#使用自定义混淆字典
-obfuscationdictionary ../data/dic.txt
-classobfuscationdictionary ../data/dic.txt
-packageobfuscationdictionary ../data/dic.txt

#代码混淆的压缩比例，值在0-7之间
-optimizationpasses 5

-dontusemixedcaseclassnames		#混淆后类名都为小写

#生成原类名和混淆后的类名的映射文件mapping 类名->转化后类名的映射
-verbose

#这样将忽略剩余的警告
-ignorewarnings
#如果应用程序引入的有jar包,并且想混淆jar包里面的class
#-dontskipnonpubliclibraryclasses

#不做预校验的操作
-dontpreverify

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#不混淆内部类
-keepattributes Exceptions,InnerClasses

#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#http://stackoverflow.com/questions/5582383/problem-with-proguard-and-roboguice-with-inject-annotations
#不混淆泛型
-keepattributes Signature
#不混淆注解
-keepattributes *Annotation*

#=========================== 基本指令区 end ===========================


#=========================== 默认保留区 start ===========================
#保持 安卓四大组件、Application 不被混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.vending.licensing.ILicensingService

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持 枚举类不被混淆
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持 aidl文件不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# 保证 使用JSONObject不报错
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

#保持 资源id不被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}
#=========================== 默认保留区 end ===========================


#=========================== 自定义区 start ===========================
#混淆 实体 的类名，但不混淆字段
-keepclassmembers class com.starry.douban.model.** { *; }

#-keep public class com.starry.douban.R$*{
#   public static final int *;
#}

#不混淆JS
-keepattributes *JavascriptInterface*
#=========================== 自定义区 end ===========================


#=========================== 第三方包 start ===========================
#=========================================================
#对于引用第三方包的情况，可以采用下面方式避免打包出错：
#-dontwarn com.xx.yy.**
#-keep class com.xx.yy.** { *;}
#=========================================================
#-keep class android.support.v4.** { *;}
#-keep public class * extends android.support.v4.**{
#	public protected *;
#}

# 不混淆glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#java.lang.IllegalStateException: Unable to load publicsuffixes.gz resource from the classpath.
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}

-dontwarn okio.**
-keep class okio.** { *;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
#=========================== 第三方包 end ===========================

