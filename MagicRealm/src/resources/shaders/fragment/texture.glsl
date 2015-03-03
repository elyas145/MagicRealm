
#version 130

uniform sampler2D texture;

in vec2 textureCoordinate;

void main() {
	gl_FragColor = texture2D(texture, textureCoordinate);
}