#version 330
#extension GL_ARB_separate_shader_objects : require

// Vivid colour grading pass. All values come from the VividConfig block, which the mod
// rewrites every frame from your settings (see PostPassMixin).

uniform sampler2D InSampler;

layout(location = 0) in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform VividConfig {
    vec4 Grade;     // saturation, vibrance, contrast, brightness
    vec4 Effects;   // vignette, bloom, sharpen, bloom threshold
    vec4 Dynamic;   // hurt flash, underwater wobble, time (seconds), unused
    vec4 Mood;      // colour multiplier rgb, strength
    vec4 WaterTint; // underwater colour rgb, strength
};

layout(location = 0) out vec4 fragColor;

const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

vec3 sampleIn(vec2 uv) {
    return texture(InSampler, clamp(uv, vec2(0.0005), vec2(0.9995))).rgb;
}

void main() {
    vec2 texel = 1.0 / InSize;
    vec2 uv = texCoord;
    float time = Dynamic.z;

    // ---- underwater wobble ----
    if (Dynamic.y > 0.0) {
        float w = Dynamic.y * 0.0035;
        uv += vec2(sin(uv.y * 28.0 + time * 1.9), cos(uv.x * 22.0 + time * 1.6)) * w;
    }

    vec3 col = sampleIn(uv);

    // ---- sharpen (unsharp mask) ----
    if (Effects.z > 0.0) {
        vec3 blur = (sampleIn(uv + vec2(texel.x, 0.0)) + sampleIn(uv - vec2(texel.x, 0.0))
                   + sampleIn(uv + vec2(0.0, texel.y)) + sampleIn(uv - vec2(0.0, texel.y))) * 0.25;
        col += (col - blur) * Effects.z * 1.5;
        col = max(col, vec3(0.0));
    }

    // ---- bloom: soft glow around bright things (two rings of 12 taps) ----
    if (Effects.y > 0.0) {
        float threshold = Effects.w;
        vec3 glow = vec3(0.0);
        float radiusPx = max(InSize.y / 180.0, 2.0);
        for (int i = 0; i < 12; i++) {
            float a = float(i) * 0.5235988 + 0.26;
            vec2 dir = vec2(cos(a), sin(a)) * texel * radiusPx;
            vec3 s1 = sampleIn(uv + dir * 2.0);
            vec3 s2 = sampleIn(uv + dir * 5.0);
            glow += max(s1 - threshold, vec3(0.0)) * 1.0;
            glow += max(s2 - threshold, vec3(0.0)) * 0.6;
        }
        glow /= (12.0 * 1.6) * max(1.0 - threshold, 0.05);
        col += glow * Effects.y;
    }

    // ---- brightness & contrast ----
    col *= Grade.w;
    col = (col - 0.5) * Grade.z + 0.5;
    col = max(col, vec3(0.0));

    // ---- saturation ----
    float luma = dot(col, LUMA);
    col = mix(vec3(luma), col, Grade.x);

    // ---- vibrance: boosts dull colours more than already-colourful ones ----
    float mx = max(col.r, max(col.g, col.b));
    float mn = min(col.r, min(col.g, col.b));
    float sat = clamp(mx - mn, 0.0, 1.0);
    luma = dot(col, LUMA);
    col = mix(vec3(luma), col, 1.0 + Grade.y * (1.0 - sat));

    // ---- dimension / weather mood ----
    col = mix(col, col * Mood.rgb, Mood.a);

    // ---- underwater tint ----
    col = mix(col, col * 0.55 + WaterTint.rgb * dot(col, LUMA) * 0.9, WaterTint.a);

    // ---- vignette & hurt flash ----
    vec2 c = texCoord - 0.5;
    float edge = length(c * vec2(OutSize.x / OutSize.y, 1.0)) / 0.85;
    col *= 1.0 - Effects.x * smoothstep(0.45, 1.25, edge);
    col = mix(col, vec3(0.75, 0.02, 0.02), Dynamic.x * smoothstep(0.35, 1.15, edge) * 0.85);

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}
