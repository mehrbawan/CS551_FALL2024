
/*************************************************
 * Draw multiple points bouncing in a circle (in parallel)
 * Modified Feb 24, 2024; by Jim X. Chen
 * Built upon by Mehr Awan for CS551 Project 1
 * 
 * a) Another method of sending values to the vertex shader(s) respectively: per-vertex values 
 * b) Efficient transfer of default per-vertex values: position, color, normal, texture coordinates, etc.
 * c) Mixing per-vertex color and uniform color, a selection in the vertex shader
 * d) Calculate pixels on the circle one unit apart, point in the circle vector reflection, etc.
 * e) Add a background sound/music (optional entertainment) 
 * 
 * VBO: arrays to store vertex positions, colors, and other per-vertex information
 * VAO: an array that packs multiple VBO for transferring to the vertex shader
 *
 */

import java.nio.FloatBuffer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_LINE_LOOP;
import static com.jogamp.opengl.GL.GL_LINE_STRIP;
import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;

public class HW5_BounceInCircle extends JOGL1_3_VertexArray {

	float r = HEIGHT / 2.3f; // physical coordinates: radius in number of pixels
	float R = 2 * r / HEIGHT; // logical coordinates: corresponding radius
	float theta = 0, dtheta = 1 / r; // delta angle for one pixel apart (size 2 for points on circle)

	int noPoints = (int) (2 * Math.PI / dtheta); // # of points on the circle
	float pointsOn[] = new float[noPoints * 3]; // each point has x, y, z values
	float color[] = { 1.0f, 1.0f, 0.0f };

	int pnum = 100; // number of points in the circle
	float pointsIn[] = new float[pnum * 3]; // points
	float direction[] = new float[pnum * 3]; // direction vectors
	float clr[] = new float[pnum * 3]; // colors of the points

	Clip clip1, clip2; // sounce clip

	float triPoints[] = new float[3 * 3]; // 3 points, each with x, y, z
	float[] angles = new float[3]; // Angles for the three points
	float angleIncrement = (float) (2 * Math.PI / 3); // 120 degrees in radians
	float speed = 0.01f; // Speed of rotation

	float letterAnchors[] = new float[3 * 3]; // anchor points for the triangle letter labels


	float initialRotationAngle = 0.0f; // rotation angle for the center initials

	
	
	public HW5_BounceInCircle() { // it runs always for inheritance, so it is better to inialize in init()
	}

	// called for handling drawing area when it is reshaped
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		System.out.println("b) need viewport for the drawing area.");
		System.out.println("It should be in reshape() instead of init(); it is meaningful after reshape() is called.");
		System.out.println("It should be in reshape() instead of display(); it needs to be called just once.");

		gl.glViewport((WIDTH - HEIGHT) / 2, 0, HEIGHT, HEIGHT); // for an initial square drawing area

		// add a background music/sound
		clip1 = sound("ImperialMarch60.wav");
		clip2 = sound("ding.wav");

