package keyboard.renderables;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapPlane extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_PLANE.toString();
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    InteractionBox iBox;
    // third Leap Point: (-60.117, 196.815, -28.5863)   Normalized: (0.244452, 0.48646, 0.306524)
    // min Leap Point: (-57.324, 138.28, -32.742)   Normalized: (0.256324, 0.237636, 0.278398)
    // max Leap Point: (37.5257, 196.318, -26.0687)   Normalized: (0.659516, 0.48435, 0.323563)
    private Vector A = new Vector(-57.324f, 138.28f, -32.742f); // min
    private Vector B =  new Vector(-60.117f, 196.815f, -28.5863f); // third
    private Vector C = new Vector(37.5257f, 196.318f, -26.0687f); // max
    //private Vector A;
    //private Vector B;
    //private Vector C;
    private Vector E = new Vector();
    private Vector planeNormal;
    private Vector planeCenter;
    private float D;
    private float angle;
    
    public LeapPlane(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public void calibrate() {
        // Calibrate the leap plane finding the min corner, third corner, and max corner.
        // We can then use these to set up the plane
        // the depth/thickness of the plane just depends on what we want it to be.
        // try to float just above the surface (5 mm maybe?) and then have it go deep into the binder
    }
    
    public void calculatePlaneFromPoints() {
        // Find the center of the plane
        planeCenter = MyUtilities.MATH_UTILITILES.midpoint(A, C);
        
        // Determine the vectors
        Vector AB = B.minus(A);
        Vector AC = C.minus(A);
        
        // Determine the normal of the plane by finding the cross product
        planeNormal = AB.cross(AC);
        System.out.println(planeNormal);
        
        // Find D, a simple variable that holds our normal time's a point on the plane
        D = MyUtilities.MATH_UTILITILES.calcPlaneD(planeNormal, A);
        System.out.println(D);
        
        E = A.minus(B).plus(C);
        System.out.println(E);
        
        Vector n = new Vector(0,1,0);
        angle = planeNormal.angleTo(n);
        System.out.println(angle);
        //System.out.println(planeNormal.times(angle));
        
        System.out.println("axis: " + planeNormal.cross(n).divide(planeNormal.cross(n).magnitude()));
        System.out.println("angle: " + Math.acos(planeNormal.dot(n)/(planeNormal.magnitude()*n.magnitude())));
    }
    
    public float distToPlane(Vector point) {
        return MyUtilities.MATH_UTILITILES.findDistanceToPlane(point, planeNormal, D);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            //gl.glRotatef(angle, x, y, z);
            gl.glColor3f(0.5f, 0.5f, 0.5f);
            gl.glBegin(GL_QUADS);
            Vector tmpA = A.minus(A);
            Vector tmpB = B.minus(A);
            Vector tmpC = C.minus(A);
            Vector tmpE = E.minus(A);
            gl.glVertex3f(tmpA.getX(), tmpA.getY(), tmpA.getZ());
            gl.glVertex3f(tmpB.getX(), tmpB.getY(), tmpB.getZ());
            gl.glVertex3f(tmpC.getX(), tmpC.getY(), tmpC.getZ());
            gl.glVertex3f(tmpE.getX(), tmpE.getY(), tmpE.getZ());
            gl.glEnd();
            gl.glPopMatrix();
        }
    }
}
