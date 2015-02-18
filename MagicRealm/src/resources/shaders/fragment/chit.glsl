
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;
uniform sampler2DArray texture;
uniform vec4 ambientColour;

in vec3 position;
in vec3 normal;
in vec2 textureCoordinate;

void main() {
	float scale = dot(position, normal) / length(position) / length(normal);
	//scale = pow(scale, 3.);
	vec4 color = texture2DArray(texture, vec3(textureCoordinate, float(index)));
	if(color.r < .5) {
		color = vec4(1.);
	}
	color *= ambientColour;
	gl_FragColor = vec4(color.gba * scale, 1.);
}
