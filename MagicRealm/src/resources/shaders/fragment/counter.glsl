
#version 130
#extension GL_EXT_texture_array : enable

uniform int index;
uniform sampler2DArray texture;
uniform vec4 counterColour;
uniform vec4 ambientColour;

in vec3 position;
in vec3 eye;
in vec3 normal;
in vec2 textureCoordinate;

void main() {
	vec3 diff = position - eye;
	float scale = -dot(diff, normal) / length(diff) / length(normal);
	//scale = pow(scale, 3.);
	vec3 color;
	vec4 raw = texture2DArray(texture, vec3(textureCoordinate, float(index)));
	if(raw.r < .5) {
		color = vec3(1.);
	}
	else {
		color = raw.gba;
	}
	color *= counterColour.rgb;
	color *= ambientColour.rgb;
	gl_FragColor = vec4(color.rgb * scale, 1.);
}
