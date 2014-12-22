package keyboard.renderables;
import java.nio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import enums.RenderableName;
import keyboard.KeyboardRenderable;
import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class KeyboardImage extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.KEYBOARD_IMAGE.toString();
    private final String ASSETS_PATH = "./assets/";
    private final String DEFAULT_FILE_PATH = "standard/";
    private final String DEFAULT_FILE_NAME = "keyboard.png";
    private String fullFilename;
    private ByteBuffer image = null;
    private int height;
    private int width;
    
    public KeyboardImage(String fileName, String filePath) {
        super(RENDER_NAME);
        fullFilename = ASSETS_PATH;
        fullFilename += filePath == null ? DEFAULT_FILE_PATH : filePath;
        fullFilename += fileName == null ? DEFAULT_FILE_NAME : fileName;
        loadImage(fullFilename);
    }

    private void loadImage(String filename) {
        // Load image and get height and width for raster.
        if(filename == null) {
            filename = fullFilename;
        }
        // Media tracker is necessary to access img data
        Image img = Toolkit.getDefaultToolkit().createImage(filename);
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage (img, 0);
        try {
            tracker.waitForAll(1000);
        } catch (InterruptedException ie) {
            System.out.println("MediaTracker Exception\n");
            ie.printStackTrace();
        }

        height = img.getHeight(null);
        width = img.getWidth(null);
        //System.out.println( "Image, w = " + width + ", h = " + height );

        // Create a raster with correct size and a colorModel and finally a bufImg.
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null); 
        ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        BufferedImage bufImg = new BufferedImage(colorModel, raster, false, null);
  
        // Filter img into bufImg and perform coordinate Transformations on the way.
        Graphics2D g = bufImg.createGraphics();
        AffineTransform gt = new AffineTransform();
        gt.translate (0, height);
        gt.scale (1, -1d);
        g.transform (gt);
        g.drawImage (img, null, null);
        
        // Retrieve underlying byte array (imgBuf) from bufImg.
        DataBufferByte imgBuf = (DataBufferByte)raster.getDataBuffer();
        image = ByteBuffer.wrap(imgBuf.getData());
        g.dispose();
    }

    public void render(GL2 gl) {    
        if(isEnabled()) {
            // Load image, if necessary.
            if(image == null) {
                loadImage(fullFilename);
            }
    
            //gl.glPushAttrib( GL.GL_DEPTH_BUFFER_BIT );
            //gl.glPushAttrib( GL.GL_COLOR_BUFFER_BIT );
          
            //gl.glDisable(GL.GL_DEPTH_TEST);
    	    
            // enable alpha mask (import from png sets alpha bits)
            gl.glEnable (GL.GL_BLEND);
            gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    
            // Draw image as bytes.
            gl.glRasterPos2i(0, 0); // moves in object space
            //gl.glWindowPos2i(150, 100); // moves in screen space
            gl.glPixelZoom( 1.0f, 1.0f ); // x-factor, y-factor
            gl.glDrawPixels(width, height, GL_RGBA, GL_UNSIGNED_BYTE, image);
    
            //gl.glPopAttrib(); 
            //gl.glPopAttrib();
        }
    }
}
