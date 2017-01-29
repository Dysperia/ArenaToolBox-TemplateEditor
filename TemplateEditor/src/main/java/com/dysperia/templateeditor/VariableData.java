package com.dysperia.templateeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class VariableData {
	public static class Variable {
		private final SimpleStringProperty name;
		public String getName() { return name.getValue(); }
		private final SimpleStringProperty meaning;
		public String getMeaning() { return meaning.getValue(); }
		public Variable(String name, String meaning) {
			this.name = new SimpleStringProperty(name);
			this.meaning = new SimpleStringProperty(meaning);
		}
	}
	
	public final static List<String> variableName = Arrays.asList("%a", "%adn", "%amn", "%an", "%apr", "%arc", "%art",
            "%ba",
            "%ccs", "%cll", "%cn", "%cn2", "%cp", "%ct",
            "%da", "%de", "%di", "%dit", "%doc", "%du", "%ds",
            "%en",
            "%fn", "%fq",
            "%g", "%g2", "%g3",
            "%hc", "%hod",
            "%i",
            "%jok",
            "%lp",
            "%mi", "%mm", "%mn", "%mpr", "%mt",
            "%n", "%nap", "%nc", "%ne", "%nh", "%nhd", "%nr", "%nt",
            "%o", "%oap", "%oc", "%omq", "%opp", "%oth",
            "%pcf", "%pcn",
            "%qc", "%qmn", "%qt",
            "%r", "%ra", "%rcn", "%rf",
            "%s", "%sn", "%st",
            "%t", "%ta", "%tan", "%tc", "%tem", "%tg", "%ti", "%tl", "%tq", "%tt",
            "%u");
	public final static List<String> variableMeaning = Arrays.asList("Amount (gold)", "Artifact Dongeon Name", "Artifact Map area Name", "Artifact information giver Name", "Artifact PRovince", "ARtifact Class", "ARTifact name",
               "Bonus Amount (gold)",
               "City of Chaos Segment", "Chaos segment Lore Location", "actual City Name", "City Name in status with actual city", "City Province", "City Type",
               "Date", "Days Exhausted", "DIrection", "Days In Tavern", "Description Of Class", "DUration in days", "Designation of Service buyer",
               "Equipment store Name",
               "FullName", "Fullname of Quest giver",
               "Gender (he, she)", "Gender (him, her)", "Gender (his, her)",
               "Home City", "HOliday Description",
               "Item name",
               "JOKe",
               "Local (current) Province",
               "Mission Item", "item price (gold)", "Monster Name", "Map PRovince", "Monster Type",
               "Name", "Negociated Artifact Price", "Name of Criminal", "Name of Escorted", "Name of Holiday", "Named Holiday Date", "Name of Relative", "Name of Tavern",
               "Opponent", "Original Artifact Price", "name Of Class", "Object of Move Quest", "OPponent's Player killer", "one of the game interjections",
               "Player Character Fullname", "Player Character Name",
               "Quest City", "Quest Monster Name", "Quest Task",
               "Relative", "RAce", "maybe a wrong city for chaos segment quest", "Ruler Fullname",
               "Stuff piece name", "Snake Name", "STatus between two cities",
               "Title (sometime Time in days)", "Task Amount (gold)", "Task Area Name", "Task City", "TEMple", "Task Group", "TIme left in days", "Task Location", "Title of Quest giver", "Transgressor Title",
               "Units number");
	
	private static List<Variable> buildVariableData() {
		List<Variable> list = new ArrayList<>();
		for (int i=0; i<variableName.size(); i++) {
			list.add(new Variable(variableName.get(i), variableMeaning.get(i)));
		}
		return list;
	}
	
	public final static List<Variable> variables = buildVariableData();
}
