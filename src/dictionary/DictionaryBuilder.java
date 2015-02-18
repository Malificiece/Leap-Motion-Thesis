package dictionary;

public class DictionaryBuilder {
	// TODO:
	// 1) Use a modifier to enable and disable this class.
	// 2) Decide if we want the enable/disable option or a button to perform this class's actions.
	// 3) Scan through the library and find like gestures
	//		- Look for distance between keys
	//		- Number of like keys / number of keys
	//		- Similar patterns in gestures
	// We can determine the similarity by measuring the total length of the path. Then, with the total length,
    // we can then trace along the two lines in equal intervals. By doing this, we can measure the distance
    // between the intervals no matter how many vertices each line has. The total distance between all intervals
    // is the measure of dissimilarity between the two lines. The more intervals we use the more accurate the
    // calculation will be but the longer it will take. A dissimilarity measure of 0 means two lines are identical.
    
    // edges = word length - 1
    // verticies = word length
    // let's only compare words of the same number of verticies?
    // this will more evenly distribute the size of the words.
    
	private boolean isEnabled = false;

    public DictionaryBuilder() {
		isEnabled = true;
	}
    
    public void update() {
        // now that it is built, do all the work here and then disable when finished
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled ;
    }
}
