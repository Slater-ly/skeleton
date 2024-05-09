package gitlet;

import java.io.Serializable;

public class Tree implements Serializable {
    private String fileName;
    private String fileContent;
    private String typeOfReference;

    public Tree(String fileName, String fileContent, String typeOfReference) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.typeOfReference = typeOfReference;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
}
