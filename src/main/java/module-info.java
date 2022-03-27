module com.example.homeBudget {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    opens com.example.homeBudget to javafx.fxml;
    exports com.example.homeBudget;


}