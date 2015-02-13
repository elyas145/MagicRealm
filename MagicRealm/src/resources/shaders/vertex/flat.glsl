
#version 130

uniform mat4 mvpMatrix;

void main() {
	gl_Position = mvpMatrix * gl_Vertex;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
