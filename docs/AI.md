# Cloud AI Architecture

## قاعدة الأمان
لا يتم وضع مفاتيح Gemini أو Groq داخل APK.

## Android
`AiQuestionService` يرسل طلب HTTPS إلى:
`BuildConfig.AI_GATEWAY_URL/v1/generate`

كل تثبيت يحصل على UUID عشوائي محفوظ في DataStore لاستخدامه كمفتاح rate-limit.
هذا المعرف ليس وسيلة مصادقة سرية، بل إشارة إضافية للحد من الإساءة.

## Gateway
Cloudflare Worker:
- يقبل مهمة `exam_questions` فقط.
- يرفض prompt أكبر من 60,000 حرف.
- يطبق Rate Limiting.
- لا يسمح للعميل بتحديد URL خارجي.
- يجرب Gemini ثم Groq بالترتيب المعرّف في `PROVIDER_ORDER`.
- لا يعيد مفاتيح أو أخطاء upstream الحساسة.

## البيانات
المدخل إلى AI يأتي من `lessons.textContent`.
الناتج يعود كأسئلة منظمة ويُخزن في جدول `questions`.

بهذا يبقى بنك الأسئلة متاحًا أوفلاين بعد نجاح التوليد مرة واحدة.
