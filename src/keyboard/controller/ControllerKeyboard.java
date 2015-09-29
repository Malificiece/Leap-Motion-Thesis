package keyboard.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import utilities.Point;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.leapmotion.leap.Vector;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;
import net.java.games.input.Component.Identifier;
import utilities.MyUtilities;
import keyboard.IKeyboard;
import keyboard.KeyboardGesture;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.VirtualKeyboard;
import enums.Attribute;
import enums.Direction;
import enums.Gesture;
import enums.Key;
import enums.KeyboardType;
import enums.Renderable;
import experiment.WordManager;
import experiment.WordObserver;
import experiment.data.ControllerDataObserver;
import experiment.data.DataManager;
import experiment.playback.ControllerPlaybackObserver;
import experiment.playback.PlaybackManager;

public class ControllerKeyboard extends IKeyboard implements ControllerPlaybackObserver, WordObserver {
    private static final float AUTO_REPEAT_DELAY = (750 * 1f/3f) + 250; // Windows default
    private static final int AUTO_REPEAT_RATE = 1000 / 31; // Windows default
    private final float HORIZONTAL_GESTURE_LENGTH = 125f;
    private final float VERTICAL_GESTURE_LENGTH;
    private final float HORIZONTAL_GESTURE_OFFSET = 25f;
    private final float VERTICAL_GESTURE_OFFSET;
    private final float CAMERA_DISTANCE;
    private final Point KEY_LAYOUT_SIZE = new Point(4, 10); // new Point(5, 14);
    private final ReentrantLock CONTROLLER_LOCK = new ReentrantLock();
    private ArrayList<ControllerDataObserver> observers = new ArrayList<ControllerDataObserver>();
    private boolean isCalibrated = false;
    private VirtualKeyboard virtualKeyboard;
    private KeyboardGestures keyboardGestures;
    private GamePad gamePad;
    private Point selectedKey;
    private Key[][] keyLayout;
    private boolean shiftOnce = false;
    private boolean shiftTwice = false;
    private Timer detectedMatchTimer;
    
