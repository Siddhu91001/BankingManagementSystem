package com.banking.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banking.model.Transaction;

public class TransactionService {

    private final File file = new File("data/transactions.txt");

    public TransactionService() {

        File directory = new File("data");

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void recordTransaction(
            String username,
            long accountNumber,
            String type,
            BigDecimal amount) {

        Transaction transaction = new Transaction(
                username,
                accountNumber,
                type,
                amount,
                LocalDateTime.now()
        );

        try (FileWriter fw = new FileWriter(file, true)) {

            fw.write(
                    transaction.getUsername() + "|"
                    + transaction.getAccountNumber() + "|"
                    + transaction.getType() + "|"
                    + transaction.getAmount().toPlainString() + "|"
                    + transaction.getDateTime()
                    + System.lineSeparator()
            );

        } catch (IOException e) {

            System.out.println(
                    "Error saving transaction: " + e.getMessage()
            );
        }
    }

    public void viewTransactionHistory(String username, long accountNumber) {

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            System.out.println("\n========== TRANSACTION HISTORY ==========");
            System.out.println("Account Number: " + accountNumber);

            while ((line = br.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length != 5) {
                    continue;
                }

                String transactionUsername = details[0];
                long transactionAccountNumber =
                        Long.parseLong(details[1]);

                if (transactionUsername.equalsIgnoreCase(username)
                        && transactionAccountNumber == accountNumber) {

                    String type = details[2];
                    BigDecimal amount = new BigDecimal(details[3]);
                    LocalDateTime dateTime =
                            LocalDateTime.parse(details[4]);

                    Transaction transaction = new Transaction(
                            transactionUsername,
                            transactionAccountNumber,
                            type,
                            amount,
                            dateTime
                    );

                    System.out.println("-----------------------------------------");
                    System.out.println(
                            "Transaction Type: " + transaction.getType()
                    );
                    System.out.println(
                            "Amount: Rs. " + transaction.getAmount()
                    );
                    System.out.println(
                            "Date & Time: " + transaction.getFormattedDateTime()
                    );

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No transactions found for this account.");
            }

            System.out.println("-----------------------------------------");

        } catch (IOException e) {

            System.out.println(
                    "Error reading transaction history: " + e.getMessage()
            );

        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {

            System.out.println(
                    "A transaction record contains invalid data."
            );
        }
    }
}