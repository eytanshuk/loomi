import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shukrun on 11/4/2017.
 */
public class RunMe {
    public static void main(String[] args) {
        try {
            String fileName = args[0];
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String currLine;
            while( (currLine = reader.readLine()) != null){
                lines.add(currLine);
            }
            String[] data = lines.toArray(new String[lines.size()]);

            LoomLogLinesMatcher matcher = new LoomLogLinesMatcher(data, 2);
            List<LOMatchResult> loMatchResults = matcher.matchPatterns();
            matcher.printOutput();

        }catch (Exception e){
            System.out.println("Usage: java -cp target\\group-similar-log-1.0-SNAPSHOT.jar RunMe [path to input file]");
            e.printStackTrace();
        }
    }
}
