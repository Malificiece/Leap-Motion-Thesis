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
import enums.Direction;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Key;
import enums.Renderable;

public class ControllerKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 3;
    private static final String KEYBOARD_NAME = "Controller Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.CONTROLLER.getName();
    private static final float AUTO_REPEAT_DELAY = (750 * 1/3) + 250; // Windows default
    private static final int AUTO_REPEAT_RATE = 1000 / 31; // Windows default
    private final Point KEY_LAYOUT_SIZE = new Point(5, 14);
    private boolean isCalibrated = false;
    private VirtualKeyboard virtualKeyboard;
    private GamePad gamePad;
    private Point selectedKey;
    private Key [][] keyLayout;
    private boolean shiftOnce = false;
    private boolean shiftTwice = false;
    
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
        keyLayout = getKeyLayout((Key[][]) keyboardAttributes.getAttribute(Attribute.KEY_ROWS).getValue());
        selectedKey = new Point(0, 0);
        // default selected is q
        selectKey(Key.VK_Q);
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
        
        // Go through controller inputs and check values
        // if held, will press, then delay, then spam at interval --- the enums will take care of this logic
        // if a - press selected key (and do whatever we're supposed to do)
        // if b - press backspace
        // if x - press shift, lock shift, unlock shift
        // if y - press space
        // if start - press enter
        Key key = getSelectedKey();
        if(Button.A.isDown() && !key.isBlank()) {
            virtualKeyboard.pressed(key);
            if(Button.A.isPressed()) {
                if(key != Key.VK_SHIFT) {
                    if(shiftOnce) {
                        keyPressed = key.toUpper();
                        shiftOnce = shiftTwice;
                        if(!shiftTwice) {
                            keyboardRenderables.swapToLowerCaseKeyboard();
                        }
                    } else {
                        keyPressed = key.getValue();   
                    }
                    notifyListenersKeyEvent();
                } else {
                    if(!shiftOnce && !shiftTwice) {
                        shiftOnce = true;
                        keyboardRenderables.swapToUpperCaseKeyboard();
                    } else if(shiftOnce && !shiftTwice) {
                        shiftTwice = true;
                    } else {
                        shiftTwice = false;
                        shiftOnce = false;
                        keyboardRenderables.swapToLowerCaseKeyboard();
                    }
                }
            }
        } else {
            virtualKeyboard.selected(key);
        }
        
        if(Button.B.isDown()) {
            virtualKeyboard.pressed(Key.VK_BACK_SPACE);
            if(Button.B.isPressed()) {
                if(shiftOnce) {
                    keyPressed = Key.VK_BACK_SPACE.toUpper();
                    shiftOnce = shiftTwice;
                    if(!shiftTwice) {
                        keyboardRenderables.swapToLowerCaseKeyboard();
                    }
                } else {
                    keyPressed = Key.VK_BACK_SPACE.getValue();   
                }
                notifyListenersKeyEvent();
            }
        } else if(key != Key.VK_BACK_SPACE) {
            virtualKeyboard.deselected(Key.VK_BACK_SPACE);
        }
        
        if(Button.X.isDown()) {
            if(Button.X.isPressed()) {
                if(!shiftOnce && !shiftTwice) {
                    shiftOnce = true;
                    keyboardRenderables.swapToUpperCaseKeyboard();
                } else if(shiftOnce && !shiftTwice) {
                    shiftTwice = true;
                } else {
                    shiftTwice = false;
                    shiftOnce = false;
                    keyboardRenderables.swapToLowerCaseKeyboard();
                }
            }
        } else if(key != Key.VK_SHIFT) {
            virtualKeyboard.deselected(Key.VK_SHIFT);
        }
        if(shiftTwice) {
            virtualKeyboard.locked(Key.VK_SHIFT);
        } else if(shiftOnce) {
            virtualKeyboard.pressed(Key.VK_SHIFT);
        }
        
        if(Button.Y.isDown()) {
            virtualKeyboard.pressed(Key.VK_SPACE);
            if(Button.Y.isPressed()) {
                if(shiftOnce) {
                    keyPressed = Key.VK_SPACE.toUpper();
                    shiftOnce = shiftTwice;
                    if(!shiftTwice) {
                        keyboardRenderables.swapToLowerCaseKeyboard();
                    }
                } else {
                    keyPressed = Key.VK_SPACE.getValue();   
                }
                notifyListenersKeyEvent();
            }
        } else if(key != Key.VK_SPACE) {
            virtualKeyboard.deselected(Key.VK_SPACE);
        }
        
        if(Button.START.isDown()) {
            virtualKeyboard.pressed(Key.VK_ENTER);
            if(Button.START.isPressed()) {
                keyPressed = Key.VK_ENTER.getValue();
                notifyListenersKeyEvent();
            }
        } else if(key != Key.VK_ENTER) {
            virtualKeyboard.deselected(Key.VK_ENTER);
        }
        
        // if left stick --- pos based on which direction we're pointed the most left, right, up, down
        // have a getClosestDirection() function in the enum itself that return the Direction
        switch(Axis.getClosestDirectionLeftStick()) {
            case DOWN:
                moveSelectedKey(-1, 0);
                break;
            case LEFT:
                moveSelectedKey(0, -1);
                break;
            case RIGHT:
                moveSelectedKey(0, 1);
                break;
            case UP:
                moveSelectedKey(1, 0);
                break;
            default: break;
        }
        
        // for gestures we can use dpad or maybe right stick. (might switch to any stick)
        // will use get exact direction
        // dpad can return diagnols... figure out what to do here
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
    
    private void moveSelectedKey(int rowDelta, int colDelta) {
        virtualKeyboard.deselected(getSelectedKey());
        Key previousKey;
        do {
            previousKey = getSelectedKey();
            int row = (rowDelta + selectedKey.x) % KEY_LAYOUT_SIZE.x;
            int col = (colDelta + selectedKey.y) % KEY_LAYOUT_SIZE.y;
            if(row < 0) { row += KEY_LAYOUT_SIZE.x; }
            if (col < 0) { col += KEY_LAYOUT_SIZE.y; }
            selectedKey.x = row;
            selectedKey.y = col;
            System.out.println("previous: " + previousKey + " selected: " + getSelectedKey() + " notEq: " + (previousKey != getSelectedKey()) + " notNull: " + (getSelectedKey() != Key.VK_NULL));
        } while(previousKey == getSelectedKey() || getSelectedKey() == Key.VK_NULL);
        
        
        virtualKeyboard.selected(getSelectedKey());
    }
    
    private Key getSelectedKey() {
        return keyLayout[selectedKey.x][selectedKey.y];
    }
    
    private Point selectKey(Key key) {
        virtualKeyboard.deselected(getSelectedKey());
        outerloop:
        for(int row = 0; row < KEY_LAYOUT_SIZE.x; row++) {
            for(int col = 0; col < KEY_LAYOUT_SIZE.y; col++) {
                if(keyLayout[row][col] == key) {
                    selectedKey.x = row;
                    selectedKey.y = col;
                    break outerloop;
                }
            }
        }
        virtualKeyboard.selected(getSelectedKey());
        return selectedKey;
    }
    
    private Key[][] getKeyLayout(Key [][] keyRows) {
        Key [][] tmpKeyLayout = new Key[KEY_LAYOUT_SIZE.x][KEY_LAYOUT_SIZE.y];
        for(int row = 0; row < KEY_LAYOUT_SIZE.x; row++) {
            Key [] tmpRow = new Key[KEY_LAYOUT_SIZE.y];
            if(row == KEY_LAYOUT_SIZE.x - 1) {
                // Pad fifth row with shift, enter, backspace, and space key buffers.
                int col = 0;
                // 2 Shift keys.
                int size = 2;
                while(col < size) {
                    tmpRow[col] = keyRows[row][0];
                    col++;
                }
                // 5 Backspace keys.
                size += 5;
                while(col < size) {
                    tmpRow[col] = keyRows[row][1];
                    col++;
                }
                // 5 Space keys.
                size += 5;
                while(col < size) {
                    tmpRow[col] = keyRows[row][2];
                    col++;
                }
                // 2 Done keys.
                size += 2;
                while(col < size) {
                    tmpRow[col] = keyRows[row][3];
                    col++;
                }
            } else if(row == KEY_LAYOUT_SIZE.x - 2) {
                // Pad fourth row with shift and enter key buffers.
                tmpRow[0] = Key.VK_SHIFT;
                tmpRow[1] = Key.VK_SHIFT;
                System.arraycopy(keyRows[row], 0, tmpRow, 2, keyRows[row].length);
                tmpRow[tmpRow.length-2] = Key.VK_ENTER;
                tmpRow[tmpRow.length-1] = Key.VK_ENTER;
            } else {
                // Pad row with null key buffers.
                tmpRow[0] = Key.VK_NULL;
                tmpRow[1] = Key.VK_NULL;
                System.arraycopy(keyRows[row], 0, tmpRow, 2, keyRows[row].length);
                tmpRow[tmpRow.length-2] = Key.VK_NULL;
                tmpRow[tmpRow.length-1] = Key.VK_NULL;
            }
            tmpKeyLayout[row] = tmpRow;
        }
        
        for(Key [] ks: tmpKeyLayout) {
            System.out.print(ks + ": ");
            for(Key k: ks) {
                System.out.print(k + " ");
            } System.out.println();
        }
        
        return tmpKeyLayout;
    }
    
    private static enum Button {
        A(Component.Identifier.Button._0),
        B(Component.Identifier.Button._1),
        X(Component.Identifier.Button._2),
        Y(Component.Identifier.Button._3),
        START(Component.Identifier.Button._7);
        
        private final Identifier identifier;
        private boolean isPressed;
        private boolean isDown;
        private boolean isRepeating;
        private long previousRepeatTime = 0;
        private long elapsedRepeatTime = 0;
        
        private static final Button[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private Button(Identifier identifier) {
            this.identifier = identifier;
            isPressed = false;
        }
        
        public static void checkPressed(Identifier identifider, boolean isPressed) {
            for(int i = 0; i < SIZE; i++) {
                if(VALUES[i].identifier == identifider) {
                    VALUES[i].setPressed(isPressed);
                }
            }
        }
        
        private void setPressed(boolean pressed) {
            if(pressed) {
                if(!isPressed && !isDown) {
                    isPressed = true;
                    isDown = true;
                    previousRepeatTime = System.currentTimeMillis();
                    elapsedRepeatTime = 0;
                    System.out.println(this + " pressed");
                } else {
                    // auto repeat interval set pressed = true;
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        isPressed = true;
                        isRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        isPressed = true;
                        elapsedRepeatTime = 0;
                    }
                }
            } else {
                if(isPressed || isDown) {
                    isPressed = false;
                    isDown = false;
                    isRepeating = false;
                    System.out.println(this + " released");
                    // stop auto press
                }
            }
        }
        
        public boolean isPressed() {
            try {
                return isPressed;
            } finally {
                // Consume the pressed key event.
                isPressed = false;
            }
        }
        
        public boolean isDown() {
            return isDown;
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
        private boolean isDown;
        private boolean isRepeating;
        private long previousRepeatTime = 0;
        private long elapsedRepeatTime = 0;
        
        private static final HatSwitch[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private HatSwitch(float min, float def, float max) {
            this.min = min;
            this.def = def;
            this.max = max;
            isPressed = false;
        }
        
        public static void checkPressed(float value) {
            for(int i = 0; i < SIZE; i++) {
                VALUES[i].setPressed(value);
            }
        }
        
        private void setPressed(float value) {
            if(value == min || value == def || value == max) {
                if(!isPressed && !isDown) {
                    isPressed = true;
                    isDown = true;
                    previousRepeatTime = System.currentTimeMillis();
                    elapsedRepeatTime = 0;
                    System.out.println(this + " pressed");
                } else {
                    // auto repeat interval set pressed = true;
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        isPressed = true;
                        isRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        isPressed = true;
                        elapsedRepeatTime = 0;
                    }
                }
            } else {
                if(isPressed || isDown) {
                    isPressed = false;
                    isDown = false;
                    isRepeating = false;
                    System.out.println(this + " released");
                    // stop auto press
                }
            }
        }
        
        public boolean isPressed() {
            try {
                return isPressed;
            } finally {
                // Consume the pressed key event.
                isPressed = false;
            }
        }
    }
    
    private static enum Axis {
        LEFT_X(Component.Identifier.Axis.X),
        LEFT_Y(Component.Identifier.Axis.Y),
        RIGHT_X(Component.Identifier.Axis.RX),
        RIGHT_Y(Component.Identifier.Axis.RY);
        
        private static final float THRESHOLD = 0.50f;
        private final Identifier identifier;
        private float axisValue;
        private boolean isDown;
        private boolean isRepeating;
        private long previousRepeatTime = 0;
        private long elapsedRepeatTime = 0;
        
        private static final Axis[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private Axis(Identifier identifier) {
            this.identifier = identifier;
            axisValue = 0f;
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
                if(this.axisValue == 0f && !isDown) {
                    this.axisValue = axisValue;
                    isDown = true;
                    previousRepeatTime = System.currentTimeMillis();
                    elapsedRepeatTime = 0;
                    System.out.println(this + " pressed");
                } else {
                    // auto repeat interval set pressed = true;
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        this.axisValue = axisValue;
                        isRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        this.axisValue = axisValue;
                        elapsedRepeatTime = 0;
                    }
                }
            } else {
                if(this.axisValue != 0f || isDown) {
                    this.axisValue = 0f;
                    isDown = false;
                    isRepeating = false;
                    System.out.println(this + " released");
                    // stop auto press
                }
            }
        }
        
        public float getAxisValue() {
            try {
                return axisValue;
            } finally {
                // Consume the axis event.
                axisValue = 0f;
            }
        }
        
        public static Direction getClosestDirectionLeftStick() {
            float x = LEFT_X.getAxisValue();
            float y = LEFT_Y.getAxisValue();
            
            if(Math.abs(x) > Math.abs(y)) {
                if(x > 0) {
                    return Direction.RIGHT;
                } else {
                    return Direction.LEFT;
                }
            } else if (Math.abs(x) < Math.abs(y)) {
                if(y > 0) {
                    return Direction.UP;
                } else {
                    return Direction.DOWN;
                }
            }
            return Direction.NONE;
        }
        
        public static Direction getClosestDirectionRightStick() {
            float x = RIGHT_X.getAxisValue();
            float y = RIGHT_Y.getAxisValue();
            
            if(Math.abs(x) > Math.abs(y)) {
                if(x > 0) {
                    return Direction.RIGHT;
                } else {
                    return Direction.LEFT;
                }
            } else if (Math.abs(x) < Math.abs(y)) {
                if(y > 0) {
                    return Direction.UP;
                } else {
                    return Direction.DOWN;
                }
            }
            return Direction.NONE;
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
                    
                    // Controller is connected.
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
                    
                    // Analog - Axis
                    if(component.isAnalog()){
                        Axis.checkPressed(componentIdentifier, component.getPollData());
                    }
                    // Digital - Buttons & HatSwitch
                    else {
                        // Hat switch.
                        if(componentIdentifier == HatSwitch.identifier) {
                            HatSwitch.checkPressed(component.getPollData());
                        }
                        // Buttons.
                        else {
                            Button.checkPressed(componentIdentifier, component.getPollData() == 1.0f);
                        }
                    }
                }
            }
        }
    }
}
