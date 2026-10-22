package com.banking.main;

import java.math.BigDecimal;
import java.util.Scanner;
import com.banking.service.TransactionService;

import com.banking.service.AuthService;
import com.banking.service.AccountService;

public class BankingApplication {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AuthService auth = new AuthService();
        AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService();

        while (true) {

            System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter username: ");
                    String newUsername = sc.nextLine();

                    System.out.print("Enter password: ");
                    String newPassword = sc.nextLine();

                    auth.signup(newUsername, newPassword);
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    if (auth.login(username, password)) {
                    	showBankingMenu(sc, accountService, transactionService, username);
                    }
                    break;

                case 3:
                    System.out.println("Thank you for using our system!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showBankingMenu(
            Scanner sc,
            AccountService accountService,
            TransactionService transactionService,
            String username) {

        while (true) 
        {

        	System.out.println("\n===== BANKING MENU =====");

        	System.out.println("1. Create Bank Account");
        	System.out.println("2. View My Accounts");
        	System.out.println("3. Deposit Money");
        	System.out.println("4. Withdraw Money");
        	System.out.println("5. Transaction History");
        	System.out.println("6. Change Account PIN");
        	System.out.println("7. Transfer Money");
        	System.out.println("8. Logout");

        	System.out.print("Enter your choice: ");


            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter account holder name: ");
                    String holderName = sc.nextLine();

                    System.out.print("Create a 4-digit PIN: ");
                    String pin = sc.nextLine();

                    accountService.createAccount(username, holderName, pin);
                    break;

                case 2:
                    accountService.viewMyAccounts(username);
                    break;

                case 3:
                    System.out.print("Enter account number: ");

                    try {
                        long accountNumber =
                                Long.parseLong(sc.nextLine().trim());

                        System.out.print("Enter deposit amount: Rs. ");

                        BigDecimal amount =
                                new BigDecimal(sc.nextLine().trim());

                        accountService.depositMoney(
                                username,
                                accountNumber,
                                amount
                        );

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Please enter a valid account number and amount."
                        );
                    }

                    break;
                
                case 4:
                    System.out.print("Enter account number: ");

                    try {
                        long accountNumber = Long.parseLong(sc.nextLine().trim());

                        System.out.print("Enter withdrawal amount: Rs. ");

                        BigDecimal amount = new BigDecimal(sc.nextLine().trim());

                        accountService.withdrawMoney(username, accountNumber, amount);

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid account number and amount.");
                    }

                    break;
                    
                case 5:

                    System.out.print("Enter account number: ");

                    try {

                        long accountNumber =
                                Long.parseLong(sc.nextLine().trim());

                        transactionService.viewTransactionHistory(
                                username,
                                accountNumber
                        );

                    } catch (NumberFormatException e) {

                        System.out.println("Please enter a valid account number.");
                    }

                    break;
                    
                case 6:

                    System.out.print("Enter account number: ");

                    try {

                        long accountNumber = Long.parseLong(sc.nextLine().trim());

                        System.out.print("Enter current PIN: ");
                        String currentPin = sc.nextLine();

                        System.out.print("Enter new 4-digit PIN: ");
                        String newPin = sc.nextLine();

                        accountService.changePin(
                                username,
                                accountNumber,
                                currentPin,
                                newPin
                        );

                    } catch (NumberFormatException e) {

                        System.out.println("Please enter a valid account number.");
                    }

                    break;
                
                case 7:
                    System.out.print("Enter sender account number: ");
                    long senderAccountNumber = Long.parseLong(sc.nextLine());

                    System.out.print("Enter receiver account number: ");
                    long receiverAccountNumber = Long.parseLong(sc.nextLine());

                    System.out.print("Enter transfer amount: ");
                    BigDecimal amount = new BigDecimal(sc.nextLine());

                    accountService.transferMoney(
                        username,
                        senderAccountNumber,
                        receiverAccountNumber,
                        amount,
                        transactionService
                    );
                    break;
                    
                case 8:
                    System.out.println("Logged out successfully.");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
