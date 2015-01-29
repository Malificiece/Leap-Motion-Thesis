package experiment.playback;

import enums.Direction;

public interface ControllerPlaybackObserver extends PlaybackObserver {
	public void directionEventObserved(Direction direction);
	public void resetEventObserved();
}
