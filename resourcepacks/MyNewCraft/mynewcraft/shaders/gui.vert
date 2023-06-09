#version 420

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texcoord;

uniform mat4 project;

out vec2 vTexcoord;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    vTexcoord = texcoord;
}