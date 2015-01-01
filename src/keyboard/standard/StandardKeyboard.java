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
import enums.FileName;
import enums.Renderable;
import enums.Key;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.renderables.VirtualKeyboard;

public class StandardKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 0;
    private static final String KEYBOARD_FILE_NAME = FileName.STANDARD_NAME.getName();
    private VirtualKeyboard virtualKeyboard;
    private KeyBindings keyBindings;
    private boolean shiftDown = false;
    private boolean isCalibrated = false;
    
    public StandardKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_FILE_NAME);
        keyboardAttributes = new StandardAttributes(this);
        keyboardSettings = new StandardSettings(this);
        keyboardRenderables = new StandardRenderables(this);
        keyboardWidth = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_WIDTH.toString());
        keyboardHeight = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString());
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderableByName(Renderable.VIRTUAL_KEYS.toString());
        keyBindings = new KeyBindings();
        keyboardAttributes.addAttribute(new KeyboardAttribute(Attribute.KEY_BINDINGS.toString(), keyBindings));
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
                Key k = Key.getByIndex(i);
                if(k != Key.VK_NULL || k != Key.VK_SHIFT_RELEASED) {
                    // Add normal keys to input map
                    if(k != Key.VK_SHIFT) {
                        inputMap.put(KeyStroke.getKeyStroke(k.getKeyCode(), 0), k.getKeyName());
                    }
                    
                    // Add shifted keys to input map
                    inputMap.put(KeyStroke.getKeyStroke(k.getKeyCode(), KeyEvent.SHIFT_DOWN_MASK, false), k.getKeyName() + Key.VK_SHIFT.getKeyName());
                    
                    // Add normal keys to action map
                    if(k != Key.VK_SHIFT) {
                        actionMap.put(k.getKeyName(), new KeyAction(k.getKeyValue()));
                    }
                    
                    // Add shifted keys to action map
                    actionMap.put(k.getKeyName() + Key.VK_SHIFT.getKeyName(), new KeyAction(k.toUpper()));
                }
            }
            inputMap.put(KeyStroke.getKeyStroke(Key.VK_SHIFT.getKeyCode(), 0, true), Key.VK_SHIFT_RELEASED.getKeyName());
            actionMap.put(Key.VK_SHIFT_RELEASED.getKeyName(), new KeyAction(Key.VK_SHIFT_RELEASED.getKeyValue()));
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
