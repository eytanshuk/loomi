import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LoomLogLinesMatcher will search for matching lines.
 * A matching lines are lines that are differ in one world only.
 * Every line can have only one word changed. //assumptions - line can be matched 1 time and on 1 word
 */
public class LoomLogLinesMatcher {

    private static final String EMPTY_SPACE_SEPER = " ";

    private final int prefixOffset;//skipping time prefixes
    //holds the lines splitted to words
    private final SplitterDataRepo dataR;
    //holds already match String
    private final Set<Integer> matchedIndexSet;
    //caches result not to duplicate compares
    private LOMatcherRepo compareCacheResults;
    //save all the groups result
    private List<LOMatchResult> results = new ArrayList<LOMatchResult>();

    /**
     * Construct an Object
     * @param data array of lines to search for matching
     * @param startOffset if there an offset in log i.e. like timestamp printing that should be excluded.
     */
    public LoomLogLinesMatcher(String[] data, int startOffset) {
        matchedIndexSet = new HashSet<Integer>();
        compareCacheResults = new LOMatcherRepo();
        dataR = new SplitterDataRepo(data, EMPTY_SPACE_SEPER ,startOffset);
        this.prefixOffset = startOffset;
    }


    /**
     * Search for a matching lines.
     * will compare each line with all others lines for pattern matching.
     * line can be match 1 time only because only one word can change in a phrase.
     * Complexity: worst case will compare every word on specific line against all other words in other lines on specific index
     * meaning: if we have n lines and k words (k the longest line for simplicity) it will be k*n*n
     * @return List<LOMatchResult> list of the matching results, each result contain the line indexes and the word index of the
     * changed word.
     */
    public List<LOMatchResult> matchPatterns(){
        //go through every line to search pattern on a certain word index
        for (int srcLineIndex=0;srcLineIndex < dataR.getNumOfLines();srcLineIndex++){
            //matched line cannot be mtached again
            if (matchedIndexSet.contains(srcLineIndex)){
                continue;
            }
            String[] srcLineParts = dataR.getLineSplitted(srcLineIndex);
            LOMatchResult groupRes = new LOMatchResult();
            //check for patterns on a certain index
            for (int wordInd=prefixOffset ; wordInd< srcLineParts.length;wordInd++) {
                //go over all the lines, on specicif index
                if(isLineMatchOnSpecificIndex(srcLineIndex, srcLineParts, groupRes, wordInd)){
                    matchedIndexSet.add(srcLineIndex);
                    break;//found matching on specific word index skip other indexes
                }
            }
        }
        return results;
    }

    /**
     * compare src line with all other lines on a specific wordInd.
     * will not search on already matched lines.
     * return true if lines are matched
     */
    private boolean isLineMatchOnSpecificIndex(int srcLineIndex, String[] srcLineParts, LOMatchResult groupRes, int wordInd) {
        //go over all lines and check matching on specific word index
        for (int currlineInd = 0; currlineInd < dataR.getNumOfLines(); currlineInd++) {
            if(matchedIndexSet.contains(currlineInd)){//if macthed already skip it
                continue;
            }
            String[] currWordParts = dataR.getLineSplitted(currlineInd);
            //first check length and if the word index are not equal than will test other words by the index.
            boolean shouldCompareLines = srcLineParts.length == currWordParts.length && !srcLineParts[wordInd].equals(currWordParts[wordInd]);
            if (shouldCompareLines) {
                //dont compare same line
                if (srcLineIndex != currlineInd) {
                    //compare every word in lines
                    compareWordsInLine(srcLineIndex, wordInd, currlineInd, groupRes);
                }
            }
        }//for every line
        return !groupRes.isEmpty();
    }
    //compare all words in lines, update results
    private void compareWordsInLine(int firstIndex, int wordInd, int secIndex, LOMatchResult groupRes) {
        String[] srcLine = dataR.getLineSplitted(firstIndex);
        String[] currWordParts = dataR.getLineSplitted(secIndex);
        int j = prefixOffset;
        for (; j < currWordParts.length; j++) {
            if(j==wordInd){
                continue;//skip the changed word in pattern candidate
            }
            //caching results not to re-compare same string pair
            Boolean isEqual = compareCacheResults.getResult(firstIndex, secIndex, j);
            if (isEqual == null) {//not computed, compare and add to compare cache
                isEqual = currWordParts[j].length() == srcLine[j].length() && currWordParts[j].equals(srcLine[j]);
                compareCacheResults.add(firstIndex, secIndex, j, isEqual);
            }
            //break the loop, or same index words are equal or notEqual and diffrent index
            if (!isEqual) {
                break;//break the identical should not be equal
            }
        }//for every word in current line
        //in case we got to the end, update match
        if(j == currWordParts.length) {
            updatedMatch(firstIndex, secIndex, wordInd, groupRes);
        }
    }

    private void updatedMatch(int srcLineIndex, int currLineIndex, int wordIndex, LOMatchResult groupRes) {
        //add matched line, if they macyhed already they cannot match other lines
        matchedIndexSet.add(currLineIndex);
        if(groupRes.isEmpty()) {
            groupRes.add(srcLineIndex);
            groupRes.setWordIndex(wordIndex);
            results.add(groupRes);
        }
        groupRes.add(currLineIndex);
    }

    public void printOutput(){
        System.out.println("printing results");
        for (LOMatchResult r:results) {
            System.out.println("*****************");
            StringBuilder sb = new StringBuilder("The changing word was: ");
            List<Integer> mergeLines = r.getMergeLines();
            if(mergeLines.size() == 0){
                System.out.println("no result");
            } else {
                for (int matchedIndex : mergeLines) {
                    String[] lineSplitted = dataR.getLineSplitted(matchedIndex);
                    for (String s : lineSplitted) {
                        System.out.print(s + " ");
                    }
                    System.out.println();
                    sb.append(dataR.getLineSplitted(matchedIndex)[r.getChangedWord()]).append(", ");
                }
                //printing the change word
                System.out.println();
                System.out.println(sb.toString());
                System.out.println("*****************");
            }
        }
        System.out.println("** end printing");

    }


    public static void main(String[] args) {

        String patter1 = "is going to a car";
        String patter2 = "is eating in a resterant";
        String patter3 = "is getting into the car";
        String noP = "just words no paatern gf ddd tt uuuuu frty";

        //3 diifrent data types
        String[] data= {"eytan " +patter1, "danny "+patter1, "boaz " +patter1,  "tomer "+ patter1, noP};
        String[] data2= {"eytan " +patter2, "danny "+patter3, "Boaz " +patter3,noP, noP};
        String[] data3= {"eytan " +patter2, "eytan " +patter2,"eytan " +patter2,"eytan " +patter2,noP, noP};

        LoomLogLinesMatcher groper = new LoomLogLinesMatcher(data,0);
        groper.matchPatterns();
        groper.printOutput();

        LoomLogLinesMatcher groper2 = new LoomLogLinesMatcher(data2,0);
        groper2.matchPatterns();
        groper2.printOutput();

        LoomLogLinesMatcher groper3 = new LoomLogLinesMatcher(data3,0);
        groper3.matchPatterns();
        groper3.printOutput();

    }

}
