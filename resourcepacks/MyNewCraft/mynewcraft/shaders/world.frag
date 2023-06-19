#version 420
#define REFLECTIVITY 0.3
#define SHINE 2.0
#define SUN_COLOR vec3(1.0, 0.9, 0.6)

in vec2 vTexcoord;
in vec3 cameraVector;
in float fogginess;
in float vTransparency;
in float vAoLevel;
in vec3 sunDirection;

uniform sampler2D colorTexture;
uniform sampler2D normalTexture;
uniform vec3 skyColor;

out vec4 fragColor;

float rand(vec2 uv) {
    return fract(sin(dot(uv, vec2(12.9898, 4.1414))) * 43758.5453);
}
float noise(vec2 uv) {
    vec2 ip = floor(uv);
    vec2 u = fract(uv);
    u = pow(u, vec2(2.0)) * (3.0 - 2.0 * u);

    float res = mix(
        mix(rand(ip), rand(ip + vec2(1.0, 0.0)), u.x),
        mix(rand(ip + vec2(0.0, 1.0)), rand(ip + vec2(1.0, 1.0)), u.x), u.y);
    return pow(res, 2.0);
}

void main() {
    vec4 textureColor = texture2D(colorTexture, vTexcoord);
    vec3 normal = normalize(texture2D(normalTexture, vTexcoord).rgb * 2.0 - vec3(1.0));

    float ambientOcclusion = (7.0 - pow(vAoLevel, 1.25)) / 7.0;
    float diffuse = dot(normal, sunDirection);
    diffuse = clamp(diffuse * 1.3, 0.6, 1.0);
    float specular = pow(max(dot(reflect(-sunDirection, normal), normalize(cameraVector)), 0.0), SHINE) * REFLECTIVITY;

    vec3 color = textureColor.rgb * ambientOcclusion * (mix(SUN_COLOR * 1.2, pow(skyColor, vec3(0.7)) + vec3(0.2), 0.4) * diffuse + specular);

    fragColor = vec4(mix(color, skyColor, fogginess), textureColor.a * (1.0 - vTransparency));
}