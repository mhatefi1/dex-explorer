package com.apk.signature.DB;

import com.apk.signature.Model.DBModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLiteJDBC {
    private static final String SCHEME_NAME = "test";
    private static final String TABLE_NAME = "signatures";
    private static final String id = "id";
    private static final String name = "name";
    private static final String permission = "permission";
    private static final String activity = "activity";
    private static final String service = "service";
    private static final String receiver = "receiver";
    private static final String strings = "strings";
    private static final String string_start = "string_start";
    private static final String string_end = "string_end";
    public String path;
    private Connection c;
    private Statement stmt;


    public SQLiteJDBC(String path) {
        this.path = "jdbc:sqlite:" + path + "/" + SCHEME_NAME + ".db";
    }

    public void createTable() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE " + TABLE_NAME + " " +
                    "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " " + name + " TEXT , " +
                    " " + permission + " TEXT , " +
                    " " + activity + " TEXT , " +
                    " " + service + " TEXT , " +
                    " " + receiver + " TEXT , " +
                    " " + strings + " TEXT , " +
                    " " + string_start + " INT , " +
                    " " + string_end + " INT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            System.out.println("Table created successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void insert(DBModel model) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + name + "," + permission + "," + activity + "," + service + "," + receiver + "," + strings + "," + string_start + "," + string_end + ") " +
                    "VALUES (" + "\"" + model.name + "\"" + ", " + "\"" + model.permissions + "\"" + ", " +
                    "\"" + model.activities + "\"" + ", " + "\"" + model.services + "\"" + ", " +
                    "\"" + model.receivers + "\"" + ", " + "\"" + model.strings + "\"" + ", " +
                    model.string_start + ", " + model.string_end + " );";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
            System.out.println("Records created successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void select() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + ";");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");

                System.out.println("ID = " + id);
                System.out.println("NAME = " + name);
                System.out.println("AGE = " + age);
                System.out.println("ADDRESS = " + address);
                System.out.println("SALARY = " + salary);
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
            System.out.println("Operation done successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void update() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "UPDATE " + TABLE_NAME + " set SALARY = 25000.00 where ID=1;";
            stmt.executeUpdate(sql);
            c.commit();

            ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY;");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");

                System.out.println("ID = " + id);
                System.out.println("NAME = " + name);
                System.out.println("AGE = " + age);
                System.out.println("ADDRESS = " + address);
                System.out.println("SALARY = " + salary);
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
            System.out.println("Operation done successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void delete() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "DELETE from COMPANY where ID=2;";
            stmt.executeUpdate(sql);
            c.commit();

            ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY;");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");

                System.out.println("ID = " + id);
                System.out.println("NAME = " + name);
                System.out.println("AGE = " + age);
                System.out.println("ADDRESS = " + address);
                System.out.println("SALARY = " + salary);
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
            System.out.println("Operation done successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}