    public ControllerKeyboard(KeyboardType keyboardType) {
        super(keyboardType);
        keyboardAttributes = new ControllerAttributes(this);
        keyboardSettings = new ControllerSettings(this);
        this.loadDefaultSettings();
        keyboardRenderables = new ControllerRenderables(this);
        CAMERA_DISTANCE = keyboardAttributes.getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
        keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        float borderSize = keyboardAttributes.getAttributeAsFloat(Attribute.BORDER_SIZE) * 2;
        imageSize = new Point(keyboardSize.x + borderSize, keyboardSize.y + borderSize);
        VERTICAL_GESTURE_LENGTH = HORIZONTAL_GESTURE_LENGTH * (imageSize.y/(float)imageSize.x);
        VERTICAL_GESTURE_OFFSET = HORIZONTAL_GESTURE_OFFSET * (imageSize.y/(float)imageSize.x);
        if(Gesture.ENABLED) {
            keyboardGestures = (KeyboardGestures) keyboardRenderables.getRenderable(Renderable.KEYBOARD_GESTURES);
        }
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(Renderable.VIRTUAL_KEYBOARD);
        gamePad = new GamePad();
        keyLayout = getKeyLayout((Key[][]) keyboardAttributes.getAttribute(Attribute.KEY_ROWS).getValue());
        selectedKey = new Point(0, 0);
        // default selected is q
        selectKey(Key.VK_Q);
        
        detectedMatchTimer = new Timer(AUTO_REPEAT_RATE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detectedMatchTimer.stop();
                keyPressed = Key.VK_ENTER.getValue();
                notifyListenersKeyEvent();
            }
        });
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
        if(Gesture.ENABLED) {
            // Remove finished gestures, update others.
            keyboardGestures.removeAndUpdateGestures();
        }
        
        if(isPlayingBack()) {
            // reset buttons
            for(Button button: Button.values()) {
                Button.checkPressed(button.getIdentifier(), false);
            }
            Axis.checkPressed(Axis.LEFT_X.getIdentifier(), 0f);
            Axis.checkPressed(Axis.LEFT_Y.getIdentifier(), 0f);
            playbackManager.update();
        } else {
            gamePad.update();
        }
        
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
                detectedMatchTimer.stop();
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
        
        // REMOVE Y FROM HERE IF WE EVER CHANGE BACK
        if(/*Button.B.isDown()*/ Button.Y.isDown()) {
            virtualKeyboard.pressed(Key.VK_BACK_SPACE);
            if(/*Button.B.isPressed()*/ Button.Y.isPressed()) {
                detectedMatchTimer.stop();
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
        
        /*if(Button.X.isDown()) {
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
        }*/
        
        // Left Stick, moves keyboard (maybe add gestures)
        moveSelectedKey(Axis.getClosestDirectionLeftStick());
        
        // Right Stick, moves keyboard (maybe add gestures)
        moveSelectedKey(Axis.getClosestDirectionRightStick());
        
        // Hat Switch, create gestures
        for(HatSwitch hatSwitch: HatSwitch.values()) {
            if(Gesture.ENABLED) {
                KeyboardGesture gesture = createSwipeGesture(hatSwitch.isPressed());
                if(gesture != null) {
                    keyboardGestures.addGesture(gesture);
                }
            } else {
                moveSelectedKey(hatSwitch.isPressed());
            }
        }
    }
    
    @Override
    public void addToUI(JPanel panel, GLCanvas canvas) {
        WordManager.registerObserver(this);
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        WordManager.removeObserver(this);
    }
    
    public void registerObserver(ControllerDataObserver observer) {
        if(observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }
    
    public void removeObserver(ControllerDataObserver observer) {
        observers.remove(observer);
    }

    protected void notifyListenersDirectionEvent(Direction direction) {
        for(ControllerDataObserver observer : observers) {
            observer.controllerDataEventObserved(direction);
        }
    }
    
    @Override
    protected boolean isPlayingBack() {
        CONTROLLER_LOCK.lock();
        try {
            return isPlayback;
        } finally {
            CONTROLLER_LOCK.unlock();
        }
    }
    
    @Override
    public void beginPlayback(PlaybackManager playbackManager) {
        CONTROLLER_LOCK.lock();
        try {
            selectKey(Key.VK_Q);
            isPlayback = true;
            playbackManager.registerObserver(this);
            this.playbackManager = playbackManager;
        } finally {
            CONTROLLER_LOCK.unlock();
        }
    }
    
    @Override
    public void resetEventObserved() {
        selectKey(Key.VK_Q);
    }
    
	@Override
	public void pressedEventObserved(Key key) {
	    switch(key) {
	        case VK_BACK_SPACE:
	            Button.checkPressed(/*Button.B.getIdentifier()*/Button.Y.getIdentifier(), true);
	            break;
	        /*case VK_SPACE:
	            Button.checkPressed(Button.Y.getIdentifier(), true);
	            break;*/
	        case VK_ENTER:
	            //Button.checkPressed(Button.START.getIdentifier(), true);
	            keyPressed = key.getValue();
	            notifyListenersKeyEvent();
	            virtualKeyboard.pressed(key);
	            break;
	        /*case VK_SHIFT:
	            Button.checkPressed(Button.X.getIdentifier(), true);
	            break;*/
	        default:
	            if(key.isAlphaNumeric()) {
	                Button.checkPressed(Button.A.getIdentifier(), true);
	            }
	            break;
	    }
	}

	@Override
	public void directionEventObserved(Direction direction) {
		switch(direction) {
    		case LEFT:
    		    Axis.checkPressed(Axis.LEFT_X.getIdentifier(), -1.0f);
    		    break;
    		case RIGHT:
    		    Axis.checkPressed(Axis.LEFT_X.getIdentifier(), 1.0f);
    		    break;
    		case UP:
    		    Axis.checkPressed(Axis.LEFT_Y.getIdentifier(), -1.0f);
    		    break;
    		case DOWN:
    		    Axis.checkPressed(Axis.LEFT_Y.getIdentifier(), 1.0f);
    		    break;
            default: break;
		}
	}
    
    @Override
    public void finishPlayback(PlaybackManager playbackManager) {
        CONTROLLER_LOCK.lock();
        try {
            playbackManager.removeObserver(this);
            isPlayback = false;
            this.playbackManager = null;
        } finally {
            CONTROLLER_LOCK.unlock();
        }
    }
    
    @Override
    public void beginExperiment(DataManager dataManager) {
        CONTROLLER_LOCK.lock();
        try {
            selectKey(Key.VK_Q);
            this.registerObserver(dataManager);
        } finally {
            CONTROLLER_LOCK.unlock();
        }
    }
    
    @Override
    public void finishExperiment(DataManager dataManager) {
        CONTROLLER_LOCK.lock();
        try {
            this.removeObserver(dataManager);
        } finally {
            CONTROLLER_LOCK.unlock();
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
    
    @Override
    public void wordSetEventObserved(String word) {
        // Do nothing
    }

    @Override
    public void currentLetterIndexChangedEventObservered(int letterIndex, Key key) {
        // Do nothing
    }

    @Override
    public void matchEventObserved() {
        if(!isPlayingBack()) {
            // Start timer for matched event
            detectedMatchTimer.start();
        }
    }
    
    private KeyboardGesture createSwipeGesture(Direction direction) {
        if(Gesture.ENABLED) {
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
                default: break;
            }
            return gesture;
        }
        return null;
    }
    
    private void moveSelectedKey(Direction direction) {
        int rowDelta = 0;
        int colDelta = 0;
        switch(direction) {
            case DOWN:
                rowDelta = 1;
                notifyListenersDirectionEvent(Direction.DOWN);
                break;
            case LEFT:
                colDelta = -1;
                notifyListenersDirectionEvent(Direction.LEFT);
                break;
            case RIGHT:
                colDelta = 1;
                notifyListenersDirectionEvent(Direction.RIGHT);
                break;
            case UP:
                rowDelta = -1;
                notifyListenersDirectionEvent(Direction.UP);
                break;
            default: return;
        }
        
        virtualKeyboard.deselected(getSelectedKey());
        Key previousKey;
        do {
            previousKey = getSelectedKey();
            if(previousKey == Key.VK_BACK_SPACE && (direction == Direction.LEFT || direction == Direction.RIGHT)) { // REMOVE ME IF WE EVER CHANGE THINGS BACK
                break;
            }
            int row = (int) ((rowDelta + selectedKey.x) % KEY_LAYOUT_SIZE.x);
            int col = (int) ((colDelta + selectedKey.y) % KEY_LAYOUT_SIZE.y);
            if(row < 0) { row += KEY_LAYOUT_SIZE.x; }
            if (col < 0) { col += KEY_LAYOUT_SIZE.y; }
            selectedKey.x = row;
            selectedKey.y = col;
        } while(previousKey == getSelectedKey() || getSelectedKey() == Key.VK_NULL);
        
        virtualKeyboard.selected(getSelectedKey());
    }
    
    private Key getSelectedKey() {
        return keyLayout[(int) selectedKey.x][(int) selectedKey.y];
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
    
    private Key[][] getKeyLayout(Key[][] keyRows) {
        Key[][] tmpKeyLayout = new Key[(int) KEY_LAYOUT_SIZE.x][(int) KEY_LAYOUT_SIZE.y];
        for(int row = 0; row < KEY_LAYOUT_SIZE.x; row++) {
            Key[] tmpRow = new Key[(int) KEY_LAYOUT_SIZE.y];
            if(row == KEY_LAYOUT_SIZE.x - 1) {
                // Pad Last row with backspace buffers and a null buffer.
                { // THIS SECTION IS BECAUSE WE GOT RID OF ALL SPECIAL KEYS EXCEPT BACKSPACE
                    for(int col = 0; col < KEY_LAYOUT_SIZE.y; col++) {
                        tmpRow[col] = keyRows[row][0];
                    }
                }
            } else {
                // Pad row with null key buffers.
                System.arraycopy(keyRows[row], 0, tmpRow, 0, keyRows[row].length);
            }
            tmpKeyLayout[row] = tmpRow;
        }
        return tmpKeyLayout;
    }
    
    /*private Key[][] getKeyLayout(Key[][] keyRows) {
        Key[][] tmpKeyLayout = new Key[KEY_LAYOUT_SIZE.x][KEY_LAYOUT_SIZE.y];
        for(int row = 0; row < KEY_LAYOUT_SIZE.x; row++) {
            Key[] tmpRow = new Key[KEY_LAYOUT_SIZE.y];
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
        return tmpKeyLayout;
    }*/
    
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
        
        public Identifier getIdentifier() {
            return identifier;
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
                } else {
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
        LEFT(0.875f, 1.0f, 0.125f, Direction.LEFT),
        UP(0.125f, 0.25f, 0.375f, Direction.UP),
        RIGHT(0.375f, 0.5f, 0.625f, Direction.RIGHT),
        DOWN(0.625f, 0.75f, 0.875f, Direction.DOWN);
        
        public final static Identifier identifier = Component.Identifier.Axis.POV;
        private final float min;
        private final float def;
        private final float max;
        private final Direction direction;
        private boolean isPressed;
        private boolean isDown;
        private boolean isRepeating;
        private long previousRepeatTime = 0;
        private long elapsedRepeatTime = 0;
        
        private static final HatSwitch[] VALUES = values();
        private static final int SIZE = VALUES.length;
        
        private HatSwitch(float min, float def, float max, Direction direction) {
            this.min = min;
            this.def = def;
            this.max = max;
            this.direction = direction;
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
                } else {
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
                }
            }
        }
        
        public Direction isPressed() {
            try {
                if(isPressed) {
                    return direction;
                } else {
                    return Direction.NONE;
                }
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
        
        public Identifier getIdentifier() {
            return identifier;
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
                } else {
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
                    return Direction.DOWN;
                } else {
                    return Direction.UP;
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
                    return Direction.DOWN;
                } else {
                    return Direction.UP;
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
