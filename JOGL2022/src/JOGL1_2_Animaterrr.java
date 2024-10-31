/*************************************************
 * Created on August 1, 2017, @author: Jim X. Chen
 *
 * Animate a point 
 * Animator and Uniform: 
 * 		Animator starts a thread that calls display() repetitively
 * 		Uniform sends a variable value from JOGL program to the shader programs (uniform value for all shader programs)
 *
 */
import java.io.File;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.util.FPSAnimator;
import java.nio.file.Paths;

public class JOGL1_2_Animaterrr extends JOGL1_1_PointVFfiles {
	static FPSAnimator animator; // for thread that calls display() repetitively
	protected static int vfPrograms; // handle to shader programs
	protected static int cnt=0; 
	float pos=0.0f, delta=0.1f; // modify between display() calls
	
	
	public JOGL1_2_Animaterrr() { // it calls super's constructor first
		
		// Frame per second animator 
		animator = new FPSAnimator(canvas, 40); // 40 calls per second; frame rate
		animator.start();
		System.out.println("\nAnimator starts a thread: display() is called repeatitively.");
	}
	
	
	public void init(GLAutoDrawable drawable) { // reading new vertex & fragment shaders
		String vShaderSource[], fShaderSource[] ;

		gl = (GL4) drawable.getGL();
		
		System.out.println("a) init: different shader programs."); 
		System.out.println(" 	display: uniform values to the shaders; multiple viewports; drawing a point");

		// 7/26/2022: this is added for accommodating packages
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL1_2_Animate_V.shader"); // read vertex shader	
		fShaderSource = readShaderSource("src/"+path+"/JOGL1_2_Animate_F.shader"); // read vertex shader			}
		
		vfPrograms = initShaders(vShaderSource, fShaderSource);		
	}

	
	// called for handling drawing area when it is reshaped
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		
		System.out.println("b) reshape: update WIDTH/HEIGHT");
		
		// update current window size
		WIDTH = w; 
		HEIGHT = h; 		
			
	}

	

	public void display(GLAutoDrawable drawable) {
	    float radius = 0.5f; 
	    delta = cnt * 0.01f; 

		// clear the display every frame: another way to set the background color
	    float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	    FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
	    gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // Clear every frame

	    float posX = radius * (float)Math.cos(delta);
	    float posY = radius * (float)Math.sin(delta);
	    
	    int posLocX = gl.glGetUniformLocation(vfPrograms, "sPos");
	    int posLocY = gl.glGetUniformLocation(vfPrograms, "sPos1");
	    
	    gl.glProgramUniform1f(vfPrograms, posLocY, posY);
	    gl.glProgramUniform1f(vfPrograms, posLocX, posX);
	    
	    gl.glPointSize(10.0f); 
	    cnt++;

	    // Draw the point
	    gl.glViewport(0, 0, WIDTH, HEIGHT); // Physical coordinates: number in pixels
	    gl.glDrawArrays(GL_POINTS, 0, 1);
	}

	

	public void dispose(GLAutoDrawable drawable) {
		animator.stop(); // stop the animator thread
		System.out.println("d) dispose: Animator thread stopped.");
	}
	
	public static void main(String[] args) {
		new JOGL1_2_Animaterrr();
		

	}
}
