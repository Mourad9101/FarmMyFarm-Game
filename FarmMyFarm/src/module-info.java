module FarmMyFarm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens models to javafx.base;
    opens main to javafx.fxml;
    opens controllers to javafx.fxml;
    exports main;
    exports controllers;
}
