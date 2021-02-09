package hangman;

import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private SortedSet<Character> guessedLetters;
    private int guessesLeft;
    private Set<String> words;
    private int wordLength = 0;

    public EvilHangmanGame() {
        guessedLetters = new TreeSet();
        guessesLeft = 0;
        words = new TreeSet<>();
    }

    public EvilHangmanGame(int guesses) {
        guessedLetters = new TreeSet();
        guessesLeft = guesses;
        words = new TreeSet<>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        this.wordLength = wordLength;

        if (dictionary == null || dictionary.length() == 0) {
            throw new EmptyDictionaryException("Dictionary is empty to begin with");
        }

        Scanner dict = new Scanner(dictionary);
        dict.useDelimiter("(\\s+)+");

        while (dict.hasNext()) {
            words.add(dict.next().toLowerCase());
        }

        if (words.size() == 0) {
            throw new EmptyDictionaryException("There are no words in the dictionary.");
        }

        Set<String> correctLengthWords = new TreeSet<>();
        for (String s : words) {
            if (s.length() == wordLength) {
                correctLengthWords.add(s);
            }
        }

        setWords(correctLengthWords);

        if (correctLengthWords.size() == 0) {
            throw new EmptyDictionaryException("there are no words that are the desired length");
        }


    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        for (Character c : guessedLetters) {
            if (guess == c) {
                throw new GuessAlreadyMadeException("This letter has already been guessed.");
            }
        }

        System.out.println("Remaining guesses: " + guessesLeft);
        System.out.println("Letters guessed: " + guessedLetters);

        guessedLetters.add(guess);
        guessesLeft--;

        Map<String, Set<String>> patternMap = getPatternMapForGuess(guess);

        Map<String, Set<String>> largestGroups = getLargestGroups(patternMap);

        if (largestGroups.size() == 1) {
//            Map.Entry<String, Set<String>> entry = largestGroups.entrySet().iterator().next();
//            return entry.getValue();
            return (Set<String>) largestGroups.values().toArray()[0];
        }

        Map<String, Set<String>> noMatchingLetterGroups = getNoMatchingLetterGroups(largestGroups, guess);

        if (noMatchingLetterGroups.size() > 0) { //maybe == 1 ???
            return (Set<String>) noMatchingLetterGroups.values().toArray()[0];
        }

        Map<String, Set<String>> fewestLetterGroups = getFewestLetterGroups(largestGroups, guess);

        if (fewestLetterGroups.size() == 1) {
            return (Set<String>) fewestLetterGroups.values().toArray()[0];
        }

        Map<String, Set<String>> rightmostLetterGroups = getRightmostLetterGroups(fewestLetterGroups, guess);

        if (rightmostLetterGroups.size() > 0) {
//            Set<String> temp = rightmostLetterGroups.get(rightmostLetterGroups.keySet().toArray()[0]);


//            return (Set<String>) rightmostLetterGroups.values().toArray();

//            return rightmostLetterGroups.values().toArray()[0];

//            Map.Entry<String, Set<String>> entry = rightmostLetterGroups.entrySet();

//            return rightmostLetterGroups.get(rightmostLetterGroups.keySet().toArray()[0]);

//            Object keyAtIndex0 = rightmostLetterGroups.keySet().toArray(new Object[rightmostLetterGroups.size()])[0];
//            Set<String> value = rightmostLetterGroups.get(keyAtIndex0);
//            return value;

//            Set<String> temp = new TreeSet<>();

            for (Map.Entry<String, Set<String>> e : rightmostLetterGroups.entrySet()) {
                return e.getValue();
            }
//            return temp;

//            return rightmostLetterGroups.keySet();
        }

        Map<String, Set<String>> rightmostLetterGroups2 = getRightmostLetterGroups(rightmostLetterGroups, guess, patternMap.keySet().toArray()[0].toString().length());

        if (rightmostLetterGroups2.size() > 0) {
            return (Set<String>) rightmostLetterGroups2.values().toArray()[0];
        }

        return null;
    }

    private Map<String, Set<String>> getLargestGroups(Map<String, Set<String>> patternMap) {
        int max = 0;
        Map<String, Set<String>> temp = new TreeMap<>();

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            if (e.getValue().size() > max) {
                max = e.getValue().size();
            }
        }

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            if ((max > 0) && (e.getValue().size() == max)) {
                temp.put(e.getKey(), e.getValue());
            }
        }

        return temp;
    }

    private Map<String, Set<String>> getNoMatchingLetterGroups(Map<String, Set<String>> patternMap, char guess) {
        Map<String, Set<String>> temp = new TreeMap<>();

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            if (e.getKey().indexOf(guess) == -1) {
                temp.put(e.getKey(), e.getValue());
            }
        }

        return temp;
    }

    private Map<String, Set<String>> getFewestLetterGroups(Map<String, Set<String>> patternMap, char guess) {
        Map<String, Set<String>> temp = new TreeMap<>();
        int min = Integer.MAX_VALUE;

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            int count = 0;
            for (int i = 0; i < e.getKey().length(); i++) {
                char c = e.getKey().charAt(i);
                if (c == guess) {
                    count++;
                }
            }
            if (count < min) {
                min = count;
            }
        }

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            int count = 0;
            for (int i = 0; i < e.getKey().length(); i++) {
                char c = e.getKey().charAt(i);
                if (c == guess) {
                    count++;
                }
            }
            if (count == min) {
                temp.put(e.getKey(), e.getValue());
            }
        }

        return temp;
    }

    private Map<String, Set<String>> getRightmostLetterGroups(Map<String, Set<String>> patternMap, char guess) {
        Map<String, Set<String>> temp = new TreeMap<>();
        int max = -1;

//        int iterations = 0;
//        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
//            for (int i = 0; i < e.getKey().length(); i++) {
//                iterations++;
//            }
//            break;
//        }
//
//        for (int j = 0; j < iterations; j++) {
        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            for (int i = e.getKey().length() - 1; i >= 0; i--) {
                char c = e.getKey().charAt(i);
//                if (guessedLetters.contains(c)) {
                if (guess == c) {
                    if (max < i) {
                        max = i;
                    }
                }
            }
        }

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            for (int i = e.getKey().length() - 1; i >= 0; i--) {
                char c = e.getKey().charAt(max);
//                if (guessedLetters.contains(c)) {
                if(guess == c) {
                    temp.put(e.getKey(), e.getValue());
                }
            }
        }

        return temp;
    }

    private Map<String, Set<String>> getRightmostLetterGroups(Map<String, Set<String>> patternMap, char guess, int startRightIndex) {
        Map<String, Set<String>> temp = new TreeMap<>();
        int max = -1;

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            for (int i = startRightIndex - 1; i >= 0; i--) {
                char c = e.getKey().charAt(i);
//                if (guessedLetters.contains(c)) {
                if (guess == c) {
                    if (max < i) {
                        max = i;
                    }
                }
            }
        }

        for (Map.Entry<String, Set<String>> e : patternMap.entrySet()) {
            for (int i = e.getKey().length() - 1; i >= 0; i--) {
                char c = e.getKey().charAt(max);
//                if (guessedLetters.contains(c)) {
                if (guess == c) {
                    temp.put(e.getKey(), e.getValue());
                }
            }
        }
        if (temp.size() != 1) {
            return getRightmostLetterGroups(temp, guess, startRightIndex - 1);
        }

        return temp;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public Map<String, Set<String>> getPatternMapForGuess(char guess) {
        Map<String, Set<String>> newMap = new TreeMap<String, Set<String>>();

        for (String s : words) {
            String pattern = pattern(guess, s);
            Set<String> wordList;

            if (newMap.containsKey(pattern)) {
                wordList = newMap.get(pattern);
            }
            else {
                wordList = new TreeSet<>();
            }

            if (!wordList.contains(s)) {
                wordList.add(s);
            }

            newMap.put(pattern, wordList);
        }

        return newMap;
    }

    protected String pattern(char guess, String word) {
        StringBuilder pattern = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            found = false;
//            for (Character s : guessedLetters) {
                if (word.charAt(i) == guess) {
                    pattern.insert(i, guess);
                    found = true;
                }
//            }
            if (found == false) {
                pattern.insert(i, '-');
            }
        }

        return pattern.toString();
    }

    private void setWords(Set<String> words) {
        //THESE ALL ARE THE CORRECT LENGTH
        //THIS SLOWLY NARROWS INTO MORE APPLICABLE WORDS AS IT'S CALLED
        this.words = words;
    }

    private Set<String> getWords() {
        return words;
    }
}
