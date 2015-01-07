package keyboard.standard;

import utilities.Point;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.leapmotion.leap.Vector;

import utilities.MyUtilities;
import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Gesture;
import enums.GestureDirection;
import enums.Renderable;
import enums.Key;
import keyboard.IKeyboard;
import keyboard.KeyboardGesture;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.VirtualKeyboard;

public class StandardKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 0;
    private static final String KEYBOARD_NAME = "Standard Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.STANDARD.getName();
    private final float HORIZONTAL_GESTURE_LENGTH = 125f;
    private final float VERTICAL_GESTURE_LENGTH;
    private final float HORIZONTAL_GESTURE_OFFSET = 25f;
    private final float VERTICAL_GESTURE_OFFSET;
    private final float CAMERA_DISTANCE;
    private VirtualKeyboard virtualKeyboard;
    private KeyBindings keyBindings;
    private KeyboardGestures keyboardGestures;
    private MouseGesture mouseGesture;
    private boolean shiftDown = false;
    private boolean isCalibrated = false;
    
    public StandardKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_NAME, KEYBOARD_FILE_NAME);
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new StandardAttributes(this);
        keyboardSettings = new StandardSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new StandardRenderables(this);
        keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        int borderSize = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE) * 2;
        imageSize = new Point(keyboardSize.x + borderSize, keyboardSize.y + borderSize);
        CAMERA_DISTANCE = keyboardAttributes.getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(Renderable.VIRTUAL_KEYS);
        keyboardGestures = (KeyboardGestures) keyboardRenderables.getRenderable(Renderable.KEYBOARD_GESTURES);
        keyBindings = new KeyBindings();
        mouseGesture = new MouseGesture();
        VERTICAL_GESTURE_LENGTH = HORIZONTAL_GESTURE_LENGTH * (imageSize.y/(float)imageSize.x);
        VERTICAL_GESTURE_OFFSET = HORIZONTAL_GESTURE_OFFSET * (imageSize.y/(float)imageSize.x);
    }
    
    @Override
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToPerspective(gl, this, true);
        gl.glPushMatrix();
        gl.glTranslatef(-imageSize.x/2f, -imageSize.y/2f, -CAMERA_DISTANCE);
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        // Remove completed gestures.
        keyboardGestures.removeFinishedGestures();
        
        keyboardGestures.updateAll();
        
        if(shiftDown) {
            virtualKeyboard.pressed(Key.VK_SHIFT_LEFT);
            virtualKeyboard.pressed(Key.VK_SHIFT_RIGHT);
        }
    }
    
    @Override
    public void addToUI(JPanel panel, GLCanvas canvas) {
        panel.add(keyBindings);
        canvas.addMouseListener(mouseGesture);
        canvas.addMouseMotionListener(mouseGesture);
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        panel.remove(keyBindings);
        canvas.removeMouseListener(mouseGesture);
        canvas.removeMouseMotionListener(mouseGesture);
        keyboardGestures.deleteQuadric();
    }
    
    @Override
    public void beginCalibration(JPanel textPanel) {
        finishCalibration();
    }

    @Override
    protected void finishCalibration() {
        isCalibrated = true;
        notifyListenersCalibrationFinished();
    }
    
    @Override
    public boolean isCalibrated() {
        return isCalibrated;
    }
    
    public KeyboardGesture createSwipeGesture(GestureDirection direction) {
        KeyboardGesture gesture = null;
        switch(direction) {
            case UP:
                gesture = new KeyboardGesture(new Vector(imageSize.x/2f, imageSize.y/2f + VERTICAL_GESTURE_OFFSET, CAMERA_DISTANCE * 0.5f), Gesture.SWIPE);
                gesture.update(new Vector(imageSize.x/2f, imageSize.y/2f + VERTICAL_GESTURE_LENGTH, CAMERA_DISTANCE * 0.5f));
                gesture.gestureFinshed();
                break;
            case DOWN:
                gesture = new KeyboardGesture(new Vector(imageSize.x/2f, imageSize.y/2f - VERTICAL_GESTURE_OFFSET, CAMERA_DISTANCE * 0.5f), Gesture.SWIPE);
                gesture.update(new Vector(imageSize.x/2f, imageSize.y/2f - VERTICAL_GESTURE_LENGTH, CAMERA_DISTANCE * 0.5f));
                gesture.gestureFinshed();
                break;
            case LEFT:
                gesture = new KeyboardGesture(new Vector(imageSize.x/2f - HORIZONTAL_GESTURE_OFFSET, imageSize.y/2f, CAMERA_DISTANCE * 0.5f), Gesture.SWIPE);
                gesture.update(new Vector(imageSize.x/2f - HORIZONTAL_GESTURE_LENGTH, imageSize.y/2f, CAMERA_DISTANCE * 0.5f));
                gesture.gestureFinshed();
                break;
            case RIGHT:
                gesture = new KeyboardGesture(new Vector(imageSize.x/2f + HORIZONTAL_GESTURE_OFFSET, imageSize.y/2f, CAMERA_DISTANCE * 0.5f), Gesture.SWIPE);
                gesture.update(new Vector(imageSize.x/2f + HORIZONTAL_GESTURE_LENGTH, imageSize.y/2f, CAMERA_DISTANCE * 0.5f));
                gesture.gestureFinshed();
                break;
        }
        return gesture;
    }
    
    public KeyboardGesture findClosestGesture(Vector direction) {
        return null;
    }
    
    @SuppressWarnings("serial")
    private class KeyBindings extends JPanel {
        
        public KeyBindings() {
            setKeyBindings();
        }
        
        private void setKeyBindings() {
            ActionMap actionMap = getActionMap();
            int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
            InputMap inputMap = getInputMap(condition);
            
            for(int i = 0; i < Key.getSize(); i++) {
                Key key = Key.getByIndex(i);
                if(key != Key.VK_NULL || key != Key.VK_SHIFT_RELEASED || key != Key.VK_SHIFT_LEFT || key != Key.VK_SHIFT_RIGHT) {
                    // Add normal keys to input map
                    if(key != Key.VK_SHIFT) {
                        inputMap.put(KeyStroke.getKeyStroke(key.getCode(), 0), key.getName());
                    }
                    
                    // Add shifted keys to input map
                    inputMap.put(KeyStroke.getKeyStroke(key.getCode(), KeyEvent.SHIFT_DOWN_MASK, false), key.getName() + Key.VK_SHIFT.getName());
                    
                    // Add normal keys to action map
                    if(key != Key.VK_SHIFT) {
                        actionMap.put(key.getName(), new KeyAction(key.getValue()));
                    }
                    
                    // Add shifted keys to action map
                    actionMap.put(key.getName() + Key.VK_SHIFT.getName(), new KeyAction(key.toUpper()));
                }
            }
            inputMap.put(KeyStroke.getKeyStroke(Key.VK_SHIFT.getCode(), 0, true), Key.VK_SHIFT_RELEASED.getName());
            actionMap.put(Key.VK_SHIFT_RELEASED.getName(), new KeyAction(Key.VK_SHIFT_RELEASED.getValue()));
        }
        
        private class KeyAction extends AbstractAction {
            public KeyAction(char actionCommand) {
                putValue(ACTION_COMMAND_KEY, actionCommand);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                keyPressed = e.getActionCommand().charAt(0);
                Key key = Key.getByCode(keyPressed) == null ? Key.getByValue(keyPressed) : Key.getByCode(keyPressed);
                if((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0 || shiftDown) {
                    shiftDown = true;
                    //keyboardRenderables.swapToUpperCaseKeyboard();
                }
                virtualKeyboard.pressed(key);
                if(key.isPrintable()) {
                    notifyListenersKeyEvent();
                } else {
                    switch(key) {
                        case VK_UP:
                            keyboardGestures.addGesture(createSwipeGesture(GestureDirection.UP));
                            break;
                        case VK_DOWN:
                            keyboardGestures.addGesture(createSwipeGesture(GestureDirection.DOWN));
                            break;
                        case VK_LEFT:
                            keyboardGestures.addGesture(createSwipeGesture(GestureDirection.LEFT));
                            break;
                        case VK_RIGHT:
                            keyboardGestures.addGesture(createSwipeGesture(GestureDirection.RIGHT));
                            break;
                        case VK_SHIFT_RELEASED:
                            shiftDown = false;
                            //keyboardRenderables.swapToLowerCaseKeyboard();
                            break;
                        default: break;
                    }
                }
            }
        }
    }
    
    private class MouseGesture implements MouseListener, MouseMotionListener {
        private KeyboardGesture gesture;
        private float velocity;
        private long previousTime;
        private long elapsedTime;
        
        @Override
        public void mouseClicked(MouseEvent e) {
            // Do nothing on quick click.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Do nothing on mouse enter
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Do nothing on mouse exit.
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Vector start = new Vector(e.getX() + imageSize.x*0.5f, -e.getY() + imageSize.y*1.5f, CAMERA_DISTANCE);
            gesture = new KeyboardGesture(start.times(0.5f), Gesture.SWIPE);
            keyboardGestures.addGesture(gesture);
            velocity = 0;
            previousTime = System.currentTimeMillis();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            gesture.gestureFinshed();
            gesture = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Vector dest = new Vector(e.getX() + imageSize.x*0.5f, -e.getY() + imageSize.y*1.5f, CAMERA_DISTANCE);
            
            dest = dest.times(0.5f);
            long now = System.currentTimeMillis();
            elapsedTime = now - previousTime;
            previousTime = now;
            System.out.println(elapsedTime);
            if(elapsedTime != 0) {
                velocity = gesture.getDestination().minus(dest).magnitude() / (elapsedTime/1000f);
            } else {
                velocity = 0;
            }
            System.out.println(velocity);
            
            gesture.update(dest);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // Do nothing when only moving the mouse.
        }
        
    }
}
