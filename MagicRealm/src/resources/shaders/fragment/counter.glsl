
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
	float scale = abs(dot(diff, normal) / length(diff) / length(normal));
	vec3 color;
	vec4 raw;
	if(index < 0) {
		raw = vec4(1.);
	}
	else {
		raw = texture2DArray(texture, vec3(textureCoordinate, float(index)));
	}
	if(raw.a < .5) {
		color = vec3(1.);
	}
	else {
		color = raw.rgb;
	}
	float fade = counterColour.a;
	color *= fade;
	color += vec3(1. - fade);
	color *= counterColour.rgb;
	color *= ambientColour.rgb;
	gl_FragColor = vec4(color * scale, 1.);
}
