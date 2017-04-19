package com.dysperia.templateeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	 * @return True if everything went well, false otherwise
	 */
	public boolean readDataFromFiles() {
		try {
			this.readOffsets();
			this.readTemplateTexts();
			return this.buildTitles();
		} catch (IOException e) {
			System.err.println("[DataManager] Error while loading data from files");
			e.printStackTrace();
			return false;
		}
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
			pointersValue.add((int)templateFile.length()+1);
		}
	}
	
	private void readTemplateTexts() throws IOException {
		try(DataInputStream templateIS = new DataInputStream(new FileInputStream(templateFile))) {
			for(int i=0; i<pointersValue.size()-1; i++) {
				int sizeToRead = pointersValue.get(i+1) - pointersValue.get(i);
				byte[] bytesArray = new byte[sizeToRead];
				int byteNumberRead = templateIS.read(bytesArray, 0, sizeToRead);
				if (byteNumberRead != sizeToRead) {
					System.err.println("[DataManager] Number of bytes read: "+byteNumberRead+", expected: "+sizeToRead+" ("+i+")");
				}
				String text = new String(bytesArray, "ASCII");
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
	 * @return False in case of error, true otherwise
	 */
	private boolean buildTitles() {
		boolean result = true;
		Pattern charsFollowingTitle = Pattern.compile("#[^\\s]*");
		for(int i=0; i<templateTexts.size(); i++) {
			String entry = templateTexts.get(i);
    	    Matcher match = charsFollowingTitle.matcher(entry);
    	    if(!match.find()) {
    	    	System.err.println("[DataManager] Title not found in " + entry);
    	    	result = false;
    	    }
    	    else {
        	    titles.add(entry.substring(match.start(), match.end()));
    	    }
		}
		return result;
	}
	
	/**
	 * Return the list of texts. The list is empty until a call to readDataFromFiles() is done
	 * @return The list of the texts contained in TEMPLATE.DAT
	 */
	public List<String> getTemplateTexts() {
		List<String> copy = this.copy(templateTexts);
		this.convertLineBreakFromWindowsToUnix(copy);
		return copy;
	}
	
	/**
	 * Set the new texts to the given list
	 * @param texts New TEMPLATE.DAT texts
	 */
	public void setTemplateTexts(List<String> texts) {
		this.templateTexts = this.copy(texts);
		convertLineBreakFromUnixToWindows(templateTexts);
	}
	
	/**
	 * Return the list of the texts titles. The list is empty until a call to readDataFromFiles() is done
	 * @return The list of the titles of the texts contained in TEMPLATE.DAT
	 */
	public List<String> getTitles() {
		return this.copy(titles);
	}
	
	private List<String> copy(List<String> list) {
		return list.stream().map(s -> { return new String(s); }).collect(Collectors.toList());
	}
	
	/**
	 * Convert the \r\n in the texts into \n
	 */
	private void convertLineBreakFromWindowsToUnix(List<String> texts) {
		for (int i = 0; i<texts.size(); i++) {
			texts.set(i, texts.get(i).replace("\r\n", "\n"));
		}
	}
	
	/**
	 * Convert the \n in the texts into \r\n
	 */
	private void convertLineBreakFromUnixToWindows(List<String> texts) {
		for (int i = 0; i<texts.size(); i++) {
			texts.set(i, texts.get(i).replace("\n", "\r\n"));
		}
	}
	
	/**
	 * Save the texts to the given files. Both files should already exists
	 * @param templateFile TEMPLATE.DAT File to write
	 * @param pointerFile POINTER1.DAT File to write
	 * @return True if the save was successful, false otherwise
	 */
	public boolean saveEditedTexts(File templateFile, File pointerFile) {
		try {
			FileOutputStream templateOS = new FileOutputStream(templateFile);
			SeekableByteChannel pointerSBC = Files.newByteChannel(pointerFile.toPath(), StandardOpenOption.WRITE);
			pointerSBC.position(0x2710);
			ByteBuffer bb = ByteBuffer.allocate(this.templateTexts.size()*4);
			int offset = 1;
			for (int i=0; i<this.templateTexts.size(); i++) {
				this.pointersValue.set(i, offset);
				bb.putInt(this.changeEndianness(offset));
				byte[] textBytes = templateTexts.get(i).getBytes("ASCII");
				templateOS.write(textBytes);
				offset += textBytes.length;
			}
			templateOS.flush();
			templateOS.close();
			bb.flip();
			pointerSBC.write(bb);
			pointerSBC.close();
			return true;
		} catch (IOException e) {
			System.err.println("[DataManager] Error while saving the data");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Save the texts to the files given at this object creation
	 * @return True if the save was successful, false otherwise
	 */
	public boolean saveEditedTexts() {
		return this.saveEditedTexts(this.templateFile, this.pointer1File);
	}
}
