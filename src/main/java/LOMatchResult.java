import java.util.ArrayList;
import java.util.List;

/**
 * Created by shukrun on 10/30/2017.
 */
public class LOMatchResult {
    private List<Integer> mergeLines;
    private int wordIndex;

    public LOMatchResult(){
        mergeLines = new ArrayList<Integer>();
        wordIndex = -1;
    }

    public boolean isEmpty(){
        return mergeLines.size() == 0;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public void add(Integer lineIndex){
        mergeLines.add(lineIndex);
    }

    public List<Integer> getMergeLines() {
        return mergeLines;
    }

    public int getChangedWord() {
        return wordIndex;
    }
}
