package experiment.playback;

import com.leapmotion.leap.Vector;

public interface LeapPlaybackObserver extends PlaybackObserver {
	public void positionEventObservered(Vector leapPoint, Vector toolDirection);
}
