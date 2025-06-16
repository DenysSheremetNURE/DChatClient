module org.example.dchatclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens org.example.dchatclient to javafx.fxml;
    opens org.example.dchatclient.JSON to com.fasterxml.jackson.databind;
    opens org.example.dchatclient.UIClasses to com.fasterxml.jackson.databind;

    exports org.example.dchatclient.UIClasses to com.fasterxml.jackson.databind;
    exports org.example.dchatclient;
}