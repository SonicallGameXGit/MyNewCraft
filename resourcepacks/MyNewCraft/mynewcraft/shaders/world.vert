#version 420

in vec3 position;
in vec2 texcoord;
in vec3 normal;

uniform mat4 project;
uniform mat4 view;
uniform mat4 transform;

out vec2 vTexcoord;
out vec3 vNormal;

void main() {
    gl_Position = project * view * transform * vec4(position, 1.0);
    vTexcoord = texcoord;
    vNormal = (transform * vec4(normal, 0.0)).xyz;
}
