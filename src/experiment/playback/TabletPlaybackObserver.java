package experiment.playback;

import com.leapmotion.leap.Vector;

public interface TabletPlaybackObserver extends PlaybackObserver {
	public void touchEventObserved(Vector touchPoint);
}
