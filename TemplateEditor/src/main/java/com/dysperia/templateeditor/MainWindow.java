package com.dysperia.templateeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

/**
 * Class of the main window
 * @author dysperia
 */
public class MainWindow extends BorderPane {
	/** Default TextArea text */
	private final static String defaultText = "First open the TEMPLATE.DAT directory.\n\n"
    		+ "Both the files POINTER1.DAT and TEMPLATE.DAT \n"
    		+ "must be present in the same directory.";
	/** List of the titles */
	private final ObservableList<String> titles;
	/** ListView used to select a text from its titles */
	private final ListView<String> listView;
	/** List of TEMPLATE.DAT texts */
	private List<String> templateTexts;
	/** TextArea used to display the chosen TEMPLATE.DAT text */
	private final TextArea textArea;
	/** FileChooser used to open a TEMPLATE.DAT file */
	private final FileChooser templateFinder;
	/** Stage of this window */
	private final Stage stage;
	/** Data manager used to load and save data from TEMPLATE.DAT and POINTER1.DAT */
	private DataManager dataManager;
//	/** About text */
//	private final String aboutText = ""
//			+ "Template editor, part of the ArenaToolBox softwares\n"
//			+ "Author: Dysperia (softwatermermaid@hotmail.fr)\n\n"
//			+ "This software is used to edit the text contained in the\n"
//			+ "TEMPLATE.DAT without character number limits, by updating\n"
//			+ "the offsets found in the POINTER1.DAT file.\n";
	
	/**
	 * Constructor
	 * @param stage Stage of the main window
	 */
	public MainWindow(Stage stage) {
		templateFinder = new FileChooser();
		templateFinder.setTitle("Open TEMPLATE.DAT");
        templateFinder.setInitialDirectory(new File(System.getProperty("user.home")));
        templateFinder.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("TEMPLATE.DAT", "TEMPLATE.DAT")
        );
        
		this.stage = stage;
		
		textArea = new TextArea(defaultText);
		
		titles = FXCollections.observableArrayList();
		listView = new ListView<>(titles);
        
