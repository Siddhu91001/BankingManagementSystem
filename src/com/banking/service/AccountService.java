package com.banking.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import com.banking.model.Account;

public class AccountService {

    private final File file = new File("data/accounts.txt");
    private final TransactionService transactionService = new TransactionService();

    public AccountService() 
    {
        File directory = new File("data");

        if (!directory.exists()) 
        {
            directory.mkdirs();
        }
    }

    public Account createAccount(String username, String holderName, String pin) 
    {

        if (holderName == null || holderName.trim().isEmpty()
                || holderName.contains("|")
                || pin == null || !pin.matches("\\d{4}")) 
        {

            System.out.println("Invalid name or PIN. PIN must contain 4 digits.");
            return null;
        }

        long accountNumber = generateAccountNumber();

        if (accountNumber == -1) 
        {
            System.out.println("Unable to generate account number.");
            return null;
        }

        Account account = new Account(
                username,
                accountNumber,
                holderName,
                pin,
                BigDecimal.ZERO
        );

        try (FileWriter fw = new FileWriter(file, true)) 
        {
            fw.write(
                    account.getUsername() + "|"
                    + account.getAccountNumber() + "|"
                    + account.getHolderName() + "|"
                    + account.getPin() + "|"
                    + account.getBalance()
                    + System.lineSeparator()
            );

            System.out.println("Account created successfully!");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Holder: " + account.getHolderName());
            System.out.println("Initial Balance: " + account.getBalance());

            return account;

        } 
        catch (IOException e) 
        {
            System.out.println("Error saving account: " + e.getMessage());
            return null;
        }
    }

