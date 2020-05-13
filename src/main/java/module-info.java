/**
 *
 */
module com.Task_2_3_client {
    requires org.controlsfx.controls;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
 //   requires net.bytebuddy;
    requires java.xml;
    requires org.neo4j.driver;

    //  requires java.xml.bind
    opens com.task2_3.client to javafx.fxml;

    exports com.task2_3.client;
}