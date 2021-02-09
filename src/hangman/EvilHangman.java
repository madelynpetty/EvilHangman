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

        while (guesses > 0) {
            try {
                System.out.print("Please guess: ");
                Scanner input = new Scanner(System.in);
                char in = input.next().charAt(0);
                Set<String> strings = game.makeGuess(in);
                //if (strings == 1) then tell them they win
            } catch (GuessAlreadyMadeException e) {
                System.out.println("Already guessed");
            }
        }
    }

}
