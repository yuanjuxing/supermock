package cn.pptest.ppmock.common;

import java.io.File;

public class SingleRootFileSource extends AbstractFileSource {

	public SingleRootFileSource(File rootDirectory) {
		super(rootDirectory);
	}
	
	public SingleRootFileSource(String rootPath) {
	    super(new File(rootPath));
	}
	
	@Override
    public FileSource child(String subDirectoryName) {
        return new SingleRootFileSource(new File(rootDirectory, subDirectoryName));
    }

    @Override
    protected boolean readOnly() {
        return false;
    }

    @Override
    public String toString() {
        return SingleRootFileSource.class.getSimpleName() + ": " + rootDirectory;
    }
}
