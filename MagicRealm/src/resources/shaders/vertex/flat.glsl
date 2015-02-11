
uniform float time;

void main() {
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	float u = sin(time) * .1;
	float s = sin(u);
	float c = cos(u);
	pos = mat4(
			c, 0., s, 0.,
			0., 1., 0., 0.,
			-s, 0., c, 0.,
			0., 0., 0., 1.
		) * pos;
	u = cos(time*.7) * .1;
	s = sin(u);
	c = cos(u);
	pos = mat4(
			1., 0., 0., 0.,
			0., c, s, 0.,
			0., -s, c, 0.,
			0., 0., 0., 1.
		) * pos;
	gl_Position = gl_ProjectionMatrix * pos;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
