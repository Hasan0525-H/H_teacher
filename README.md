# المعلم H

تطبيق أندرويد احترافي للمعلم السعودي، يعمل بأسلوب **Offline-first** وقابل للتوسع.

## المرحلة الحالية
**v0.1.0 — تأسيس التطبيق + طبقة التخزين المحلية**

تم تجهيز:
- Kotlin عبر Built-in Kotlin في AGP 9.
- Jetpack Compose + Material 3.
- MVVM foundation.
- RTL عربي.
- هوية أولية وأيقونة خاصة.
- Light/Dark theme.
- Room Database.
- DataStore Preferences.
- Repository + AppContainer خفيفان بدون إطار DI ثقيل.
- GitHub Actions لبناء Debug APK تلقائيًا.

## الهوية الثابتة
- App name: **المعلم H**
- Application ID: `com.hasan0525.hteacher`
- Min SDK: 26
- Target / Compile SDK: 36

## التخزين المحلي
قاعدة Room الحالية تبدأ بالإصدار `1` وتغطي:
- المواد.
- الصفوف.
- المناهج.
- بنك الأسئلة.

DataStore مخصص للإعدادات الخفيفة مثل:
- اسم المعلم.
- المدرسة.
- وضع العرض.

لا يوجد `fallbackToDestructiveMigration`، حتى لا يتم حذف بيانات المستخدم تلقائيًا عند تغييرات قاعدة البيانات. أي رفع لإصدار القاعدة يجب أن يصاحبه Migration صريح.

## القادم
1. واجهة إدارة المواد والصفوف.
2. استيراد ملفات PDF عبر Storage Access Framework.
3. حفظ بيانات المنهج وربط الملف محليًا.
4. قارئ PDF محلي خفيف.
