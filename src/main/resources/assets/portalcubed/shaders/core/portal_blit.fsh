#version 150

uniform sampler2D Sampler0;

uniform vec2 ScreenSize;

out vec4 fragColor;

void main(){
    fragColor = texture(Sampler0, gl_FragCoord.xy / ScreenSize);
}
