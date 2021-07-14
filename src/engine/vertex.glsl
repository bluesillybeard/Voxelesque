#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 modelViewMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
//these are seperate becase 1: i'm lazy, and 2: it means less work for the CPU
//I don't think it really matters though, it's such a small amount of data and processing
//that it doesn't matter all that much.

void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelViewMatrix * vec4(position, 1.0);
    outTexCoord = texCoord; //OpenGL automatically interpolating this for the fragment shader is so convinient.
}