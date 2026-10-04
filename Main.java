
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final AccountDAO dao = new AccountDAO();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1 ->
                        createAccount();
                    case 2 ->
                        viewAllAccounts();
                    case 3 ->
                        viewAccountByNo();
                    case 4 ->
                        updateAccount();
                    case 5 ->
                        deleteAccount();
                    case 6 ->
                        deposit();
                    case 7 ->
                        withdraw();
                    case 8 ->
                        transfer();
                    case 9 ->
                        running = false;
                    default ->
                        System.out.println("Invalid choice, try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Thank you for using Bank Management System.");
    }

    private static void printMenu() {
        System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
        System.out.println("1. Create Account");
        System.out.println("2. View All Accounts");
        System.out.println("3. View Account by Number");
        System.out.println("4. Update Account");
        System.out.println("5. Delete Account");
        System.out.println("6. Deposit");
        System.out.println("7. Withdraw");
        System.out.println("8. Transfer Funds");
        System.out.println("9. Exit");
    }

    private static void createAccount() throws SQLException {
        System.out.print("Holder name: ");
        String name = sc.nextLine();
        System.out.print("Account type (SAVINGS/CURRENT): ");
        String type = sc.nextLine().toUpperCase();
        BigDecimal initial = readAmount("Initial deposit: ");

        Account acc = new Account(name, type, initial);
        int accNo = dao.createAccount(acc);
        System.out.println("Account created successfully! Account No: " + accNo);
    }

    private static void viewAllAccounts() throws SQLException {
        List<Account> accounts = dao.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.printf("%-10s %-20s %-10s %s%n", "AccNo", "Name", "Type", "Balance");
        accounts.forEach(System.out::println);
    }

    private static void viewAccountByNo() throws SQLException {
        int accNo = readInt("Enter account number: ");
        Account acc = dao.getAccountByNo(accNo);
        System.out.println(acc != null ? acc : "Account not found.");
    }

    private static void updateAccount() throws SQLException {
        int accNo = readInt("Enter account number to update: ");
        Account existing = dao.getAccountByNo(accNo);
        if (existing == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("New holder name: ");
        String name = sc.nextLine();
        System.out.print("New account type (SAVINGS/CURRENT): ");
        String type = sc.nextLine().toUpperCase();

        existing.setHolderName(name);
        existing.setAccountType(type);
        boolean updated = dao.updateAccount(existing);
        System.out.println(updated ? "Account updated." : "Update failed.");
    }

    private static void deleteAccount() throws SQLException {
        int accNo = readInt("Enter account number to delete: ");
        boolean deleted = dao.deleteAccount(accNo);
        System.out.println(deleted ? "Account deleted." : "Account not found.");
    }

    private static void deposit() throws SQLException {
        int accNo = readInt("Enter account number: ");
        BigDecimal amount = readAmount("Amount to deposit: ");
        boolean ok = dao.deposit(accNo, amount);
        System.out.println(ok ? "Deposit successful." : "Account not found.");
    }

    private static void withdraw() throws SQLException {
        int accNo = readInt("Enter account number: ");
        BigDecimal amount = readAmount("Amount to withdraw: ");
        boolean ok = dao.withdraw(accNo, amount);
        System.out.println(ok ? "Withdrawal successful." : "Account not found.");
    }

    private static void transfer() throws SQLException {
        int from = readInt("From account number: ");
        int to = readInt("To account number: ");
        BigDecimal amount = readAmount("Amount to transfer: ");
        dao.transfer(from, to, amount);
        System.out.println("Transfer successful.");
    }

    // ---- input helpers ----
    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine(); // consume newline
        return val;
    }

    private static BigDecimal readAmount(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextBigDecimal()) {
            System.out.print("Please enter a valid amount: ");
            sc.next();
        }
        BigDecimal val = sc.nextBigDecimal();
        sc.nextLine();
        return val;
    }
}
