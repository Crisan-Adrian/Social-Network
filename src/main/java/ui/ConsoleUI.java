package ui;

import controller.Controller;
import exceptions.InvalidCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    Scanner inputScanner;
    Controller controller;

    public ConsoleUI(Controller controller) {
        inputScanner = new Scanner(System.in);
        this.controller = controller;
    }

    /**
     * Landing page UI loop
     */
    public void runUI() {
        while (true) {
            System.out.print("Input command: ");
            List<String> inputWords = getInputWords();

            ProcessReturn processReturn = processLoginSignup(inputWords);
            switch (processReturn) {
                case EXIT:
                    return;
                case CONTINUE:
                    break;
                case LOGIN:
                case SIGNUP:
                    mainUI();
                    break;
            }
        }
    }

    /**
     * Main UI loop
     */
    private void mainUI() {
        String currentUser = controller.getCurrentUser();
        System.out.printf("Hello, %s!%n", currentUser);
        while (true) {
            System.out.print("Input user command: ");
            List<String> inputWords = getInputWords();

            ProcessReturn processReturn = processInput(inputWords);
            switch (processReturn) {
                case LOGOUT:
                    return;
                case CONTINUE:
                    break;
            }
        }
    }

    /**
     * Function to get user input
     *
     * @return separated input words
     */
    private List<String> getInputWords() {
        String input = inputScanner.nextLine();
        return new LinkedList<>(Arrays.asList(input.split("\\s+")));
    }

    public String getInput(String prompt)
    {
        System.out.println(prompt);
        return inputScanner.nextLine();
    }

    /**
     * Input processing function that communicates with the controller
     *
     * @param inputWords a list of strings; first element is the command
     *                   name and the rest are the arguments
     * @return EXIT if the exit command was given;
     * CONTINUE for all other commands
     */
    private ProcessReturn processInput(List<String> inputWords) {
        if (inputWords.get(0).equalsIgnoreCase("logout")) {
            System.out.println("Logging out.\n");
            return ProcessReturn.LOGOUT;
        } else {
            try {
                String result;
                result = controller.processCommand(inputWords, this);
                System.out.println(result);
            } catch (InvalidCommand exception) {
                System.out.println(exception.getMessage());
            }

            return ProcessReturn.CONTINUE;

        }
    }

    public void display(String string)
    {
        System.out.println("\t" + string);
    }

    private ProcessReturn processLoginSignup(List<String> inputWords) {
        if (inputWords.get(0).equalsIgnoreCase("exit")) {
            return ProcessReturn.EXIT;
        }
        if (inputWords.get(0).equalsIgnoreCase("login")) {
            if(inputWords.size()!=2)
            {
                System.out.println("Wrong use of 'login' command\n Correct use: 'login [email]/admin'");
                return ProcessReturn.CONTINUE;
            }
            if (controller.processLogin(inputWords.subList(1, inputWords.size()))) {
                return ProcessReturn.LOGIN;
            } else {
                System.out.println("That email is not registered, please try again");
                return ProcessReturn.CONTINUE;
            }
        } else if (inputWords.get(0).equalsIgnoreCase("signup")) {
            try {
                String res = controller.processSignUp(inputWords.subList(1, inputWords.size()));
                System.out.println(res);
                if(res.equals("Account created"))
                {
                    return ProcessReturn.SIGNUP;
                }
                else
                {
                    return ProcessReturn.CONTINUE;
                }
            }
            catch(InvalidCommand exception)
            {
                System.out.println(exception.getLocalizedMessage());
                return  ProcessReturn.CONTINUE;
            }
        } else {
            System.out.println("Please log in or create an account");
            return ProcessReturn.CONTINUE;
        }
    }
}
