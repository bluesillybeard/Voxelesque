#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 modelViewMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

int getRandom(int seed){
    seed ^= seed << 13;
    seed ^= seed >> 17;
    seed ^= seed << 5;
    return seed;
}
int getRandom(float floatSeed){
    return getRandom(int(floatSeed*16384));
}
void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelViewMatrix * vec4(position, 1.0); //calculate initial gl_Position
    vec3 newPosition = vec3(
        position.x + getRandom(gl_Position.x)/2147483648.0,
        position.y + getRandom(gl_Position.y)/2147483648.0,
        position.z + getRandom(gl_Position.z)/2147483648.0); //randomize position based on gl_Position
    gl_Position = projectionMatrix * viewMatrix * modelViewMatrix * vec4(newPosition, 1.0); //create new gl_Posotion based on randomized position

    outTexCoord = texCoord;
}
