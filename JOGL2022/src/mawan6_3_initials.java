
/*************************************************
 * Created on August 1, 2017, @author: Jim X. Chen
 *
 * Draw multiple points (in parallel)
 * 
 * a) Another method of sending values to the vertex shader(s) respectively
 * b) Efficient transfer of default per-vertex values: position, color, normal, texture coordinates, etc.
 * 
 * VBO: arrays to store vertex positions, colors, and other per-vertex information
 * VAO: an array that packs multiple VBO for transferring to the vertex shader
 *
 */

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL4.*;

public class mawan6_3_initials extends JOGL1_2_Animate {
	protected static int vao[] = new int[1]; // vertex array object (handle), for sending to the vertex shader
	protected static int vbo[] = new int[4]; // vertex buffers objects (handles) to stores position, color, normal, etc
	private long oldTime = System.currentTimeMillis();
	// array of vertices and colors corresponding to the vertices: a triangle
	private int currLetter = 0;
	private String[] letters = {"m", "a", "x"};
	private String[] colors = {"red", "green", "blue"};
	
	float aColors[] = { 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f};
	
	float mColors[] = { 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f};
	
	float xColors[] = {0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
	};
	
	static float vPoints[]= {};
	
	static float aPoints[]= { -0.5f, -0.5f, 0.0f, 
			0.0f, 0.5f, 0.0f, 
			0.0f, 0.5f, 0.0f, 
			0.5f, -0.5f, 0.0f, 
			-0.25f, 0.0f, 0.0f, 
			0.25f, 0.0f, 0.0f };

	static float mPoints[] = { -0.5f, -0.5f, 0.0f, 
			0.5f, 0.5f, 0.0f, 
			-0.5f, 0.5f, 0.0f, 
			0.5f, -0.5f, 0.0f };

	static float xPoints[] = { -0.5f, -0.5f, 0.0f, 
			-0.5f, 0.5f, 0.0f, 
			-0.5f, 0.5f, 0.0f, 
			0.0f, 0.0f, 0.0f, 
			0.0f, 0.0f,	0.0f, 
			0.5f, 0.5f, 0.0f, 
			0.5f, 0.5f, 0.0f, 
			0.5f, -0.5f, 0.0f };

	public void display(GLAutoDrawable drawable) {
		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		// 3. load vbo[0] with vertex data
//		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
//		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints); // vertices packed to upload to V shader
//		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
//				vBuf, // the vertex positions
//				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
//		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
//		// layout (location = 0) in V shader; 3 values for each vertex position
//		// layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

		
		gl.glPointSize(6.0f);
		
//		long time = System.currentTimeMillis();
//		
//		if (time-oldTime > 2000)
//		{
//			currLetter = (currLetter + 1)%3;
//			oldTime = time;
//		}
//		
//		myCharacter(letters[currLetter], colors[currLetter]);
		
		myCharacter("a", "red");
		myCharacter("m", "red");
		myCharacter("x", "red");
		
		// 6. draw 3 points: VAO has two arrays of corresponding vertices and colors
		//gl.glDrawArrays(GL_LINES, 0, 8);
	}

	private void myCharacter(String letter, String color) {
		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(aPoints);

		// 4. load vbo[1] with color data

		
		if (letter=="a"){
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
			vBuf = Buffers.newDirectFloatBuffer(aPoints); // vertices packed to upload to V shader
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex positions
					GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
			// layout (location = 0) in V shader; 3 values for each vertex position
			// layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
			gl.glDrawArrays(GL_LINES, 0, 6);
			
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
			vBuf = Buffers.newDirectFloatBuffer(aColors);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex colors
					GL_STATIC_DRAW);
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
			// layout (location = 1) in V shader; 3 values for each vertex color

			
			}
		else if (letter=="m"){
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
			vBuf = Buffers.newDirectFloatBuffer(mPoints); // vertices packed to upload to V shader
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex positions
					GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
			gl.glDrawArrays(GL_LINES, 0, 8);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
			vBuf = Buffers.newDirectFloatBuffer(mColors);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex colors
					GL_STATIC_DRAW);
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
			// layout (location = 1) in V shader; 3 values for each vertex color

			
			
			}
		else if (letter=="x"){
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
			vBuf = Buffers.newDirectFloatBuffer(xPoints); // vertices packed to upload to V shader
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex positions
					GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
			gl.glDrawArrays(GL_LINES, 0, 4);
			
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
			vBuf = Buffers.newDirectFloatBuffer(xColors);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
					vBuf, // the vertex colors
					GL_STATIC_DRAW);
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
			// layout (location = 1) in V shader; 3 values for each vertex color

			
			}

		gl.glDrawArrays(GL_LINES, 0, 8);
		if (color=="red"){	}
		else if (color=="blue"){  }
		else if (color=="green"){  }
		

		
	}

	public void init(GLAutoDrawable drawable) {

		String vShaderSource[], fShaderSource[];
		gl = (GL4) drawable.getGL();

		System.out.println(
				"a) init: per-vertex values (vertex attributes: positions, colors, etc.) to the vertex shaders in parallel");

		// 7/26/2022: this is added for accommodating packages
		String path = this.getClass().getPackageName().replace(".", "/");

		vShaderSource = readShaderSource("src/" + path + "/JOGL1_3_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/" + path + "/JOGL1_3_F.shader"); // read vertex shader }

		vfPrograms = initShaders(vShaderSource, fShaderSource);

		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		System.out.println("	Generate a VAO to hold VBO - arrays of vertex attributes (positions, colors)"); // we
																												// only
																										// use
																												// one
																												// vao
		gl.glBindVertexArray(vao[0]); // use handle 0

		// 2. generate vertex buffers indexed by vbo: here to store vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);
		System.out.println("	Generate VBO (" + vbo.length + ") to hold arrays of vertex attributes."); // we use two:
																											// position
		// and color
		// 5. enable VAO with loaded VBO data: accessible in the vertex shader
		gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
		System.out.print("	Enable corresponding vertex attributes, "); // we use two: position and color
		System.out.println("and then load the attributes in display()."); // we use two: position and color

	}

	public static void main(String[] args) {
		new mawan6_3_initials();
		System.out.println(
				"per-vertex values to the shaders; drawing points, lines, and triangles with per-vertex colors.");

	}
}
