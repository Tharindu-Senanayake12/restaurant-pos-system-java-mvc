package controller;

import java.util.Scanner;

public class AdminLogin {
    private AdminDatabase adminDatabase;

    public AdminLogin(AdminDatabase adminDatabase) {
        this.adminDatabase = adminDatabase;
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (adminDatabase.validateLogin(username, password)) {
            System.out.println("Login Successful");
            // Redirect to dashboard or next steps here
        } else {
            System.out.println("Invalid credentials");
        }
    }

    public static void main(String[] args) {
        AdminDatabase db = new AdminDatabase();
        AdminLogin login = new AdminLogin(db);
        login.login();
    }
}
