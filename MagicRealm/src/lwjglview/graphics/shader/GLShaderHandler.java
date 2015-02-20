package lwjglview.graphics.shader;

import static org.lwjgl.opengl.ARBFragmentShader.*;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.*;
 
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import utils.math.Matrix;
import utils.resources.ResourceHandler;

public class GLShaderHandler {
	
	public GLShaderHandler(ResourceHandler rh) {
		resources = rh;
		vertShaders = new HashMap<String, Integer>();
		fragShaders = new HashMap<String, Integer>();
		programs = new HashMap<ShaderType, Integer>();
		uniforms = new HashMap<ShaderType, Map<String, Integer>>();
		matrixBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void loadShaderProgram(ShaderType shader) throws IOException {
		if(!hasProgram(shader)) {
			String vsfn = getShaderPath("vertex",
					GLShaderHandler.getVSFname(shader));
			int vs = getShader(vsfn, vertShaders, GL_VERTEX_SHADER_ARB);
			String fsfn = getShaderPath("fragment",
					GLShaderHandler.getFSFname(shader));
			int fs = getShader(fsfn, fragShaders, GL_FRAGMENT_SHADER_ARB);
			int program = glCreateProgramObjectARB();
			glAttachObjectARB(program, vs);
			glAttachObjectARB(program, fs);
			glLinkProgramARB(program);
	        if(glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
	            throw new RuntimeException("Error creating program: "
	            		+ glGetInfoLogARB(program, glGetObjectParameteriARB(program, GL_OBJECT_INFO_LOG_LENGTH_ARB)));
	        }
	        programs.put(shader, program);
		}
	}
	
	public void setUniformIntValue(ShaderType st, String name, int value) {
		int loc = initUniform(st, name);
		glUniform1i(loc, value);
	}
	
	public void setUniformFloatValue(ShaderType st, String name, float value) {
		int loc = initUniform(st, name);
		glUniform1f(loc, value);
	}

	public void setUniformFloatArrayValue(ShaderType st, String name, int sz,
			FloatBuffer values) {
		int loc = initUniform(st, name);
		glUniform4f(loc, values.get(0), values.get(1), values.get(2), values.get(3));
	}
	
	public void setUniformMatrixValue(ShaderType st, String name,
			Matrix mat) {
		int loc = initUniform(st, name);
		mat.toFloatBuffer(matrixBuffer);
		glUniformMatrix4(loc, true, matrixBuffer);
	}
	
	public void useShaderProgram(ShaderType shader) {
		glUseProgramObjectARB(programs.get(shader));
	}
	
	public boolean hasProgram(ShaderType shader) {
		return programs.containsKey(shader);
	}
	
	private int initUniform(ShaderType st, String name) {
		if(!hasProgram(st)) {
			throw new RuntimeException("The shader has not been loaded yet: " + st);
		}
		if(!uniforms.containsKey(st)) {
			uniforms.put(st,  new HashMap<String, Integer>());
		}
		Map<String, Integer> locs = uniforms.get(st);
		if(!locs.containsKey(name)) {
			locs.put(name, glGetUniformLocation(programs.get(st), name));
		}
		return locs.get(name);
	}
	
	private static String getVSFname(ShaderType shader) {
		return "flat.glsl";
	}
	
	private static String getFSFname(ShaderType shader) {
		switch(shader) {
		case TILE_SHADER:
			return "tile.glsl";
		case CHIT_SHADER:
			return "counter.glsl";
		}
		return "flat.glsl";
	}
	
	private String getShaderPath(String folder, String shaderName) {
		return ResourceHandler.joinPath("shaders", folder, shaderName);
	}
	
	private int getShader(String fname, Map<String, Integer> shaders, int type) throws IOException {
		if(shaders.containsKey(fname)) {
			return shaders.get(fname);
		}
		int shade = glCreateShaderObjectARB(type);
		String shader = resources.readFile(fname);
		glShaderSourceARB(shade, shader);
		glCompileShaderARB(shade);
		if(glGetObjectParameteriARB(shade, GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
            throw new RuntimeException("Error creating shader: "
            		+ glGetInfoLogARB(shade, glGetObjectParameteriARB(shade, GL_OBJECT_INFO_LOG_LENGTH_ARB)));
		}
		shaders.put(fname, shade);
		return shade;
	}
	
	private ResourceHandler resources;
	
	private Map<String, Integer> vertShaders;
	private Map<String, Integer> fragShaders;
	private Map<ShaderType, Integer> programs;
	private Map<ShaderType, Map<String, Integer>> uniforms;
	
	private FloatBuffer matrixBuffer;

}
