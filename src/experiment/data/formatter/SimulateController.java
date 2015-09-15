package experiment.data.formatter;

import keyboard.IKeyboard;
import utilities.Point;
import enums.Attribute;
import enums.Direction;
import enums.Key;

public class SimulateController {
    private final Point KEY_LAYOUT_SIZE = new Point(4, 10); // new Point(5, 14);
    private Point selectedKey;
    private Key[][] keyLayout;
    
    public SimulateController(IKeyboard keyboard) {
        this.keyLayout = getKeyLayout((Key[][]) keyboard.getAttributes().getAttribute(Attribute.KEY_ROWS).getValue());
        selectedKey = new Point(0, 0);
        // default selected is q
        selectKey(Key.VK_Q);
    }
    
    public void moveSelectedKey(Direction direction) {
        int rowDelta = 0;
        int colDelta = 0;
        switch(direction) {
            case DOWN:
                rowDelta = 1;
                break;
            case LEFT:
                colDelta = -1;
                break;
            case RIGHT:
                colDelta = 1;
                break;
            case UP:
                rowDelta = -1;
                break;
            default: return;
        }
        
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
    }
    
    public Key getSelectedKey() {
        return keyLayout[(int) selectedKey.x][(int) selectedKey.y];
    }
    
    public Point selectKey(Key key) {
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
}
