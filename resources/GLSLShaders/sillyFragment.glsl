#version 330

in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform float timeSeconds;
void main()
{
    fragColor = abs(texture(texture_sampler, outTexCoord)-(sin(timeSeconds*2)+0.5));
}
