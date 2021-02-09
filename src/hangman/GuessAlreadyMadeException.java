package hangman;

public class GuessAlreadyMadeException extends Exception {
    public GuessAlreadyMadeException(String problem) {
        System.out.println(problem);
    }
}
