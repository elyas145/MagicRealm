
#version 130

out vec2 textureCoordinate;

void main() {
	gl_Position = gl_Vertex;
	textureCoordinate = gl_MultiTexCoord0.xy;
}