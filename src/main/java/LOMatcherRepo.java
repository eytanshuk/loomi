import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for saving compare string results
 */
public class LOMatcherRepo {

    //basic hashTable
    private Map<String, Boolean> resultMap = new HashMap<String, Boolean>();

    void add(int firstInd,int secondInd, int wordInd, boolean result){
        String key=createKey(firstInd, secondInd, wordInd);
        resultMap.put(key,result);
    }

    //null if not exists
    Boolean getResult(int firstInd,int secondInd, int wordInd){
        return resultMap.get(createKey(firstInd,secondInd,wordInd));
    }


    //create simple key
    private String createKey(int firstInd, int secondInd, int wordInd) {
        return firstInd+"_"+secondInd+"_"+wordInd;
    }
}
