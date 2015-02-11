
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;

uniform sampler2DArray texture;

void main() {
	gl_FragColor = vec4(texture2DArray(texture, vec3(gl_TexCoord[0].xy, float(index))).gba, 1.);
}
