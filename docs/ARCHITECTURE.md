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
- Cloud AI gateway + Android client ✅\n- نشر البوابة وضبط الأسرار ⏳

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

## Cloud AI
التطبيق لا يحتوي API keys. يحتوي فقط عنوان البوابة عبر
`BuildConfig.AI_GATEWAY_URL`.

التدفق:
```
Indexed lesson text
  ↓
Android AiQuestionService
  ↓ HTTPS
Cloudflare Worker
  ↓
Workers AI / Gemini / Groq / OpenRouter
  ↓
Question Bank (Room)
```

البوابة تطبق Rate Limiting وprovider failover، ولا تسمح للعميل
باختيار endpoint خارجي أو تمرير API key.

بيانات الطلاب والحضور والدرجات ليست جزءًا من طلبات AI الحالية.

## Package
`com.hasan0525.hteacher`
