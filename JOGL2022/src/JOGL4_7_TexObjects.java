



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;

import java.io.File;
import java.nio.FloatBuffer;


public class JOGL4_7_TexObjects extends JOGL4_6_Texture {
	
	// used for texture objects
	int	gmuTexture; 
	int	vseTexture; 
	int cubeTexture; 
	int cube1Texture; 
	
	
	  public void drawSphere() {

	    // start drawing a sphere
		int cLoc = gl.glGetUniformLocation(vfPrograms,  "dSphere"); 
		gl.glProgramUniform1i(vfPrograms,  cLoc,  1);
		
		super.drawSphere(); 

		cLoc = gl.glGetUniformLocation(vfPrograms,  "dSphere"); 
		gl.glProgramUniform1i(vfPrograms,  cLoc,  0);

	  }

	

	  protected void drawSolar(float E, float e, float M, float m) {
			 
		  	    // using different cube textures
		  		gl.glActiveTexture(GL_TEXTURE2);
				if (cnt % 1000 < 500) 
					gl.glBindTexture(GL_TEXTURE_2D,	cubeTexture);
				else gl.glBindTexture(GL_TEXTURE_2D,	cube1Texture);

				
				super.drawSolar(E, e, M, m);				
	  }	  
	  
 
	
	  
	public void init(GLAutoDrawable drawable) {
		
		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[] ;
					
		System.out.println("a) init: prepare shaders, VAO/VBO, read images as textures"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL4_7_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL4_7_F.shader"); // read fragment shader
		vfPrograms = initShaders(vShaderSource, fShaderSource);		
		
		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		gl.glBindVertexArray(vao[0]); // use handle 0
		
		// 2. generate vertex buffers indexed by vbo: here vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		// 3. enable VAO with loaded VBO data
		gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
		
		// if you don't use it, you should not enable it
		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: Normal
					
		// if you don't use it, you should not enable it
		gl.glEnableVertexAttribArray(2); // enable the 2th vertex attribute: TextureCoord
					
		//4. specify drawing into only the back_buffer
		gl.glDrawBuffer(GL_BACK); 
		
		// 5. Enable zbuffer and clear framebuffer and zbuffer
		gl.glEnable(GL_DEPTH_TEST); 

		// read an image as texture
		Texture	joglTexture	=	loadTexture("src/"+path+"/gmu.jpg");						
		gmuTexture	=	joglTexture.getTextureObject(); 
		Texture	joglTexture1	=	loadTexture("src/"+path+"/VSE.jpg");						
		vseTexture	=	joglTexture1.getTextureObject(); 
		Texture	joglTexture2	=	loadTexture("src/"+path+"/earthCube.jpg");						
		cubeTexture	=	joglTexture2.getTextureObject(); 
		Texture	joglTexture3	=	loadTexture("src/"+path+"/natureCube.jpg");						
		cube1Texture	=	joglTexture3.getTextureObject(); 

		// activate texture #0 and bind it to the texture object
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D,	gmuTexture);
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D,	vseTexture);
		gl.glActiveTexture(GL_TEXTURE2);
		gl.glBindTexture(GL_TEXTURE_2D,	cubeTexture);
		//gl.glBindTexture(GL_TEXTURE_2D,	cube1Texture);
		
		//Width and Height are used to retrieve corresponding texel in the shader
		//Connect JOGL variable with shader variable by name
		int whLoc = gl.glGetUniformLocation(vfPrograms,  "Height"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  HEIGHT);

		//Connect JOGL variable with shader variable by name
		whLoc = gl.glGetUniformLocation(vfPrograms,  "Width"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  WIDTH);

		}


	  public static void main(String[] args) {
	    new JOGL4_7_TexObjects();
	  }

}
