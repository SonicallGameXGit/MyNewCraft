#version 420

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texcoord;
layout(location = 2) in vec3 normal;
layout(location = 3) in float transparency;
layout(location = 4) in float aoLevel;

uniform mat4 project;
uniform mat4 view;
uniform mat4 transform;
uniform float time;
uniform float viewDistance;

out vec2 vTexcoord;
out vec3 vNormal;
out vec3 cameraVector;
out float fogginess;
out float vTransparency;
out float vAoLevel;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}
float noise(vec2 p){
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = pow(u, vec2(2.0)) * (3.0 - 2.0 * u);

    float res = mix(
        mix(rand(ip), rand(ip + vec2(1.0, 0.0)), u.x),
        mix(rand(ip + vec2(0.0, 1.0)), rand(ip + vec2(1.0, 1.0)), u.x), u.y);
    return pow(res, 2.0);
}

void main() {
    vec4 transformPosition = transform * vec4(position, 1.0);
    vec4 viewPosition = view * transformPosition;

    gl_Position = project * viewPosition;
    vTexcoord = texcoord;
    vNormal = (transform * vec4(normal, 0.0)).xyz;

    float fogDistance = length(viewPosition.xyz) + noise(vec2(transformPosition.xy * 4.0 - transformPosition.z) / 64.0 + vec2(0.0, time / 3.0)) * 9.0 + noise(vec2(transformPosition.xy * 4.0 - transformPosition.z) + vec2(0.0, time + 0.34 * 4.15)) * 1.5;
    fogginess = exp(-pow((fogDistance * viewDistance), 12.0));
    fogginess = 1.0 - clamp(fogginess, 0.0, 1.0);
    cameraVector = (inverse(view) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - transformPosition.xyz;
    vTransparency = transparency;
    vAoLevel = aoLevel;
}