package experiment;

import enums.Key;

public interface PlaybackObserver {
	public void pressedEventObserved(Key key, boolean upper);
}
