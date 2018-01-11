import edu.stanford.nlp.simple.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MarkovChain {
    Map<String, Map<String, Double>> chain;
    Map<String, Double> startingWords;

    MarkovChain (String str) {
        Document doc = new Document(str);
        List<String> words = doc.sentences().stream()
                .flatMap(s -> s.words().stream())
                .collect(Collectors.toList());
        startingWords = new HashMap<>();
        // Initialize startingWords with starting words (duh), and number of their occurence.
        doc.sentences().stream()
                .map(s -> s.words().get(0))
                .forEach((String w) -> {
                    if (startingWords.putIfAbsent(w, 1.0) != null)
                        startingWords.replace(w, startingWords.get(w)+1);
                });
        // Compute probabilities for all the starting words.
        for (Map.Entry<String, Double> word : startingWords.entrySet())
            startingWords.compute(word.getKey(), (k, v) -> v/startingWords.values().size());
        //System.out.println(startingWords);
        //words.stream().forEach(s -> System.out.println(s));
        Map<String, Map<String, Double>> chain = new HashMap<>();
        Map<String, Double> num = new HashMap<>();

        String prev;
        prev = null;
        for (String word : words) {
            if (prev == null) {
                prev = word;
                continue;
            }
            if (!chain.containsKey(prev)) {
                chain.put(prev, new HashMap<>());
                num.put(prev, 0.0);
            }
            Map<String, Double> chainRow;
            if (!(chainRow = chain.get(prev)).containsKey(word))
                chainRow.put(word, 0.0);
            chain.get(prev).replace(word, chain.get(prev).get(word)+1);
            num.replace(prev, num.get(prev)+1);

            prev = word;
        }

        for (Map.Entry<String, Map<String,Double>> row : chain.entrySet())
            for (String word : row.getValue().keySet())
                row.getValue().compute(word, (k, v) -> v/num.get(row.getKey()));
    }

    String nextWord () {
        Random rand = new Random();
        String word = "";
        double die;

        die = rand.nextDouble();
        for (Map.Entry<String, Double> entry : startingWords.entrySet())
            if (die >= entry.getValue())
                die -= entry.getValue();
            else {
                word = entry.getKey();
                break;
            }
        System.out.print(word+" ");

        while (true) {
            die = rand.nextDouble();
            for (Map.Entry<String, Double> entry : chain.get(word).entrySet())
                if (die >= entry.getValue())
                    die -= entry.getValue();
                else {
                    word = entry.getKey();
                    break;
                }
            System.out.print(word+" ");
            if (word.equals("."))
                break;
        }
        return
    }
}
