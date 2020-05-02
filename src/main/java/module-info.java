module com.aridaje {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
 //   requires net.bytebuddy;
    requires java.xml;
  //  requires java.xml.bind;

    opens com.task2_3.client to javafx.fxml;
    exports com.task2_3.client;
}