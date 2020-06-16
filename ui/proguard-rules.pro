# Squelch all warnings, they're harmless but ProGuard
# escalates them as errors.

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable


# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# Retain service method parameters.
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留R下面的资源
-keep class **.R$* {*;}


-dontwarn sun.misc.Unsafe

# Fragment 1.2.4 allows Fragment classes to be obfuscated but
# databinding references in XML seem to not be rewritten to
# match, so we preserve the names as 1.2.3 did.
-if public class ** extends androidx.fragment.app.Fragment
-keep public class <1> {
    public <init>();
}

# eventbus
-keep class org.greenrobot.eventbus.** { *; }
-keep interface org.greenrobot.eventbus.** { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.** { *; }

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

 #保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
# webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-keepattributes JavaScriptInterface

-keepclassmembers class *{
    @android.webkit.javascriptInterface <methods>;
}

#--- For:Gson ---

-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.***
-keep class com.google.gson.stream.** { *; }


-keep public class * implements com.bumptech.glide.module.GlideModule

-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

-ignorewarnings

-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }



-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn core.**
-keep class core.**{*;}

-dontwarn lifecycle.**
-keep class lifecycle.**{*;}

-dontwarn rxjava2.**
-keep class rxjava2.**{*;}

-dontwarn com.squareup.okhttp.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }






-dontwarn sun.misc.Unsafe
# Fragment 1.2.4 allows Fragment classes to be obfuscated but
# databinding references in XML seem to not be rewritten to
# match, so we preserve the names as 1.2.3 did.
-if public class ** extends androidx.fragment.app.Fragment
-keep public class <1> {
    public <init>();
}

# Don't obfuscate
-dontobfuscate
