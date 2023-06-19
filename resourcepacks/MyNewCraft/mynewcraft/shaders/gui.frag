#version 420

in vec2 vTexcoord;

uniform sampler2D textureSampler;

out vec4 fragColor;

void main() {
    fragColor = texture2D(textureSampler, vTexcoord);
}