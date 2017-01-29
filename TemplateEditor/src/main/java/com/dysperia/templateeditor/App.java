package com.dysperia.templateeditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Template Editor");
        primaryStage.setScene(new Scene(new MainWindow()));
        primaryStage.show();
    }
}