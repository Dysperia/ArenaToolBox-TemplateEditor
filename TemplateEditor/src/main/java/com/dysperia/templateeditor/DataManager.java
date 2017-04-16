package com.dysperia.templateeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class managing the TEMPLATE.DAT and POINTER1.DAT data
 * @author dysperia
 */
public class DataManager {
	/** TEMPLATE.DAT file */
	private File templateFile;
	/** POINTER1.DAT file */
	private File pointer1File;
	/** Offsets from POINTER1.DAT */
	private List<Integer> pointersValue;
	/** Texts from TEMPLATE.DAT */
	private List<String> templateTexts;
	/** Titles of the template.dat texts */
	private List<String> titles;

	/**
	 * Constructor
	 * @param templateFile The TEMPLATE.DAT file
	 * @param pointer1File The POINTER1.DAT file
	 */
	public DataManager(File templateFile, File pointer1File) {
		this.templateFile = templateFile;
		this.pointer1File = pointer1File;
		pointersValue = new ArrayList<>();
		templateTexts = new ArrayList<>();
		titles = new ArrayList<>();
	}
	
	/**
	 * Load the text data using POINTER1.DAT and TEMPLATE.DAT
	 * @throws IOException
	 */
	public void readDataFromFiles() throws IOException {
		this.readOffsets();
		this.readTemplateTexts();
		this.buildTitles();
	}
	
	private void readOffsets() throws IOException {
		try(DataInputStream pointer1IS = new DataInputStream(new FileInputStream(pointer1File))) {
			pointer1IS.skipBytes(0x2710);
			int offset = changeEndianness(pointer1IS.readInt());
			while(offset != 0) {
				pointersValue.add(offset);
				offset = changeEndianness(pointer1IS.readInt());
			}
			// Adding end of file offset to compute the last template entry length
			pointersValue.add((int)templateFile.length());
		}
	}
	
	private void readTemplateTexts() throws IOException {
		try(DataInputStream templateIS = new DataInputStream(new FileInputStream(templateFile))) {
			for(int i=0; i<pointersValue.size()-1; i++) {
				int sizeToRead = pointersValue.get(i+1) - pointersValue.get(i);
				byte[] bytesArray = new byte[sizeToRead];
				int byteNumberRead = templateIS.read(bytesArray, 0, sizeToRead);
				if (byteNumberRead != sizeToRead) {
					System.err.println("[DataManager] Number of bytes read: "+byteNumberRead+", expected: "+sizeToRead);
				}
				String text = new String(bytesArray, "Cp1252");
				templateTexts.add(text);
			}
		}
	}
	
	/**
	 * Change the endianness of a 32 bits int
	 * @param val Int to convert
	 * @return The converted int
	 */
	private int changeEndianness(int val)
	{
		int i1 = (val >>  0) & 0xff;
		int i2 = (val >>  8) & 0xff;
		int i3 = (val >> 16) & 0xff;
		int i4 = (val >> 24) & 0xff;
		return i1 << 24 | i2 << 16 | i3 << 8 | i4 << 0;
	}
	
	/**
	 * Built the titles from the TEMPLATE.DAT texts
	 */
	private void buildTitles() {
		Pattern charsFollowingTitle = Pattern.compile("#[^\\s]*");
		for(int i=0; i<templateTexts.size(); i++) {
			String entry = templateTexts.get(i);
    	    Matcher match = charsFollowingTitle.matcher(entry);
    	    if(!match.find()) {
    	    	System.err.println("[DataManager] Title not found in " + entry);
    	    }
    	    titles.add(entry.substring(match.start(), match.end()));
		}
	}
	
	/**
	 * Return the list of texts. The list is empty until a call to readDataFromFiles() is done
	 * @return The list of the texts contained in TEMPLATE.DAT
	 */
	public List<String> getTemplateTexts() {
		List<String> copy = new ArrayList<>(this.templateTexts);
		Collections.copy(copy, this.templateTexts);
		return copy;
	}
	
	/**
	 * Set the new texts to the given list
	 * @param texts new TEMPLATE.DAT texts
	 */
	public void setTemplateTexts(List<String> texts) {
		this.templateTexts = texts;
		this.buildTitles();
	}
	
	/**
	 * Return the list of the texts titles. The list is empty until a call to readDataFromFiles() is done
	 * @return The list of the titles of the texts contained in TEMPLATE.DAT
	 */
	public List<String> getTitles() {
		List<String> copy = new ArrayList<>(this.titles);
		Collections.copy(copy, this.titles);
		return copy;
	}
	
	private void convertNewLineCharacters() {
		// TODO convert
	}
	
	/**
	 * Save the texts to the given files
	 * @param templateFile TEMPLATE.DAT File
	 * @param pointerFile POINTER1.DAT File
	 */
	public void saveEditedTexts(File templateFile, File pointerFile) {
		this.convertNewLineCharacters();
		// TODO save
	}
	
	/**
	 * Save the texts to the files given at this object creation
	 */
	public void saveEditedTexts() {
		this.saveEditedTexts(this.templateFile, this.pointer1File);
	}
}
