package cn.pptest.ppmock.common;

import java.util.List;


public interface FileSource {

    BinaryFile getBinaryFileNamed(String name);
	TextFile getTextFileNamed(String name);
	void createIfNecessary();
	FileSource child(String subDirectoryName);
	String getPath();
	List<TextFile> listFiles();
	List<TextFile> listFilesRecursively();
	void writeTextFile(String name, String contents);
    void writeBinaryFile(String name, byte[] contents);
    boolean exists();
}
