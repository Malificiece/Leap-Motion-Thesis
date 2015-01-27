package experiment;

import com.leapmotion.leap.Vector;

public interface LeapDataObserver {
    public void leapDataEventObserved(Vector leapPoint, Vector toolDirection);
}
