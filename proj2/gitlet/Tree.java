package gitlet;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "Tree{" +
                "fileName='" + fileName + '\'' +
                ", fileContent='" + fileContent + '\'' +
                ", typeOfReference='" + typeOfReference + '\'' +
                '}';
    }
    // 重写 equals() 方法
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tree otherTree = (Tree) obj;
        return Objects.equals(fileName, otherTree.fileName) &&
                Objects.equals(fileContent, otherTree.fileContent) &&
                Objects.equals(typeOfReference, otherTree.typeOfReference);
    }

    // 重写 hashCode() 方法
    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileContent, typeOfReference);
    }
    public boolean diffName(Tree tree){
        return fileName.equals(tree.getFileName());
    }
}
