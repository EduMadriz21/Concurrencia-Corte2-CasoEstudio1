module org.example.reservaconcurrenteentradas {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.reservaconcurrenteentradas to javafx.fxml;
    exports org.example.reservaconcurrenteentradas;
}