		this.setupListViewAndTextArea();
        this.setupButtonsAndToolbar();
	}
	
	/**
	 * Init and setup the ListView and the TextArea
	 */
	private void setupListViewAndTextArea() {
        listView.setPrefSize(150, 400);
        listView.setEditable(false);
        listView.setItems(titles);
        listView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() != -1) {
					textArea.setText(templateTexts.get(newValue.intValue()));
				}
			}
		});
		setLeft(listView);
		
        textArea.setPrefSize(650, 400);
        textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int idx = listView.getSelectionModel().getSelectedIndex();
				if (idx != -1) {
					templateTexts.set(listView.getSelectionModel().getSelectedIndex(), newValue.toString());
				}
			}
		});
        setCenter(textArea);
	}
	
	/**
	 * Init and setup the buttons and the toolbar
	 */
	private void setupButtonsAndToolbar() {
		Button openButton = new Button(null, new ImageView(new Image("icon/open.png",24,24,true,true)));
        openButton.setTooltip(new Tooltip("open"));
        openButton.setOnAction(e -> {
            File templateFile = templateFinder.showOpenDialog(this.stage);
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
        saveButton.setOnAction(e -> {
        	if (dataManager != null) {
            	dataManager.setTemplateTexts(this.templateTexts);
            	if (!dataManager.saveEditedTexts()) {
					showInfoAlert("Cannot write files", "It was impossible to write the data to POINTER1.DAT or TEMPLATE.DAT");
				}
        	}
        	else {
        		this.showInfoAlert("Save", "No texts have been loaded");
        	}
        });
        
        Button showVariableButton = new Button(null, new ImageView(new Image("icon/view_variable.png",24,24,true,true)));
        showVariableButton.setTooltip(new Tooltip("show variables"));
        showVariableButton.setOnAction(e -> {
        	Stage newStage = new Stage();
        	newStage.setTitle("Variables");
        	newStage.setScene(new Scene(new VariableTable()));
        	newStage.show();
        });
        
        /*****************************************************************************************/
        Button testButton = new Button("Test the software");
        testButton.setOnAction(e -> {
    		try {
	            File templateFile = templateFinder.showOpenDialog(this.stage);
	            File templateSave = new File(templateFile.getParentFile(), "TEMPLATE.TEST");
	            templateSave.createNewFile();
	            File pointer1File = new File(templateFile.getParentFile(), "POINTER1.DAT");
	            File pointer1Save = new File(pointer1File.getParentFile(), "POINTER1.TEST");
	            // Build test data from file
	        	DataManager dm = new DataManager(templateFile, pointer1File);
	        	boolean result = dm.readDataFromFiles();
				// Load most of test data into text area but not all
				TextArea ta = new TextArea();
	    		List<String> texts = dm.getTemplateTexts();
	    		for (int i=20; i<texts.size(); i++) {
	    			ta.setText(texts.get(i));
	    			texts.set(i, ta.getText());
	    		}
	    		dm.setTemplateTexts(texts);
	    		// Save test data
	    		result = result && dm.saveEditedTexts(templateSave, pointer1Save);
	    		// Test the data : must be the same
	    		int templateLength = (int)templateFile.length();
	    		int pointerLength = (int)pointer1File.length();
	    		result = result && templateLength == templateSave.length() && pointerLength == pointer1Save.length();
	    		DataInputStream tOld = new DataInputStream(new FileInputStream(templateFile));
	    		DataInputStream pOld = new DataInputStream(new FileInputStream(pointer1File));
	    		DataInputStream tNew = new DataInputStream(new FileInputStream(templateSave));
	    		DataInputStream pNew = new DataInputStream(new FileInputStream(pointer1Save));
	    		byte[] oldArray = new byte[templateLength];
	    		byte[] newArray = new byte[templateLength];
	    		tOld.readFully(oldArray, 0, templateLength);
	    		tNew.readFully(newArray, 0, templateLength);
	    		tOld.close();
	    		tNew.close();
	    		result = result && Arrays.equals(oldArray, newArray);
	    		oldArray = new byte[pointerLength];
	    		newArray = new byte[pointerLength];
	    		pOld.readFully(oldArray, 0, pointerLength);
	    		pNew.readFully(newArray, 0, pointerLength);
	    		pOld.close();
	    		pNew.close();
	    		result = result && Arrays.equals(oldArray, newArray);
	    		this.showInfoAlert("Test", "The test "+(result ? "succeded" : "failed (the data loaded and saved are not the same or it was impossible to load or save)"));
    		} catch (IOException except) {
    			showInfoAlert("File error", "It was impossible to read some data");
    			except.printStackTrace();
    		}
        });
        /*****************************************************************************************/
        
        ToolBar toolbar = new ToolBar(openButton, saveButton, showVariableButton, testButton);
        setTop(toolbar);
	}
	
	/**
	 * Show an information alert with the given title and message
	 * @param title Title of the alert
	 * @param message Message of the alert
	 */
	private void showInfoAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	/**
	 * Build the data and update the view
	 * @param templateFile TEMPLATE.DAT File
	 * @param pointer1File POINTER1.DAT File
	 */
	private void buildDataFromFilesAndUpdateView(File templateFile, File pointer1File) {
		dataManager = new DataManager(templateFile, pointer1File);
		if (dataManager.readDataFromFiles())
		{
    		templateTexts = dataManager.getTemplateTexts();
    		titles.addAll(dataManager.getTitles());
    		listView.getSelectionModel().clearSelection();
    		listView.getSelectionModel().selectFirst();
		}
		else {
			showInfoAlert("Cannot read files", "The file POINTER1.DAT or TEMPLATE.DAT was not found or it was impossible to read the data from them");
		}
	}
}
