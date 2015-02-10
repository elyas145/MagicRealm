package lwjglview.graphics;

import static org.lwjgl.opengl.ARBFragmentShader.*;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.*;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

import utils.resources.ResourceHandler;

public class GLShaderHandler {
	
	public enum ShaderType {
		TILE_SHADER
	}
	
	public GLShaderHandler(ResourceHandler rh) {
		resources = rh;
		vertShaders = new HashMap<String, Integer>();
		fragShaders = new HashMap<String, Integer>();
		programs = new HashMap<ShaderType, Integer>();
	}
	
	public void loadShaderProgram(ShaderType shader) throws IOException {
		if(!hasProgram(shader)) {
			String vsfn = GLShaderHandler.getVSFname(shader);
			int vs = getShader(vsfn, vertShaders, GL_VERTEX_SHADER_ARB);
			String fsfn = GLShaderHandler.getFSFname(shader);
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
		glUseProgramObjectARB(programs.get(shader));
	}
	
	private boolean hasProgram(ShaderType shader) {
		return programs.containsKey(shader);
	}
	
	private static String getVSFname(ShaderType shader) {
		return "shaders/vertex/flat.vs";
	}
	
	private static String getFSFname(ShaderType shader) {
		return "shaders/fragment/flat.fs";
	}
	
	private int getShader(String fname, Map<String, Integer> shaders, int type) throws IOException {
		if(shaders.containsKey(fname)) {
			return shaders.get(fname);
		}
		int shade = glCreateShaderObjectARB(type);
		glShaderSourceARB(shade, resources.readFile(fname));
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

}
