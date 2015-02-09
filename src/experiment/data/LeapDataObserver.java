package experiment.data;

import com.leapmotion.leap.Vector;

public interface LeapDataObserver {
    public void leapToolDataEventObserved(Vector leapPoint, Vector toolDirection);
    public void leapHandDataEventObserved(Vector leapPoint);
}
