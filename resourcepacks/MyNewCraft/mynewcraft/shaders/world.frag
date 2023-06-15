#version 420
#define SUN_DIRECTION vec3(0.7, -1.0, 0.3)
#define REFLECTIVITY 0.01
#define SHINE 0.8

in vec2 vTexcoord;
in vec3 vNormal;
in vec3 cameraVector;
in float fogginess;
in float vTransparency;
in float vAoLevel;

uniform sampler2D colorTexture;
uniform vec3 skyColor;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture2D(colorTexture, vTexcoord);
    fragColor = vec4(mix(textureColor.rgb * ((8.0 - pow(vAoLevel, 1.5)) / 8.0) * (max(dot(normalize(vNormal), -normalize(SUN_DIRECTION)), 0.0) * 0.6 + 0.4) + pow(max(dot(reflect(-normalize(SUN_DIRECTION), normalize(vNormal)), normalize(cameraVector)), 0.0) * REFLECTIVITY, SHINE), skyColor, fogginess), textureColor.a * (1.0 - vTransparency));
}