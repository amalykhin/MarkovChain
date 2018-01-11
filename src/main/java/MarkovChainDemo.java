import edu.stanford.nlp.simple.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class MarkovChainDemo {
    public static void main(String[] args) {
        String str = "";
        try {
            Path filePath = Paths.get("src/main/resources/text").toAbsolutePath();
            //System.out.println(filePath);
            str = Files.lines(filePath).reduce("", (s, l) -> s.concat(l));
//            System.out.println(str);
        }
        catch (IOException e) {
            System.err.println("IO Exception");
        }


        /*chain.entrySet()
                .stream()
                .forEach(e -> System.out.println(e.getKey()+" "+e.getValue()));
                */


    }
}
