package com.code;

import advancedsql.MySQL;
import advancedsql.query.Create;
import advancedsql.query.Insert;
import advancedsql.query.Select;
import advancedsql.table.ITable;
import com.code.sql.Connection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Connection.init();

        options();
    }

    public static void options() {
        try {
            if (!Auth.isAutenticated()) {
                System.out.println("Select an option.");
                System.out.println("[0] Login.");
                System.out.println("[1] Register.");
                System.out.println("[2] Exit.");
            } else {
                System.out.println("Welcome " + Auth.getUser().get("username") + ", select an option.");
                System.out.println("[0] Insert product.");
                System.out.println("[1] Delete product.");
                System.out.println("[2] Product list.");
                System.out.println("[3] Exit.");
            }

            int option = Integer.parseInt(scanner.nextLine());

            if (Auth.isAutenticated()) executeAuthenticated(option); else executeRegular(option);

            System.out.println("");

            options();
        } catch (NumberFormatException e) {
            System.out.println("Invalid option, try again.");

            options();
        }
    }

    public static void executeRegular(int option) {
        switch (option) {
            case 0:
                try {
                    System.out.println("Enter your username: ");

                    String username = scanner.nextLine();

                    ITable usersTable = Connection.getUsersTable();

                    Map<String, Object> fetch = usersTable.select().where("username = ?", username).fetch();

                    if (fetch.get("username") == null) {
                        System.out.println("Username does not exists, try again.");

                        executeRegular(option);

                        return;
                    }

                    System.out.println("Enter your password: ");

                    String password = scanner.nextLine();

                    if (!password.equals((String) fetch.get("password"))) {
                        System.out.println("Invalid password, try again.");

                        executeRegular(option);

                        return;
                    }

                    Auth.setAuthenticated(true);

                    Auth.setUser(fetch);

                    System.out.println("User successfully logged in.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;

            case 1:
                try {
                    System.out.println("Enter your username: ");

                    String username = scanner.nextLine();

                    ITable usersTable = Connection.getUsersTable();

                    Map<String, Object> fetch = usersTable.select().where("username = ?", username).fetch();

                    if (fetch.get("username") != null) {
                        System.out.println("Username is already in use, try again.");

                        executeRegular(option);

                        return;
                    }

                    System.out.println("Enter your email: ");

                    String email = scanner.nextLine();

                    fetch = usersTable.select().where("email = ?", email).fetch();

                    if (fetch.get("username") != null) {
                        System.out.println("Email is already in use, try again.");

                        executeRegular(option);

                        return;
                    }

                    System.out.println("Enter your password: ");

                    String password = scanner.nextLine();

                    usersTable.insert().fields(new HashMap<>(){{
                        put("username", username);
                        put("email", email);
                        put("password", password);
                    }}).execute();

                    Auth.setAuthenticated(true);

                    Auth.setUser(usersTable.select().where("username = ?" , username).fetch());

                    System.out.println("User successfully registered.");
                } catch (SQLException e){
                    e.printStackTrace();
                }

                break;

            case 2:

                System.exit(0);

                break;

            default:
                System.out.println("Invalid option, try again.");
                break;
        }
    }

    public static void executeAuthenticated(int option) {
        ITable usersProducts = Connection.getUsersProducts();

        switch (option) {
            case 0:
                System.out.println("Product name: ");

                String productName = scanner.nextLine();

                System.out.println("Product price: ");

                try {
                    float productPrice = Float.parseFloat(scanner.nextLine());

                    usersProducts.insert(new HashMap<>(){{
                        put("name", productName);
                        put("price", productPrice);
                        put("user_id", Auth.getUser().get("id"));
                    }}).execute();

                    System.out.println("Product successfully inserted.");
                } catch (NumberFormatException | SQLException e) {
                    if (e instanceof NumberFormatException) {
                        System.out.println("Invalid product price, try again.");

                        executeAuthenticated(option);

                        return;
                    }

                    ((SQLException) e).printStackTrace();

                    return;
                }

                break;

            case 1:

                try {
                    System.out.println("Enter the product ID: ");

                    String id = scanner.nextLine();

                    Map<String, Object> fetch = usersProducts.select().where("id = ? AND user_id = ?", id, Auth.getUser().get("id")).fetch();

                    if (fetch.get("name") == null) {
                        System.out.println("Product does not exists, try again.");

                        executeAuthenticated(option);

                        return;
                    }

                    usersProducts.delete().where("id = ?", id).execute();

                    System.out.println("Product successfully deleted.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;

            case 2:

                try {
                    List<Map<String, Object>> products = usersProducts.select().where("user_id = ?", Auth.getUser().get("id")).fetchAllAsList();

                    for (Map<String, Object> product: products) {
                        System.out.println("Product ID: " + product.get("id"));
                        System.out.println("Product name: " + product.get("name"));
                        System.out.println("Product price: " + product.get("price"));
                        System.out.println("");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;

            case 3:

                System.exit(0);

                break;

            default:
                System.out.println("Invalid option, try again.");
                break;
        }
    }
}
