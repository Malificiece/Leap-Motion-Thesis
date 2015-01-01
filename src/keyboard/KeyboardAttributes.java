package keyboard;

import java.util.ArrayList;
import java.util.TreeMap;

import com.leapmotion.leap.Vector;

public abstract class KeyboardAttributes {
    private TreeMap<String, KeyboardAttribute> keyboardAttributes = new TreeMap<String, KeyboardAttribute>();
    
    public void addAttribute(KeyboardAttribute attribute) {
        keyboardAttributes.put(attribute.getName(), attribute);
    }
    
    public KeyboardAttribute getAttributeByName(String name) {
        return keyboardAttributes.get(name);
    }
    
    public Object getValueByName(String name) {
        return keyboardAttributes.get(name).getValue();
    }
    
    public Integer getValueByNameAsInteger(String name) {
        return keyboardAttributes.get(name).getValueAsInteger();
    }
    
    public Vector getValueByNameAsVector(String name) {
        return keyboardAttributes.get(name).getValueAsVector();
    }
    
    public ArrayList<KeyboardAttribute> getAllAttributes() {
        return new ArrayList<KeyboardAttribute>(keyboardAttributes.values());
    }
}
