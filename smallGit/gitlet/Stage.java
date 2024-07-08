package gitlet;

import java.io.Serializable;

public class Stage implements Serializable {
    private String fileName;
    private String fileContent;
    private Integer fileStatus;
    //暂存区定义

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Stage(String fileName, String fileContent, Integer fileStatus) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.fileStatus = fileStatus;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public Integer getFileStatus() {
        return fileStatus;
    }
}
