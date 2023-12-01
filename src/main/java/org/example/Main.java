package org.example;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.sql.*;
import java.util.Scanner;

import java.io.File;

public class Main {

    private static final String url = "jdbc:mariadb://192.168.128.250:3306/";
    private static final String dbuser = "remote";
    private static final String dbpassword = "*******";

    public static void main(String[] args) {

        System.out.println("|============================={ Northwind Systems }=============================|\n");

        menuUI();

    }

    public static void menuUI(){

        System.out.println("    1. Initialize database");
        System.out.println("    2. Database Access");
        System.out.println("    3. Add Employee");
        System.out.println("    4. Import JSON");
        System.out.println("    5. Check DB Server Status");
        System.out.println("    6. Stock");
        System.out.println("    7. Employee list");
        System.out.println("    8. List Orders");
        System.out.println("    9. Add Product to Fav");
        System.out.println("    10. Add New Order\n");

        System.out.println(" [0] Exit\n");

        System.out.println("|-------------------------------------------------------------------------------|");
        menuUX();

    }


    public static void menuUX(){

        System.out.print("| [> "); int option; Scanner scanner = new Scanner(System.in); option = scanner.nextInt();

        switch(option){

            case 1:

                System.out.println("|-------------------------------------------------------------------------------|");

                System.out.println("\n    You're about to initialize the database with the default configuration, ");
                System.out.println("    this should ONLY be done once, when creating other database.\n");

                System.out.println("|-------------------------------------------------------------------------------|");
                System.out.println("| Do you wish to continue? [yes/no]");
                System.out.print("| [> "); String response; Scanner scanner_response= new Scanner(System.in); response = scanner_response.nextLine();


                if (response.equals("yes") || response.equals("YES")) {

                    System.out.println("|-------------------------------------------------------------------------------|");
                    System.out.println("| Do you want to use default name 'almacén'? [yes/no]");
                    System.out.print("| [> "); Scanner scanner_response2 = new Scanner(System.in); response = scanner_response2.nextLine();

                    System.out.println("|-------------------------------------------------------------------------------|");

                    if (response.equals("yes") || response.equals("YES")){

                        dbSetup("almacen", url, dbuser, dbpassword);

                        System.out.println("|-------------------------------------------------------------------------------|\n");

                        menuUI();

                    } else{

                        System.out.print("| Write a name for the database -> "); Scanner scanner_dbname = new Scanner(System.in); response = scanner_dbname.nextLine();
                        System.out.println("|-------------------------------------------------------------------------------|");
                        dbSetup(response, url, dbuser, dbpassword);
                        System.out.println("|-------------------------------------------------------------------------------|\n");

                        menuUI();

                    }

                } else {

                    System.out.println("|-------------------------------------------------------------------------------|\n");
                    menuUI();

                }

                break;

            case 2:



                break;

            case 3:

                employeeRegister();

                break;

            case 4:

                System.out.println("|-------------------------------------------------------------------------------|");
                createDir("./inputJSON", "inputJSON");
                System.out.print("| ");basicLoadingBar(60); System.out.print("OK");
                System.out.println("\n|-------------------------------------------------------------------------------|");
                jsontodb("./inputJSON/inputJSON.json", "almacen");
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            case 5:

                System.out.println("|-------------------------------------------------------------------------------|");
                dbcheck();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            case 6:

                System.out.println("|-------------------------------------------------------------------------------|");

                System.out.println("| Would you like to specify a max price for the products list? [yes/no]");
                System.out.print("| [> "); Scanner scanner_minprice_option = new Scanner(System.in); response = scanner_minprice_option.nextLine();

                System.out.println("|-------------------------------------------------------------------------------|");

                if (response.equals("yes") || response.equals("YES")){

                    System.out.print("| Specify a maximum price -> "); Scanner scanner_minprice = new Scanner(System.in); int minprice = scanner_minprice.nextInt();
                    System.out.println("|-------------------------------------------------------------------------------|");
                    listLowerThan(minprice, "almacen");
                    pause();
                    System.out.println("|-------------------------------------------------------------------------------|\n");
                    menuUI();

                }else{

                    listProducts("almacen");
                    pause();
                    System.out.println("|-------------------------------------------------------------------------------|\n");
                    menuUI();

                }

                break;

            case 7:

                System.out.println("|-------------------------------------------------------------------------------|");
                System.out.println("| List of Registered Employees");
                listUser("almacen");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            case 8:

                System.out.println("|-------------------------------------------------------------------------------|");
                listOrder("almacen");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            case 9:

                System.out.println("|-------------------------------------------------------------------------------|");
                insertFavGraterThan(1000, "almacen");
                System.out.println("|-------------------------------------------------------------------------------|");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            case 10:

                placeOrder();

                break;

            case 0:
                System.out.println("|-------------------------------------------------------------------------------|");
                System.exit(0);
                System.out.println("|-------------------------------------------------------------------------------|");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

                break;

            default:

                System.out.println("|-------------------------------------------------------------------------------|");
                System.out.println("| Invalid Option < " + option + ">");
                System.out.println("|-------------------------------------------------------------------------------|");
                menuUX();

        }

    }

