package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trees implements Serializable {
    /* tree 存储的在当时 git仓库的状态*/
    List<Tree> Trees = new ArrayList<>();
}
