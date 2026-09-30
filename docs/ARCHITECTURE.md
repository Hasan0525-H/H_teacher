# المعمارية — المعلم H

## الوضع الحالي
التطبيق Offline-first ويحتفظ بالبيانات الأساسية على الجهاز.

## قاعدة البيانات
### v1
- subjects
- grades
- curricula
- questions

### v2
- portfolio_items
- portfolio_attachments

### v3
- students
- attendance
- grade_records

المسارات:
- 1→2 عبر `MIGRATION_1_2`
- 2→3 عبر `MIGRATION_2_3`

لا توجد destructive migrations.

## الوحدات الوظيفية
- المناهج وPDF ✅
- بنك الأسئلة ومولد الاختبارات ✅
- تصدير الأسئلة والإجابة PDF ✅
- ملف الإنجاز والمرفقات ✅
- الطلاب والحضور والدرجات والتقارير ✅
- فهرسة المنهج ⏳
- Cloud AI ⏳

## التخزين
- البيانات المنظمة: Room.
- الإعدادات: DataStore.
- ملفات المناهج: `filesDir/curricula`.
- مرفقات ملف الإنجاز: `filesDir/portfolio`.
- PDF output: Storage Access Framework.

## التحديث
- Application ID ثابت.
- Debug signing ثابت للاختبارات.
- Release signing منفصل ويجب حفظه خارج المستودع.
- CI يستخدم `1000 + GITHUB_RUN_NUMBER` كـ versionCode.

## AI المستقبلي
طبقة AI ستكتب النتائج إلى بنية البيانات المحلية الحالية بدل إنشاء مسار بيانات منفصل.
بيانات الطلاب الحساسة لا تُرسل افتراضيًا إلى السحابة.

## Package
`com.hasan0525.hteacher`
