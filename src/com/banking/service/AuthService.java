package com.banking.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.banking.model.User;

public class AuthService 
{
	private final File file = new File("data/users.txt");
	
	public AuthService()
    {
        File directory = new File("data");

        if (!directory.exists())
        {
            directory.mkdirs();
        }
    }
	public boolean signup(String username, String password)
    {
        if (username == null || password == null ||
            username.trim().isEmpty() || password.isEmpty() ||
            username.contains("|") || password.contains("|"))
        {
            System.out.println("Invalid username or password.");
            return false;
        }

        try
        {
            if (file.exists())
            {
                try (BufferedReader br = new BufferedReader(new FileReader(file)))
                {
                    String line;

                    while ((line = br.readLine()) != null)
                    {
                        String[] details = line.split("\\|", 2);

                        if (details.length == 2 &&
                            details[0].equalsIgnoreCase(username))
                        {
                            System.out.println("Username already exists.");
                            return false;
                        }
                    }
                }
            }

            User user = new User(username, password);

            try (FileWriter fw = new FileWriter(file, true))
            {
                fw.write(user.getUsername() + "|" +
                         user.getPassword() + System.lineSeparator());
            }

            System.out.println("Signup successful!");
            return true;
        }
        catch (IOException e)
        {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
	
	public boolean login(String username, String password)
    {
        if (username == null || password == null)
        {
            return false;
        }

        if (!file.exists())
        {
            System.out.println("No registered users found.");
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                String[] details = line.split("\\|", 2);

                if (details.length == 2 &&
                    details[0].equals(username) &&
                    details[1].equals(password))
                {
                    System.out.println("Login successful!");
                    return true;
                }
            }

            System.out.println("Invalid username or password.");
            return false;
        }
        catch (IOException e)
        {
            System.out.println("Error reading user data: " + e.getMessage());
            return false;
        }
    }
}
