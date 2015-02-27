package keyboard.renderables;
import java.nio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import enums.FileName;
import enums.FilePath;
import enums.Renderable;
import keyboard.KeyboardRenderable;
import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class KeyboardImage extends KeyboardRenderable {
    public final static float SCALE = 1.0f;
    private final static String UPPER = "upper";
    private static final String ASSETS_PATH = FilePath.ASSETS.getPath();
    private static final String DEFAULT_FILE_NAME = FileName.STANDARD.getName() + FileName.KEYBOARD_IMAGE.getName();
    private String fileName;
    private ByteBuffer image = null;
    private int imageHeight;
    private int imageWidth;
    
    public KeyboardImage(String fileName) {
        super(fileName.contains(UPPER) ? Renderable.KEYBOARD_IMAGE_UPPER : Renderable.KEYBOARD_IMAGE);
        this.fileName = ASSETS_PATH;
        this.fileName += fileName == null ? DEFAULT_FILE_NAME : fileName;
        loadImage(this.fileName);
    }
    
    public int getHeight() {
        return imageHeight;
    }
    
    public int getWidth() {
        return imageWidth;
    }

    private void loadImage(String filename) {
        // Load image and get height and width for raster.
        if(filename == null) {
            filename = this.fileName;
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

        imageHeight = img.getHeight(null);
        imageWidth = img.getWidth(null);
        //System.out.println( "Image, w = " + width + ", h = " + height );

        // Create a raster with correct size and a colorModel and finally a bufImg.
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, imageWidth, imageHeight, 4, null); 
        ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        BufferedImage bufImg = new BufferedImage(colorModel, raster, false, null);
  
        // Filter img into bufImg and perform coordinate Transformations on the way.
        Graphics2D g = bufImg.createGraphics();
        AffineTransform gt = new AffineTransform();
        gt.translate (0, imageHeight);
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
                loadImage(fileName);
            }
            
    	    gl.glPushMatrix();
            // enable alpha mask (import from png sets alpha bits)
            gl.glEnable (GL.GL_BLEND);
            gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    
            // Draw image as bytes.
            gl.glRasterPos2i(0, 0); // moves in object space
            gl.glPixelZoom(SCALE, SCALE); // x-factor, y-factor
            gl.glDrawPixels(imageWidth, imageHeight, GL_RGBA, GL_UNSIGNED_BYTE, image);
            gl.glPopMatrix();
        }
    }
}
