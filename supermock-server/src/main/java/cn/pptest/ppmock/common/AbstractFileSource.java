package cn.pptest.ppmock.common;

import com.google.common.base.Function;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public abstract class AbstractFileSource implements FileSource {

    protected final File rootDirectory;
    
    public AbstractFileSource(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    
    protected abstract boolean readOnly();

    @Override
    public BinaryFile getBinaryFileNamed(final String name) {
        return new BinaryFile(new File(rootDirectory, name));
    }

    @Override
    public TextFile getTextFileNamed(final String name) {
    	return new TextFile(new File(rootDirectory, name));
    }

    @Override
    public void createIfNecessary() {
        assertWritable();
        if (rootDirectory.exists() && rootDirectory.isFile()) {
            throw new IllegalStateException(rootDirectory + " already exists and is a file");
        } else if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
    }

    @Override
    public String getPath() {
    	return rootDirectory.getPath();
    }

    @Override
    public List<TextFile> listFiles() {
    	assertExistsAndIsDirectory();
    	List<File> fileList = asList(rootDirectory.listFiles(filesOnly()));
    	return toTextFileList(fileList);
    }

    @Override
    public List<TextFile> listFilesRecursively() {
    	assertExistsAndIsDirectory();
    	List<File> fileList = newArrayList();
    	recursivelyAddFilesToList(rootDirectory, fileList);
    	return toTextFileList(fileList);
    }

    private void recursivelyAddFilesToList(File root, List<File> fileList) {
    	File[] files = root.listFiles();
    	for (File file: files) {
    		if (file.isDirectory()) {
    			recursivelyAddFilesToList(file, fileList);
    		} else {
    			fileList.add(file);
    		}
    	}
    }

    private List<TextFile> toTextFileList(List<File> fileList) {
    	return newArrayList(transform(fileList, new Function<File, TextFile>() {
    		public TextFile apply(File input) {
    			return new TextFile(input.getPath());
    		}
    	}));
    }

    @Override
    public void writeTextFile(String name, String contents) {
    	writeTextFileAndTranslateExceptions(contents, writableFileFor(name));
    }

    @Override
    public void writeBinaryFile(String name, byte[] contents) {
        writeBinaryFileAndTranslateExceptions(contents, writableFileFor(name));
    }

    @Override
    public boolean exists() {
        return rootDirectory.exists();
    }

    private File writableFileFor(String name) {
        assertExistsAndIsDirectory();
        assertWritable();
        return new File(rootDirectory, name);
    }

    private void assertExistsAndIsDirectory() {
        if (rootDirectory.exists() && !rootDirectory.isDirectory()) {
            throw new RuntimeException(rootDirectory + " is not a directory");
        } else if (!rootDirectory.exists()) {
            throw new RuntimeException(rootDirectory + " does not exist");
        }
    }

    private void assertWritable() {
        if (readOnly()) {
            throw new UnsupportedOperationException("Can't write to read only file sources");
        }
    }

    private void writeTextFileAndTranslateExceptions(String contents, File toFile) {
        try {
            Files.write(contents, toFile, UTF_8);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void writeBinaryFileAndTranslateExceptions(byte[] contents, File toFile) {
        try {
            Files.write(contents, toFile);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private FileFilter filesOnly() {
    	return new FileFilter() {
    		public boolean accept(File file) {
    			return file.isFile();
    		}
    	};
    }

}
