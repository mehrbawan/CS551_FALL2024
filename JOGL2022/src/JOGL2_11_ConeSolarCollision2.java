




public class JOGL2_11_ConeSolarCollision2 extends JOGL2_11_ConeSolar {
	  //direction and speed of rotation
	  protected static float coneD = 0.03f;
	  protected static float sphereD = -0.04f;
	  protected static float cylinderD = 0.001f;
	  
	  // initial position
	  protected static float spherem = 0;
	protected static float cylinderm = 120*dg;
	protected static float conem = 240*dg;

	  // centers of the objects
	  protected static float[] earthC = new float[4];
	  protected static float[] coneC = new float[4];
	  protected static float[] sphereC = new float[4];
	  protected static float[] cylinderC = new float[4];
	  protected static float[] sunC = new float[4];
  
	 protected void drawSolar(float E, float e, float M, float m) {
		 float tiltAngle = 45*dg; 
		 float[] tmp = {0, 0, 0, 1};
		 
			    
		  
		 myPushMatrix();
		 myPushMatrix();
			      cylinderm = cylinderm+cylinderD;
			      myRotatef(cylinderm, 0.0f, 1.0f, 0.0f);
			      // rotating around the "earth"
			      myTranslatef(M, 0.0f, 0.0f);
			      myScalef(E/8, E/8, E/8);
			      drawCylinder();
				  gl.glLineWidth(1);
				  myTransHomoVertex(tmp, cylinderC);
			      // retrieve the center of the cylinder
			      // the matrix is stored column major left to right
			    myPopMatrix();
			    myPushMatrix();
			      spherem = spherem+sphereD;
			      myRotatef(spherem, 0.0f, 1.0f, 0.0f);
			      // rotating around the "earth"
			      myTranslatef(M, 0.0f, 0.0f);
			      myScalef(E/8, E/8, E/8);
				  myTransHomoVertex(tmp, sphereC);
				  drawCylinder();
			      // retrieve the center of the sphere
				    myPopMatrix();

			    myPushMatrix();
			      conem = conem+coneD;
			      myRotatef(conem, 0.0f, 1.0f, 0.0f);
			      // rotating around the "earth"
			      myTranslatef(M, 0.0f, 0.0f);
			      myScalef(E/8, E/8, E/8);
				  myTransHomoVertex(tmp, coneC);
				  drawCylinder();
			      // retrieve the center of the cone
			    myPopMatrix();
			myPopMatrix();

		    if (distance(coneC, sphereC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = coneD;
		        coneD = sphereD;
		        sphereD = tmpD;
			    conem = conem+2*coneD;
			    spherem = spherem+2*sphereD;
		      }
		      if (distance(coneC, cylinderC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = coneD;
		        coneD = cylinderD;
		        cylinderD = tmpD;
			    conem = conem+2*coneD;
			    cylinderm = cylinderm+2*cylinderD;
		      }
		      if (distance(cylinderC, sphereC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = cylinderD;
		        cylinderD = sphereD;
		        sphereD = tmpD;
			    cylinderm = cylinderm+2*cylinderD;
			    spherem = spherem+2*sphereD;
		      } 
	    }


	    // distance between two points
	    public float distance (float[] c1, float[] c2) {
	    	
	      float tmp = (c2[0]-c1[0])*(c2[0]-c1[0])+
	                  (c2[1]-c1[1])*(c2[1]-c1[1])+
	                  (c2[2]-c1[2])*(c2[2]-c1[2]);

	      return (float)Math.sqrt((double)tmp);
	    }

	 
	 
	  public static void main(String[] args) {
	    new JOGL2_11_ConeSolarCollision2();
	  }

}
