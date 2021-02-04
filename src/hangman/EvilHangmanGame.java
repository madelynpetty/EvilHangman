package hangman;

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
    }

    public EvilHangmanGame(int guesses) {
        guessedLetters = new TreeSet();
        guessesLeft = guesses;
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        this.wordLength = wordLength;

        if (dictionary == null || dictionary.length() == 0) {
            throw new EmptyDictionaryException();
        }

        Scanner dict = new Scanner(dictionary);
        dict.useDelimiter("(\\s+)+");

        Set<String> wordsInDictionary = new TreeSet<>();

        while (dict.hasNext()) {
            wordsInDictionary.add(dict.next().toLowerCase());
        }

        if (wordsInDictionary.size() == 0) {
            throw new EmptyDictionaryException();
        }

        Set<String> correctLengthWords = new TreeSet<>();
        for (String s : wordsInDictionary) {
            if (s.length() == wordLength) {
                correctLengthWords.add(s);
            }
        }

        setWords(correctLengthWords);

        if (correctLengthWords.size() == 0) {
            throw new EmptyDictionaryException();
        }

        while (guessesLeft > 0) {
            try {
                System.out.print("Please guess: ");
                Scanner input = new Scanner(System.in);
                char in = input.next().charAt(0);

                Set<String> strings = makeGuess(in);
                words.clear();
                setWords(strings);
            } catch (GuessAlreadyMadeException e) {
                System.out.println("Already guessed");
            }
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        for (Character c : guessedLetters) {
            if (guess == c) {
                throw new GuessAlreadyMadeException();
            }
        }

        System.out.println("Remaining guesses: " + guessesLeft);
        System.out.println("Letters guessed: " + guessedLetters);

        guessedLetters.add(guess);
        guessesLeft--;

        Set<String> strings = new TreeSet<>();

        Map<String, Set<String>> patternMap = getPatternMapForGuess(guess);
        int max = 0;
        for (Set<String> ss: patternMap.values()) {
            int i = ss.size();
            if (i > max) {
                max = i;
                strings = ss;
            }
        }

        return strings;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public Map<String, Set<String>> getPatternMapForGuess(char guess) {
        Map<String, Set<String>> newMap = new TreeMap<String, Set<String>>();

        for (String s : words) {
            String pattern = getPatternForWord(guess, s);
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

    public String getPatternForWord(char guess, String word) {
        //regex to match all non-guess characters (ex: [^E] if guess was 'E')
        String replaceRegex = "[^" + guess + "]";

        //replace all non-guess characters with '-' (ex: replace all non-'E' with '-')
        String pattern = word.replaceAll(replaceRegex, "-");
        return pattern;
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
