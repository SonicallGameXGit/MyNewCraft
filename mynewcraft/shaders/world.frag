#version 420
#define SUN_DIRECTION normalize(vec3(-0.7, -1.0, 0.4))

in vec2 vTexcoord;
in vec3 vNormal;

uniform sampler2D colorSampler;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture2D(colorSampler, vTexcoord);
    vec3 color = textureColor.rgb;

    float brightness = dot(normalize(vNormal), -SUN_DIRECTION);
    brightness = min(brightness, 1.0);
    brightness = max(brightness, 0.0);
    brightness *= 0.6;
    brightness += 0.4;

    fragColor = vec4(color * brightness, textureColor.a);
}