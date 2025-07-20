uniform float iTime;
uniform float2 iResolution;
uniform float4 iColor;

float f(float3 p) {
    p.z -= iTime * 10.0;
    float a = p.z * 0.1;
    float2x2 rot = float2x2(cos(a), sin(a), -sin(a), cos(a));
    p.xy = rot * p.xy;
    return 0.1 - length(cos(p.xy) + sin(p.yz));
}

half4 main(float2 fragCoord) {
    float3 d = float3(0.5 - fragCoord / iResolution.y, 1.0);
    float3 p = float3(0.0);
    for (int i = 0; i < 32; i++) {
        p += f(p) * d;
    }
    float3 col = (sin(p) + iColor.rgb) / length(p);
    return half4(col, 1.0);
}
