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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
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
    @FXML
    private TableColumn selectPurchaseColumn;
    @FXML
    private TableColumn selectIncomeColumn;
    private CheckBox selectAllPurchaseCheckbox;
    private CheckBox selectAllIncomeCheckbox;
    private VBox vBoxStatsAll = new VBox();
    private ScrollPane scrollPaneStats = new ScrollPane();
    private ObservableList<String> shopList = FXCollections.observableArrayList();
    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> shopListTableView = FXCollections.observableArrayList();
    private ObservableList<String> categoryListTableView = FXCollections.observableArrayList();
    private ObservableList<String> incomeCategoryList = FXCollections.observableArrayList();
    private ObservableList<String> incomeCategoryListTableView = FXCollections.observableArrayList();
    private ObservableList<Purchases> purchasesObservableList = FXCollections.observableArrayList();
    private ObservableList<Incomes> incomesObservableList = FXCollections.observableArrayList();


    Database database = new Database();

    public ControllerHomeBudget() throws SQLException, ClassNotFoundException {
        selectAllPurchaseCheckbox = new CheckBox();
        selectAllIncomeCheckbox = new CheckBox();
        try {
            updatePurchaseTable();
        }catch (Exception e){
            System.out.println("jest blad " + e);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectPurchaseColumn.setGraphic(selectAllPurchaseCheckbox);
        selectIncomeColumn.setGraphic(selectAllIncomeCheckbox);
        purchasesTableView.setItems(purchasesObservableList);
        purchasesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        incomesTableView.setItems(incomesObservableList);
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

        selectAllPurchaseCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                for (Purchases purchase:purchasesObservableList) {
                    purchase.getSelect().setSelected(newValue);
                }
            }
        });

        selectAllIncomeCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                for (Incomes income: incomesObservableList) {
                    income.getSelect().setSelected(newValue);
                }
            }
        });

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
        try {
            shopComboBox.setValue(shopList.get(0));

        }catch (IndexOutOfBoundsException ignore){
        }

    }

    protected void editPurchase(int id, String newValue, String column) throws IOException, SQLException {
        JSONObject itemToEditJson = new JSONObject();
        itemToEditJson.put("id",id);
        itemToEditJson.put("column",column);
        itemToEditJson.put("newValue",newValue);
        database.editPurchase(itemToEditJson.toString());
    }

    @FXML
    protected void removePurchase() throws IOException, SQLException {
        rightVBox.getChildren().remove(scrollPaneStats); //remove stats view
        boolean removeAllPurchases = true;
        JSONObject removePurchaseJson = new JSONObject();
        JSONArray idsToRemove = new JSONArray();
        StringBuilder alertString = new StringBuilder();
        int i = 0;
        for (Purchases purchase: purchasesObservableList) {
            if(purchase.getSelect().isSelected()){
                if(i < 5){
                    alertString.append(purchase).append("\n");
                }
                idsToRemove.put(purchase.getId());
                i++;
            }else{
                removeAllPurchases = false;
            }
        }
        if(i == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nothing to do");
            alert.setHeaderText("Information");
            alert.setContentText("You didn't select any purchase");
            alert.showAndWait();
        }
        else{
            if(i >= 5){
                alertString.append("...and ").append(i - 5).append(" more row(s)").append("\n");
            }
            removePurchaseJson.put("id",idsToRemove);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            if(removeAllPurchases){
                alert.setHeaderText("Remove all purchases");
                alert.setContentText("Are you sure you want to remove all purchases?");
                removePurchaseJson.put("removeAll",true);
            }else{
                alert.setHeaderText("Remove purchase(s)");
                alert.setContentText("Are you sure you want to remove these purchase(s)?\n" +
                        alertString);
                removePurchaseJson.put("removeAll",false);

            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                database.removePurchase(removePurchaseJson.toString());
                updatePurchaseTable();
            }
        }

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
            if (isNumeric(amount) && Float.parseFloat(amount) > 0){ //check if amount is a number
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
                alert.setContentText("Field \"Amount\" must contain number and be > 0!");
                alert.showAndWait();
            }
        }
        updateIncomesTable();
    }

    @FXML
    protected void removeIncome() throws IOException, SQLException {
        rightVBox.getChildren().remove(scrollPaneStats); //remove stats view

        boolean removeAllIncomes = true;
        JSONObject removeIncomeJson = new JSONObject();
        JSONArray idsToRemove = new JSONArray();
        StringBuilder alertString = new StringBuilder();
        int i = 0;
        for (Incomes income: incomesObservableList) {
            if(income.getSelect().isSelected()){
                if(i < 5){
                    alertString.append(income).append("\n");
                }
                idsToRemove.put(income.getId());
                i++;
            }else{
                removeAllIncomes = false;
            }
        }
        if(i == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nothing to do");
            alert.setHeaderText("Information");
            alert.setContentText("You didn't select any income");
            alert.showAndWait();
        }
        else {
            if (i >= 5) {
                alertString.append("...and ").append(i - 5).append(" more row(s)").append("\n");
            }
            removeIncomeJson.put("id", idsToRemove);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            if(removeAllIncomes){
                alert.setHeaderText("Remove all incomes");
                alert.setContentText("Are you sure you want to remove all incomes?");
                removeIncomeJson.put("removeAll",true);
            }
            else{
                alert.setHeaderText("Remove income(s)");
                alert.setContentText("Are you sure you want to remove these income(s)?\n" +
                        alertString);
                removeIncomeJson.put("removeAll",false);
            }

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                database.removeIncome(removeIncomeJson.toString());
                System.out.println(removeIncomeJson);
                updateIncomesTable();
            }
        }
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
        try{
            categoryComboBoxRemove.setValue(categoryList.get(0));

        }catch (IndexOutOfBoundsException ignore){}

        shopList = updateShopList(false);
        shopComboBoxRemove.setItems(shopList);
        try{
            shopComboBoxRemove.setValue(shopList.get(0));

        }catch (IndexOutOfBoundsException ignore){}

        incomeCategoryList = updateIncomeCategorylist(false);
        incomeCategoryComboBoxRemove.setItems(incomeCategoryList);
        try {
            incomeCategoryComboBoxRemove.setValue(incomeCategoryList.get(0));

        }catch (IndexOutOfBoundsException ignore){}

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
            if (isNumeric(price) && Float.parseFloat(price) > 0){ //check if price is a number and if is > 0
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
                alert.setContentText("Field \"Price\" must contain number and be > 0!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void addPurchaseCategory() throws IOException {
        if(isAlpha(addCategoryTextField.getText()))
        {
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
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("illegal characters");
            alert.setContentText("You can only use letters in Category!");
            alert.showAndWait();
        }
        addCategoryTextField.setText("");
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
        if(isAlpha(addIncomeCategoryTextField.getText())){
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
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("illegal characters");
            alert.setContentText("You can only use letters in Category!");
            alert.showAndWait();
        }
        addIncomeCategoryTextField.setText("");

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
                purchasesObservableList.add(new Purchases(i , date, shop, category, price + " zł", new CheckBox()));
            }
            catch (JSONException ignored){
            }
        }
        updateBudget();
        selectAllPurchaseCheckbox.setSelected(false);
    }

    protected void updateIncomesTable() throws SQLException, IOException {
        selectAllIncomeCheckbox.setSelected(false);
        incomesTableViewVBox.setVisible(true);
        incomesTableViewVBox.setManaged(true);
        purchasesTableViewVBox.setVisible(false);
        purchasesTableViewVBox.setManaged(false);
        incomesObservableList.clear();
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
            //try catch is because there may not be a income with id because it has been deleted
            try{
                incomeJson = allPurchaseJson.getJSONObject(Integer.toString(i));
                String date = incomeJson.optString("date");
                String category = incomeJson.optString("category");
                String amount = String.format("%.2f",incomeJson.optFloat("amount"));
                incomesObservableList.add(new Incomes(i , date, category, amount + " zł",new CheckBox()));
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
        try{
            shopJson = shopJson.getJSONArray("shop").getJSONObject(0);
            key = shopJson.names();
            for (int i = 0; i < key.length(); ++i) {
                String keys = key.getString(i);
                String value = shopJson.getString(keys);
                shopListTableView.add(value);
                shopList.add(value);
            }
        }catch (JSONException ignore){}

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
        try {
            categoryJson = categoryJson.getJSONArray("category").getJSONObject(0);
            key = categoryJson.names();
            for (int i = 0; i < key.length(); ++i) {
                String keys = key.getString(i);
                String value = categoryJson.getString(keys);
                categoryList.add(value);
                categoryListTableView.add(value);
            }
        }catch (JSONException ignore){
//            System.out.println(e);
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

    private boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
}


//TODO change sorting category to "usage sorting"
//TODO improve removing from table
//TODO add clearing table
//TODO change view of combobox in tableView
//TODO improve removing shop & category (show which rows will be deleted)
//TODO improve alert while removing