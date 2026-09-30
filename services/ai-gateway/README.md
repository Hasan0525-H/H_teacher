# H Teacher AI Gateway

Cloudflare Worker وسيط بين تطبيق المعلم H ومزودات الذكاء الاصطناعي.

## لماذا البوابة؟
- لا يوجد API key داخل APK.
- تغيير المزود أو النموذج لا يتطلب تحديث التطبيق.
- failover تلقائي بين المزودات.
- Rate Limiting لكل IP + installation ID.
- المهمة المسموحة حاليًا: توليد أسئلة من نصوص الدروس فقط.

## المزودات
ترتيب المحاولة الافتراضي:
1. Workers AI
2. Gemini
3. Groq
4. OpenRouter

أي مزود لا يملك model/key مضبوطًا يتم تخطيه تلقائيًا.
إذا تجاوز المزود 18 ثانية ينتقل النظام تلقائيًا إلى المزود التالي.

النماذج الافتراضية الحالية:
- Workers AI: `@cf/zai-org/glm-4.7-flash`
- Gemini: `gemini-3.8-flash`

يمكن تغيير أسماء النماذج من إعدادات Worker دون تحديث APK.

## الإعداد

```bash
cd services/ai-gateway
npm install
```

أسرار المزودات تحفظ في Cloudflare فقط:

```bash
npx wrangler secret put GEMINI_API_KEY
npx wrangler secret put GROQ_API_KEY
npx wrangler secret put OPENROUTER_API_KEY
```

Workers AI وGemini لهما أسماء افتراضية في `wrangler.jsonc`.
لـ Groq وOpenRouter اضبط فقط اسم النموذج إذا أردت تفعيلهما:
- `GROQ_MODEL`
- `OPENROUTER_MODEL`

ويمكن تغيير:
- `PROVIDER_ORDER`
- `PROVIDER_TIMEOUT_MS`
- `CF_AI_MODEL`
- `GEMINI_MODEL`
بدون أي تعديل في تطبيق Android.

ثم:

```bash
npm run check
npm run deploy
```

## ربط Android

في بناء التطبيق مرر:

```bash
AI_GATEWAY_URL=https://YOUR-WORKER.workers.dev gradle :app:assembleDebug
```

الرابط ليس سرًا. الأسرار تبقى في Worker.

## الخصوصية
الربط الحالي يرسل:
- اسم المادة.
- اسم المنهج.
- نصوص الدروس المفهرسة.
- إعدادات نوع وصعوبة وعدد الأسئلة.

لا يرسل الطلاب أو الحضور أو الدرجات أو ملف الإنجاز.
