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

اضبط أسماء النماذج كمتغيرات بيئة في Cloudflare:
- `CF_AI_MODEL`
- `GEMINI_MODEL`
- `GROQ_MODEL`
- `OPENROUTER_MODEL`

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
