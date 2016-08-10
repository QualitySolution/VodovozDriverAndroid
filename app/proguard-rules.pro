-dontshrink
-keepdirectories
-keeppackagenames javax.mail.**
-keeppackagenames javax.activation.**
-keeppackagenames com.sun.mail.**
-keeppackagenames myjava.**
-keeppackagenames org.apache.harmony.**
-keeppackagenames mailcap.**
-keeppackagenames mimetypes.**
-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}
-keep class javax.mail.internet.** {*;}

-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}

-keep class javax.ws.rs.** { *; }

-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}

-dontwarn com.fasterxml.jackson.**
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

-ignorewarnings