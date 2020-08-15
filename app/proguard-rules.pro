#-------------------------------------------基本不用动区域--------------------------------------------
-dontwarn
-keepattributes Signature,SourceFile,LineNumberTable
-keepattributes *Annotation*
-keeppackagenames
-ignorewarnings
-dontwarn android.support.v4.**,**CompatHoneycomb,com.tenpay.android.**
-optimizations !class/unboxing/enum,!code/simplification/arithmetic