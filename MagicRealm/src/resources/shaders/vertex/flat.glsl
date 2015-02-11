
void main() {
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	gl_Position = gl_ProjectionMatrix * pos;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
