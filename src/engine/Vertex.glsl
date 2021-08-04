#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 modelViewMatrix; //model position, rotation, and scale
uniform mat4 viewMatrix; //camera position and rotation
uniform mat4 projectionMatrix; //projection (perspecive & FOV)

void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelViewMatrix * vec4(position, 1.0);
    outTexCoord = texCoord;
}