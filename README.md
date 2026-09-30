# المعلم H

تطبيق أندرويد احترافي للمعلم السعودي، يعمل بأسلوب **Offline-first** وقابل للتوسع.

## المرحلة الحالية
**v0.1.0 — الأساس + التخزين المحلي + إدارة المناهج**

تم تجهيز:
- Kotlin + Jetpack Compose + Material 3.
- MVVM foundation.
- RTL عربي.
- هوية أولية وأيقونة خاصة.
- Light/Dark theme.
- Room Database.
- DataStore Preferences.
- Repository + AppContainer خفيفان بدون إطار DI ثقيل.
- إدارة المواد والصفوف: إضافة / تعديل / حذف.
- اختيار المادة والصف.
- استيراد ملفات PDF عبر Storage Access Framework.
- نسخ PDF إلى مساحة التطبيق الداخلية للعمل أوفلاين.
- ربط المنهج بالمادة والصف في Room.
- تعديل اسم المنهج وحذفه.
- قارئ PDF أوفلاين باستخدام Android PdfRenderer بدون مكتبة PDF ثقيلة.
- GitHub Actions لبناء Debug APK تلقائيًا.

## الهوية الثابتة
- App name: **المعلم H**
- Application ID: `com.hasan0525.hteacher`
- Min SDK: 26
- Target / Compile SDK: 36

## حماية البيانات
قاعدة Room تبدأ بالإصدار `1`.
لا يوجد `fallbackToDestructiveMigration`.
أي تغيير في schema يجب أن يرفع رقم الإصدار ويضيف Migration صريحًا.

ملفات المناهج لا تُخزن داخل Room. تحفظ كملفات PDF مستقلة داخل مساحة التطبيق، وRoom يحتفظ بالمسار والبيانات الوصفية فقط.

## القادم
1. فهرسة محتوى المناهج محليًا.
2. تقسيم المنهج إلى وحدات ودروس.
3. بناء بنك الأسئلة.
4. مولد الاختبارات.
5. تصدير الاختبار ونموذج الإجابة إلى PDF.
