package com.code.sql;

import advancedsql.MySQL;
import advancedsql.query.Alter;
import advancedsql.query.Create;
import advancedsql.table.ITable;

import java.sql.SQLException;

public class Connection {

    private static MySQL connection;

    private static ITable usersTable;

    private static ITable usersProducts;

    public static void init() {
        try {
            connection = new MySQL("127.0.0.1", 3306, "root", "", "testfacebook");

            if (!connection.isConnected()) {
                System.out.println("Not connected, execute the code when your server is online.");

                return;
            }

            usersTable = connection.table("users");

            if (!usersTable.exists()) {
                Create create = usersTable.create();

                create.id();
                create.string("username");
                create.string("password");
                create.string("email");
                create.execute();
            }

            usersProducts = connection.table("users_products");

            if (!usersProducts.exists()) {
                Create create = usersProducts.create();

                create.id();
                create.string("name");
                create.decimal("price");
                create.integer("user_id");

                create.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ITable getUsersTable() {
        return usersTable;
    }

    public static ITable getUsersProducts() {
        return usersProducts;
    }

    public static MySQL getConnection() {
        return connection;
    }
}
