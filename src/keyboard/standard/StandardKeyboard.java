package keyboard.standard;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import utilities.MyUtilities;
import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Renderable;
import enums.Key;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.renderables.VirtualKeyboard;

public class StandardKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 0;
    private static final String KEYBOARD_NAME = "Standard Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.STANDARD.getName();
    private VirtualKeyboard virtualKeyboard;
    private KeyBindings keyBindings;
    private boolean shiftDown = false;
    private boolean isCalibrated = false;
    
    public StandardKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_NAME, KEYBOARD_FILE_NAME);
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new StandardAttributes(this);
        keyboardSettings = new StandardSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new StandardRenderables(this);
        keyboardWidth = keyboardAttributes.getAttribute(Attribute.KEYBOARD_WIDTH);
        keyboardHeight = keyboardAttributes.getAttribute(Attribute.KEYBOARD_HEIGHT);
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(Renderable.VIRTUAL_KEYS);
        keyBindings = new KeyBindings();
        keyboardAttributes.addAttribute(new KeyboardAttribute(this, Attribute.KEY_BINDINGS, keyBindings));
    }
    
    @Override
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToOrthogonal(gl, this, true);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -0.1f);
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        if(shiftDown) {
            virtualKeyboard.pressed(Key.VK_SHIFT);
        }
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
                if(key != Key.VK_NULL || key != Key.VK_SHIFT_RELEASED) {
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
                    virtualKeyboard.pressed(key);
                } else {
                    virtualKeyboard.pressed(key);
                }
                if(key != Key.VK_SHIFT && key != Key.VK_SHIFT_RELEASED) {
                    notifyListenersKeyEvent();
                }
                if(key == Key.VK_SHIFT_RELEASED) {
                    shiftDown = false;
                }
            }
       }
    }
}
