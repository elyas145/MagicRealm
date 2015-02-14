
#version 130

#define PI 3.14159

uniform mat4 modelViewMatrix;
uniform float xScale;
uniform float yScale;
uniform float nearRadius;
uniform float oneOverRadiusDifference;

varying vec2 textureCoordinate;

vec4 fishEyeProjection(vec4 coord) {
	coord /= coord.w;
	coord.z *= -1f;
	return vec4(
		xScale * atan(coord.x, coord.z),
		yScale * atan(coord.y, coord.z),
		(length(coord.xyz) - nearRadius) * oneOverRadiusDifference,
		1.
	);
}

void main() {
	gl_Position = fishEyeProjection(modelViewMatrix * gl_Vertex);
	textureCoordinate = gl_MultiTexCoord0.xy;
}
