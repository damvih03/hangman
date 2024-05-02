package com.damvih.hangman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Hangman {
    private static final String[] SCAFFOLD_STATES = {
            """
        ___________               
        |   \\    |
        |  
        |  
        |  
        |
        ~~~~~~~~~~~
        """,
            """
        ___________               
        |   \\    |
        |   ()
        |  
        |  
        |
        ~~~~~~~~~~~
        """,
            """
        ___________                
        |   \\    |
        |   ()
        |   []
        |  
        |
        ~~~~~~~~~~~
        """,
            """
        ___________                
        |   \\    |
        |   ()
        |  /[]\\
        |  
        |
        ~~~~~~~~~~~
        """,
            """
        ___________                
        |   \\    |
        |   ()
        |  /[]\\
        |  
        |
        ~~~~~~~~~~~
        """,
            """
        ___________                
        |   \\    |
        |   ()
        |  /[]\\
        |  /   
        |
        ~~~~~~~~~~~
        """,
            """
        ___________                
        |   \\    |
        |   ()
        |  /[]\\
        |  /  \\
        |
        ~~~~~~~~~~~
        """
    };
    private static final int MAX_MISTAKES = 6;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String GAME_STATE_LOSE = "Вы проиграли!";
    private static final String GAME_STATE_WINNING = "Вы победили!";
    private static final String GAME_STATE_CONTINUE = "Игра продолжается!";
    private static List<String> words;

    public static void main(String[] args) {
        loadWords();
        startGame();
    }

    private static void loadWords() {
        words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/words.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                words.add(word);
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private static String inputLetter() {
        String alphabet = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
        while (true) {
            System.out.print("Введите букву: ");
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (alphabet.contains(input) && input.length() == 1) {
                return input;
            }
            System.out.println("Некорректный ввод!");
        }
    }

    private static String showCorrectLetterInHiddenWord(String hiddenWord, String secretWord, String letter) {
        char[] hiddenWordLetters = hiddenWord.toCharArray();
        for (int i = 0; i < secretWord.length(); i++) {
            if (letter.equals(secretWord.charAt(i) + "")) {
                hiddenWordLetters[i] = letter.toCharArray()[0];
            }
        }
        return new String(hiddenWordLetters);
    }

    private static void showUsedLetters(HashSet<String> usedLetters) {
        System.out.print("Использованные буквы: ");
        for (String usedLetter : usedLetters) {
            System.out.print(usedLetter + ", ");
        }
        System.out.println();
    }

    private static void startGame() {
        while (true) {
            System.out.print("Хотите сыграть? (да/нет): ");
            String input = SCANNER.nextLine().toLowerCase().strip();
            if (input.equals("да")) {
                startRound();
            } else if (input.equals("нет")) {
                break;
            } else {
                System.out.println("Неккоректный ввод! Ожидается: 'да' или 'нет'");
            }
        }
    }

    private static String checkGameState(int mistakes, String hiddenWord, String secretWord) {
        if (mistakes == MAX_MISTAKES) {
            return GAME_STATE_LOSE;
        } else if (hiddenWord.equals(secretWord)) {
            return GAME_STATE_WINNING;
        }
        return GAME_STATE_CONTINUE;
    }

    private static void startRound() {
        String secretWord = getRandomWord();
        String hiddenWord = "_".repeat(secretWord.length());
        HashSet<String> usedLetters = new HashSet<>();
        int mistakes = 0;
        String gameState;
        while (true) {
            System.out.println(SCAFFOLD_STATES[mistakes]);
            System.out.println(hiddenWord);
            if (!(gameState = checkGameState(mistakes, hiddenWord, secretWord)).equals(GAME_STATE_CONTINUE)) {
                break;
            }
            String input = inputLetter();
            if (!usedLetters.add(input)) {
                System.out.println("Эту букву уже вводили!");
                showUsedLetters(usedLetters);
            } else {
                if (!secretWord.contains(input)) {
                    mistakes += 1;
                } else {
                    hiddenWord = showCorrectLetterInHiddenWord(hiddenWord, secretWord, input);
                }
            }
            System.out.println("Ошибки: " + mistakes + "/" + MAX_MISTAKES + "\n");
        }
        System.out.println(gameState);
        System.out.println("Загаданное слово: " + secretWord + "\n");
    }
}
