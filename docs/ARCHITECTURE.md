# المعمارية — المعلم H

## المبادئ
1. Offline-first.
2. واجهة Compose فقط.
3. الحالة عبر ViewModel.
4. Room هو المصدر الأساسي لبيانات المجال.
5. DataStore للإعدادات الصغيرة.
6. الملفات تحفظ في مساحة التطبيق الداخلية.
7. لا توجد destructive migrations.
8. PDF uses Android APIs المدمجة لتقليل الحجم.
9. الذكاء الاصطناعي السحابي خلف abstraction مستقل.
10. الاستقرار مقدم على SDK Preview.

## طبقات التطبيق
```
UI (Compose)
  ↓
ViewModel
  ↓
Repository / Engines
  ↓
Room / DataStore / Internal Files / PdfDocument
```

## قاعدة البيانات
### v1
- subjects
- grades
- curricula
- questions

### v2
- portfolio_items
- portfolio_attachments

الانتقال `1 → 2` يتم عبر `MIGRATION_1_2` ولا يحذف الجداول السابقة.

## ملف الإنجاز
```
Portfolio UI
  ↓
PortfolioViewModel
  ├── AppSettingsRepository
  ├── OfflineTeacherRepository
  ├── PortfolioFileStore
  └── PdfPortfolioExporter
```

المرفقات تحفظ في `filesDir/portfolio` بينما Room يحفظ البيانات الوصفية والمسار فقط.

## التوقيع
- Debug: مفتاح تطوير ثابت داخل المستودع لاستخدام الاختبارات فقط.
- Release: مفتاح إنتاج دائم يجب أن يبقى خارج المستودع.
- CI: `versionCode = 1000 + GITHUB_RUN_NUMBER`.

## المراحل
- Phase 1: App shell, identity, RTL. ✅
- Phase 2: Room + DataStore. ✅
- Phase 3: Curriculum/PDF management. ✅
- Phase 4: Exam generator + PDF export. ✅
- Phase 5: Professional portfolio. ✅
- Phase 6: Students, attendance, grades, reports.
- Cloud AI: provider abstraction + protected server gateway.

## Package
`com.hasan0525.hteacher`

## Android baseline
- minSdk 26
- compileSdk 36
- targetSdk 36
