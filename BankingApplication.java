import java.io.*;
import java.util.*;

// Class representing a User Account (encapsulation)
class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private double balance;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0.0;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
        } else {
            System.out.println("Invalid withdraw amount or insufficient balance.");
        }
    }
}

// Bank class to handle user accounts (uses file handling for persistence)
class Bank {
    private static final String FILE_NAME = "users.dat";
    private Map<String, Account> users = new HashMap<>();

    // Constructor to load accounts from file
    public Bank() {
        loadAccounts();
    }

    // Register a new account
    public void createAccount(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("Account already exists!");
        } else {
            Account newAccount = new Account(username, password);
            users.put(username, newAccount);
            saveAccounts();
            System.out.println("Account created successfully!");
        }
    }

    // Authenticate user
    public Account login(String username, String password) {
        Account account = users.get(username);
        if (account != null && account.authenticate(password)) {
            System.out.println("Login successful.");
            return account;
        } else {
            System.out.println("Invalid username or password.");
            return null;
        }
    }

    // Save accounts to a file
    private void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // Load accounts from a file
    private void loadAccounts() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                users = (Map<String, Account>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading accounts: " + e.getMessage());
            }
        }
    }
}

// Main class for the Banking Application
public class BankingApplication {
    private static Scanner scanner = new Scanner(System.in);
    private static Bank bank = new Bank();

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\nWelcome to the Banking Application!");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    exit = true;
                    System.out.println("Thank you for using the Banking Application!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        bank.createAccount(username, password);
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Account account = bank.login(username, password);

        if (account != null) {
            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Check Balance");
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        account.deposit(depositAmount);
                        bank.createAccount(account.getUsername(), account.authenticate(password) ? password : ""); // Update user info
                        break;
                    case 2:
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        account.withdraw(withdrawAmount);
                        bank.createAccount(account.getUsername(), account.authenticate(password) ? password : ""); // Update user info
                        break;
                    case 3:
                        System.out.println("Your balance is: $" + account.getBalance());
                        break;
                    case 4:
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}
