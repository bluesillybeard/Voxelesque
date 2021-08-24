#version 330

in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform float timeSeconds;
void main()
{
    if(color.a < 0.1){
        discard; //I am aware that this is a lame solution for transparency, but honestly I think it's no big deal.
    }
    fragColor = abs(texture(texture_sampler, outTexCoord)-(sin(timeSeconds*2)+0.5));
}
