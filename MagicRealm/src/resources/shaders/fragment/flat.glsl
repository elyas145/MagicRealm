
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;
uniform sampler2DArray texture;

varying vec2 textureCoordinate;

void main() {
	vec4 color = texture2DArray(texture, vec3(textureCoordinate, float(index)));
	if(color.r < 1.) {
		color = vec4(1.);
	}
	gl_FragColor = vec4(color.gba, 1.);
}
