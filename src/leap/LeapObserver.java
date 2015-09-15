package leap;

import com.leapmotion.leap.InteractionBox;

public interface LeapObserver {
    public void leapEventObserved(LeapData leapData);
    public void leapInteractionBoxSet(InteractionBox iBox);
}
