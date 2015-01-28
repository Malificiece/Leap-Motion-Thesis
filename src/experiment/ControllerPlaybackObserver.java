package experiment;

import enums.Direction;

public interface ControllerPlaybackObserver extends PlaybackObserver {
	public void directionEventObserver(Direction direction);
}
