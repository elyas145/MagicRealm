
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;
uniform sampler2DArray texture;

varying vec2 textureCoordinate;

void main() {
	gl_FragColor = vec4(texture2DArray(texture, vec3(textureCoordinate, float(index))).gba, 1.);
}
