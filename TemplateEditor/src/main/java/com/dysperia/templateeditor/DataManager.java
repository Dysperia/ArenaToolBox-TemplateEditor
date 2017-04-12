package com.dysperia.templateeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
	private File templateFile;
	private File pointer1File;
	private List<Integer> pointersValue;
	private List<String> templateTexts;

	public DataManager(File templateFile, File pointer1File) {
		this.templateFile = templateFile;
		this.pointer1File = pointer1File;
		pointersValue = new ArrayList<>();
		templateTexts = new ArrayList<>();
	}
	
	public void readDataFromFiles() throws IOException {
		DataInputStream pointer1IS= new DataInputStream(new FileInputStream(pointer1File));
		pointer1IS.skipBytes(0x2710);
		int offset = toLittleEndian(pointer1IS.readInt());
		while(offset != 0) {
			pointersValue.add(offset);
			offset = toLittleEndian(pointer1IS.readInt());
		}
		pointer1IS.close();
		// Adding end of file offset to compute the last template entry length
		pointersValue.add((int)templateFile.length());
	}
	
	private int toLittleEndian(int val)
	{
		int i1 = (val >>  0) & 0xff;
		int i2 = (val >>  8) & 0xff;
		int i3 = (val >> 16) & 0xff;
		int i4 = (val >> 24) & 0xff;
		return i1 << 24 | i2 << 16 | i3 << 8 | i4 << 0;
	}
}
