
#version 130

out vec2 textureCoordinate;

void main() {
	gl_Position = gl_Vertex;
	textureCoordinate = (gl_Vertex.xy + vec2(1.)) * .5;
}