# المعمارية — المعلم H

## المبادئ
1. Offline-first.
2. كل ميزة مستقلة وقابلة للفصل لاحقًا إلى Gradle module.
3. واجهة Compose فقط.
4. الحالة عبر ViewModel.
5. Room هو المصدر الأساسي لبيانات المجال.
6. DataStore مخصص للإعدادات الصغيرة فقط.
7. ملفات PDF تحفظ خارج Room في مساحة التطبيق الداخلية.
8. لا توجد destructive migrations لبيانات المستخدم.
9. الذكاء الاصطناعي السحابي لاحقًا خلف abstraction ولا يرتبط بمزود واحد.
10. الاستقرار مقدم على الاعتماد على SDK Preview.

## الطبقات
```
UI (Compose)
  ↓
ViewModel
  ↓
Repository
  ↓
Room / DataStore / Internal Files
```

## قاعدة البيانات v1
- subjects
- grades
- curricula
- questions

أي تغيير مستقبلي في schema يجب أن يرفع رقم الإصدار ويضيف Migration صريحًا.

## ملفات المناهج
التدفق الحالي:
```
Storage Access Framework
  ↓
CurriculumFileStore
  ↓
Internal app files /curricula
  ↓
Room curriculum metadata
  ↓
Android PdfRenderer
```

لا نعتمد على URI خارجي لبقاء المنهج؛ يتم إنشاء نسخة داخل التطبيق.

## Dependency Injection
في المرحلة الحالية نستخدم `AppContainer` يدويًا بدل Hilt لتقليل الحجم والتعقيد.

## المراحل
- Phase 1: App shell, identity, RTL, UI foundation. ✅
- Phase 2: Room + DataStore + repositories. ✅
- Phase 3: Curriculum/PDF management. ✅
- Phase 4: Exam generator + PDF export.
- Phase 5: Professional portfolio.
- Phase 6: Students, attendance, grades, reports.

## Package
`com.hasan0525.hteacher`

## Android baseline
- minSdk 26
- compileSdk 36
- targetSdk 36

يجب عدم تغيير Application ID بعد أول إصدار مستخدم.
