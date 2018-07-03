# Retrofit 1.X

-keep class okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface okhttp.** { *; }

-dontwarn okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn javax.annotation.concurrent.GuardedBy
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
