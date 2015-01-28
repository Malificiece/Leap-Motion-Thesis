package experiment;

import java.util.ArrayList;

import com.leapmotion.leap.Vector;

import enums.Direction;
import enums.Key;

public class PlaybackManager {
	private ArrayList<PlaybackObserver> observers = new ArrayList<PlaybackObserver>();
	private boolean isRepeating = false;
	private Vector point;
	private Vector toolDirection;
	private Direction direction;
	
	// TODO:
	// 1) Need to open and read file based on subject ID or 'tutorial'
	// 2) Scan through contents and determine start time
	// 3) Use a timer to know when to throw events
	// 4) Use observer pattern to update keyboard contents
	// 5) If repeat is enabled, loop playback until shutdown.
	// 6) If repeat is disabled, play once
	
	public PlaybackManager(boolean repeat) {
		isRepeating = repeat;
		// need to know what file to read/open
		// use subject ID + keyboard + * + .Dat to get file
	}
	
	public void update() {
		// if still events to fire:
		// go through data and fire events
		
		if(isRepeating /*&& at end of data*/) {
			// restart data here
		}
	}
	
	public void setRepeat(boolean repeat) {
		isRepeating = repeat;
	}
	
    public void registerObserver(PlaybackObserver observer) {
        if(observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }
    
    public void removeObserver(PlaybackObserver observer) {
        observers.remove(observer);
    }

    protected void notifyListenersSpecialEvent() {
        for(PlaybackObserver observer : observers) {
        	if(observer instanceof ControllerPlaybackObserver) {
        		((ControllerPlaybackObserver) observer).directionEventObserver(direction);
        	} else if(observer instanceof LeapPlaybackObserver) {
        		((LeapPlaybackObserver) observer).positionEventObservered(point, toolDirection);
        	} else if(observer instanceof TabletPlaybackObserver) {
        		((TabletPlaybackObserver) observer).touchEventObserved(point);
        	} else {
        		// No special playback data.
        	}
        }
    }
    
    protected void notifyListenersPressedEvent(Key key, boolean upper) {
        for(PlaybackObserver observer : observers) {
        	observer.pressedEventObserved(key, upper);
        }
    }
}
