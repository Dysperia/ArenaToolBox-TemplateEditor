package com.dysperia.templateeditor;

import com.dysperia.templateeditor.VariableData.Variable;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class VariableTable extends TableView<Variable> {
	public VariableTable() {
		setEditable(false);
		TableColumn<Variable, String> name = new TableColumn<>("Name");
		name.setPrefWidth(100);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Variable, String> meaning = new TableColumn<>("Meaning");
		meaning.setPrefWidth(400);
		meaning.setCellValueFactory(new PropertyValueFactory<>("meaning"));
		getColumns().addAll(name, meaning);
		setItems(FXCollections.observableArrayList(VariableData.variables));
	}
}
