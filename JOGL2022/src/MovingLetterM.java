import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import javax.swing.*;

public class MovingLetterM implements GLEventListener {
    private float angle = 0.0f;  // Angle for circular motion
    private float radius = 0.5f;  // Radius of the circular path

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Moving Letter M");
            GLCanvas canvas = new GLCanvas();
            MovingLetterM movingLetterM = new MovingLetterM();
            canvas.addGLEventListener(movingLetterM);
            Animator animator = new Animator(canvas);
            frame.getContentPane().add(canvas);
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            animator.start();
        });
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 0); // Black background
        gl.glColor3f(1, 1, 1); // White color for "M"
        gl.glPointSize(5.0f); // Size of points
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0) height = 1; // Prevent division by zero
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1, 1, -1, 1, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // Update angle for circular motion
        angle += 0.02; // Increment angle
        float x = (float) (radius * Math.cos(angle));
        float y = (float) (radius * Math.sin(angle));

        // Draw the letter "M" using points and lines
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(x - 0.1f, y - 0.2f); // Left bottom
        gl.glVertex2f(x - 0.1f, y + 0.2f); // Left top
        gl.glVertex2f(x - 0.1f, y + 0.2f); // Left top
        gl.glVertex2f(x, y);               // Middle top
        gl.glVertex2f(x, y);               // Middle top
        gl.glVertex2f(x + 0.1f, y + 0.2f); // Right top
        gl.glVertex2f(x + 0.1f, y + 0.2f); // Right top
        gl.glVertex2f(x + 0.1f, y - 0.2f); // Right bottom
        gl.glEnd();

    }
}
