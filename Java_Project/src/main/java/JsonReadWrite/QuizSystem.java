package JsonReadWrite;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

    class Question {
        String question;
        String option1;
        String option2;
        String option3;
        String option4;
        int answerkey;

        public Question(String question, String option1, String option2, String option3, String option4, int answerkey) {
            this.question = question;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.answerkey = answerkey;
        }
    }

    class User {
        String username;
        String password;
        String role;

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    public class QuizSystem {
        private static final String USERS_FILE = "users.json";
        private static final String QUIZ_FILE = "quiz.json";
        private static List<User> users;
        private static List<Question> questions;

        public static void main(String[] args) {
            loadUsers();
            loadQuestions();
            Scanner scanner = new Scanner(System.in);

            System.out.print("System:> Enter your username\nUser:> ");
            String username = scanner.nextLine();
            System.out.print("System:> Enter password\nUser:> ");
            String password = scanner.nextLine();

            User loggedInUser = authenticate(username, password);
            if (loggedInUser != null) {
                if (loggedInUser.role.equals("admin")) {
                    adminMenu(scanner);
                } else {
                    studentMenu(scanner);
                }
            } else {
                System.out.println("Invalid credentials.");
            }
        }

        private static void loadUsers() {
            try (InputStream userStream = QuizSystem.class.getResourceAsStream("/users.json");
                 Reader reader = new InputStreamReader(userStream)) {
                if (userStream == null) {
                    System.err.println("Cannot find users.json");
                    return;
                }
                Gson gson = new Gson();
                users = gson.fromJson(reader, new TypeToken<List<User>>() {
                }.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void loadQuestions() {
            try (InputStream quizStream = QuizSystem.class.getResourceAsStream("/quiz.json");
                 Reader reader = new InputStreamReader(quizStream)) {
                if (quizStream == null) {
                    System.err.println("Cannot find quiz.json");
                    return;
                }
                Gson gson = new Gson();
                questions = gson.fromJson(reader, new TypeToken<List<Question>>() {
                }.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static User authenticate(String username, String password) {
            for (User user : users) {
                if (user.username.equals(username) && user.password.equals(password)) {
                    return user;
                }
            }
            return null;
        }


        private static void adminMenu(Scanner scanner) {
            System.out.println("System:> Welcome admin! Please create new questions in the question bank.");
            while (true) {
                System.out.print("System:> Input your question\nAdmin:> ");
                String questionText = scanner.nextLine();
                System.out.print("System: Input option 1:\nAdmin:> ");
                String option1 = scanner.nextLine();
                System.out.print("System: Input option 2:\nAdmin:> ");
                String option2 = scanner.nextLine();
                System.out.print("System: Input option 3:\nAdmin:> ");
                String option3 = scanner.nextLine();
                System.out.print("System: Input option 4:\nAdmin:> ");
                String option4 = scanner.nextLine();

                int answerKey = -1;
                while (true) {
                    System.out.print("System: What is the answer key? (1-4)\nAdmin:> ");
                    if (scanner.hasNextInt()) {
                        answerKey = scanner.nextInt();
                        if (answerKey >= 1 && answerKey <= 4) {
                            break; // valid input, break the loop
                        } else {
                            System.out.println("Answer key must be between 1 and 4.");
                        }
                    } else {
                        System.out.println("Please enter a valid integer.");
                        scanner.next(); // consume invalid input
                    }
                }
                scanner.nextLine(); // consume newline after nextInt

                questions.add(new Question(questionText, option1, option2, option3, option4, answerKey));
                System.out.println("System:> Saved successfully! Do you want to add more questions? (press s for start and q for quit)");
                String choice = scanner.nextLine();
                if (choice.equals("q")) {
                    break;
                }
            }
            saveQuestions();
        }

        private static void saveQuestions() {
            try (Writer writer = new FileWriter(QUIZ_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(questions, writer);
            } catch (IOException e) {
                System.err.println("Failed to save questions: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private static void studentMenu(Scanner scanner) {
            System.out.println("System:> Welcome to the quiz! We will throw you 10 questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' to start.");
            String start = scanner.nextLine();
            if (start.equals("s")) {
                takeQuiz(scanner);
            }
        }

        //        private static void takeQuiz(Scanner scanner) {
//            int score = 0;
//            Random random = new Random();
//            Set<Integer> askedQuestions = new HashSet<>();
//
//            for (int i = 0; i < 10; i++) {
//                int questionIndex;
//                do {
//                    questionIndex = random.nextInt(questions.size());
//                } while (askedQuestions.contains(questionIndex));
//                askedQuestions.add(questionIndex);
//
//                Question q = questions.get(questionIndex);
//                System.out.println("[Question " + (i + 1) + "] " + q.question);
//                System.out.println("1. " + q.option1);
//                System.out.println("2. " + q.option2);
//                System.out.println("3. " + q.option3);
//                System.out.println("4. " + q.option4);
//                System.out.print("Student:> ");
//                if (scanner.hasNextInt()) {
//                    int answer = scanner.nextInt();
//                    if (answer == q.answerkey) {
//                        score++;
//                        System.out.println("Correct answer!");
//                    } else {
//                        System.out.println("Incorrect answer. The correct answer was option " + q.answerkey);
//                    }
//                } else {
//                    System.out.println("Invalid input. Please enter a number from 1 to 4.");
//                    scanner.next(); // consume invalid input
//                    i--; // decrement i to repeat the attempt for this question
//                }
//            }
//
//            displayResult(score);
//        }
//
//
//        private static void displayResult(int score) {
//            String message;
//            if (score >= 8) {
//                message = "Excellent! You have got " + score + " out of 10.";
//            } else if (score >= 5) {
//                message = "Good. You have got " + score + " out of 10.";
//            } else if (score >= 2) {
//                message = "Very poor! You have got " + score + " out of 10.";
//            } else {
//                message = "Very sorry you have failed. You have got " + score + " out of 10.";
//            }
//            System.out.println(message);
//        }
//    }
        private static void takeQuiz(Scanner scanner) {
            int score = 0;
            Random random = new Random();
            Set<Integer> askedQuestions = new HashSet<>();
            int totalQuestions = Math.min(10, questions.size()); // Adjust total questions to the number available

            for (int i = 0; i < totalQuestions; i++) {
                int questionIndex;
                do {
                    questionIndex = random.nextInt(questions.size());
                } while (askedQuestions.contains(questionIndex));

                askedQuestions.add(questionIndex);
                Question q = questions.get(questionIndex);
                System.out.println("[Question " + (i + 1) + "] " + q.question);
                System.out.println("1. " + q.option1);
                System.out.println("2. " + q.option2);
                System.out.println("3. " + q.option3);
                System.out.println("4. " + q.option4);
                System.out.print("Student:> ");

                if (scanner.hasNextInt()) {
                    int answer = scanner.nextInt();
                    scanner.nextLine(); // Consume any leftover newline

                    if (answer >= 1 && answer <= 4) {
                        if (answer == q.answerkey) {
                            score++;
                            System.out.println("Correct answer!");
                        } else {
                            System.out.println("Incorrect answer. The correct answer was option " + q.answerkey);
                        }
                    } else {
                        System.out.println("Please choose a valid option between 1 and 4.");
                        i--; // Repeat the question
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume invalid input
                    i--; // Repeat the question
                }
            }

            // Now display the score after all questions
            displayResult(score);
        }

        private static void displayResult(int score) {
            String message;
            if (score >= 8) {
                message = "Excellent! You have got " + score + " out of 10.";
            } else if (score >= 5) {
                message = "Good. You have got " + score + " out of 10.";
            } else if (score >= 2) {
                message = "Very poor! You have got " + score + " out of 10.";
            } else {
                message = "Very sorry you have failed. You have got " + score + " out of 10.";
            }
            System.out.println(message);
        }
    }