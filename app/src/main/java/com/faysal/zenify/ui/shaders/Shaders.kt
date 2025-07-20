package com.faysal.zenify.ui.shaders

import org.intellij.lang.annotations.Language


@Language("AGSL")
val MUSIC_SHADER = """
uniform float2 size;
uniform float time;
layout(color) uniform half4 color;
layout(color) uniform half4 color2;

float f(float3 p) {
    p.z -= time * 5.0;
    float a = p.z * 0.1;
    p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
    return 0.1 - length(cos(p.xy) + sin(p.yz));
}

half4 main(float2 fragcoord) {
    float3 d = 0.5 - float3(fragcoord, 0.0) / size.y;
    float3 p = float3(0.0);

    for (int i = 0; i < 32; i++) {
        p += f(p) * d;
    }

    float intensity = clamp(length(p) * 0.2, 0.0, 1.0); // Stronger blend control

    // Mix the two colors based on intensity of the fractal result
    float3 blendedColor = mix(color.rgb, color2.rgb, intensity);

    return half4(blendedColor, 1.0);
}
"""

@Language("AGSL")
val FRACTAL_SHADER_SRC = """
uniform float2 size;
uniform float time;
layout(color) uniform half4 color;

float f(float3 p) {
    p.z -= time * 5.0;
    float a = p.z * 0.1;
    float cosA = cos(a);
    float sinA = sin(a);
    float2 rotatedXY = float2(
        p.x * cosA + p.y * sinA,
        -p.x * sinA + p.y * cosA
    );
    p.xy = rotatedXY;
    return 0.1 - length(cos(p.xy) + sin(p.yz));
}

half4 main(float2 fragcoord) {
    float3 d = float3(0.5 - fragcoord.xy / size.y, 1.0);
    float3 p = float3(0.0);

    for (int i = 0; i < 32; i++) {
        p += f(p) * d;
    }

    float3 fractalColor = (sin(p) + float3(2.0, 5.0, 12.0)) / length(p);
    fractalColor *= color.rgb;

    return half4(fractalColor, 1.0);
}
""".trimIndent()