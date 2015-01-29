package experiment.playback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import utilities.FileUtilities;
import utilities.MyUtilities;
import keyboard.IKeyboard;

import com.leapmotion.leap.Vector;

import enums.DataType;
import enums.Direction;
import enums.FileExt;
import enums.FilePath;
import enums.Key;

public class PlaybackManager {
	private ArrayList<PlaybackObserver> observers = new ArrayList<PlaybackObserver>();
	private boolean isRepeating = false;
	private Vector point;
	private Vector toolDirection;
	private Direction direction;
	private ArrayList<PlaybackData> playbackData = new ArrayList<PlaybackData>();
	private int dataIndex = 0;
	private long elapsedTime = 0;
	private long previousTime;
	private long currentTime;
	
	// TODO:
	// 1) Need to open and read file based on subject ID or 'tutorial'
	// 2) Scan through contents and determine start time
	// 3) Use a timer to know when to throw events
	// 4) Use observer pattern to update keyboard contents
	// 5) If repeat is enabled, loop playback until shutdown.
	// 6) If repeat is disabled, play once
	
	public PlaybackManager(boolean repeat, String subjectID, IKeyboard keyboard) {
		isRepeating = repeat;
		String filePath = FilePath.DATA.getPath() + subjectID + "/";;
		String wildcardFileName = subjectID + "_" + keyboard.getFileName() + FileUtilities.WILDCARD + FileExt.DAT.getExt();
		try {
            ArrayList<String> fileData = MyUtilities.FILE_IO_UTILITIES.readListFromWildcardFile(filePath, wildcardFileName);
            parseFileData(fileData);
        } catch (IOException e) {
            System.out.println("Failed to open up data file for playback.");
            e.printStackTrace();
        }
		currentTime = 0;
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
    
    private void parseFileData(ArrayList<String> fileData) {
        for(String line: fileData) {
            // separate based on white space --- my format sucks so this is annoying
            //playbackData.add(e);
        }
    }
    
    private class PlaybackData {
        private long eventTime;
        private ArrayList<Entry<DataType, Object>> eventData;
        private int eventIndex = 0;
        
        PlaybackData(long eventTime, ArrayList<Entry<DataType, Object>> eventData) {
            this.eventTime = eventTime;
            this.eventData = eventData;
        }
        
        public long getTime() {
            return eventTime;
        }
        
        public boolean hasNext() {
            return eventIndex < eventData.size();
        }
        
        public Entry<DataType, Object> next() {
            if(hasNext()) {
                return eventData.get(eventIndex++);
            }
            return null;
        }
    }
}
