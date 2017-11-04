import java.util.ArrayList;

/**
 * Created by shukrun on 11/1/2017.
 */
public class SplitterDataRepo {

    //using direct access list
    private ArrayList<String[]> ds = new ArrayList();

    public SplitterDataRepo(String[] srcData, String seperator, int offset) {
        parseData(srcData, seperator);
    }

    private void parseData(String[] data, String seperator) {
        for (String line:data) {
            if(line != null) {
                ds.add(line.split(seperator));//use regex to optimize
            }//skipped empty lines
        }
    }

    String[] getLineSplitted(int index){
        return ds.get(index);
    }

    String getWordInLine(int lineInd, int wordInd){
        return ds.get(lineInd)[wordInd];
    }

    int getNumOfLines(){
        return ds.size();
    }
}
