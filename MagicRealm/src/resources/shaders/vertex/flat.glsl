
#version 130

uniform mat4 mvpMatrix;

varying vec2 textureCoordinate;

void main() {
	gl_Position = mvpMatrix * gl_Vertex;
	textureCoordinate = gl_MultiTexCoord0;
}
