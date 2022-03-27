package com.example.homeBudget;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.controlsfx.control.ToggleSwitch;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sqlite.SQLiteException;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;


public class ControllerHomeBudget implements Initializable {
    @FXML
    private TableView<Purchases> purchasesTableView;
    @FXML
    private TableView<Incomes> incomesTableView;
    @FXML
    private ComboBox shopComboBox;
    @FXML
    private ComboBox shopComboBoxRemove;
    @FXML
    private ComboBox incomeCategoryComboBoxRemove;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private DatePicker incomeDateDatePicker;
    @FXML
    private TextField shopTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField amountTextField;
    @FXML
    private TextField addIncomeCategoryTextField;
    @FXML
    private TextField budgetTextField;
    @FXML
    private Label budgetAmount = new Label();
    @FXML
    private VBox menuVBox;
    @FXML
    private VBox addVBox;
    @FXML
    private VBox rightVBox;
    @FXML
    private VBox purchaseMenu;
    @FXML
    private VBox incomeMenu;
    @FXML
    private VBox addIncomeVBox;
    @FXML
    private VBox incomesTableViewVBox;
    @FXML
    private VBox purchasesTableViewVBox;
    @FXML
    private Button addButton;
    @FXML
    private Button addIncomeButton;
    @FXML
    private ComboBox categoryComboBox;
    @FXML
    private ComboBox categoryComboBoxRemove;
    @FXML
    private ComboBox incomeCategoryComboBox;
    @FXML
    private VBox settingsVbox;
    @FXML
    private TextField addCategoryTextField;
    @FXML
    private ScrollPane settingsScrollPane;
    @FXML
    private TableColumn shopPurchaseColumn;
    @FXML
    private TableColumn categoryPurchaseColumn;
    @FXML
    private TableColumn pricePurchaseColumn;
    @FXML
    private TableColumn categoryIncomeColumn;
    @FXML
    private TableColumn amountIncomeColumn;
    @FXML
    private TableColumn dateIncomeColumn;
    @FXML
    private TableColumn datePurchaseColumn;
    private VBox vBoxStatsAll = new VBox();
    private ScrollPane scrollPaneStats = new ScrollPane();
    private ObservableList<String> shopList = FXCollections.observableArrayList();
    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> shopListTableView = FXCollections.observableArrayList();
    private ObservableList<String> categoryListTableView = FXCollections.observableArrayList();
    private ObservableList<String> incomeCategoryList = FXCollections.observableArrayList();
    private ObservableList<String> incomeCategoryListTableView = FXCollections.observableArrayList();
    private ObservableList<Purchases> purchasesObservableList = FXCollections.observableArrayList();
    private ObservableList<Incomes> IncomesObservableList = FXCollections.observableArrayList();

    Database database = new Database();

    public ControllerHomeBudget() throws IOException, SQLException, ClassNotFoundException {
        updatePurchaseTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        purchasesTableView.setItems(purchasesObservableList);
        purchasesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        incomesTableView.setItems(IncomesObservableList);
        incomesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        purchasesTableView.setEditable(true);
        incomesTableView.setEditable(true);
        try {
            shopPurchaseColumn.setCellFactory(ComboBoxTableCell.forTableColumn(updateShopList(true)));
            categoryPurchaseColumn.setCellFactory(ComboBoxTableCell.forTableColumn(updatePurchaseCategoryList(true)));
            categoryIncomeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(updateIncomeCategorylist(true)));

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        pricePurchaseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountIncomeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        datePurchaseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dateIncomeColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        updateBudget();

        shopPurchaseColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Purchases purchase = (Purchases) cellEditEvent.getRowValue();
                try {
                    editPurchase(purchase.getId(), cellEditEvent.getNewValue().toString(), "shop_id");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        categoryPurchaseColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Purchases purchase = (Purchases) cellEditEvent.getRowValue();
                try {
                    editPurchase(purchase.getId(), cellEditEvent.getNewValue().toString(), "category_id");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        pricePurchaseColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Purchases purchase = (Purchases) cellEditEvent.getRowValue();
                String price = cellEditEvent.getNewValue().toString().replace(",",".");
                if (isNumeric(price)){ //check if price is a number
                    try {
                        editPurchase(purchase.getId(), price,"price");
                        updatePurchaseTable();
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data error");
                    alert.setContentText("Field \"Price\" must contain number!");
                    alert.showAndWait();
                }
            }
        });

        categoryIncomeColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Incomes income = (Incomes) cellEditEvent.getRowValue();
                try {
                    editIncome(income.getId(), cellEditEvent.getNewValue().toString(), "category_id");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        amountIncomeColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Incomes income = (Incomes) cellEditEvent.getRowValue();
                String amount = cellEditEvent.getNewValue().toString().replace(",",".");
                if (isNumeric(amount)){ //check if price is a number
                    try {
                        editIncome(income.getId(), amount,"amount");
                        updateIncomesTable();
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data error");
                    alert.setContentText("Field \"Amount\" must contain number!");
                    alert.showAndWait();
                }
            }
        });

        datePurchaseColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Purchases purchase = (Purchases) cellEditEvent.getRowValue();

                if(isDate(cellEditEvent.getNewValue().toString())){
                    try {
                        editPurchase(purchase.getId(),cellEditEvent.getNewValue().toString(), "date" );
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dateIncomeColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                Incomes income = (Incomes) cellEditEvent.getRowValue();

                if(isDate(cellEditEvent.getNewValue().toString())){
                    try {
                        editIncome(income.getId(),cellEditEvent.getNewValue().toString(), "date" );
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void showPurchaseMenu() throws SQLException, IOException {
        rightVBox.getChildren().remove(scrollPaneStats);
        menuVBox.setVisible(false);
        menuVBox.setManaged(false);
        addVBox.setManaged(false);
        addVBox.setVisible(false);
        purchaseMenu.setVisible(true);
        purchaseMenu.setManaged(true);
        purchasesTableViewVBox.setVisible(true);
        purchasesTableViewVBox.setManaged(true);
        incomesTableViewVBox.setManaged(false);
        incomesTableViewVBox.setVisible(false);
        updatePurchaseTable();
    }

    @FXML
    protected void addPurchase() throws IOException, SQLException {
        //setting GUI

        purchaseMenu.setVisible(false);
        purchaseMenu.setManaged(false);
        addVBox.setVisible(true);
        addVBox.setManaged(true);
        purchasesTableView.setVisible(true);
        purchasesTableView.setManaged(true);
        addButton.setText("Add");

        try{
            categoryList = updatePurchaseCategoryList(false);
            categoryComboBox.setItems(categoryList);
            categoryComboBox.setValue(categoryList.get(0));
        }
        catch (RuntimeException ignore){
        }

        shopList = updateShopList(false);
        dateDatePicker.setValue(LocalDate.now());
        shopComboBox.setItems(shopList);
        shopComboBox.setValue(shopList.get(0));

    }

    protected void editPurchase(int id, String newValue, String column) throws IOException, SQLException {
        JSONObject itemToEditJson = new JSONObject();
        itemToEditJson.put("id",id);
        itemToEditJson.put("column",column);
        itemToEditJson.put("newValue",newValue);
        database.editPurchase(itemToEditJson.toString());
    }

    @FXML
    protected void removePurchase() throws IOException{

        rightVBox.getChildren().remove(scrollPaneStats); //remove stats view
        Purchases purchase = purchasesTableView.getSelectionModel().getSelectedItem(); //get item from tableView
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Remove purchase");
        //check if item from table view is not null
        try {
            alert.setContentText("Are you sure you want to delete this purchase?\n" +
                    purchase.getId() + "\t" + purchase.getDate() + "\t" + purchase.getShop() + "\t" + purchase.getCategory() +  "\t " +
                    purchase.getPrice());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                JSONObject removePurchaseJson = new JSONObject();
                removePurchaseJson.put("action","removePurchase");
                removePurchaseJson.put("id",purchase.getId());
                database.removePurchase(removePurchaseJson.toString());
                updatePurchaseTable();
            }
        }catch (NullPointerException | SQLException e) {
            if (!purchasesTableView.isVisible()){
                purchasesTableView.setVisible(true);
                purchasesTableView.setManaged(true);
            }
            else{
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Remove error");
                alert.setContentText("You have to select purchase to remove it!");
                alert.showAndWait();
            }

        }
        purchasesTableView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void showIncomeMenu() throws SQLException, IOException {
        menuVBox.setVisible(false);
        menuVBox.setManaged(false);
        incomeMenu.setManaged(true);
        incomeMenu.setVisible(true);
        addIncomeVBox.setManaged(false);
        addIncomeVBox.setVisible(false);
        rightVBox.getChildren().remove(scrollPaneStats);
        updateIncomesTable();
    }

    @FXML
    protected void addIncome() {

        incomeMenu.setVisible(false);
        incomeMenu.setManaged(false);
        addIncomeVBox.setManaged(true);
        addIncomeVBox.setVisible(true);
        addIncomeButton.setText("Add");

        incomeCategoryList = incomeCategoryList.sorted();
        incomeCategoryComboBox.setItems(incomeCategoryList);
        incomeCategoryComboBox.setValue(incomeCategoryList.get(0));
        incomeDateDatePicker.setValue(LocalDate.now());

    }

    protected void editIncome(int id, String newValue, String column) throws SQLException, IOException {
        JSONObject itemToEditJson = new JSONObject();
        itemToEditJson.put("id",id);
        itemToEditJson.put("column",column);
        itemToEditJson.put("newValue",newValue);
        database.editIncome(itemToEditJson.toString());
    }

    @FXML
    protected void addIncomeToDatabase() throws SQLException, IOException {
        JSONObject data = new JSONObject();

        data.put("date",incomeDateDatePicker.getValue().toString());
        data.put("category",incomeCategoryComboBox.getValue());
        String amount = amountTextField.getText();
        amount = amount.replace(",",".");

        //check if values are properly set and send to database
        if(incomeDateDatePicker.getValue() == null
                || incomeCategoryComboBox.getValue() == null || amountTextField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data error");
            alert.setContentText("No field can be empty!");
            alert.showAndWait();
        }
        else{
            if (isNumeric(amount)){ //check if amount is a number
                data.put("amount",amount);
                database.addIncome(data.toString());

                incomeDateDatePicker.setValue(LocalDate.now());
                incomeCategoryComboBox.setValue(incomeCategoryList.get(0));
                amountTextField.setText("");
                updatePurchaseTable();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Data error");
                alert.setContentText("Field \"Amount\" must contain number!");
                alert.showAndWait();
            }
        }
        updateIncomesTable();
    }

    @FXML
    protected void removeIncome() throws IOException{
        rightVBox.getChildren().remove(scrollPaneStats); //remove stats view
        Incomes income = incomesTableView.getSelectionModel().getSelectedItem(); //get item from tableView
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Removing income");
        //check if item from table view is not null
        try {
            alert.setContentText("Are you sure that you want remove?\n" +
                    income.getId() + "\t" + income.getDate()  + "\t" + income.getCategory() +  "\t " +
                    income.getAmount());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                JSONObject removeIncomeJson = new JSONObject();
                removeIncomeJson.put("action","removeIncome");
                removeIncomeJson.put("id",income.getId());

                database.removeIncome(removeIncomeJson.toString());
                updateIncomesTable();
            }
        }catch (NullPointerException | SQLException e) {
            if (!incomesTableView.isVisible()){
                incomesTableView.setVisible(true);
                incomesTableView.setManaged(true);
            }
            else{
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Remove error");
                alert.setContentText("You have to select income to remove it!");
                alert.showAndWait();
            }

        }
        purchasesTableView.getSelectionModel().clearSelection();
    }

    @FXML
    public void showStats() throws SQLException {
        //setting GUI
        purchasesTableViewVBox.setManaged(false);
        purchasesTableViewVBox.setVisible(false);
        incomesTableViewVBox.setVisible(false);
        incomesTableViewVBox.setManaged(false);
        vBoxStatsAll.getChildren().clear();
        scrollPaneStats.setContent(null);
        scrollPaneStats.setVvalue(0);

        String[] months = {"January", "February", "March", "April",
                "May", "June", "July", "August", "September",
                "November", "October", "December"};
        JSONObject showStatsJson = new JSONObject(database.showStats());
        //creating array with months (int) then sort it
        JSONArray monthsJsonArray = showStatsJson.names();
        ArrayList<Integer> monthsArray = new ArrayList<>();
        for (int i = 0; i < monthsJsonArray.length(); i++){
            monthsArray.add(monthsJsonArray.getInt(i));
        }
        Collections.sort(monthsArray);

        //adding stats to GUI: rightVBox -> scrollPaneStats -> vBoxStatsAll -> VBoxStats -> monthLabel && (gridPaneStats -> && categoryLabel && priceLabel)
        JSONObject statsJson;
        Iterator<String> keys;
        for (int i = 0; i < monthsJsonArray.length(); i++) {
            statsJson = showStatsJson.getJSONObject(Integer.toString(monthsArray.get(i)));
            keys = statsJson.keys();
            Label monthLabel = new Label(months[monthsArray.get(i)-1]);
            monthLabel.setId("monthStatsLabel");
            VBox VBoxStats = new VBox();
            VBoxStats.getChildren().add(monthLabel);

            while (keys.hasNext()){
                String key = keys.next();
                String price = String.format("%.2f",statsJson.getFloat(key));
                GridPane gridPaneStats = new GridPane();
                Label priceLabel = new Label(price + "zł");
                priceLabel.setId("priceStatsLabel");
                Label categoryLabel = new Label(key);
                categoryLabel.setId("categoryStatsLabel");
                gridPaneStats.add(categoryLabel,0,0,1,1);
                gridPaneStats.add(priceLabel,1,0,1,1);
                gridPaneStats.setPadding(new Insets(0,0, 0, 20));
                VBoxStats.getChildren().add(gridPaneStats);
            }
            vBoxStatsAll.getChildren().add(VBoxStats);
            VBoxStats.setId("VBoxStats");
            vBoxStatsAll.setId("vBoxStatsAll");
        }
        rightVBox.getChildren().remove(scrollPaneStats);
        scrollPaneStats.setContent(vBoxStatsAll);
        rightVBox.getChildren().add(scrollPaneStats);

    }

    @FXML
    protected void backToMenu(ActionEvent event){
        Button sourceButton = (Button)event.getSource();

        if (sourceButton.getParent().equals(settingsVbox))
        {
            settingsScrollPane.setVisible(false);
            settingsScrollPane.setManaged(false);
        }
        else{
            sourceButton.getParent().setVisible(false);
            sourceButton.getParent().setManaged(false);
        }
        menuVBox.setVisible(true);
        menuVBox.setManaged(true);
    }

    @FXML
    private void showSettings() throws SQLException, IOException {
        menuVBox.setVisible(false);
        menuVBox.setManaged(false);
        settingsScrollPane.setVisible(true);
        settingsScrollPane.setManaged(true);
        addCategoryTextField.setText("");
        addCategoryTextField.setPromptText("New Category");
        addIncomeCategoryTextField.setText("");
        addIncomeCategoryTextField.setPromptText("New Category");

        budgetTextField.setText(database.showInitialBudget());

        categoryList = updatePurchaseCategoryList(false);
        categoryComboBoxRemove.setItems(categoryList);
        categoryComboBoxRemove.setValue(categoryList.get(0));

        shopList = updateShopList(false);
        shopComboBoxRemove.setItems(shopList);
        shopComboBoxRemove.setValue(shopList.get(0));

        incomeCategoryList = updateIncomeCategorylist(false);
        incomeCategoryComboBoxRemove.setItems(incomeCategoryList);
        incomeCategoryComboBoxRemove.setValue(incomeCategoryList.get(0));

    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    protected void addToDatabase() throws IOException, SQLException, ParseException { //adding or edit in database
        JSONObject data = new JSONObject();

        data.put("date",dateDatePicker.getValue().toString());
        if(!shopTextField.getText().equals("")){
            String shopCapital = shopTextField.getText().substring(0,1).toUpperCase() +
                    shopTextField.getText().substring(1).toLowerCase();
            data.put("shop",shopCapital);
        }
        else{
            data.put("shop",shopComboBox.getValue());
        }
        data.put("category",categoryComboBox.getValue());
        String price = priceTextField.getText();
        price = price.replace(",",".");

        //check if values are properly set and send to database
        if(dateDatePicker.getValue() == null || (shopComboBox.getValue() == null && shopTextField.getText().equals(""))
                || categoryComboBox.getValue() == null || priceTextField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data error");
            alert.setContentText("No field can be empty!");
            alert.showAndWait();
        }
        else{
            if (isNumeric(price)){ //check if price is a number
                data.put("price",price);
                database.addPurchase(data);


                dateDatePicker.setValue(LocalDate.now());
                categoryComboBox.setValue(categoryList.get(0));
                shopComboBox.setValue(shopList.get(0));
                shopTextField.setText("");
                priceTextField.setText("");
                updatePurchaseTable();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Data error");
                alert.setContentText("Field \"Price\" must contain number!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void addPurchaseCategory() throws IOException {
        try{
            String categoryCapital = addCategoryTextField.getText().substring(0,1).toUpperCase() +
                    addCategoryTextField.getText().substring(1).toLowerCase();
            database.addPurchaseCategoryToDatabase(categoryCapital);
            showSettings();
        }
        catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate");
            alert.setContentText("there is already a category \""+addCategoryTextField.getText()+"\"!");
            alert.showAndWait();
        }
    }

    @FXML
    private void removePurchaseCategory() throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Remove category");
        alert.setContentText("Are you sure that you want to remove \"" + categoryComboBoxRemove.getValue().toString() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                database.removePurchaseCategory(categoryComboBoxRemove.getValue().toString());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setContentText("properly deleted \"" + categoryComboBoxRemove.getValue().toString() + "\"");
                alert.showAndWait();
                showSettings();
            } catch (SQLException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Can't remove \"" + categoryComboBoxRemove.getValue().toString() + "\"!");
                e.printStackTrace();
                alert.showAndWait();
            }
            updatePurchaseTable();
        }
    }

    @FXML
    private void removeShop() throws SQLException, IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Remove Shop");
        alert.setContentText("Are you sure that you want to remove \"" + shopComboBoxRemove.getValue().toString() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                database.removeShop(shopComboBoxRemove.getValue().toString());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setContentText("properly deleted \"" + shopComboBoxRemove.getValue().toString() + "\"");
                alert.showAndWait();
                showSettings();
            } catch (SQLException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Can't remove \"" + shopComboBoxRemove.getValue().toString() + "\"!");
                e.printStackTrace();
                alert.showAndWait();
            }
            updatePurchaseTable();
        }
    }

    @FXML
    private void addIncomeCategory()throws IOException {
        try{
            String categoryCapital = addIncomeCategoryTextField.getText().substring(0,1).toUpperCase() +
                    addIncomeCategoryTextField.getText().substring(1).toLowerCase();
            database.addIncomeCategoryToDatabase(categoryCapital);
            showSettings();
        }
        catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate");
            alert.setContentText("there is already a category \""+addIncomeCategoryTextField.getText()+"\"!");
            alert.showAndWait();
        }
    }

    @FXML
    private void removeIncomeCategory() throws SQLException, IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText("Remove category");
        alert.setContentText("Are you sure that you want to remove \"" + incomeCategoryComboBoxRemove.getValue().toString() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                database.removeIncomeCategory(incomeCategoryComboBoxRemove.getValue().toString());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setContentText("properly deleted \"" + incomeCategoryComboBoxRemove.getValue().toString() + "\"");
                alert.showAndWait();
                showSettings();
            } catch (SQLException | IOException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Can't remove \"" + incomeCategoryComboBoxRemove.getValue().toString() + "\"!");
                e.printStackTrace();
                alert.showAndWait();
            }
            updateIncomesTable();
        }
    }

    @FXML
    private void editBudget() throws SQLException {
        if(isNumeric(budgetTextField.getText())){
            database.editInitialBudget(budgetTextField.getText());
            updateBudget();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data error");
            alert.setContentText("Field \"Initial budget\" must contain number!");
            alert.showAndWait();
        }
    }

    protected void updatePurchaseTable() throws IOException, SQLException {
        purchasesObservableList.clear();
        JSONObject allPurchaseJson;
        allPurchaseJson = new JSONObject(database.showPurchases());


        JSONObject purchaseJson;
        Iterator<String> keys = allPurchaseJson.keys();
        int maxID = 1;
        //find maxID
        while (keys.hasNext()){
            String idKey = keys.next();
            if(Integer.parseInt(idKey) > maxID){
                maxID = Integer.parseInt(idKey);
            }
        }
        //iterate through purchases by id
        for (int i = 1; i <= maxID; i++){
            //try catch is because there may not be a purchase with id because it has been deleted
            try{
                purchaseJson = allPurchaseJson.getJSONObject(Integer.toString(i));
                String date = purchaseJson.optString("date");
                String category = purchaseJson.optString("category");
                String shop = purchaseJson.optString("shop");
                String price = String.format("%.2f",purchaseJson.optFloat("price"));
                purchasesObservableList.add(new Purchases(i , date, shop, category, price + " zł"));
            }
            catch (JSONException ignored){
            }
        }
        updateBudget();
    }

    protected void updateIncomesTable() throws SQLException, IOException {
        incomesTableViewVBox.setVisible(true);
        incomesTableViewVBox.setManaged(true);
        purchasesTableViewVBox.setVisible(false);
        purchasesTableViewVBox.setManaged(false);
        IncomesObservableList.clear();
        JSONObject allPurchaseJson;

        allPurchaseJson = new JSONObject(database.showIncomes());

        JSONObject incomeJson;
        Iterator<String> keys = allPurchaseJson.keys();
        int maxID = 1;
        //find maxID
        while (keys.hasNext()){
            String idKey = keys.next();
            if(Integer.parseInt(idKey) > maxID){
                maxID = Integer.parseInt(idKey);
            }
        }
        //iterate through purchases by id
        for (int i = 1; i <= maxID; i++){
            //try catch is because there may not be a purchase with id because it has been deleted
            try{
                incomeJson = allPurchaseJson.getJSONObject(Integer.toString(i));
                String date = incomeJson.optString("date");
                String category = incomeJson.optString("category");
                String amount = String.format("%.2f",incomeJson.optFloat("amount"));
                IncomesObservableList.add(new Incomes(i , date, category, amount + " zł"));
            }
            catch (JSONException ignored){
            }
        }
        updateBudget();
    }

    private void updateBudget(){
        try {
            budgetAmount.setText(String.format("%.2f",Float.parseFloat(database.showBudget()))+" zł");

        } catch (SQLException ignore) {}
    }

    private ObservableList<String> updateShopList(boolean tableView) throws IOException, SQLException {
        shopList = FXCollections.observableArrayList();
        shopListTableView = FXCollections.observableArrayList();
        JSONObject shopJson;
        shopJson = new JSONObject(database.makeJSonShop());
        JSONArray key;
        shopJson = shopJson.getJSONArray("shop").getJSONObject(0);
        key = shopJson.names();
        for (int i = 0; i < key.length(); ++i) {
            String keys = key.getString(i);
            String value = shopJson.getString(keys);
            shopListTableView.add(value);
            shopList.add(value);
        }
        if(tableView){
            shopListTableView = shopListTableView.sorted();
            return shopListTableView;
        }
        shopList = shopList.sorted();
        return shopList;
    }

    private ObservableList<String> updatePurchaseCategoryList(boolean tableView) throws SQLException {
        categoryListTableView = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
        JSONObject categoryJson = new JSONObject(database.makeJSonCategory());
        JSONArray key;
        //receive categories
        categoryJson = categoryJson.getJSONArray("category").getJSONObject(0);
        key = categoryJson.names();
        for (int i = 0; i < key.length(); ++i) {
            String keys = key.getString(i);
            String value = categoryJson.getString(keys);
            categoryList.add(value);
            categoryListTableView.add(value);
        }
        if(tableView){
            categoryListTableView = categoryListTableView.sorted();
            return categoryListTableView;
        }
        categoryList = categoryList.sorted();
        return categoryList;
    }

    private ObservableList<String> updateIncomeCategorylist(boolean tableView) throws SQLException{
        incomeCategoryListTableView = FXCollections.observableArrayList();
        incomeCategoryList = FXCollections.observableArrayList();
        JSONObject categoryJson = new JSONObject(database.makeJSonIncomeCategory());
        JSONArray key;
        //receive income categories
        try {
            categoryJson = categoryJson.getJSONArray("category").getJSONObject(0);
            key = categoryJson.names();
            for (int i = 0; i < key.length(); ++i) {
                String keys = key.getString(i);
                String value = categoryJson.getString(keys);
                incomeCategoryList.add(value);
                incomeCategoryListTableView.add(value);
            }
        } catch (RuntimeException ignore){}
        if(tableView){
            incomeCategoryListTableView = incomeCategoryListTableView.sorted();
            return incomeCategoryListTableView;
        }
        incomeCategoryList = incomeCategoryList.sorted();
        return incomeCategoryList;
    }

    private boolean isDate(String date){
        return date.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d");
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}


//TODO add started budget
//TODO change sorting category to "usage sorting"
//TODO improve removing from table
//TODO add clearing table
//TODO add changing table view (income, purchase)
//TODO change view of combobox in tableView
//TODO improve removing shop & category (show which rows will be deleted)
//TODO price, amount > 0
//TODO only letters in shop and category
//TODO can't remove category if there is only one