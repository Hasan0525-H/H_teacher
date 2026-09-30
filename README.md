# المعلم H

تطبيق أندرويد احترافي للمعلم السعودي، يعمل بأسلوب **Offline-first** وقابل للتوسع.

## المزايا المنجزة
- Kotlin + Jetpack Compose + MVVM.
- RTL عربي.
- Room + DataStore.
- إدارة المواد والصفوف والمناهج.
- PDF أوفلاين وقارئ محلي.
- بنك أسئلة ومولد اختبارات.
- PDF للأسئلة ونموذج الإجابة.
- ملف الإنجاز المهني والمرفقات وPDF.
- إدارة الطلاب.
- الحضور اليومي: حاضر / غائب / متأخر / مستأذن.
- سجل درجات لكل طالب.
- حساب متوسط الدرجات.
- تقرير PDF شامل للطلاب والحضور والدرجات.
- فهرسة المناهج إلى وحدات ودروس ونطاق صفحات ونصوص.
- عميل Cloud AI بدون مفاتيح داخل APK.
- بوابة Cloudflare متعددة المزودات مع failover وRate Limiting.
- توليد أسئلة AI من نصوص الدروس وحفظها في بنك الأسئلة المحلي.
- توقيع Debug ثابت.
- versionCode متزايد تلقائيًا في CI.

## قاعدة البيانات
- v1: subjects, grades, curricula, questions.
- v2: portfolio_items, portfolio_attachments.
- v3: students, attendance, grade_records.
- v4: curriculum_units, lessons.
- `MIGRATION_1_2` و`MIGRATION_2_3` و`MIGRATION_3_4` صريحة.
- لا يوجد `fallbackToDestructiveMigration`.

## الهوية
- App name: **المعلم H**
- Application ID: `com.hasan0525.hteacher`
- minSdk 26
- compileSdk / targetSdk 36

## المتبقي
1. نشر Cloudflare Worker وضبط مفاتيح المزودات خارج المستودع.
2. تحسينات UI واختبارات.
3. إعداد Release signing للإصدارات النهائية.
