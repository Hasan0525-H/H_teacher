# المعمارية — المعلم H

## المبادئ
1. Offline-first.
2. واجهة Compose فقط.
3. الحالة عبر ViewModel.
4. Room هو المصدر الأساسي لبيانات المجال.
5. DataStore للإعدادات الصغيرة.
6. ملفات PDF تحفظ في مساحة التطبيق الداخلية.
7. لا توجد destructive migrations.
8. PDF export يستخدم Android APIs المدمجة لتقليل الحجم.
9. الذكاء الاصطناعي السحابي سيبقى خلف abstraction مستقل.
10. الاستقرار مقدم على SDK Preview.

## الطبقات
```
UI (Compose)
  ↓
ViewModel
  ↓
Repository / Engines
  ↓
Room / DataStore / Internal Files / PdfDocument
```

## قاعدة البيانات v1
- subjects
- grades
- curricula
- questions

## المناهج
```
Storage Access Framework
  ↓
CurriculumFileStore
  ↓
Internal app files /curricula
  ↓
Room metadata
  ↓
Android PdfRenderer
```

## مولد الاختبارات
```
Question Bank (Room)
  ↓
Local Exam Selection Engine
  ↓
GeneratedExam
  ├── UI Preview
  ├── Questions PDF
  └── Answers PDF
```

توليد الأسئلة بالذكاء الاصطناعي سيكتب إلى نفس Question Bank، لذلك إضافة AI لاحقًا لا تتطلب إعادة تصميم مولد الاختبارات.

## المراحل
- Phase 1: App shell, identity, RTL, UI foundation. ✅
- Phase 2: Room + DataStore + repositories. ✅
- Phase 3: Curriculum/PDF management. ✅
- Phase 4: Exam generator + PDF export. ✅ (local question-bank engine)
- Phase 5: Professional portfolio.
- Phase 6: Students, attendance, grades, reports.
- Cloud AI: provider abstraction + protected server gateway.

## Package
`com.hasan0525.hteacher`

## Android baseline
- minSdk 26
- compileSdk 36
- targetSdk 36
