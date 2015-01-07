package keyboard.controller;

import utilities.Point;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;
import net.java.games.input.Component.Identifier;
import utilities.MyUtilities;
import keyboard.IKeyboard;
import keyboard.renderables.VirtualKeyboard;
import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Renderable;

public class ControllerKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 3;
    private static final String KEYBOARD_NAME = "Controller Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.CONTROLLER.getName();
    private boolean isCalibrated = false;
    private VirtualKeyboard virtualKeyboard;
    private GamePad gamePad;
    
    public ControllerKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_NAME, KEYBOARD_FILE_NAME);
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new ControllerAttributes(this);
        keyboardSettings = new ControllerSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new ControllerRenderables(this);
        keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        int borderSize = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE) * 2;
        imageSize = new Point(keyboardSize.x + borderSize, keyboardSize.y + borderSize);
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(Renderable.VIRTUAL_KEYS);
        gamePad = new GamePad();
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
        gamePad.update();
        
        // Figure out xbox layout and controls here
    }
    
    @Override
    public void addToUI(JPanel panel, GLCanvas canvas) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        // TODO Auto-generated method stub
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
    
    private static enum Button {
        A(Component.Identifier.Button._0),
        B(Component.Identifier.Button._1),
        X(Component.Identifier.Button._2),
        Y(Component.Identifier.Button._3),
        START(Component.Identifier.Button._7);
        
        private final Identifier identifier;
        private boolean isPressed;
        
        private static final Button[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private Button(Identifier identifier) {
            this.identifier = identifier;
            isPressed = false;
        }
        
        public static int getSize() {
            return SIZE;
        }
        
        public static void checkPressed(Identifier identifider, boolean isPressed) {
            for(int i = 0; i < SIZE; i++) {
                if(VALUES[i].identifier == identifider) {
                    VALUES[i].setPressed(isPressed);
                }
            }
        }
        
        private void setPressed(boolean isPressed) {
            this.isPressed = isPressed;
        }
        
        public boolean isPressed() {
            return isPressed;
        }
    }
    
    private static enum HatSwitch {
        LEFT(0.875f, 1.0f, 0.125f),
        UP(0.125f, 0.25f, 0.375f),
        RIGHT(0.375f, 0.5f, 0.625f),
        DOWN(0.625f, 0.75f, 0.875f);
        
        public final static Identifier identifier = Component.Identifier.Axis.POV;
        private final float min;
        private final float def;
        private final float max;
        private boolean isPressed;
        
        private static final HatSwitch[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private HatSwitch(float min, float def, float max) {
            this.min = min;
            this.def = def;
            this.max = max;
            isPressed = false;
        }
        
        public static int getSize() {
            return SIZE;
        }
        
        public static void checkPressed(float value) {
            for(int i = 0; i < SIZE; i++) {
                VALUES[i].setPressed(value);
            }
        }
        
        private void setPressed(float value) {
            if(value == min || value == def || value == max) {
                isPressed = true;
            } else {
                isPressed = false;
            }
        }
        
        public boolean isPressed() {
            return isPressed;
        }
    }
    
    private static enum Axis {
        LEFT_X(Component.Identifier.Axis.X),
        LEFT_Y(Component.Identifier.Axis.Y),
        RIGHT_X(Component.Identifier.Axis.RX),
        RIGHT_Y(Component.Identifier.Axis.RY);
        
        private static final float THRESHOLD = 0.5f;
        private final Identifier identifier;
        private float axisValue;
        
        private static final Axis[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private Axis(Identifier identifier) {
            this.identifier = identifier;
            axisValue = 0f;
        }
        
        public static int getSize() {
            return SIZE;
        }
        
        public static void checkPressed(Identifier identifider, float axisValue) {
            for(int i = 0; i < SIZE; i++) {
                if(VALUES[i].identifier == identifider) {
                    VALUES[i].setPressed(axisValue);
                }
            }
        }
        
        private void setPressed(float axisValue) {
            if(axisValue <= -THRESHOLD || THRESHOLD <= axisValue) {
                this.axisValue = axisValue;
            } else {
                this.axisValue = 0f;
            }
        }
        
        public float getAxisValue() {
            return axisValue;
        }
    }
    
    private class GamePad {
        private final int CHECK_CONTROLLER_PULSE = 3000;
        private Controller controller;
        private long previousCheckTime = 0;
        private long elapsedCheckTime = 0;
        //private bool
        
        public GamePad() {
            previousCheckTime = System.currentTimeMillis();
        }
        
        public void update() {
            if(controller == null) {
                long now = System.currentTimeMillis();
                elapsedCheckTime += now - previousCheckTime;
                previousCheckTime = now;
                if(elapsedCheckTime > CHECK_CONTROLLER_PULSE) {
                    searchForController();
                    elapsedCheckTime = 0;
                }
            } else {
                // Detect and populate the Controller input.
                detectControllerData();
            }
        }

        private void searchForController() {
            Controller[] controllers;
            // To force the update of the controller list, we create a new environment. This allows us
            // to detect newly turned on controllers or reconnect lost controllers.
            DirectAndRawInputEnvironmentPlugin directEnv = new DirectAndRawInputEnvironmentPlugin();
            if (directEnv.isSupported()) {
                controllers = directEnv.getControllers();
            } else {
                controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            }

            for(int i = 0; i < controllers.length; i++){
                Controller controller = controllers[i];
                if (controller.getType() == Controller.Type.STICK || 
                        controller.getType() == Controller.Type.GAMEPAD || 
                        controller.getType() == Controller.Type.WHEEL || 
                        controller.getType() == Controller.Type.FINGERSTICK) {
                    
                    // Controller is connected
                    this.controller = controller;
                    System.out.println("Xbox Controller: connected");
                }
            }
        }
        
        private void detectControllerData() {
            if (!controller.poll()) {
                // Controller is disconnected.
                System.out.println("Xbox Controller: disconnected");
                controller = null;
            } else {
                Component[] components = controller.getComponents();
                for(int i = 0; i < components.length; i++) {
                    Component component = components[i];
                    Identifier componentIdentifier = component.getIdentifier();
                    
                    // Analog - Axes
                    if(component.isAnalog()){
                        //float axisValue = component.getPollData();
                        Axis.checkPressed(componentIdentifier, component.getPollData());
                        //System.out.println("component: " + component.getName() + " identifier: " + componentIdentifier + " value: " + axisValue);
                    }
                    // Digital - Buttons & HatSwitch
                    else {
                        // Hat switch.
                        if(componentIdentifier == HatSwitch.identifier) {
                            //float hatPosition = component.getPollData();
                            HatSwitch.checkPressed(component.getPollData());
                            //System.out.println("component: " + component.getName() + " identifier: " + componentIdentifier + " value: " + hatPosition);
                        }
                        // Buttons.
                        else {
                            /*boolean isPressed = false;
                            if(component.getPollData() == 1.0f) {
                                isPressed = true;
                            }*/
                            Button.checkPressed(componentIdentifier, component.getPollData() == 1.0f);
                            
                            //System.out.println("component: " + component.getName() + " identifier: " + componentIdentifier + " index: " + componentIdentifier.toString() + " pressed: " + isPressed);
                        }
                    }
                }
            }
        }
    }
}