    private long generateAccountNumber() 
    {
        long accountNumber = 100000;

        if (!file.exists()) 
        {
            return accountNumber + 1;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {
            String line;

            while ((line = br.readLine()) != null) 
            {

                String[] details = line.split("\\|", -1);

                if (details.length == 5) 
                {

                    long existingNumber = Long.parseLong(details[1]);

                    if (existingNumber > accountNumber) 
                    {
                        accountNumber = existingNumber;
                    }
                }
            }

        } 
        catch (IOException | NumberFormatException e) 
        {
            System.out.println("Error reading account records: " + e.getMessage());
            return -1;
        }

        return accountNumber + 1;
    }

    public void viewMyAccounts(String username) 
    {

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {

            String line;

            while ((line = br.readLine()) != null) 
            {

                String[] details = line.split("\\|", -1);

                if (details.length != 5) {
                    continue;
                }

                String accountUsername = details[0];

                if (accountUsername.equalsIgnoreCase(username)) 
                {
                    long accountNumber = Long.parseLong(details[1]);
                    String holderName = details[2];
                    BigDecimal balance = new BigDecimal(details[4]);

                    System.out.println("\n------------------------------");
                    System.out.println("Account Number : " + accountNumber);
                    System.out.println("Holder Name    : " + holderName);
                    System.out.println("Balance        : Rs. " + balance);
                    System.out.println("------------------------------");

                    found = true;
                }
            }

            if (!found) 
            {
                System.out.println("You have not created any bank accounts yet.");
            }

        } 
        catch (IOException e) 
        {
            System.out.println("Error reading account records: " + e.getMessage());
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("An account record contains invalid numeric data.");
        }
    }

    public void depositMoney(String username, long accountNumber, BigDecimal amount) 
    {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) 
        {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        List<String> records = new ArrayList<>();
        boolean accountFound = false;
        BigDecimal updatedBalance = BigDecimal.ZERO;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {

            String line;

            while ((line = br.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length != 5) {
                    records.add(line);
                    continue;
                }

                long existingAccountNumber = Long.parseLong(details[1]);

                if (details[0].equalsIgnoreCase(username)
                        && existingAccountNumber == accountNumber) {

                    BigDecimal currentBalance = new BigDecimal(details[4]);

                    updatedBalance = currentBalance.add(amount);

                    details[4] = updatedBalance.toPlainString();

                    line = String.join("|", details);

                    accountFound = true;
                }

                records.add(line);
            }

        } 
        catch (IOException e) 
        {
            System.out.println("Error reading account records: " + e.getMessage());
            return;

        } 
        catch (NumberFormatException e) 
        {
            System.out.println("An account record contains invalid numeric data.");
            return;
        }

        if (!accountFound) 
        {
            System.out.println("Account not found or you do not own this account.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) 
        {

            for (String record : records) 
            {
                bw.write(record);
                bw.newLine();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error saving account records: " + e.getMessage());
        }
        
        transactionService.recordTransaction(
                username,
                accountNumber,
                "DEPOSIT",
                amount
        );
        
        System.out.println("Deposit successful!");
        System.out.println("Deposited Amount: Rs. " + amount);
        System.out.println("Updated Balance: Rs. " + updatedBalance);
    }
    
    public void withdrawMoney(String username, long accountNumber, BigDecimal amount) 
    {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) 
        {
            System.out.println("Withdrawal amount must be greater than zero.");
            return;
        }

        List<String> records = new ArrayList<>();
        boolean accountFound = false;
        BigDecimal updatedBalance = BigDecimal.ZERO;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {

            String line;

            while ((line = br.readLine()) != null) 
            {
                String[] details = line.split("\\|", -1);

                if (details.length != 5) 
                {
                    records.add(line);
                    continue;
                }

                long existingAccountNumber = Long.parseLong(details[1]);

                if (details[0].equalsIgnoreCase(username)
                        && existingAccountNumber == accountNumber) 
                {
                    BigDecimal currentBalance = new BigDecimal(details[4]);

                    if (currentBalance.compareTo(amount) < 0) 
                    {
                        System.out.println("Insufficient balance.");
                        return;
                    }

                    updatedBalance = currentBalance.subtract(amount);

                    details[4] = updatedBalance.toPlainString();

                    line = String.join("|", details);

                    accountFound = true;
                }

                records.add(line);
            }

        } 
        catch (IOException e) 
        {
            System.out.println("Error reading account records: " + e.getMessage());
            return;

        } 
        catch (NumberFormatException e) 
        {
            System.out.println("An account record contains invalid numeric data.");
            return;
        }

        if (!accountFound) 
        {
            System.out.println("Account not found or you do not own this account.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) 
        {

            for (String record : records) 
            {
                bw.write(record);
                bw.newLine();
            }


        } 
        catch (IOException e) 
        {
            System.out.println("Error saving account records: " + e.getMessage());
        }
        
        transactionService.recordTransaction(
                username,
                accountNumber,
                "WITHDRAWAL",
                amount
        );
        
        System.out.println("Withdrawal successful!");
        System.out.println("Withdrawn Amount: Rs. " + amount);
        System.out.println("Remaining Balance: Rs. " + updatedBalance);
    }
    
    public void changePin(
            String username,
            long accountNumber,
            String currentPin,
            String newPin) {

        if (newPin == null || !newPin.matches("\\d{4}")) {
            System.out.println("New PIN must contain exactly 4 digits.");
            return;
        }

        List<String> records = new ArrayList<>();
        boolean accountFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length != 5) {
                    records.add(line);
                    continue;
                }

                long existingAccountNumber = Long.parseLong(details[1]);

                if (details[0].equalsIgnoreCase(username)
                        && existingAccountNumber == accountNumber) {

                    accountFound = true;

                    if (!details[3].equals(currentPin)) {
                        System.out.println("Incorrect current PIN.");
                        return;
                    }

                    details[3] = newPin;

                    line = String.join("|", details);
                }

                records.add(line);
            }

        } catch (IOException e) {

            System.out.println("Error reading account records: " + e.getMessage());
            return;

        } catch (NumberFormatException e) {

            System.out.println("An account record contains invalid numeric data.");
            return;
        }

        if (!accountFound) {
            System.out.println("Account not found or you do not own this account.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {

            for (String record : records) {
                bw.write(record);
                bw.newLine();
            }

            System.out.println("PIN changed successfully.");

        } catch (IOException e) {

            System.out.println("Error saving account records: " + e.getMessage());
        }
    }
    
    public void transferMoney(String username,
            long senderAccountNumber,
            long receiverAccountNumber,
            BigDecimal amount,
            TransactionService transactionService) {

		// Step 1: Validate transfer amount
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
		System.out.println("Transfer amount must be greater than zero.");
		return;
		}
		
		// Step 2: Prevent transferring to the same account
		if (senderAccountNumber == receiverAccountNumber) {
		System.out.println("Sender and receiver accounts must be different.");
		return;
		}
		
		List<String> records = new ArrayList<>();
		
		boolean senderFound = false;
		boolean receiverFound = false;
		
		String receiverUsername = null;
		
		BigDecimal senderBalance = BigDecimal.ZERO;
		BigDecimal receiverBalance = BigDecimal.ZERO;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		
		String line;
		
		while ((line = br.readLine()) != null) {
		
		String[] details = line.split("\\|", -1);
		
		if (details.length != 5) {
		  records.add(line);
		  continue;
		}
		
		long existingAccountNumber = Long.parseLong(details[1]);
		
		// Step 3: Find sender account
		if (details[0].equalsIgnoreCase(username)
		      && existingAccountNumber == senderAccountNumber) {
		
		  senderFound = true;
		
		  senderBalance = new BigDecimal(details[4]);
		
		  if (senderBalance.compareTo(amount) < 0) {
		      System.out.println("Insufficient balance.");
		      return;
		  }
		
		  // Deduct the transfer amount
		  BigDecimal updatedSenderBalance =
		          senderBalance.subtract(amount);
		
		  details[4] = updatedSenderBalance.toString();
		
		  line = String.join("|", details);
		}
		
		// Step 4: Find receiver account
		else if (existingAccountNumber == receiverAccountNumber) {
		
		  receiverFound = true;
		
		  receiverUsername = details[0];
		
		  receiverBalance = new BigDecimal(details[4]);
		
		  // Add the transfer amount
		  BigDecimal updatedReceiverBalance =
		          receiverBalance.add(amount);
		
		  details[4] = updatedReceiverBalance.toString();
		
		  line = String.join("|", details);
		}
		
		records.add(line);
		}
		
		} catch (IOException e) {
		
		System.out.println("Error reading account records: "
		  + e.getMessage());
		return;
		
		} catch (NumberFormatException e) {
		
		System.out.println("Invalid numeric data in account records.");
		return;
		}
		
		// Step 5: Validate both accounts
		if (!senderFound) {
		System.out.println(
		  "Sender account not found or you do not own this account.");
		return;
		}
		
		if (!receiverFound) {
		System.out.println("Receiver account not found.");
		return;
		}
		
		// Step 6: Save both updated account records
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
		
		for (String record : records) {
		bw.write(record);
		bw.newLine();
		}
		
		} catch (IOException e) {
		
		System.out.println("Error saving account records: "
		  + e.getMessage());
		return;
		}
		
		// Step 7: Record both sides of the transfer
		transactionService.recordTransaction(
		username,
		senderAccountNumber,
		"TRANSFER_OUT",
		amount
		);
		
		transactionService.recordTransaction(
		receiverUsername,
		receiverAccountNumber,
		"TRANSFER_IN",
		amount
		);
		
		// Step 8: Display confirmation
		System.out.println("Money transferred successfully!");
		System.out.println("From Account: " + senderAccountNumber);
		System.out.println("To Account: " + receiverAccountNumber);
		System.out.println("Transfer Amount: ₹" + amount);
	}
    
}

