package keyboard;

import utilities.Point;
import java.util.ArrayList;
import java.util.TreeMap;

import com.leapmotion.leap.Vector;

import enums.Attribute;

public abstract class KeyboardAttributes {
    private TreeMap<Attribute, KeyboardAttribute> keyboardAttributes = new TreeMap<Attribute, KeyboardAttribute>();
    
    public void addAttribute(KeyboardAttribute attribute) {
        keyboardAttributes.put(attribute.getType(), attribute);
    }
    
    public KeyboardAttribute getAttribute(Attribute attribute) {
        return keyboardAttributes.get(attribute);
    }
    
    public Object getAttributeValue(Attribute attribute) {
        KeyboardAttribute ka = keyboardAttributes.get(attribute);
        if(ka != null) {
            return ka.getValue();
        }
        return null;
    }
    
    public Integer getAttributeAsInteger(Attribute attribute) {
        KeyboardAttribute ka = keyboardAttributes.get(attribute);
        if(ka != null) {
            return ka.getValueAsInteger();
        }
        return null;
    }
    
    public Float getAttributeAsFloat(Attribute attribute) {
        KeyboardAttribute ka = keyboardAttributes.get(attribute);
        if(ka != null) {
            return ka.getValueAsFloat();
        }
        return null;
    }
    
    public Vector getAttributeAsVector(Attribute attribute) {
        KeyboardAttribute ka = keyboardAttributes.get(attribute);
        if(ka != null) {
            return ka.getValueAsVector();
        }
        return null;
    }
    
    public Point getAttributeAsPoint(Attribute attribute) {
        KeyboardAttribute ka = keyboardAttributes.get(attribute);
        if(ka != null) {
            return ka.getValueAsPoint();
        }
        return null;
    }
    
    public ArrayList<KeyboardAttribute> getAllAttributes() {
        return new ArrayList<KeyboardAttribute>(keyboardAttributes.values());
    }
}
