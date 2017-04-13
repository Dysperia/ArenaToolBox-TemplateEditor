package com.dysperia.templateeditor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MainWindow extends BorderPane {
	private final static String defaultText = "First open the TEMPLATE.DAT directory.\n\n"
    		+ "Both the files POINTER1.DAT and TEMPLATE.DAT \n"
    		+ "must be present in the same directory.";
	
	private final ObservableList<String> titles = FXCollections.observableArrayList();
	private final ListView<String> listView;
	private List<String> templateTexts;
	private final TextArea textArea;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	private final FileChooser fileChooser;
	private final Stage stage;
	private DataManager dataManager;
	
	public MainWindow(Stage stage) {
		fileChooser = new FileChooser();
		this.stage = stage;
		
		listView = new ListView<>(titles);
        listView.setPrefSize(150, 400);
        listView.setEditable(false);
        listView.setItems(titles);
        listView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				textArea.setText(templateTexts.get(newValue.intValue()));
			}
		});;
		setLeft(listView);
        
        textArea = new TextArea(defaultText);
        textArea.setPrefSize(800, 400);
        textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				templateTexts.set(listView.getSelectionModel().getSelectedIndex(), newValue.toString());
			}
		});;
        setCenter(textArea);

        Button openButton = new Button(null, new ImageView(new Image("icon/open.png",24,24,true,true)));
        openButton.setTooltip(new Tooltip("open"));
        openButton.setOnAction(e -> {
        	fileChooser.setTitle("Open TEMPLATE.DAT");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TEMPLATE.DAT", "TEMPLATE.DAT")
            );
            File templateFile = fileChooser.showOpenDialog(this.stage);
            if (templateFile != null && templateFile.exists()) {
            	File pointer1File = new File(templateFile.getParentFile(), "POINTER1.DAT");
            	if (pointer1File.exists()) {
            		buildDataFromFilesAndUpdateView(templateFile, pointer1File);
            	}
            	else {
            		showInfoAlert("File not found", "The file POINTER1.DAT was not found");
            	}
            }
        });
        
        Button saveButton = new Button(null, new ImageView(new Image("icon/save.png",24,24,true,true)));
        saveButton.setTooltip(new Tooltip("save"));
        saveButton.disableProperty().bind(modifiedProperty.not());
        
        Button showVariableButton = new Button(null, new ImageView(new Image("icon/view_variable.png",24,24,true,true)));
        showVariableButton.setTooltip(new Tooltip("show variables"));
        showVariableButton.setOnAction(e -> {
        	Stage newStage = new Stage();
        	newStage.setTitle("Variables");
        	newStage.setScene(new Scene(new VariableTable()));
        	newStage.show();
        });
        ToolBar toolbar = new ToolBar(openButton, saveButton, showVariableButton);
        setTop(toolbar);
	}
	
	public void resetContent() {
		titles.clear();
		textArea.setText(defaultText);
	}
	
	private void showInfoAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private void buildDataFromFilesAndUpdateView(File templateFile, File pointer1File) {
		dataManager = new DataManager(templateFile, pointer1File);
		try {
			dataManager.readDataFromFiles();
    		templateTexts = dataManager.getTemplateTexts();
    		Pattern charsFollowingTitle = Pattern.compile("#[^\\s]*");
    		for(int i=0; i<templateTexts.size(); i++) {
    			String entry = templateTexts.get(i);
        	    Matcher match = charsFollowingTitle.matcher(entry);
        	    if(!match.find()) {
        	    	System.out.println("[Main] Title not found in " + entry);
        	    }
        	    titles.add(entry.substring(match.start(), match.end()));
    		}
		} catch (IOException e) {
			showInfoAlert("Cannot read files", "The file POINTER1.DAT or TEMPLATE.DAT was not found or it was impossible to read the data from thme");
			e.printStackTrace();
		}
	}
}
