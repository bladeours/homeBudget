package com.example.homeBudget;

import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Database {
    private static Connection connection;
    public Database() throws ClassNotFoundException, SQLException {

        File f = new File("db\\shopping.db");
        if(!f.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection error");
            alert.setContentText("Can't connect with database!");
            alert.showAndWait();
        }else{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db\\shopping.db");
        }

    }

    public String showPurchases() throws SQLException {
        JSONObject allPurchasesJson = new JSONObject();
        JSONObject purchaseJson = new JSONObject();
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT \"Purchases\".id, date(date) AS date, \"Shops\".name AS shop,\n" +
                "                \"Categories\".name AS category, \"Purchases\".price\n" +
                "                FROM Purchases\n" +
                "                 INNER JOIN Categories on Categories.id = Purchases.category_id\n" +
                "                INNER JOIN Shops on Shops.id = Purchases.shop_id;");
        while (result.next()){
            purchaseJson.clear();
            purchaseJson.put("date",result.getString("date"));
            purchaseJson.put("shop",result.getString("shop"));
            purchaseJson.put("category",result.getString("category"));
            purchaseJson.put("price",result.getFloat("price"));
            allPurchasesJson.put(Integer.toString(result.getInt("id")),new JSONObject(purchaseJson.toString()));
        }
        return allPurchasesJson.toString();
    }

    public String makeJSonCategory() throws SQLException {
        JSONObject categoryJson = new JSONObject();
        JSONObject categoryJsonSmall = new JSONObject();
        JSONArray categoryArray = new JSONArray();
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"Categories\";");
        while (result.next()) {
            categoryArray.clear();
            int id = result.getInt("id");
            String name = result.getString("name");
            categoryJsonSmall.put(Integer.toString(id),name);
            categoryArray.put(categoryJsonSmall);
        }
        categoryJson.put("category",categoryArray);
        return categoryJson.toString();
    }

    public String makeJSonIncomeCategory() throws SQLException {
        JSONObject categoryJson = new JSONObject();
        JSONObject categoryJsonSmall = new JSONObject();
        JSONArray categoryArray = new JSONArray();
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"IncomeCategories\";");
        while (result.next()) {
            categoryArray.clear();
            int id = result.getInt("id");
            String name = result.getString("name");
            categoryJsonSmall.put(Integer.toString(id),name);
            categoryArray.put(categoryJsonSmall);
        }
        categoryJson.put("category",categoryArray);
        return categoryJson.toString();
    }

    public String makeJSonShop() throws SQLException {
        JSONObject shopJson = new JSONObject();
        JSONObject shopJsonSmall = new JSONObject();
        JSONArray shopArray = new JSONArray();
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"Shops\";");
        while (result.next()) {
            shopArray.clear();
            int id = result.getInt("id");
            String name = result.getString("name");
            shopJsonSmall.put(Integer.toString(id),name);
            shopArray.put(shopJsonSmall);
        }
        shopJson.put("shop",shopArray);
        return shopJson.toString();
    }

    public String showStats() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT CAST(strftime('%m', date) as INTEGER) as month  FROM Purchases group by month;");
        //create list of months which exist in database and sor it
        ArrayList<Integer> months = new ArrayList<>();
        while (result.next()){
            months.add(result.getInt("month"));
        }
        Collections.sort(months);
        JSONObject monthJson = new JSONObject();
        JSONObject statsJson = new JSONObject();

        result = stmt.executeQuery("SELECT Categories.name AS category,CAST(strftime('%m', date) as INTEGER) AS month , SUM(price) AS sum\n" +
                "        from Purchases\n" +
                "        JOIN Categories on Categories.id = Purchases.category_id\n" +
                "        group by CAST(strftime('%m', date) as INTEGER), Categories.name order by month;");
        int i=0;

        //saving sums of categories into Json
        while (result.next()){
            if (result.getInt("month") == months.get(i)){
                monthJson.put(result.getString("category"),result.getFloat("sum"));
            }
            else{
                statsJson.put(Integer.toString(months.get(i)), new JSONObject(monthJson.toString()));
                i++;
                monthJson.clear();
                if (i < months.size())
                {
                    monthJson.put(result.getString("category"), result.getFloat("sum"));
                }
            }
        }
        statsJson.put(Integer.toString(months.get(i)), new JSONObject(monthJson.toString()));
        return statsJson.toString();
    }

    public void addPurchase(JSONObject fromClient) throws SQLException, ParseException {
        Statement stmt = connection.createStatement();
        String shop = fromClient.get("shop").toString();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"Shops\" WHERE name='" + shop + "';");
        result.next();
        int shop_id;
        //check if shop exists, if not then add shop to table "Shop"
        try {
            shop_id = result.getInt("id");
        }
        catch (SQLException e){
            stmt.executeUpdate("INSERT INTO \"Shops\" (name) VALUES ('" + shop + "');");
            result = stmt.executeQuery("SELECT * FROM \"Shops\" WHERE name='" + shop + "';");
            result.next();
            shop_id = result.getInt("id");
        }
        //round price
        DecimalFormat df = new DecimalFormat("0.00");
        float price = Float.parseFloat(fromClient.getString("price"));
        //format date
        String stringDate = fromClient.getString("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(stringDate,formatter);
        //check category ID
        String category = fromClient.get("category").toString();
        result = stmt.executeQuery("SELECT * FROM \"Categories\" WHERE name='" + category + "';");
        result.next();
        int category_id = result.getInt("id");
        PreparedStatement st = connection.prepareStatement(
                "INSERT INTO \"Purchases\" (category_id, price, date, shop_id)" +
                        "VALUES (?,?,?,?)");
        //add data to database
        st.setObject(1,category_id);
        st.setObject(2,Math.round(price*100.0)/100.0);
        st.setObject(3,date);
        st.setObject(4,shop_id);
        st.executeUpdate();
    }

    public void removePurchase(String idToRemove) throws SQLException {
        JSONObject removePurchaseJson = new JSONObject(idToRemove);
        JSONArray idsToRemove = new JSONArray(removePurchaseJson.getJSONArray("id"));
        for(int i=0;i<idsToRemove.length();i++){
        int id = idsToRemove.getInt(i);
        Statement stmt = connection.createStatement();
        String sql = "DELETE FROM \"Purchases\" WHERE id = " + id + ";";
        stmt.executeUpdate(sql);
        }
    }

    public void addPurchaseCategoryToDatabase(String category) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"Categories\";");
        while (result.next()){
            if(result.getString("name").toLowerCase(Locale.ROOT).equals(category.toLowerCase())){
                throw new SQLException();
            }
        }
        stmt.executeUpdate("INSERT INTO \"Categories\" (name) VALUES ('" + category + "');");
    }

    public void removePurchaseCategory(String category) throws SQLException {
        Statement stmt = connection.createStatement();
        Statement stmt2 = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT \"Purchases\".id, \"Categories\".name AS category\n" +
                "FROM \"Purchases\"\n" +
                "         INNER JOIN \"Categories\" on \"Categories\".id = \"Purchases\".category_id\n" +
                "where Categories.name='"+ category +"';");
        while (result.next()){
            stmt2.executeUpdate("DELETE from \"Purchases\" where id="+result.getInt("id")+";");
        }
        stmt.executeUpdate("DELETE from \"Categories\" where name='"+ category +"'");
    }

    public void removeShop(String shop) throws SQLException{
        Statement stmt = connection.createStatement();
        Statement stmt2 = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT \"Purchases\".id, \"Shops\".name AS category\n" +
                "FROM \"Purchases\"\n" +
                "         INNER JOIN \"Shops\" on \"Shops\".id = \"Purchases\".shop_id\n" +
                "where Shops.name='"+ shop +"';");
        while (result.next()){
            stmt2.executeUpdate("DELETE from \"Purchases\" where id="+result.getInt("id")+";");
        }
        stmt.executeUpdate("DELETE from \"Shops\" where name='"+ shop +"'");
    }

    public void editPurchase(String toEdit) throws SQLException {
        JSONObject editPurchaseJson = new JSONObject(toEdit);
        Statement stmt = connection.createStatement();
        int idToEdit = editPurchaseJson.getInt("id");
        String column = editPurchaseJson.getString("column");
        String newValue = editPurchaseJson.getString("newValue");
        String table = "";
        switch (column) {
            case "shop_id":
                table = "Shops";
                break;
            case "category_id":
                table = "Categories";
                break;
            case "date":
                stmt.executeUpdate("update \"Purchases\" set " + column + "='" + newValue + "' where id="+ idToEdit +";");
                return;
            case "price":
                stmt.executeUpdate("update \"Purchases\" set " + column + "=" + newValue + " where id="+ idToEdit +";");
                return;
        }

        int idCategoryOrShop = 0;
        ResultSet resultSet = stmt.executeQuery("select * from \"" + table + "\";");
        while (resultSet.next()){
            if(resultSet.getString("name").equals(newValue)){
                idCategoryOrShop = resultSet.getInt("id");
                break;
            }
        }
        stmt.executeUpdate("update \"Purchases\" set " + column + "=" + idCategoryOrShop + " where id="+ idToEdit +";");
    }

    public void editIncome(String toEdit) throws SQLException {
        JSONObject editIncomeJson = new JSONObject(toEdit);
        Statement stmt = connection.createStatement();
        int idToEdit = editIncomeJson.getInt("id");
        String column = editIncomeJson.getString("column");
        String newValue = editIncomeJson.getString("newValue");
        String table = "";
        switch (column) {
            case "category_id":
                table = "IncomeCategories";
                break;
            case "date":
                stmt.executeUpdate("update \"Incomes\" set " + column + "='" + newValue + "' where id="+ idToEdit +";");
                return;
            case "amount":
                stmt.executeUpdate("update \"Incomes\" set " + column + "=" + newValue + " where id="+ idToEdit +";");
                return;
        }

        int idCategory = 0;
        ResultSet resultSet = stmt.executeQuery("select * from \"" + table + "\";");
        while (resultSet.next()){
            if(resultSet.getString("name").equals(newValue)){
                idCategory = resultSet.getInt("id");
                break;
            }
        }
        stmt.executeUpdate("update \"Incomes\" set " + column + "=" + idCategory + " where id="+ idToEdit +";");
    }

    public void addIncome(String fromClient) throws SQLException {
        JSONObject income = new JSONObject(fromClient);
        Statement stmt = connection.createStatement();

        //round amount
        DecimalFormat df = new DecimalFormat("0.00");
        float amount = Float.parseFloat(income.getString("amount"));
        //format date
        String stringDate = income.getString("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(stringDate,formatter);
        //check category ID
        String category = income.get("category").toString();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"IncomeCategories\" WHERE name='" + category + "';");
        result.next();
        int category_id = result.getInt("id");
        PreparedStatement st = connection.prepareStatement(
                "INSERT INTO \"Incomes\" (category_id, amount, date)" +
                        "VALUES (?,?,?)");
        //add data to database
        st.setObject(1,category_id);
        st.setObject(2,Math.round(amount*100.0)/100.0);
        st.setObject(3,date);
        st.executeUpdate();

    }

    public void addIncomeCategoryToDatabase(String category) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM \"IncomeCategories\";");
        while (result.next()){
            if(result.getString("name").toLowerCase(Locale.ROOT).equals(category.toLowerCase())){
                throw new SQLException();
            }
        }
        stmt.executeUpdate("INSERT INTO \"IncomeCategories\" (name) VALUES ('" + category + "');");
    }

    public void removeIncomeCategory (String category) throws SQLException {
        Statement stmt = connection.createStatement();
        Statement stmt2 = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT \"Incomes\".id, \"IncomeCategories\".name AS category\n" +
                "FROM \"Incomes\"\n" +
                "         INNER JOIN \"IncomeCategories\" on \"IncomeCategories\".id = \"Incomes\".category_id\n" +
                "where category='"+ category +"';");
        while (result.next()){
            stmt2.executeUpdate("DELETE from \"Incomes\" where id="+result.getInt("id")+";");
        }
        stmt.executeUpdate("DELETE from \"IncomeCategories\" where name='"+ category +"'");
    }

    public String showIncomes() throws SQLException {
        JSONObject allPurchasesJson = new JSONObject();
        JSONObject purchaseJson = new JSONObject();
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT \"Incomes\".id, date(date) AS date, \"IncomeCategories\".name AS category,\n" +
                "       \"Incomes\".amount\n" +
                "FROM Incomes\n" +
                "    INNER JOIN IncomeCategories on IncomeCategories.id = Incomes.category_id;");
        while (result.next()){
            purchaseJson.clear();
            purchaseJson.put("date",result.getString("date"));
            purchaseJson.put("category",result.getString("category"));
            purchaseJson.put("amount",result.getFloat("amount"));
            allPurchasesJson.put(Integer.toString(result.getInt("id")),new JSONObject(purchaseJson.toString()));
        }
        return allPurchasesJson.toString();
    }

    public void removeIncome(String idToRemove) throws SQLException {
        JSONObject removeIncomeJson = new JSONObject(idToRemove);
        JSONArray idsToRemove = new JSONArray(removeIncomeJson.getJSONArray("id"));
        for(int i=0;i<idsToRemove.length();i++) {
            int id = idsToRemove.optInt(i);
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM \"Incomes\" WHERE id = " + id + ";";
            stmt.executeUpdate(sql);
        }
    }

    public String showBudget() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("select (budget - purchases + incomes) as budget from" +
                                                            " (select ifnull(sum(Incomes.amount),0)  as incomes,\n" +
                                                            "(select ifnull(Budget.amount,0) from Budget) as budget,\n" +
                                                            "(select ifnull(sum(price),0) from Purchases) as purchases\n" +
                                                            "from Incomes);");
        resultSet.next();
        return resultSet.getString("budget");
    }

    public String showInitialBudget() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("select * from Budget");
        resultSet.next();

        return resultSet.getString("amount");
    }

    public void editInitialBudget(String newValue) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("update Budget set amount=?;");
        stmt.setObject(1,newValue);
        stmt.executeUpdate();
    }
}

