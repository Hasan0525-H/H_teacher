# المعمارية — المعلم H

## المبادئ
1. Offline-first.
2. كل ميزة مستقلة وقابلة للفصل لاحقًا إلى Gradle module.
3. واجهة Compose فقط.
4. الحالة عبر ViewModel.
5. البيانات المحلية هي المصدر الأساسي للحقيقة.
6. الذكاء الاصطناعي السحابي لاحقًا خلف abstraction ولا يرتبط بمزود واحد.
7. الاستقرار مقدم على الاعتماد على SDK Preview.

## المراحل
- Phase 1: App shell, identity, RTL, UI foundation.
- Phase 2: Room + DataStore + repositories.
- Phase 3: Curriculum/PDF management.
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
