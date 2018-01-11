import edu.stanford.nlp.simple.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MarkovChain {
    private Map<String, Map<String, Double>> chain;
    private Map<String, Double> startingWords;
    private Random rand;
    private String previousWord;

    MarkovChain (String str) {
        previousWord = null;
        rand = new Random();
        Document doc = new Document(str);
        startingWords = new HashMap<>();
        chain = new HashMap<>();

        List<String> words = doc.sentences().stream()
                .flatMap(s -> s.words().stream())
                .collect(Collectors.toList());
        // Initialize startingWords with starting words (duh), and number of their occurrence.
        doc.sentences().stream()
                .map(s -> s.words().get(0))
                .forEach((String w) -> {
                    if (startingWords.putIfAbsent(w, 1.0) != null)
                        startingWords.replace(w, startingWords.get(w)+1);
                });
        // Compute probabilities for all the starting words.
        for (Map.Entry<String, Double> word : startingWords.entrySet())
            startingWords.compute(word.getKey(), (k, v) -> v/startingWords.values().size());
        // Total number of occurrences of pair "prev <some word>" in text.
        // Helps me to compute the probabilities later.
        Map<String, Double> num = new HashMap<>();
        String prev = null;
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

        // Compute probabilities for the chain.
        for (Map.Entry<String, Map<String,Double>> row : chain.entrySet())
            for (String word : row.getValue().keySet())
                row.getValue().compute(word, (k, v) -> v/num.get(row.getKey()));
    }

    String nextWord () {
        String word = "";
        double die;

        die = rand.nextDouble();
        if (previousWord == null) {
            die = rand.nextDouble();
            for (Map.Entry<String, Double> entry : startingWords.entrySet())
                if (die >= entry.getValue())
                    die -= entry.getValue();
                else {
                    word = entry.getKey();
                    break;
                }
            previousWord = word;
            return word;
        }

        for (Map.Entry<String, Double> entry : chain.get(previousWord).entrySet())
            if (die >= entry.getValue())
                die -= entry.getValue();
            else {
                word = entry.getKey();
                break;
            }
        if (word.equals("."))
            previousWord = null;
        else
            previousWord = word;

        return word;
    }

    boolean isInSentence () {
        return (previousWord == null) ? false : true;
    }
}
