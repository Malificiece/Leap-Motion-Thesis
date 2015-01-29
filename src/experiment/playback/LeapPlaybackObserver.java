package experiment.playback;

import com.leapmotion.leap.Vector;

public interface LeapPlaybackObserver extends PlaybackObserver {
	public void positionEventObserved(Vector leapPoint);
	public void directionEventObserved(Vector toolDirection);
}
