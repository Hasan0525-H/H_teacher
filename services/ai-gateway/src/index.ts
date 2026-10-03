interface RateLimitBinding {
  limit(input: { key: string }): Promise<{ success: boolean }>;
}

interface Env {
  AI_RATE_LIMITER: RateLimitBinding;

  PROVIDER_ORDER?: string;
  PROVIDER_TIMEOUT_MS?: string;

  GEMINI_API_KEY?: string;
  GEMINI_MODEL?: string;

  GROQ_API_KEY?: string;
  GROQ_MODEL?: string;

}

type GenerateBody = {
  task?: string;
  prompt?: string;
  maxOutputTokens?: number;
};

type ProviderResult = {
  text: string;
  provider: string;
  model: string;
};

const jsonHeaders = {
  "content-type": "application/json; charset=utf-8",
  "cache-control": "no-store"
};

export default {
  async fetch(
    request: Request,
    env: Env
  ): Promise<Response> {
    const url = new URL(request.url);

    if (request.method === "GET" && url.pathname === "/health") {
      return json({
        ok: true,
        service: "hteacher-ai-gateway"
      });
    }

    if (
      request.method !== "POST" ||
      url.pathname !== "/v1/generate"
    ) {
      return json({ error: "not_found" }, 404);
    }

    const installId = (
      request.headers.get("X-HTeacher-Install") || ""
    ).trim();

    if (!/^[a-zA-Z0-9-]{16,80}$/.test(installId)) {
      return json({ error: "invalid_client" }, 400);
    }

    const ip = request.headers.get("CF-Connecting-IP") || "unknown";
    const rate = await env.AI_RATE_LIMITER.limit({
      key: ip + ":" + installId
    });

    if (!rate.success) {
      return json(
        { error: "rate_limited" },
        429
      );
    }

    const contentLength = Number(
      request.headers.get("content-length") || "0"
    );

    if (contentLength > 80_000) {
      return json({ error: "request_too_large" }, 413);
    }

    let body: GenerateBody;

    try {
      body = await request.json<GenerateBody>();
    } catch {
      return json({ error: "invalid_json" }, 400);
    }

    if (body.task !== "exam_questions") {
      return json({ error: "unsupported_task" }, 400);
    }

    const prompt = (body.prompt || "").trim();

    if (!prompt || prompt.length > 60_000) {
      return json({ error: "invalid_prompt" }, 400);
    }

    const maxOutputTokens = clamp(
      body.maxOutputTokens || 4096,
      512,
      4096
    );

    const providers = (
      env.PROVIDER_ORDER ||
      "gemini,groq"
    )
      .split(",")
      .map(value => value.trim())
      .filter(Boolean);

    const failures: string[] = [];
    const providerTimeoutMs = clamp(
      Number(env.PROVIDER_TIMEOUT_MS || "18000"),
      5_000,
      45_000
    );

    for (const provider of providers) {
      try {
        const result = await withTimeout(
          callProvider(
            provider,
            prompt,
            maxOutputTokens,
            env
          ),
          providerTimeoutMs
        );

        if (result) {
          return json({
            ok: true,
            text: result.text,
            provider: result.provider,
            model: result.model
          });
        }
      } catch (error) {
        failures.push(
          provider + ":" +
            safeErrorName(error)
        );
      }
    }

    return json(
      {
        error: "no_provider_available",
        attempts: failures
      },
      503
    );
  }
};

async function callProvider(
  provider: string,
  prompt: string,
  maxOutputTokens: number,
  env: Env
): Promise<ProviderResult | null> {
  switch (provider) {
    case "gemini":
      return callGemini(
        prompt,
        maxOutputTokens,
        env
      );
    case "groq":
      return callOpenAiCompatible({
        provider: "groq",
        endpoint:
          "https://api.groq.com/openai/v1/chat/completions",
        apiKey: env.GROQ_API_KEY,
        model: env.GROQ_MODEL,
        prompt,
        maxOutputTokens
      });
    default:
      return null;
  }
}

async function callGemini(
  prompt: string,
  maxOutputTokens: number,
  env: Env
): Promise<ProviderResult | null> {
  const apiKey = env.GEMINI_API_KEY;
  const model = env.GEMINI_MODEL;

  if (!apiKey || !model) return null;

  const endpoint =
    "https://generativelanguage.googleapis.com/v1beta/models/" +
    encodeURIComponent(model) +
    ":generateContent?key=" +
    encodeURIComponent(apiKey);

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      "content-type": "application/json"
    },
    body: JSON.stringify({
      contents: [
        {
          role: "user",
          parts: [{ text: prompt }]
        }
      ],
      generationConfig: {
        temperature: 0.3,
        maxOutputTokens,
        responseMimeType: "application/json"
      }
    })
  });

  const payload = await readJson(response);

  if (!response.ok) {
    throw new Error("upstream_" + response.status);
  }

  const text = (
    payload?.candidates?.[0]?.content?.parts || []
  )
    .map((part: { text?: string }) => part?.text || "")
    .join("")
    .trim();

  if (!text) {
    throw new Error("empty_response");
  }

  return {
    text,
    provider: "gemini",
    model
  };
}

async function callOpenAiCompatible(input: {
  provider: string;
  endpoint: string;
  apiKey?: string;
  model?: string;
  prompt: string;
  maxOutputTokens: number;
}): Promise<ProviderResult | null> {
  if (!input.apiKey || !input.model) return null;

  const response = await fetch(input.endpoint, {
    method: "POST",
    headers: {
      "content-type": "application/json",
      "authorization": "Bearer " + input.apiKey
    },
    body: JSON.stringify({
      model: input.model,
      messages: [
        {
          role: "user",
          content: input.prompt
        }
      ],
      temperature: 0.3,
      max_tokens: input.maxOutputTokens
    })
  });

  const payload = await readJson(response);

  if (!response.ok) {
    throw new Error("upstream_" + response.status);
  }

  const content =
    payload?.choices?.[0]?.message?.content;

  const text = typeof content === "string"
    ? content.trim()
    : "";

  if (!text) {
    throw new Error("empty_response");
  }

  return {
    text,
    provider: input.provider,
    model: input.model
  };
}

async function readJson(
  response: Response
): Promise<any> {
  const text = await response.text();

  try {
    return JSON.parse(text);
  } catch {
    throw new Error("invalid_upstream_json");
  }
}

function json(
  body: unknown,
  status = 200
): Response {
  return new Response(
    JSON.stringify(body),
    {
      status,
      headers: jsonHeaders
    }
  );
}

function clamp(
  value: number,
  min: number,
  max: number
): number {
  if (!Number.isFinite(value)) return min;
  return Math.max(
    min,
    Math.min(max, Math.floor(value))
  );
}

async function withTimeout<T>(
  promise: Promise<T>,
  timeoutMs: number
): Promise<T> {
  let timer: ReturnType<typeof setTimeout> | undefined;

  const timeout = new Promise<never>((_, reject) => {
    timer = setTimeout(
      () => reject(new Error("provider_timeout")),
      timeoutMs
    );
  });

  try {
    return await Promise.race([promise, timeout]);
  } finally {
    if (timer !== undefined) {
      clearTimeout(timer);
    }
  }
}

function safeErrorName(error: unknown): string {
  if (error instanceof Error) {
    return error.message
      .replace(/[^a-zA-Z0-9_-]/g, "_")
      .slice(0, 60);
  }

  return "unknown";
}
