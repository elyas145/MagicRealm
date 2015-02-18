
#version 130

uniform mat4 modelViewMatrix;
uniform mat4 mvpMatrix;

out vec3 position;
out vec3 normal;
out vec2 textureCoordinate;

void main() {
	gl_Position = mvpMatrix * gl_Vertex;
	position = (modelViewMatrix * gl_Vertex).xyz;
	position.z *= -1.;
	normal = gl_Normal;
	textureCoordinate = gl_MultiTexCoord0.xy;
}