		// Too loud clip1 compared to clip2: Reduce volume by 10 decibels
		FloatControl gainControl = (FloatControl) clip1.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.

	}

	// add a background sound: soundFile in wav format
	public Clip sound(String soundFile) {
		Clip myclip = null;

		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(soundFile));
			myclip = AudioSystem.getClip();
			myclip.open(audioInputStream);
			// myclip.start(); // you can start this sound thread at a specific location in
			// the code
			// If you want the sound to loop infinitely, then put:
			// myclip.loop(Clip.LOOP_CONTINUOUSLY);
			// If you want to stop the sound, then use myclip.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return myclip;
	}

	public void display(GLAutoDrawable drawable) {

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);

		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		// draw a circle: the points can be loaded in VAO[0] in init(), because they
		// don't change
		drawCircle();

		drawTriPoints();

		drawletterAnchors();

		// draw points bounce in the circle: the points can be loaded every frame in
		// VAO[1]
		drawPoints();

		// draw initials, pass in anchor point coords
		drawM(new float[] { letterAnchors[0], letterAnchors[1], letterAnchors[2] });
		drawA(new float[] { letterAnchors[3], letterAnchors[4], letterAnchors[5] });
		drawX(new float[] { letterAnchors[6], letterAnchors[7], letterAnchors[8] });
		
		//rotationTest();
		drawCenterLetters();

		// add background sound
		cnt++;
		if (cnt == 200)
			clip1.start();
		// if (cnt>2000) clip1.stop();

	}

	public void drawCircle() {

		// send an uniform color to the vertex shader
		color[0] = 0;
		color[1] = 1;
		color[2] = 1;
		uploadColor(color);

		// send the circle points to the vertex shader
		// 3. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(pointsOn); // vertices packed to upload to V shader
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
		// layout (location = 0) in V shader; 3 values for each vertex position

		// Using one VBO only
		// gl.glEnableVertexAttribArray(0); // Enable the 0th attribute: vertices
		gl.glDisableVertexAttribArray(1); // disable the 1th attribute: not using per-vertex color

		// draw the circle
		gl.glPointSize(2.0f); // set the points size a little bigger
		gl.glDrawArrays(GL_POINTS, 0, noPoints); // 3: size of the array (attributes)

	}

	public void drawPoints() {

		// send the negative uniform value as an indicator to use per-vertex color
		// send the list of vertices to the vertex shader
		// send the list of colors to the vertex shader
		// draw the vertices in parallel
		// update the vertices: moving and bouncing

		color[0] = -1f; // using negative value to indicate per-vertex color
		uploadColor(color);

		// 3. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(pointsIn); // vertices packed to upload to V shader
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
		// layout (location = 0) in V shader; 3 values for each vertex position

		// 4. load vbo[1] with color data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
		vBuf = Buffers.newDirectFloatBuffer(clr);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex colors
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
		// layout (location = 1) in V shader; 3 values for each vertex color

		// 5. enable VAO with loaded VBO data: accessible in the vertex shader
		// gl.glEnableVertexAttribArray(0); // per-vertex points
		gl.glEnableVertexAttribArray(1); // per-vertex color

		gl.glPointSize(5.0f); // points in the circle bigger
		gl.glDrawArrays(GL_POINTS, 0, pnum); // 3: size of the array (attributes)

		// update the vertices so they bounce in the circle
		for (int i = 0; i < pnum * 3; i += 3) {

			// points move one step
			pointsIn[i] += direction[i];
			pointsIn[i + 1] += direction[i + 1];

			// bounce when point on or outside the circle
			if (distance(0, 0, pointsIn[i], pointsIn[i + 1]) > R) {

				clip2.stop();
				clip2.setMicrosecondPosition(0); // from beginning
				clip2.start();

				// bring the point back into the circle
				pointsIn[i] -= direction[i];
				pointsIn[i + 1] -= direction[i + 1];

				float dir[] = new float[3]; // for calculating bouncing direction
				float normal[] = new float[3]; // for radius direction: normal

				dir[0] = -direction[i];
				dir[1] = -direction[i + 1];
				dir[2] = 0;

				normal[0] = -pointsIn[i];
				normal[1] = -pointsIn[i + 1];
				normal[2] = 0;

				// change the direction of the point around r
				float dir1[] = new float[3]; // for calculating bouncing direction dir1
				reflect(dir, normal, dir1);

				direction[i] = dir1[0];
				direction[i + 1] = dir1[1];
			}
		}
	}

	public void drawTriPoints() {
		// function to draw the triangle vertexes + triangle
		color[0] = 1;
		color[1] = 1;
		color[2] = 0;
		uploadColor(color);

		float radius = R;

		for (int i = 0; i < 3; i++) {
			angles[i] += speed;

			if (angles[i] >= 2 * Math.PI) {
				angles[i] -= 2 * Math.PI;
			}

			triPoints[i * 3] = radius * (float) Math.cos(angles[i]);
			triPoints[i * 3 + 1] = radius * (float) Math.sin(angles[i]);
			triPoints[i * 3 + 2] = 0;
		}

		// send the circle points to the vertex shader
		// 3. load vbo[2] with tri data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(triPoints); // vertices packed to upload to V shader
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands

		gl.glPointSize(5.0f);

		// Use the VBO to draw the points
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // Bind the VBO for equidistant points
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // Associate VBO with vertex attribute
		gl.glEnableVertexAttribArray(0); // Enable the vertex attribute array

		// Draw the points
		gl.glDrawArrays(GL_LINE_LOOP, 0, 3); 
	}

	public void drawletterAnchors() {
		//function to draw anchor points for translated letters
		float radius = R + 0.1f;

		for (int i = 0; i < 3; i++) {
			angles[i] += speed;

			if (angles[i] >= 2 * Math.PI) {
				angles[i] -= 2 * Math.PI;
			}

			letterAnchors[i * 3] = radius * (float) Math.cos(angles[i]);
			letterAnchors[i * 3 + 1] = radius * (float) Math.sin(angles[i]);
			letterAnchors[i * 3 + 2] = 0;
		}

		// send the circle points to the vertex shader
		// 3. load vbo[2] with tri data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(letterAnchors); // vertices packed to upload to V shader
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands

		gl.glPointSize(0.1f);

		// Use the VBO to draw the points
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]); // Bind the VBO for equidistant points
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // Associate VBO with vertex attribute
		gl.glEnableVertexAttribArray(0); // Enable the vertex attribute array

		// Draw the points
		gl.glDrawArrays(GL_POINTS, 0, 3); 
	}
	
	

	public void drawM(float[] anchorPoint) {
		// draw letter m at given anchor point
		color[0] = 1;
		color[1] = 0;
		color[2] = 1;
		uploadColor(color);

		float offsetX = anchorPoint[0];
		float offsetY = anchorPoint[1];

		float mPoints[] = { offsetX - 0.03f, offsetY - 0.03f, 0f, offsetX - 0.03f, offsetY + 0.03f, 0f, offsetX,
				offsetY, 0f, offsetX + 0.03f, offsetY + 0.03f, 0f, offsetX + 0.03f, offsetY - 0.03f, 0f };

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(mPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
		// layout (location = 0) in V shader; 3 values for each vertex position

		// Using one VBO only
		// gl.glEnableVertexAttribArray(0); // Enable the 0th attribute: vertices
		gl.glDisableVertexAttribArray(1); // disable the 1th attribute: not using per-vertex color

		gl.glPointSize(5.0f); // Set point size
		gl.glDrawArrays(GL_LINE_STRIP, 0, mPoints.length / 3); // Draw M shape
	}

	public void rotationTest() {
		//testing rotation
		// this is a mess lol
	    color[0] = 1;
	    color[1] = 0;
	    color[2] = 1;
	    uploadColor(color);
	
	    initialRotationAngle += 0.01f; 
	
	    // rotate all m points
	    float mPoints[] = new float[] {
	        rotateX(- 0.03f, - 0.03f, initialRotationAngle),
	        rotateY(- 0.03f, - 0.03f, initialRotationAngle),
	        0f,
	        rotateX(- 0.03f, 0.03f, initialRotationAngle),
	        rotateY(- 0.03f, 0.03f, initialRotationAngle),
	        0f,
	        rotateX(0, 0, initialRotationAngle),
	        rotateY(0, 0, initialRotationAngle),
	        0f,
	        rotateX(0.03f, 0.03f, initialRotationAngle),
	        rotateY(0.03f, 0.03f, initialRotationAngle),
	        0f,
	        rotateX(0.03f, - 0.03f, initialRotationAngle),
	        rotateY(0.03f, - 0.03f, initialRotationAngle),
	        0f
	    };
	    
	    
	
	    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
	    FloatBuffer vBuf = Buffers.newDirectFloatBuffer(mPoints);
	    gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);
	    gl.glPointSize(5.0f);
	    gl.glDrawArrays(GL_LINE_STRIP, 0, mPoints.length / 3); // Draw M shape
	}

	public void drawCenterLetters() {
		// draw 2 initials in center and rotate
	    color[0] = 1;
	    color[1] = 0;
	    color[2] = 1;
	    uploadColor(color);

	    initialRotationAngle -= 0.01f; 

	    // this is really crap but i am too lazy to fix this system lololol
	    float initialPoints[] = new float[] {
	        rotateX(-0.09f, -0.03f, initialRotationAngle), rotateY(-0.09f, -0.03f, initialRotationAngle), 0f, // m left leg
	        rotateX(-0.09f, 0.03f, initialRotationAngle), rotateY(-0.09f, 0.03f, initialRotationAngle), 0f, 
	        
	        rotateX(-0.09f, 0.03f, initialRotationAngle), rotateY(-0.09f, 0.03f, initialRotationAngle), 0f, //m slope down 
	        rotateX(-0.06f, 0.0f, initialRotationAngle), rotateY(-0.06f, 0.0f, initialRotationAngle), 0f,   
	        
	        rotateX(-0.06f, 0.0f, initialRotationAngle), rotateY(-0.06f, 0.0f, initialRotationAngle), 0f,   //m slope up
	        rotateX(-0.03f, 0.03f, initialRotationAngle), rotateY(-0.03f, 0.03f, initialRotationAngle), 0f, 
	        
	        rotateX(-0.03f, 0.03f, initialRotationAngle), rotateY(-0.03f, 0.03f, initialRotationAngle), 0f, //m right leg
	        rotateX(-0.03f, -0.03f, initialRotationAngle), rotateY(-0.03f, -0.03f, initialRotationAngle), 0f, 
	        
	        rotateX(0.03f, -0.03f, initialRotationAngle), rotateY(0.03f, -0.03f, initialRotationAngle), 0f, //a left leg half
	        rotateX(0.045f, 0.0f, initialRotationAngle), rotateY(0.045f, 0.0f, initialRotationAngle), 0f,   

	        rotateX(0.045f, 0.0f, initialRotationAngle), rotateY(0.045f, 0.0f, initialRotationAngle), 0f,   //a left leg second half
	        rotateX(0.06f, 0.03f, initialRotationAngle), rotateY(0.06f, 0.03f, initialRotationAngle), 0f,   
	        
	        rotateX(0.06f, 0.03f, initialRotationAngle), rotateY(0.06f, 0.03f, initialRotationAngle), 0f,   //a right rgiht leg half
	        rotateX(0.075f, 0.0f, initialRotationAngle), rotateY(0.075f, 0.0f, initialRotationAngle), 0f,   

	        rotateX(0.075f, 0.0f, initialRotationAngle), rotateY(0.075f, 0.0f, initialRotationAngle), 0f,   //a right leg second half
	        rotateX(0.09f, -0.03f, initialRotationAngle), rotateY(0.09f, -0.03f, initialRotationAngle), 0f,   
	        
	        rotateX(0.045f, 0.0f, initialRotationAngle), rotateY(0.045f, 0.0f, initialRotationAngle), 0f,  //a cross bar
	        rotateX(0.075f, 0.0f, initialRotationAngle), rotateY(0.075f, 0.0f, initialRotationAngle), 0f   

};

	    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
	    FloatBuffer vBuf = Buffers.newDirectFloatBuffer(initialPoints);
	    gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);
	    gl.glPointSize(5.0f);
	    gl.glDrawArrays(GL_LINES, 0, initialPoints.length / 3);
	}


	public void drawA(float[] anchorPoint) {
		// draw letter a at anchor
		float offsetX = anchorPoint[0];
		float offsetY = anchorPoint[1];

		float aPoints[] = { offsetX - 0.03f, offsetY - 0.03f, 0f, offsetX - 0.015f, offsetY + 0.0f, 0f, offsetX + 0.0f,
				offsetY + .03f, 0f, offsetX + 0.015f, offsetY + 0.0f, 0f, offsetX + 0.03f, offsetY - 0.03f, 0f,
				offsetX + 0.015f, offsetY + 0.0f, 0f, offsetX - 0.015f, offsetY + 0.0f, 0f };

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(aPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);

		gl.glPointSize(5.0f); // Set point size
		gl.glDrawArrays(GL_LINE_STRIP, 0, aPoints.length / 3); // Draw M shape
	}

	public void drawX(float[] anchorPoint) {
		//draw letter x at anchor
		float offsetX = anchorPoint[0];
		float offsetY = anchorPoint[1];

		float xPoints[] = { offsetX - 0.03f, offsetY - 0.03f, 0f, offsetX + 0.03f, offsetY + 0.03f, 0f, offsetX - 0.03f,
				offsetY + 0.03f, 0f, offsetX + 0.03f, offsetY - 0.03f, 0f };

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(xPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);

		gl.glPointSize(5.0f); // Set point size
		gl.glDrawArrays(GL_LINES, 0, xPoints.length / 3); // Draw M shape
	}

	void uploadColor(float[] color) { // called "uColor" in the shader
		// send color data to vertex shader through uniform (array): color here is not
		// per-vertex
		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(color);

		// Connect JOGL variable with shader variable by name
		int colorLoc = gl.glGetUniformLocation(vfPrograms, "uColor");
		gl.glProgramUniform3fv(vfPrograms, colorLoc, 1, cBuf);

	}

	// distance between two points
	double distance(double x0, double y0, double x1, double y1) {

		return (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)));
	}

	// rotation helper funcs
	private float rotateX(float x, float y, float angle) {
	    return (float) (x * Math.cos(angle) - y * Math.sin(angle));
	}

	private float rotateY(float x, float y, float angle) {
	    return (float) (x * Math.sin(angle) + y * Math.cos(angle));
	}

	// reflect v1 around n to v2
	public void reflect(float v1[], float n[], float v2[]) {
		normalize(n);

		// v2 = 2*dot(v1, n)*n + v1
		for (int i = 0; i < 3; i++) {
			v2[i] = 2 * dotprod(v1, n) * n[i] - v1[i];
		}
	}

	// dot product of two vectors
	public float dotprod(float[] a, float[] b) {

		return (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]);
	}

	// normalize a vector to unit vector
	public void normalize(float vector[]) {
		float d = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);

		if (d == 0) {
			System.err.println("0 length vector: normalize().");
			return;
		}
		vector[0] /= d;
		vector[1] /= d;
		vector[2] /= d;
	}

	public void init(GLAutoDrawable drawable) {

		String vShaderSource[], fShaderSource[];
		gl = (GL4) drawable.getGL();

		System.out.println("\na) init: ");

		String path = this.getClass().getPackageName().replace(".", "/");
		System.out.println("	include package directory: " + path);

		vShaderSource = readShaderSource("src/" + path + "/HW5_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/" + path + "/HW5_F.shader"); // read fragment shader
		vfPrograms = initShaders(vShaderSource, fShaderSource);

		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		System.out.println("	Generate VAO to hold arrays of vertex attributes."); // we use one for the circle:
																						// vao[0]
		gl.glBindVertexArray(vao[0]); // use handle 0: circle

		// 2. generate vertex buffers indexed by vbo: here to store vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);
		System.out.println("	Generate VBO (" + vbo.length + ") to hold vertex  attributes."); // we use one: position

		// 5. enable VAO with loaded VBO data: accessible in the vertex shader
		gl.glEnableVertexAttribArray(0); // per-vertex points
		// gl.glEnableVertexAttribArray(1); // per-vertex color

		// building a list of vertices on the circle (once)
		for (int i = 0; i < 3 * noPoints; i++) { // all points on the circle are generated in the array: pointsOn[]
			theta = theta + dtheta;
			pointsOn[i] = (float) (R * Math.cos(theta));
			i++;
			pointsOn[i] = (float) (R * Math.sin(theta));
			i++;
			pointsOn[i] = 0;
		}

		// points in the circle:
		// building a list of vertices, moving vectors (directions), and colors
		// (randomly generated,
		// once)
		for (int i = 0; i < 3 * pnum; i = i + 3) {
			// generate a point inside the circle with random positions
			pointsIn[i + 0] = (float) (R * (Math.random() - 0.5));
			pointsIn[i + 1] = (float) (R * (Math.random() - 0.5));
			pointsIn[i + 2] = 0;

			// randomly generated directions: the "velocities" are randomly generated
			direction[i + 0] = (float) (Math.random() - 0.5) / 100f;
			direction[i + 1] = (float) (Math.random() - 0.5) / 100f;
			direction[i + 2] = 0;
			// normalize(direction[i]); // make it a unit vector (speed)

			// randomly generated colors
			clr[i + 0] = (float) Math.random();
			clr[i + 1] = (float) Math.random();
			clr[i + 2] = (float) Math.random();

		}
		for (int i = 0; i < 3; i++) {
			angles[i] = i * angleIncrement; // 0, 120, and 240 degrees in radians
		}

		
		// points that form the triangle
		int numPoints = 3;

		for (int i = 0; i < numPoints; i++) {
			float angle = i * angleIncrement;
			triPoints[i * 3] = R * (float) Math.cos(angle);
			triPoints[i * 3 + 1] = R * (float) Math.sin(angle);
			triPoints[i * 3 + 2] = 0;
		}

		// points that form the anchors for the outside letters
		for (int i = 0; i < numPoints; i++) {
			float angle = i * angleIncrement;
			letterAnchors[i * 3] = R * (float) Math.cos(angle);
			letterAnchors[i * 3 + 1] = R * (float) Math.sin(angle);
			letterAnchors[i * 3 + 2] = 0;
		}
		

//		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]); 
//		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(triPoints);
//		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);

	}

	public static void main(String[] args) {
		HW5_BounceInCircle f = new HW5_BounceInCircle();
		f.setTitle("HW5 -- bouncing inside a circle");

	}
}