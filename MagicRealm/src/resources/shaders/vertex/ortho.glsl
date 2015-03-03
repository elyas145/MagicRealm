
#version 130

uniform mat4 modelViewMatrix;

out vec2 textureCoordinate;

void main() {
	gl_Position = modelViewMatrix * gl_Vertex;
	textureCoordinate = gl_MultiTexCoord0.xy;
}
