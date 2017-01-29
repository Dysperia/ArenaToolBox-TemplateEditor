package com.dysperia.templateeditor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;import javafx.stage.Stage;


public class MainWindow extends BorderPane {
	private final static String defaultText = "First open the TEMPLATE.DAT directory.\n\n"
    		+ "Both the files POINTER1.DAT and TEMPLATE.DAT \n"
    		+ "must be present in the same directory.";
	
	private final ObservableList<String> titles = FXCollections.observableArrayList();
	private final ListView<String> listView;
	private final TextArea textArea;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	
	public MainWindow() {
		listView = new ListView<>(titles);
        listView.setPrefSize(150, 400);
        listView.setEditable(false);
        listView.setItems(titles);
        setLeft(listView);
        
        textArea = new TextArea(defaultText);
        textArea.setPrefSize(400, 400);
        setCenter(textArea);

        Button openButton = new Button(null, new ImageView(new Image("icon/open.png",24,24,true,true)));
        Button saveButton = new Button(null, new ImageView(new Image("icon/save.png",24,24,true,true)));
        saveButton.disableProperty().bind(modifiedProperty.not());
        Button showVariableButton = new Button(null, new ImageView(new Image("icon/view_variable.png",24,24,true,true)));
        showVariableButton.setOnAction(e -> {
        	Stage stage = new Stage();
    		stage.setTitle("Variables");
    		stage.setScene(new Scene(new VariableTable()));
    		stage.show();
        });
        ToolBar toolbar = new ToolBar(openButton, saveButton, showVariableButton);
        setTop(toolbar);
	}
	
	public void resetContent() {
		titles.clear();
		textArea.setText(defaultText);
	}
}
