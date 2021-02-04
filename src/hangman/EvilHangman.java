package hangman;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);

        EvilHangmanGame game = new EvilHangmanGame(guesses);

        try {
            game.startGame(dictionary, wordLength);
        } catch (EmptyDictionaryException | IOException e) {
            System.out.println("Empty Dictionary");
        }
    }

}
