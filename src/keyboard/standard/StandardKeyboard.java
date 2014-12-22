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

import enums.AttributeName;
import enums.RenderableName;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardSettings;
import keyboard.renderables.VirtualKeyboard;

public class StandardKeyboard extends IKeyboard {
    private VirtualKeyboard virtualKeyboard;
    private KeyBindings keyBindings;
    
    public StandardKeyboard() {
        keyboardAttributes = new StandardAttributes(this);
        keyboardSettings = new StandardSettings(this);
        keyboardRenderables = new StandardRenderables(this);
        width = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString());
        height = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString());
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderableByName(RenderableName.VIRTUAL_KEYS.toString());
        keyBindings = new KeyBindings();
        keyboardAttributes.addAttribute(new KeyboardAttribute(AttributeName.KEY_BINDINGS.toString(), keyBindings));
    }
    
    @Override
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -100.0f);
        // TODO: Figure out what order is best for drawing. Image on top of colors or colors on top of image etc.
        //drawBackground(); // convert to drawing the leap plane in order to determine if leap plane is correct
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
        
        //gl.glTranslatef(-323.5f, -192.5f, -1000.0f); // figure out what to do here in order to do perspective if we use texture
        //gl.GL_TEXTURE_RECTANGLE_ARB --- use this for exact texturing if imaging attempt fails.
    }
    
    @Override
    public void update() {
        // TODO Add the key listener stuff in here maybe?
        // notifyListeners(); when they fire
        // What other point does update serve if not for that?
        // Possibly for leap we can give it the leap object
        // We'll have to tract that in a different way then rather than passing
        // it to Calibration control/experiment control which don't really care about the leap
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

            //String shift = "shift";
            String a = "a";
            String b = "b";
            String c = "c";
            String d = "d";
            String e = "e";
            String f = "f";
            String g = "g";
            String h = "h";
            String i = "i";
            String j = "j";
            String k = "k";
            String l = "l";
            String m = "m";
            String n = "n";
            String o = "o";
            String p = "p";
            String q = "q";
            String r = "r";
            String s = "s";
            String t = "t";
            String u = "u";
            String v = "v";
            String w = "w";
            String x = "x";
            String y = "y";
            String z = "z";
            String comma = ",";
            String period = ".";
            String space = " ";
            
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), a);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), b);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), c);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), d);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), e);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), f);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), g);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), h);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), i);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0), j);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0), k);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), l);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), m);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0), n);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), o);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), p);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), q);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), r);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), s);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), t);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0), u);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0), v);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), w);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), x);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0), y);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), z);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), comma);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), period);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), space);

            actionMap.put(a, new KeyAction(a));
            actionMap.put(b, new KeyAction(b));
            actionMap.put(c, new KeyAction(c));
            actionMap.put(d, new KeyAction(d));
            actionMap.put(e, new KeyAction(e));
            actionMap.put(f, new KeyAction(f));
            actionMap.put(g, new KeyAction(g));
            actionMap.put(h, new KeyAction(h));
            actionMap.put(i, new KeyAction(i));
            actionMap.put(j, new KeyAction(j));
            actionMap.put(k, new KeyAction(k));
            actionMap.put(l, new KeyAction(l));
            actionMap.put(m, new KeyAction(m));
            actionMap.put(n, new KeyAction(n));
            actionMap.put(o, new KeyAction(o));
            actionMap.put(p, new KeyAction(p));
            actionMap.put(q, new KeyAction(q));
            actionMap.put(r, new KeyAction(r));
            actionMap.put(s, new KeyAction(s));
            actionMap.put(t, new KeyAction(t));
            actionMap.put(u, new KeyAction(u));
            actionMap.put(v, new KeyAction(v));
            actionMap.put(w, new KeyAction(w));
            actionMap.put(x, new KeyAction(x));
            actionMap.put(y, new KeyAction(y));
            actionMap.put(z, new KeyAction(z));
            actionMap.put(comma, new KeyAction(comma));
            actionMap.put(period, new KeyAction(period));
            actionMap.put(space, new KeyAction(space));
        }
        
        private class KeyAction extends AbstractAction {
            public KeyAction(String actionCommand) {
                putValue(ACTION_COMMAND_KEY, actionCommand);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                key = e.getActionCommand().charAt(0);
                virtualKeyboard.pressed(key); // we can change this to receiving a string if we use shift
                notifyListeners();
            }
       }
    }
}
