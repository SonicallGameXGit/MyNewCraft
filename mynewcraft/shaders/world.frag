#version 420
#define SUN_DIRECTION vec3(0.7, -1.0, 0.3)

in vec2 vTexcoord;
in vec3 vNormal;

uniform sampler2D colorTexture;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture2D(colorTexture, vTexcoord);

    float brightness = dot(normalize(vNormal), -normalize(SUN_DIRECTION));
    brightness = max(brightness, 0.0);
    brightness *= 0.6;
    brightness += 0.4;

    fragColor = vec4(textureColor.rgb * brightness, textureColor.a);
}