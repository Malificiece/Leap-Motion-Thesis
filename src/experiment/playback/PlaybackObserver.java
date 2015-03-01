package experiment.playback;

import enums.Key;

public interface PlaybackObserver {
	public void pressedEventObserved(Key key);
}
