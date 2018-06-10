# Android DataBinding ProGuard rules.

-keep class android.databinding.** { *; }

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes *Annotation*
-keepattributes javax.xml.bind.annotation.*
-keepattributes javax.annotation.processing.*

-keepclassmembers class * extends java.lang.Enum { *; }

-keepclasseswithmembernames class android.**
-keepclasseswithmembernames interface android.**

-dontobfuscate

-libraryjars  <java.home>/lib/rt.jar
-libraryjars  <java.home>/lib/jce.jar

-dontwarn