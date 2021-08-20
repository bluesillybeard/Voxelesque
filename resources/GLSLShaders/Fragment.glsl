#version 330

in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform float timeSeconds; //to avoid annoying errors

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
}
