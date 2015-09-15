package experiment;

import enums.Key;

public interface WordObserver {
    public void wordSetEventObserved(String word);
    public void currentLetterIndexChangedEventObservered(int letterIndex, Key key);
    public void matchEventObserved();
}
