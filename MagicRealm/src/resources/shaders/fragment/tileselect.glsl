
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;
uniform sampler2DArray texture;
uniform vec4 color;

in vec2 textureCoordinate;

void main() {
	if(index < 0) {
		gl_FragColor = color;
	}
	else {
		gl_FragColor = texture2DArray(texture, vec3(textureCoordinate, float(index)));
	}
}