    public static void placeOrder(){

        Scanner scanner_id = new Scanner(System.in);
        Scanner scanner_descripcion = new Scanner(System.in);
        Scanner scanner_amount = new Scanner(System.in);

        System.out.println("|-------------------------------------------------------------------------------|");
        System.out.println("| Please fill all fields for placing the order");
        System.out.println("|===============================================================================|");
        System.out.print("| Product's ID        [> "); int product_id = scanner_id.nextInt();
        System.out.print("| Description         [> "); String descripcion = scanner_descripcion.nextLine();
        System.out.print("| Amount [1,2,3...]   [> "); int amount = scanner_amount.nextInt();
        System.out.println("|-------------------------------------------------------------------------------|");
        insertOrder("almacen", product_id, descripcion, amount);

    }

    public static void insertOrder(String dbName, int product_id, String descripcion, int amount){

        String dburl = url + dbName;
        double totalPrice = 0.0;
        String product = "";

        String sqlQuery = "SELECT nombre, precio FROM productos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            pstmt.setInt(1, product_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                product = rs.getString("nombre");
                double price = rs.getDouble("precio");
                totalPrice = price * amount;
            } else {
                System.out.println("| Product ID <" + product_id + "> was not found.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String sqlInsert = "INSERT INTO pedidos (id_producto, descripcion, precio_total) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setInt(1, product_id);
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, totalPrice);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {

                System.out.println("| Order Placed Correctly!");
                System.out.println("|*******************************************************************************|");
                System.out.println("| Order's Product ID    | " + product_id); //" placed correctly for $" + totalPrice);
                System.out.println("| Item Purchased        | " + product);
                System.out.println("| Nº Items Purchased    | " + amount);
                System.out.println("|*******************************************************************************|");
                System.out.println("| Total                 |-> " + totalPrice + "$");
                System.out.println("|-------------------------------------------------------------------------------|");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

            } else {

                System.out.println("| Order containing product ID " + product_id + " failed to create!");
                System.out.println("|-------------------------------------------------------------------------------|");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listOrder(String dbName) {

        String dburl = url + dbName;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             Statement stmt = conn.createStatement()) {

            stmt.execute("USE " + dbName);

            String sql = "SELECT * FROM pedidos";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("| Orders Pending Process");
            System.out.println("|===============================================================================|");
            while (rs.next()) {
                int id = rs.getInt("id");
                int idProducto = rs.getInt("id_producto");
                String descripcion = rs.getString("descripcion");
                double precioTotal = rs.getDouble("precio_total");

                System.out.println("| Ticket Nº" + id);
                System.out.println("|");
                System.out.println("| Product's ID          |-> " + idProducto);
                System.out.println("| Client's Description  |-> " + descripcion);
                System.out.println("| Total                 |-> " + precioTotal + " $");
                System.out.println("|===============================================================================|");

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void listUser(String dbName) {

        String dburl = url + dbName;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM empleados";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("|===============================================================================|");

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                String surname = rs.getString("apellidos");
                String email = rs.getString("correo");

                System.out.println("| Employee' ID nº " + id);
                System.out.println("|");
                System.out.println("| Name      -> " + name);
                System.out.println("| Surname   -> " + surname);
                System.out.println("| eMail     -> " + email);
                System.out.println("|===============================================================================|");

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String executeSQL(String sql) {

        try (Connection conn = DriverManager.getConnection(url, dbuser, dbpassword);
             Statement stmt = conn.createStatement()) {

            boolean isQuery = sql.trim().toLowerCase().startsWith("select");
            if (isQuery) {
                stmt.executeQuery(sql);
            } else {
                stmt.executeUpdate(sql);
            }
            return "OK";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }

    }

    public static void insertFavGraterThan(int price, String dbName) {

        String dburl = url + dbName;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO productos_fav (id_producto) " +
                    "SELECT id FROM productos WHERE precio > " + price;

            int rowsAffected = stmt.executeUpdate(sql);

            System.out.println("| " + rowsAffected + " products have been added to favorites");

        }catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static void listLowerThan(int price, String dbName) {

        String dburl = url + dbName;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos WHERE precio < ?")) {

            stmt.setInt(1, price);

            ResultSet rs = stmt.executeQuery();

            System.out.println("| Showing products under " + price + " $ :");
            System.out.println("|===============================================================================|");

            int id = 1;

            while (rs.next()) {

                //int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");

                System.out.println("| Nº " + id);
                System.out.println("|");
                System.out.println("| Product           |-> " + nombre);
                System.out.println("| Description       |-> " + descripcion);
                System.out.println("| Available Units   |-> " + cantidad + " Units Left");
                System.out.println("| Price             |-> " + precio + " $");
                System.out.println("|===============================================================================|");

                id++;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void employeeRegister(){

        Scanner scanner = new Scanner(System.in);

        System.out.println("|-------------------------------------------------------------------------------|");
        System.out.println("| Fill with Employee's Information");
        System.out.println("|===============================================================================|");
        System.out.print("| Name      [> "); String name = scanner.nextLine();
        System.out.print("| Surname   [> "); String surname = scanner.nextLine();
        System.out.print("| eMail     [> "); String email = scanner.nextLine();
        System.out.println("|-------------------------------------------------------------------------------|");

        addEmployeetoDB("almacen", name, surname, email);

    }

    public static void addEmployeetoDB(String dbName, String name, String surname, String email) {

        String dburl = url + dbName;

        String sql = "INSERT INTO empleados (nombre, apellidos, correo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {

                System.out.println("| Added user " + name + " " + surname + " to the database!");
                System.out.println("|-------------------------------------------------------------------------------|");
                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

            } else {

                System.out.println("| There was an error adding user " + name + " " + surname + " to the database.");
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

            }
        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static void listProducts(String dbName) {

        String dburl = url + dbName;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             Statement stmt = conn.createStatement()) {

            stmt.execute("USE " + dbName);

            String sql = "SELECT * FROM productos";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("| Showing All Products:");
            System.out.println("|===============================================================================|");
            while (rs.next()) {

                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");

                System.out.println("| Nº " + id);
                System.out.println("|");
                System.out.println("| Product           |-> " + nombre);
                System.out.println("| Description       |-> " + descripcion);
                System.out.println("| Available Units   |-> " + cantidad + " Units Left");
                System.out.println("| Price             |-> " + precio + " $");
                System.out.println("|===============================================================================|");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dbSetup(String dbName, String url, String dbuser, String dbpassword){

        String dburl = url + "?user=" + dbuser + "&password=" + dbpassword; // Update with your database credentials

        try (Connection conn = DriverManager.getConnection(dburl)) {

            Statement stmt = conn.createStatement();

            stmt.execute("DROP DATABASE IF EXISTS " + dbName);

            String sqlCreateDB = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(sqlCreateDB);

            stmt.execute("USE " + dbName);

            String sqlCreateProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(255), " +
                    "descripcion VARCHAR(255), " +
                    "cantidad INT, " +
                    "precio DECIMAL(10, 2))";
            stmt.executeUpdate(sqlCreateProductos);

            String sqlCreateEmpleados = "CREATE TABLE IF NOT EXISTS empleados (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(255), " +
                    "apellidos VARCHAR(255), " +
                    "correo VARCHAR(255))";
            stmt.executeUpdate(sqlCreateEmpleados);

            String sqlCreatePedidos = "CREATE TABLE IF NOT EXISTS pedidos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_producto INT, " +
                    "descripcion VARCHAR(255), " +
                    "precio_total DECIMAL(10, 2), " +
                    "FOREIGN KEY (id_producto) REFERENCES productos(id))";
            stmt.executeUpdate(sqlCreatePedidos);

            String sqlCreateProductosFav = "CREATE TABLE IF NOT EXISTS productos_fav (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_producto INT, " +
                    "FOREIGN KEY (id_producto) REFERENCES productos(id))";
            stmt.executeUpdate(sqlCreateProductosFav);

            System.out.print("| "); basicLoadingBar(30);
            System.out.println("Database <" + dbName + "> created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void dbcheck(){

        try {
            Connection connection = DriverManager.getConnection(url, dbuser, dbpassword);
            System.out.println("| The DB Server is online - Connected to -> " + url);
            connection.close();
        } catch (SQLException e) {
            System.out.println("| ERROR - DB Server seems to be down/inaccessible");
            //e.printStackTrace();
        }

    }

    public static void createDir(String path, String folderName){

        File folder = new File(path);

        if (!folder.exists()) {

            System.out.println("|-------------------------------------------------------------------------------|");
            System.out.println("| Folder " + folderName + " does not exist. Do you want to create it?");
            System.out.print("| [> "); String response; Scanner scanner = new Scanner(System.in); response = scanner.nextLine();
            System.out.println("|-------------------------------------------------------------------------------|");

            if (response.equals("yes") || response.equals("YES")) {

                boolean isCreated = folder.mkdirs();

                if (isCreated) {

                    basicLoadingBar(30);

                    System.out.println("| Folder < " + folderName + " > was created successfully.");
                    System.out.println("|-------------------------------------------------------------------------------|");

                    //Continues with import

                } else {

                    System.out.println("| Failed to create the folder < " + folderName + " >.");
                    System.out.println("|-------------------------------------------------------------------------------|\n");
                    menuUI();

                }

            } else{

                pause();
                System.out.println("|-------------------------------------------------------------------------------|\n");
                menuUI();

            }

        } else {

            System.out.println("| Folder < " + folderName + " > already created! Proceeding with import...");
            System.out.println("|-------------------------------------------------------------------------------|");

            //Continues with import

        }

    }

    public static void jsontodb(String jsonFilePath, String dbName){

        try (Connection conn = DriverManager.getConnection(url, dbuser, dbpassword)) {

            JSONTokener tokener = new JSONTokener(new FileReader(jsonFilePath));
            JSONObject obj = new JSONObject(tokener);

            JSONArray products = obj.getJSONArray("products");

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                insertProductIntoDB(product, conn, dbName);
            }

            System.out.println("| Products imported successfully into DB [ " + dbName + " ]");

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void insertProductIntoDB(JSONObject product, Connection conn,String dbName) throws SQLException {

        Statement stmt = conn.createStatement();

        stmt.execute("USE " + dbName);

        String sql = "INSERT INTO productos (nombre, descripcion, cantidad, precio) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getString("title"));
            pstmt.setString(2, product.getString("description"));
            pstmt.setInt(3, product.getInt("stock"));
            pstmt.setDouble(4, product.getDouble("price"));

            pstmt.executeUpdate();
        }
    }

    public static void basicLoadingBar(int length) {

        System.out.print("|");

        for (int i = 0; i < length; i++) {
            System.out.print("#");

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Loading bar interrupted.");
                break;
            }
        }

        System.out.print("| ");

    }
    public static void pause(){

        //System("pause");

        Scanner scanner = new Scanner(System.in);
        System.out.print("| Press Enter to continue...");
        scanner.nextLine();

    }

    public static void sleep(int miliseconds){

        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}