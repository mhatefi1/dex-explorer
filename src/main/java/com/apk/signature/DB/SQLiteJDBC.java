package com.apk.signature.DB;

import com.apk.signature.Model.DBModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.Util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLiteJDBC {
    private static final String SCHEME_NAME = "export";
    private static final String TABLE_NAME = "signatures";
    private static final String id = "id";
    private static final String NAME = "name";
    private static final String PERMISSION = "permission";
    private static final String ACTIVITY = "activity";
    private static final String SERVICE = "service";
    private static final String RECEIVER = "receiver";
    private static final String STRINGS = "strings";
    private static final String STRING_START = "string_start";
    private static final String STRING_END = "string_end";
    public String path;
    private Connection c;
    private Statement stmt;


    public SQLiteJDBC(String path) {
        this.path = "jdbc:sqlite:" + path + "/" + SCHEME_NAME + ".db";
    }

    public SQLiteJDBC(File file) {
        this.path = "jdbc:sqlite:" + file.getAbsolutePath();
    }

    public void createTable() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE " + TABLE_NAME + " " +
                    "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " " + NAME + " TEXT , " +
                    " " + PERMISSION + " TEXT , " +
                    " " + ACTIVITY + " TEXT , " +
                    " " + SERVICE + " TEXT , " +
                    " " + RECEIVER + " TEXT , " +
                    " " + STRINGS + " TEXT , " +
                    " " + STRING_START + " INT , " +
                    " " + STRING_END + " INT)";
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
            String sql = "INSERT INTO " + TABLE_NAME + " (" + NAME + "," + PERMISSION + "," + ACTIVITY + "," + SERVICE + "," + RECEIVER + "," + STRINGS + "," + STRING_START + "," + STRING_END + ") " +
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

    public ArrayList<SignatureModel> select() {
        ArrayList<SignatureModel> result = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + ";");


            ArrayList<String> permission_list = new ArrayList<>();
            ArrayList<String> activity_list = new ArrayList<>();
            ArrayList<String> service_list = new ArrayList<>();
            ArrayList<String> receiver_list = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString(NAME);
                String permissions = rs.getString(PERMISSION);
                String activities = rs.getString(ACTIVITY);
                String services = rs.getString(SERVICE);
                String receivers = rs.getString(RECEIVER);
                String strings = rs.getString(STRINGS);
                int startIndex = rs.getInt(STRING_START);
                int endIndex = rs.getInt(STRING_END);
                SignatureModel model = new SignatureModel();
                model = new Util().createSignatureModel(permissions, activities, services, receivers, strings, startIndex, endIndex);
                model.setName(name);
                result.add(model);
            }
            rs.close();
            stmt.close();
            c.close();
            System.out.println("Operation done successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
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