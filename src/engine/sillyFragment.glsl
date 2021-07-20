#version 330

in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
    fragColor = abs(texture(texture_sampler, outTexCoord)-0.5);
}
