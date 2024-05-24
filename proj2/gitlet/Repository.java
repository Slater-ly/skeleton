package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static gitlet.Utils.*;
import static java.lang.System.exit;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 * When init ,creat a new folder .gitlet
 *  @author TODO
 */
public class Repository {
    // TODO:5.9 解决bug!!!!!!!!!!!!!!!!
    // TODO:4.30:修改commit的bug
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "com");
    public static final File BLOB_DIR = join(OBJECT_DIR, "blo");
    public static final File TREE_DIR = join(OBJECT_DIR, "tre");
    public static final File Stages = join(GITLET_DIR, "stage");
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File MAPBRANCH_DIR = join(BRANCH_DIR, "mapBranch");
    public static final File CURRENTBRANCH_DIR = join(BRANCH_DIR, "Name");
    public static String currentBranchName = CURRENTBRANCH_DIR.list() == null ? "master" : plainFilenamesIn(CURRENTBRANCH_DIR).get(0);

    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */
    /* TODO: fill in the rest of this class. */


    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            Commit initial = new Commit("initial commit", null, null);
            initial.setTimestamp("Wed Dec 31 16:00:00 1969 -0800");
            initEmptyRepository(sha1(sha11(initial)));
            PersistenceCommit(initial);
        }
    }

    private static void initEmptyRepository(String commitSha1) throws IOException {
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        TREE_DIR.mkdir();
        BRANCH_DIR.mkdir();
        MAPBRANCH_DIR.mkdir();
        CURRENTBRANCH_DIR.mkdir();
        join(CURRENTBRANCH_DIR, "master").createNewFile();
        Stages.mkdir();
    }

    private static void PersistenceCommit(Commit waitToPersistence) throws IOException {
        //第一个提交没有父提交
        String temp = sha1((sha11(waitToPersistence)));
        File masterCommit = join(COMMIT_DIR, temp);
        writeObject(masterCommit, waitToPersistence);
        writeObject(HEAD, temp);
//        updateCurrentBranchAndHEAD(temp);
        masterCommit.createNewFile();
//        saveCommit.createNewFile();
        HEAD.createNewFile();
        createBranch("master");
    }

    private static void createBranch(String branchName) throws IOException {
        if (Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).contains(branchName)) {
            System.out.println("A branch with that name already exists.");
        }
        File temp1 = join(MAPBRANCH_DIR, branchName);
        File temp = join(BRANCH_DIR, branchName);
        String tempCommit = readContentsAsString(HEAD);
        // 创建分支时,应该让其指向最新的commit
        tempCommit = tempCommit.substring(tempCommit.length() - 40);
        writeObject(temp, tempCommit);
        writeObject(temp1, tempCommit);
        temp.createNewFile();
        temp1.createNewFile();
    }

    public static void add(String fileName) throws IOException {
        if (!Objects.requireNonNull(plainFilenamesIn(CWD)).contains(fileName)) {
            System.out.println("File does not exist.");
        } else {
            File fileContent = join(CWD, fileName);
            String fileSha1 = sha1(readContentsAsString(fileContent));
//            System.out.println(fileSha1);
            // 检验是否有内容相同的文件存在
            List<String> a = plainFilenamesIn(BLOB_DIR);
//            System.out.print("List:");
//            System.out.println(a);
//            System.out.print("fileSha1:" + fileSha1);
            if (a != null) {
                if (!a.contains(fileSha1)) {
                    // 如果没有，则添加一条blob
                    addIntoBlob(fileName, join(BLOB_DIR, fileSha1));
//                    System.out.println("ready");
                    // 添加至暂存区文件夹
                }
                addIntoStage(fileName, fileSha1, 0);
            }
        }
    }

    // TODO: 5.8: 修复tree的bug 检查并修复bug
    private static String dealWithTree() {
        // 获取并校验阶段文件内容
        Stage stage;
        Tree tempTree = null;
        Trees tempTrees = new Trees();
        Commit tempCommit;
        tempCommit = readObject(join(COMMIT_DIR, getLatestCommit()), Commit.class);
        if (tempCommit.getParent() != null) {
            tempTrees = readObject(join(TREE_DIR, tempCommit.getTreeSha1()), Trees.class);
        }
        for(String s: Objects.requireNonNull(plainFilenamesIn(Stages))){
            stage = readObject(join(Stages, s), Stage.class);
//            System.out.println("fileName:" + stage.getFileName() + "+++++fileContent:" + stage.getFileContent());
            tempTree = new Tree(stage.getFileName(), stage.getFileContent(), "blob");
//            System.out.println("name:" + tempTree.getFileName() + " ");
            if(stage.getFileStatus() != 1){
                if(tempTrees.Trees.stream().anyMatch(tempTree::diffName)){
//                    System.out.println("sameName:" + tempTree.getFileName());
                    tempTrees.Trees.removeIf(tempTree::diffName);
                }
                tempTrees.Trees.add(tempTree);
            }
            else {
//                System.out.println("diffName:" + tempTree.getFileName());
                tempTrees.Trees.remove(tempTree);
            }
        }
        String treeSha1 = sha1(sha11(tempTrees));
        writeObject(join(TREE_DIR, treeSha1), tempTrees);
        return treeSha1;
    }
    public static void commit(String message) throws IOException {
        if(Objects.requireNonNull(plainFilenamesIn(Stages)).size() == 0){
            System.out.println("No changes added to the commit.");
        }
        else{
            judgeVoidCommit(message);
            Commit tempCommit = new Commit(message, null, dealWithTree());
            if(message.startsWith("Merge")){
//                System.out.println(message);
                tempCommit.setMergeFlag(true);
                tempCommit.setParents(returnCurrentAndGivenCommit(returnGivenBranchName(tempCommit.getMessage())));
            }
            // 将commit添加到文件夹内
            if (Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).size() != 0) {
                tempCommit.setParent(getLatestCommit());
            }
            String sha1 = sha1(sha11(tempCommit));
            File com = join(COMMIT_DIR, sha1);
            writeObject(com, tempCommit);
            com.createNewFile();
            // 添加完之后应该删除掉暂存区内的内容
            for(File f: plainFilenamesIn(Stages).stream().map(x -> join(Stages, x)).toArray(File[]::new)){
                if (readObject(f, Stage.class).getFileStatus() == 1) {
                    restrictedDelete(join(CWD, readObject(f, Stage.class).getFileName()));
                }
                restrictedDelete(f);
            }
            // 更新指针
            updateCurrentBranchAndHEAD(sha1);
            // 此时头指针已经更新但是master指针还没更新
//            System.out.println("111222333444");
//            showTree();
        }
    }

    public static void rm(String fileName) throws IOException {
        /*
         * 界定几个状态。
         * 1.已暂存 还未提交 add 之后马上执行的话 清空暂存区
         * 2.已被追踪 则要将文件标记为待删除的状态 且要删除在工作区中的文件
         * 3.未被追踪且不在暂存区 则提供错误用例
         * */
        String latestCommit = getLatestCommit();
        String substring = latestCommit.substring(latestCommit.length() - 40);
//        if(Objects.requireNonNull(plainFilenamesIn(Stages)).isEmpty()){
            Commit tempCommit = readObject(join(COMMIT_DIR, substring), Commit.class);
            // 如果树对象是是空的,则直接清除暂存区
            if(Objects.requireNonNull(plainFilenamesIn(TREE_DIR)).isEmpty()){
                //restrictedDelete(Stages);
                System.out.println("No reason to remove the file");
            }
            else{
                boolean flag = false;
//                System.out.println(tempCommit.getTreeSha1());
                Trees tempTree = readObject(join(TREE_DIR, tempCommit.getTreeSha1()), Trees.class);
                // 如果是当前提交对应的tree里面有此文件,则将文件标记位待删除
                if (tempTree.Trees.stream().anyMatch(x -> x.getFileName().equals(fileName))) {
                    Optional<String> fileContents = tempTree.Trees.stream().filter(x -> x.getFileName().equals(fileName)).map(Tree::getFileContent).findFirst();
                    if (fileContents.isPresent()) {
                        String fileContent = fileContents.get();
                        // 此时应该更新一条待删除的commit
                        if (Objects.requireNonNull(plainFilenamesIn(CWD)).contains(fileName)) {
                            flag = true;
                            addIntoStage(fileName, fileContent, 1);
                            restrictedDelete(join(CWD, fileName));
//                System.out.println("successful set the status");
                        }
                    }
                }
                // 如果不是的话 则既没有被暂存也没有被追踪 直接铲掉
                else{
                    System.out.println("No reason to remove the file");
                }
                if(!flag){
                    if(!Objects.requireNonNull(plainFilenamesIn(Stages)).isEmpty()) {
                        Stage tempStage;
                        for(File f: Objects.requireNonNull(plainFilenamesIn(Stages)).stream().map(x -> join(Stages, x)).toArray(File[]::new)){
                            tempStage = readObject(f, Stage.class);
                            if (fileName.equals(tempStage.getFileName()) && tempStage.getFileStatus() == 0) {
                                restrictedDelete(join(Stages, sha1(readContentsAsString(join(CWD,fileName)))));
                                break;
                            }
                        }
                    }
                }
            }
//        }
//        else {
//        }
    }

    public static void globalLog() {
        List<String> strings = plainFilenamesIn(COMMIT_DIR);
        if (strings != null && !strings.isEmpty()) {
            strings.forEach(as -> showCommit(readObject(join(COMMIT_DIR, as), Commit.class), as));
        }
    }

    public static void Log() {
        // 待完成: 合并分支时显示两个parent
        String currentBranch = readContentsAsString(join(BRANCH_DIR, currentBranchName));
        currentBranch = currentBranch.substring(currentBranch.length() - 40);
        Commit commit = readObject(join(COMMIT_DIR, currentBranch), Commit.class);
        while (commit.getParent() != null) {
            showCommit(commit, currentBranch);
            currentBranch = commit.getParent();
            commit = readObject(join(COMMIT_DIR, currentBranch), Commit.class);
        }
        showCommit(commit, currentBranch);
    }

    private static void  showCommit(Commit commit, String commitName) {
        System.out.println("===");
        System.out.println("commit " + commitName);
        if(commit.isMergeFlag()){
            System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7) + " " + commit.getParents().get(1).substring(0, 7));
//            System.out.println(".");
        }
        System.out.println("Date: " + commit.getTimestamp());
//        System.out.println("TreeSha1:" + commit.getTreeSha1());
//        System.out.println("Parent: " + commit.getParent());
        if(commit.isMergeFlag()){
            System.out.print(commit.getMessage());
            System.out.println(".");
        }
        else{
            System.out.println(commit.getMessage());
        }
        System.out.println();
    }

    public static void find(String message) {
        List<String> strings = plainFilenamesIn(COMMIT_DIR);
        Commit commit;
        boolean flag = false;
        for (String s : strings) {
            commit = readObject(join(COMMIT_DIR, s), Commit.class);
            if (commit.getMessage().equals(message)) {
                flag = true;
                System.out.println(s);
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        checkIfInitialized();
        printCaption("Branches");
        System.out.println("*" + currentBranchName);
        System.out.println();
        for (String s : Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR))) {
            if (!s.equals(currentBranchName)) {
                System.out.println(s);
            }
        }
        Stage stage;
        printCaption("Staged Files");
        List<String> removeStage = new ArrayList<>();
        for (String s : Objects.requireNonNull(plainFilenamesIn(Stages))) {
            stage = readObject(join(Stages, s), Stage.class);
            if (stage.getFileStatus() == 0) {
                System.out.println(stage.getFileName());
            } else {
                removeStage.add(s);
            }
        }
        System.out.println();
        printCaption("Removed Files");
        for (String s : removeStage) {
            stage = readObject(join(Stages, s), Stage.class);
            if (stage.getFileStatus() == 1) {
                System.out.println(stage.getFileName());
            }
        }
        if(! Objects.requireNonNull(plainFilenamesIn(TREE_DIR)).isEmpty() && Objects.requireNonNull(plainFilenamesIn(Stages)).size() != 0){
            checkDelByUser();
        }
        System.out.println();
        printCaption("Modifications Not Staged For Commit");
//        showModificationsNotStaged();
        System.out.println();
        printCaption("Untracked Files");
//        showUntrackedFiles();
        System.out.println();
    }
    public static void checkIfInitialized() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    private static void checkDelByUser() {
        Trees tempTree = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR,getLatestCommit()), Commit.class).getTreeSha1()), Trees.class);
        List<String> trackedFiles = new HashSet<>(tempTree.Trees).stream().map(Tree::getFileName).collect(Collectors.toList());
        trackedFiles.stream().filter(fileName -> !Objects.requireNonNull(plainFilenamesIn(CWD)).contains(fileName)).forEach(System.out::println);
    }
    public static void showTree(){
        for(String f: Objects.requireNonNull(plainFilenamesIn(TREE_DIR))){
            Trees s = readObject(join(TREE_DIR, f), Trees.class);
            System.out.println("trees:" + f);
            s.Trees.forEach(tree -> System.out.println(tree.getFileName() + ":::" + tree.getFileContent()));
        }
    }
    private static void showUntrackedFiles() {
//        List<Tree> trees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR,getLatestCommit()), Commit.class).getTreeSha1()), Trees.class).Trees;
//        for(String fileName: Objects.requireNonNull(plainFilenamesIn(CWD))){
//            for(Tree tree:trees){
//                if(tree.getFileName().equals(fileName) && tree.getFileContent().equals(sha1(readContentsAsString(join(CWD,fileName))))){
//                    System.out.println(fileName);
//                }
//                if(Objects.requireNonNull(Stages.listFiles()).length != 0){
//                    Stage temp = readObject(join(Stages, plainFilenamesIn(Stages).get(0)), Stage.class);
//                    if(temp.getFileName().equals(tree.getFileName())){
//                        if(temp.getFileStatus() == 0){
//                            System.out.println(temp.getFileName());
//                        }
//                        if(temp.getFileStatus() == 1){
//
//                        }
//                    }
//                }
//            }
//        }
        /*
        * 这部分用于列出工作目录中存在的，既未被暂存为新增也未被跟踪的文件。
          这也包括已被暂存为移除，但之后在Gitlet不知情的情况下重新创建的文件。
          忽略任何可能引入的子目录，因为Gitlet不处理它们。
        */
        List<String> sss = plainFilenamesIn(CWD);
        List<String> fileNameInCWD = new ArrayList<>(sss);
        if(Objects.requireNonNull(Stages.listFiles()).length != 0){
            Stage temp = readObject(join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0)), Stage.class);
            if(temp.getFileStatus() == 0){
                fileNameInCWD.remove(temp.getFileName());
            }
        }
        else {
            //找到未被追踪的文件
            if(Objects.requireNonNull(TREE_DIR.listFiles()).length != 0){
                Trees tempTrees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR,getLatestCommit()), Commit.class).getTreeSha1()), Trees.class);
                for(String name : fileNameInCWD){
                    if(tempTrees.Trees.stream().anyMatch(tree -> tree.getFileName().equals(name) && tree.getFileContent().equals(sha1(readContentsAsString(join(CWD,name)))))){
                        fileNameInCWD.remove(name);
                    }
                }
            }
        }
        fileNameInCWD.forEach(System.out::println);
    }
    private static void showModificationsNotStaged(){
        boolean flag = false;
        // 在当前提交被追踪 但在工作目录中修改但未被暂存
        List<String> fileName = Arrays.asList(Objects.requireNonNull(CWD.list()));
        String sss ="";
        Tree a;
        if(Objects.requireNonNull(Stages.listFiles()).length != 0){
            Stage tempStage = readObject(join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0)),Stage.class);
            if(!tempStage.getFileContent().equals(sha1(readContentsAsString(join(CWD,tempStage.getFileName())))) || !Objects.requireNonNull(plainFilenamesIn(CWD)).contains(tempStage.getFileName())){
                flag = true;
                sss = tempStage.getFileName();
            }
            if(Objects.requireNonNull(TREE_DIR.listFiles()).length != 0) {
                a = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR,getLatestCommit()), Commit.class).getTreeSha1()), Trees.class).Trees.get(0);
                if(tempStage.getFileStatus() == 1 && tempStage.getFileName().equals(a.getFileName()) && !fileName.contains(tempStage.getFileName())){
                    flag = true;
                    sss = tempStage.getFileName();
                }
            }
        }
        else{
            if(Objects.requireNonNull(TREE_DIR.listFiles()).length != 0){
                a = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR,getLatestCommit()), Commit.class).getTreeSha1()), Trees.class).Trees.get(0);
                if(!a.getFileContent().equals(sha1(readContentsAsString(join(CWD,a.getFileName()))))){
                    flag = true;
                    sss = a.getFileName();
                }
            }
        }
        if(flag){
            System.out.println(sss);
        }
    }

    /* TODO:
        head指针指向当前的分支 而分支则指向最新提交. 当没有自己创建分支的时候，head指针指向的分支则默认为master √
        分支实际上只是对最新提交的引用 既有最新提交的时候 应该同步更新分支内的内容  √
    */
    public static void checkout(String... args) throws IOException {
        int length = args.length;
        switch (length) {
            case 2:
                dealWithBranch(args[1]);
                break;
            case 3:
                dealWithFile(args[2]);
                break;
            case 4:
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                checkOutFileWithCommit(args[1], args[3]);
                break;
        }
    }

    /*
     * 检查当前HEAD指向的内容是否与指定分支的内容相同
     * 拿出给定分支的最新提交中的所有文件到工作区
     * 如果当前HEAD的提交对应的文件有与这些文件不同的则删除
     * 清空暂存区
     * */
    private static List<Tree> returnTreeFromCommit(String commitId) {
        /*TODO:5.14
         *完善checkout切换分支的功能
         * 拿到追踪分支时 应该查看的是是否有父提交 如果有父提交则比较异同拿出此提交所进行的更改 如果没有父提交(即初始提交) 此时返回的为空.
         * 如果一方为初始提交而另一方非初始提交 则返回非初始提交的树
         * 非 则
         * */
        Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        Commit parentCommit = readObject(join(COMMIT_DIR, commit.getParent()), Commit.class);
        Trees tempTrees = readObject(join(TREE_DIR, commit.getTreeSha1()), Trees.class);
//        System.out.println(tempTrees.Trees.stream().map(Tree::toString).collect(Collectors.joining("\n")));
        List<Tree> diff = tempTrees.Trees;
        if(parentCommit.getTreeSha1() != null){
//            System.out.println("================");
            Trees tempParentTrees = readObject(join(TREE_DIR, parentCommit.getTreeSha1()), Trees.class);
            diff = tempTrees.Trees.stream().filter(aTree ->  tempParentTrees.Trees.stream().noneMatch(aTree::equals)).collect(Collectors.toList());
//            System.out.println(tempParentTrees.Trees.stream().map(Tree::toString).collect(Collectors.joining("\n")));
//            System.out.println("================");
        }
//        System.out.println("diff");
//        diff.forEach(Tree::showTree);
//        System.out.println(diff.stream().map(Tree::toString).collect(Collectors.joining("\n")));
        return diff;
    }

    private static void dealWithBranch(String name){
        /*TODO:5:17
         *debug!!!!!!!!!!!!!!!!!!!!!!!!
         *
         *
         * */
//        showTree();
        if (!plainFilenamesIn(BRANCH_DIR).contains(name)) {
            judgeVoidCmdInCheckout(3);
        }
        else{
            String branchToCommit = readContentsAsString(join(BRANCH_DIR, name));
            // 检查当前HEAD指向的内容是否与指定分支的内容相同
            String CurrentBranch = readContentsAsString(HEAD).substring(readContentsAsString(HEAD).length() - 40);
            String ImminentBranch = branchToCommit.substring(readContentsAsString(join(BRANCH_DIR, name)).length() - 40);
            if (CurrentBranch.equals(ImminentBranch) && currentBranchName.equals(name)) {
                judgeVoidCmdInCheckout(4);
            } else {
                //检查两个提交中的文件
                Commit currentCommit = readObject(join(COMMIT_DIR, CurrentBranch), Commit.class);
//                System.out.println("currentCommit:" + CurrentBranch);
                Commit immientCommit = readObject(join(COMMIT_DIR, ImminentBranch), Commit.class);
//                System.out.println("ImmientCommit:" + ImminentBranch);
//                showTree();
                if(currentCommit.getTreeSha1() == null || immientCommit.getTreeSha1() == null){
                    if(currentCommit.getTreeSha1() == null  && immientCommit.getTreeSha1() != null){
                        if(!plainFilenamesIn(CWD).isEmpty()){
//                            System.out.println("ddd");
                            judgeVoidCmdInCheckout(5);
                        }
                        readObject(join(TREE_DIR, immientCommit.getTreeSha1()), Trees.class).Trees.forEach(aTree -> {
                            try {
//                                checkUntrackedFiles(CurrentBranch, ImminentBranch);
                                extractFromBlob(join(BLOB_DIR, aTree.getFileContent()), join(CWD, aTree.getFileName()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }else if(currentCommit.getTreeSha1() != null  && immientCommit.getTreeSha1() == null){
//                        checkUntrackedFiles(CurrentBranch, ImminentBranch);
                        readObject(join(TREE_DIR, currentCommit.getTreeSha1()), Trees.class).Trees.forEach(aTree -> restrictedDelete(join(CWD, aTree.getFileName())));
                    }
                }else if(currentCommit.getTreeSha1() != null && immientCommit.getTreeSha1() != null){
                    checkUntrackedFiles(CurrentBranch,ImminentBranch);
                    if(!currentCommit.getTreeSha1().equals(immientCommit.getTreeSha1())){
//                        System.out.println("===================");
//                        showTree();
                        Trees fileCurrent = readObject(join(TREE_DIR, currentCommit.getTreeSha1()), Trees.class);
                        Trees fileImminent = readObject(join(TREE_DIR, immientCommit.getTreeSha1()), Trees.class);
//                        fileImminent.Trees.removeAll(fileCurrent.Trees);
                        // 拿到仅被当前分支跟踪的文件
                        List<Tree> diffToCurrent = fileImminent.Trees.stream().filter(aTree ->  fileCurrent.Trees.stream().noneMatch(aTree::diffName)).collect(Collectors.toList());
                        diffToCurrent.forEach(bTree -> {
                            try {
                                extractFromBlob(join(BLOB_DIR, bTree.getFileContent()), join(CWD, bTree.getFileName()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
//                        System.out.println("fileCurrent");
//                        fileCurrent.Trees.forEach(Tree::showTree);
//                        System.out.println("fileImminent");
//                        fileImminent.Trees.forEach(Tree::showTree);
//                        System.out.println("diffToImminent");
//                        diffToCurrent.forEach(Tree::showTree);
                        // 拿到相同文件名的文件
                        List<Tree> same = fileImminent.Trees.stream().filter(aTree ->  fileCurrent.Trees.stream().anyMatch(aTree::diffName)).collect(Collectors.toList());
                        //拿到两个分支中都有的文件 并且直接将b中有的文件写入至工作区
                        List<Tree> dealWithSame = same.stream().filter(aTree ->  fileCurrent.Trees.stream().noneMatch(aTree::equals)).collect(Collectors.toList());
//                        System.out.println("dealWithSame");
//                        dealWithSame.forEach(Tree::showTree);
                        dealWithSame.forEach(cTree->{
                            try {
                                extractFromBlob(join(BLOB_DIR, cTree.getFileContent()), join(CWD, cTree.getFileName()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        // 拿到仅被待转分支跟踪的文件
                        List<Tree> diffToImminent = fileCurrent.Trees.stream().filter(aTree ->  fileImminent.Trees.stream().noneMatch(aTree::diffName)).collect(Collectors.toList());
//                        System.out.println("diffToCurrent");
//                        diffToImminent.forEach(Tree::showTree);
                        diffToImminent.forEach(bTree -> {
                            if(Objects.requireNonNull(plainFilenamesIn(Stages)).size() != 0 && readObject(join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0)),Stage.class).getFileName().equals(bTree.getFileName())){
//                                System.out.println("ttt");
                                judgeVoidCmdInCheckout(5);
                            }else{
                                restrictedDelete(join(CWD, bTree.getFileName()));
                            }
                        });
                    }
                }
                join(CURRENTBRANCH_DIR, currentBranchName).renameTo(join(CURRENTBRANCH_DIR, name));
                writeContents(HEAD, ImminentBranch);
                restrictedDelete(Stages);
            }
        }
    }
    private static void checkUntrackedFiles(String currentCommit, String immientCommit) {
        /*
        *为了处理文件被用户修改的情况
        *
        * */
        List<String> files = plainFilenamesIn(CWD);
        Trees t = readObject(join(TREE_DIR,readObject(join(COMMIT_DIR, immientCommit), Commit.class).getTreeSha1()), Trees.class);
//        List<Tree> k = t.Trees.stream().filter(aTree -> files.contains(aTree.getFileName())).collect(Collectors.toList());
        Commit currentCommit1 = readObject(join(COMMIT_DIR, currentCommit), Commit.class);
        if(currentCommit1.getTreeSha1() != null ){
            Trees tt = readObject(join(TREE_DIR, currentCommit1.getTreeSha1()), Trees.class);
            List<Tree> kk = tt.Trees.stream().filter(aTree -> files.contains(aTree.getFileName())).collect(Collectors.toList());
//            kk.forEach(Tree::showTree);
//            System.out.println(sha1(readContentsAsString(join(CWD, kk.get(0).getFileName()))));
            if(kk.stream().anyMatch(aTree ->!sha1(readContentsAsString(join(CWD, aTree.getFileName()))).equals(aTree.getFileContent()))){
                judgeVoidCmdInCheckout(5);
//                System.out.println("sss");
                exit(0);
            }
        }
//        k.forEach(Tree::showTree);
//        if(k.stream().anyMatch(aTree ->!sha1(readContentsAsString(join(CWD, aTree.getFileName()))).equals(aTree.getFileContent()))){
//            System.out.println("ddd");
//            judgeVoidCmdInCheckout(5);
//        }
    }
    private static Trees returnTreesFromCommit(String commitId) {
        return readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, commitId), Commit.class).getTreeSha1()), Trees.class);
    }

    private static String returnOnlyFileContentFromTree(String commitId, String fileName) {
        Trees tempTrees = returnTreesFromCommit(commitId);
        String fileContent = null;
        Optional<String> fileContents = tempTrees.Trees.stream().filter(x -> x.getFileName().equals(fileName)).map(Tree::getFileContent).findFirst();
        if (fileContents.isPresent()) {
            fileContent = fileContents.get();
            // 此时应该更新一条待删除的commit
        }
        return fileContent;
    }
    /*TODO:根据Commit 的tree 找到文件，然后通过父提交往前回溯找到tree中是否有此文件名的文件 拿到blob 最后将blob的内容写入到当前目录*/

    /**
     * 处理指定名称的文件。
     *
     * @param name 需要处理的文件名称。
     * @throws IOException 如果读取或写入文件时发生错误。
     */
    private static void dealWithFile(String name) throws IOException {
        String CurrentCommit = readContentsAsString(HEAD);
        CurrentCommit = CurrentCommit.substring(CurrentCommit.length() - 40);
        Trees tempTrees = returnTreesFromCommit(CurrentCommit);
//        System.out.println(name);
//        System.out.println("==========================================");
//        System.out.println(tempTrees.Trees.get(0).getFileContent() + tempTrees.Trees.get(0).getFileName());
//        System.out.println(tempTrees.Trees.get(1).getFileContent() + tempTrees.Trees.get(1).getFileName());
//        System.out.println("==========================================");
        for(Tree tree : tempTrees.Trees){
            if(tree.getFileName().equals(name)){
                extractFromBlob(join(BLOB_DIR, returnOnlyFileContentFromTree(CurrentCommit, name)), join(CWD, name));
                break;
            }
            else {
                judgeVoidCmdInCheckout(1);
            }
        }

        // 遍历提交目录下的所有文件，查找对应的提交对象
//        if(COMMIT_DIR.listFiles() != null){
//            // 从提交目录中读取指定名称的提交对象
//            Commit commit = readObject(join(COMMIT_DIR, fileSha1), Commit.class);
//            tree tempTree;
//            // 循环遍历提交对象，直到找到包含指定文件的提交
//            while(commit != null){
//                // 根据提交对象中的树SHA1，从树目录中读取对应的树对象
//                tempTree = readObject(join(TREE_DIR, commit.getTreeSha1()), tree.class);
//                // 如果树对象中的文件名与指定名称匹配，则进行文件解压并写入当前工作目录
//                if(tempTree.getFileName().equals(name)){
//                    writeDecompress(readContents(join(BLOB_DIR, tempTree.getFileContent())), join(CWD, name));
////                    addIntoStage(tempTree.getFileName(), tempTree.getFileContent(), 0);
//                    break;
//                }
//                // 继续向上查找父提交对象
//                commit = readObject(join(COMMIT_DIR, commit.getParent()), Commit.class);
//            }
//        }
    }

    /**
     * 根据提交ID和字段名检查出对应的文件。
     * 如果给定的提交ID在提交目录中不存在，则判断为无效命令。
     * 如果找到对应的提交，会进一步查找该提交的树对象，然后在树对象中查找指定字段名的文件。
     * 如果找到对应的文件内容，则将其解压并写入当前工作目录。
     *
     * @param commitId 提交的唯一标识符
     * @param fileName 需要检出的文件名
     * @throws IOException 如果读取或写入文件时发生错误
     */
    private static void checkOutFileWithCommit(String commitId, String fileName) throws IOException {
//        System.out.println("tempTree");
        // 检查提交ID是否存在于提交目录中
        if (!Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).contains(commitId)) {
            judgeVoidCmdInCheckout(2);
        }
        else {
            // 读取指定提交对象
            Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
            boolean flag = true;
            // 如果提交对象中的树SHA1不为空，则尝试读取对应的树对象sssss
            if (commit.getTreeSha1() != null) {
                List<Tree> tempTrees = returnTreeFromCommit(commitId);
//                System.out.println("tempTree");
//                tempTrees.forEach(Tree::showTree);
                for(Tree tempTree:tempTrees){
                    if (tempTree.getFileName().equals(fileName)) {
                        // 如果找到，将文件内容解压并写入当前工作目录
//                writeDecompress(readContents(join(BLOB_DIR, tempTree.getFileContent())), join(CWD, fileName));
                        extractFromBlob(join(BLOB_DIR, tempTree.getFileContent()), join(CWD, tempTree.getFileName()));
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    judgeVoidCmdInCheckout(1);
                    exit(0);
                }
                // 在树对象中查找指定字段名的文件
            }
        }
    }

    private static void judgeVoidCmdInCheckout(int kind) {
        switch (kind) {
            case 1:
                System.out.println("File does not exist in that commit.");
                break;
            case 2:
                System.out.println("No commit with that id exists.");
                break;
            case 3:
                System.out.println("No such branch exists.");
                break;
            case 4:
                System.out.println("No need to checkout the current branch.");
                break;
            case 5:
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                break;
        }
    }

    /*
     * 对于分支而言 创建分支并不代表着要立即切换分支 如果没有签出 则默认是当前分支
     * */
    public static void branch(String branchName) throws IOException {
        createBranch(branchName);
    }

    public static void rmBranch(String branchName) throws IOException {
        judgeIfBranchExistOrCurrent(branchName);
        restrictedDelete(join(BRANCH_DIR, branchName));
    }

    public static void reset(String commitID) throws IOException {
        /*
         * 如有未跟踪的文件 打印提示信息
        *  拿到对应commit跟踪的文件
        *  还原这些文件
        *  将当前分支的头指针移到给定提交
        * */
        if (!Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).contains(commitID)) {
            System.out.println("No commit with that id exists.");
        }
        else{
//            updateCurrentBranchAndHEAD(commitID);
//            checkout(currentBranchName);
            // 拿到给定提交的文件
            Trees givenTree = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, commitID), Commit.class).getTreeSha1()), Trees.class);
            List<String> fileNames = plainFilenamesIn(CWD);
            if(givenTree.Trees.stream().noneMatch(tree -> fileNames.contains(tree.getFileName()))){
                if(Objects.requireNonNull(plainFilenamesIn(Stages)).size() != 0){
                    List<Stage> aa = new ArrayList<>(Collections.singletonList(new Stage("111", "222", 3)));
                    Objects.requireNonNull(plainFilenamesIn(Stages)).forEach(aStage ->{
                        Stage a = readObject(join(Stages, aStage),Stage.class);
                        if(a != null){
                            aa.add(a);
                        }
                    });
                    aa.remove(0);
                    if(aa.stream().anyMatch(stage -> fileNames.equals(stage.getFileName()) && !stage.getFileContent().equals(sha1(readContentsAsString(join(CWD, stage.getFileName())))))){
                        judgeVoidCmdInCheckout(5);
                        exit(0);
                    }
                    Objects.requireNonNull(plainFilenamesIn(Stages)).forEach(all->restrictedDelete(join(Stages,all)));
                }else{
                    judgeVoidCmdInCheckout(5);
                    exit(0);
                }
            }
            else{
                updateCurrentBranchAndHEAD(commitID);
                Objects.requireNonNull(plainFilenamesIn(Stages)).forEach(all->restrictedDelete(join(Stages,all)));
                givenTree.Trees.forEach(aTree -> {
                    try {
                        if(fileNames.contains(aTree.getFileName())){
                            if(!sha1(readContentsAsString(join(CWD, aTree.getFileName()))).equals(aTree.getFileContent())){
                                judgeVoidCmdInCheckout(5);
                            }
                            else {
                                checkOutFileWithCommit(commitID, aTree.getFileName());
                                exit(0);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
    /*
     * 如何确定是否存在承接关系呢？
     * 维护每个分支与头部提交的对应关系
     * */
    public static void merge(String branchName) throws IOException {
        //TODO:debug!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // 当前分支的commit
        if(branchName.equals(currentBranchName)){
            System.out.println("Cannot merge a branch with itself.");
            exit(0);
        }
        judgeIfBranchExistOrCurrent(branchName);
        String currentBranchCommit = returnCurrentAndGivenCommit(branchName).get(0);
        String givenBranchCommit = returnCurrentAndGivenCommit(branchName).get(1);
        String commit;
//        Repository.globalLog();

        judgeIfBranchExistOrCurrent(branchName);

        if(!Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).contains(branchName)){
            commitMessageOfMerge(3);
        }
//        checkIfUntracked();
        for (commit = currentBranchCommit;commit != null; commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()) {
//            System.out.println("ssss" + commit + "tttt" + givenBranchCommit);
            if (commit.equals(givenBranchCommit)) {
                commitMessageOfMerge(2);
                exit(0);
            }
        }
        for (commit = givenBranchCommit;commit != null; commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()) {
            if (commit.equals(currentBranchCommit)) {
                checkout(branchName);
                commitMessageOfMerge(1);
                exit(0);
            }
        }
        for(String commit1 = currentBranchCommit, commit2 = givenBranchCommit;commit1 != null || commit2 != null;commit1 = readObject(join(COMMIT_DIR, commit1), Commit.class).getParent(), commit2 = readObject(join(COMMIT_DIR, commit2), Commit.class).getParent()){
            if(commit1.equals(commit2)){
                findModifiedFiles(commit1, branchName);
                break;
            }
        }
        String s = ("Merged " + branchName + " into " + currentBranchName);
        Repository.commit(s);
    }

    private static void checkIfUntracked() {
        List<String> fileNames = plainFilenamesIn(CWD);
        String commit = getLatestCommit();
        Trees trees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, commit), Commit.class).getTreeSha1()), Trees.class);
//        System.out.println("ttttt");
//        trees.Trees.forEach(Tree::showTree);
//        System.out.println("fffff");
//        System.out.println(fileNames);
//        System.out.println(fileNames.size());
//        String fileContent = "";
        for(String name: fileNames){
//            System.out.println(name);
            List<Tree> a = trees.Trees.stream().filter(aTree -> aTree.getFileName().equals(name)).collect(Collectors.toList());
            if(a.isEmpty()){
//                System.out.println(a);
//                fileContent = a.get(0).getFileContent();
//                if(!sha1(readContentsAsString(join(CWD, name))).equals(fileContent)){
//                    judgeVoidCmdInCheckout(5);
//                    exit(0);
//                }
                judgeVoidCmdInCheckout(5);
                exit(0);
            }
        }
//        showTree();
//        globalLog();
//        System.out.println("sb");
//        exit(0);
    }

    private static List<String> returnCurrentAndGivenCommit(String branchName){
        List<String> a = new ArrayList<>();
        a.add(readContentsAsString(HEAD).substring(readContentsAsString(HEAD).length() - 40));
        a.add(readContentsAsString(join(BRANCH_DIR, branchName)).substring(readContentsAsString(join(BRANCH_DIR, branchName)).length() - 40));
        return a;
    }
    private static String returnGivenBranchName(String mergedMessage) {
        // 使用正则表达式找到"merged"和"into"之间的内容
        String patternString = "(?<=Merged\\s)[^\\s]+(?=\\sinto)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(mergedMessage);

        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalArgumentException("Invalid merge message format.");
        }
    }

    private static void findModifiedFiles(String commitOfSplitPoint, String branchName) throws IOException {
//        dealWithFailureCase(branchName);
//        String currentBranchCommit = readContentsAsString(HEAD).substring(readContentsAsString(HEAD).length() - 40);
        String givenBranchCommit = readContentsAsString(join(BRANCH_DIR, branchName)).substring(readContentsAsString(join(BRANCH_DIR, branchName)).length() - 40);
//        Map<String, Map<String, String>> sha1ToFileNameForGivenBranch = findFiles(givenBranchCommit, commitOfSplitPoint);
//        Map<String, Map<String, String>> sha1ToFileNameForOriginalBranch = findFiles(currentBranchCommit, commitOfSplitPoint);
//        //在给定分支中修改过、但在当前分支中没有修改过的文件
//        findModifiedFilesInGivenBranchAfterSplitPoint(sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch);
//        // 在分割点不存在且只存在于给定分支中的文件
//        findFilesJustInGivenBranch(commitOfSplitPoint, sha1ToFileNameForGivenBranch);
//        //任何在分割点存在、在当前分支中未被修改、但在给定分支中不存在的文件
//        rmInGivenBranchAndUnModifiedInOriginalBranch(commitOfSplitPoint, sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch);
//        // 找到冲突文件
//        findConflictFileAndResolve(sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch, branchName);
        Trees currentFileTrees = returnTreesCurrentAndGiven(branchName).get(0);
        Trees givenFileTrees = returnTreesCurrentAndGiven(branchName).get(1);
        Trees splitFilesTrees;
        if(readObject(join(COMMIT_DIR, commitOfSplitPoint), Commit.class).getTreeSha1() == null){
            splitFilesTrees = null;
        }
        else{
            splitFilesTrees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, commitOfSplitPoint), Commit.class).getTreeSha1()) ,Trees.class);
        }
        // 只在给定分支修改:指定文件与分支点不同 且 当前分支与分支点相同 把这些文件切换到给定分支的版本 并自动添加到暂存区
//        System.out.println("====================================");
//        System.out.println("current");
//        currentFileTrees.Trees.forEach(Tree::showTree);
//        System.out.println("given");
//        givenFileTrees.Trees.forEach(Tree::showTree);
//        System.out.println("====================================");
        findJustModifiedInGivenBranch(currentFileTrees, givenFileTrees, splitFilesTrees);
        findJustModifiedInOriginalBranch(currentFileTrees, givenFileTrees, splitFilesTrees);
        findExistAfterSplitPointInOriginalBranch(currentFileTrees, givenFileTrees, splitFilesTrees);
        findExistAfterSplitPointInGivenBranch(givenBranchCommit,currentFileTrees, givenFileTrees, splitFilesTrees);
        ExitInSplitButUnModifiedAndUnExistInGivenBranch(currentFileTrees, givenFileTrees, splitFilesTrees);
        ExitInSplitButUnModifiedAndUnExistInOriginalBranch(currentFileTrees, givenFileTrees, splitFilesTrees);
        SolveConflictFile(currentFileTrees, givenFileTrees, splitFilesTrees);
    }
    private static List<Trees> returnTreesCurrentAndGiven(String branchName){
        List<String> re = returnCurrentAndGivenCommit(branchName);
        List<Trees> ret = new ArrayList<>();
        Trees currentFileTrees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, re.get(0)), Commit.class).getTreeSha1()) ,Trees.class);
        Trees givenFileTrees = readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, re.get(1)), Commit.class).getTreeSha1()) ,Trees.class);
        ret.add(currentFileTrees);
        ret.add(givenFileTrees);
        return ret;
    }

    private static void findJustModifiedInGivenBranch(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {
       // 只在给定分支修改:指定文件与分支点不同 且 当前分支与分支点相同 把这些文件切换到给定分支的版本 并自动添加到暂存区
        List<Tree> JustModifiedInGivenBranch;
        if(splitFilesTrees != null){
            JustModifiedInGivenBranch = splitFilesTrees.Trees.stream().
                    filter(aTree -> givenFileTrees.Trees.stream().
                            anyMatch(aTree::sameNameAndDiffContent) && currentFileTrees.Trees.stream().anyMatch(aTree::equals)).
                    collect(Collectors.toList());
            JustModifiedInGivenBranch.forEach(tree -> {
                try {
                    extractFromBlob(join(BLOB_DIR, tree.getFileContent()), join(CWD, tree.getFileName()));
                    addIntoStage(tree.getFileName(), tree.getFileContent(), 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void findJustModifiedInOriginalBranch(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {
        
    }

    private static void findExistAfterSplitPointInOriginalBranch(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {

    }

    private static void findExistAfterSplitPointInGivenBranch(String givenBranchCommit, Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {
    // 签出仅在给定分支里有的文件并暂存
        List<Tree> existAfterSplitPointInGivenBranch;
        if(splitFilesTrees != null){
            existAfterSplitPointInGivenBranch  = givenFileTrees.Trees.stream().
                    filter(aTree -> splitFilesTrees.Trees.stream().noneMatch(aTree::equals) &&
                            currentFileTrees.Trees.stream().noneMatch(aTree::equals))
                    .collect(Collectors.toList());
        }
        else {
            // 如果分割点是原始提交 则所有在给定分支的文件 都是仅存在于给定分支的文件
            existAfterSplitPointInGivenBranch = new ArrayList<>(givenFileTrees.Trees);
        }

//        System.out.println("existAfterSplitPointInGivenBranch");
//        existAfterSplitPointInGivenBranch.forEach(Tree::showTree);
//        System.out.println("currentFileTrees");
//        currentFileTrees.Trees.forEach(Tree::showTree);
//        System.out.println("givenFileTrees");
//        givenFileTrees.Trees.forEach(Tree::showTree);
        existAfterSplitPointInGivenBranch.forEach(tree -> {
            try {
                checkOutFileWithCommit(givenBranchCommit, tree.getFileName());
                addIntoStage(tree.getFileName(), tree.getFileContent(), 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void ExitInSplitButUnModifiedAndUnExistInGivenBranch(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {
        if(splitFilesTrees != null){
            List<Tree> unExistInGivenBranch = currentFileTrees.Trees.stream().
                    filter(aTree -> splitFilesTrees.Trees.stream().anyMatch(aTree::equals) &&
                            givenFileTrees.Trees.stream().noneMatch(aTree::equals))
                    .collect(Collectors.toList());
            unExistInGivenBranch.forEach(tree -> {
                restrictedDelete(join(CWD, tree.getFileName()));
            });
        }

    }

    private static void ExitInSplitButUnModifiedAndUnExistInOriginalBranch(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) {

    }

    private static void SolveConflictFile(Trees currentFileTrees, Trees givenFileTrees, Trees splitFilesTrees) throws IOException {
    /*找到三种类型的文件
    * 内容冲突/状态冲突
    *
    * *
     */
//        showTree();
//        Repository.globalLog();
//        System.out.println("current");
//        currentFileTrees.Trees.forEach(Tree::showTree);
//        System.out.println("given");
//        givenFileTrees.Trees.forEach(Tree::showTree);
        // 1.内容冲突
//        System.out.println("aaattt");
        List<Tree> conflictContent = currentFileTrees.Trees.stream()
                .filter(aTree -> givenFileTrees.Trees.stream().anyMatch(aTree::sameNameAndDiffContent))
                .collect(Collectors.toList());
//        System.out.println(conflictContent.size());
        if(splitFilesTrees != null){
            for(Tree tree : splitFilesTrees.Trees){
                if((currentFileTrees.Trees.stream().anyMatch(tree::sameNameAndDiffContent) && givenFileTrees.Trees.stream().noneMatch(tree::diffName) || (givenFileTrees.Trees.stream().anyMatch(tree::sameNameAndDiffContent) && currentFileTrees.Trees.stream().noneMatch(tree::diffName)))){
                    conflictContent.add(tree);
                }
            }
        }
//        System.out.println("conflicts");
//        conflictContent.forEach(Tree::showTree);
        if(conflictContent.size() != 0){
            System.out.println("Encountered a merge conflict.");
            String content;
            String content1;
            String result;
            List<String> filesInCWD = plainFilenamesIn(CWD);
            for(Tree tree : conflictContent){
                result = "";
//                System.out.println("aaaaaaa");
//                System.out.println(join(CWD, tree.getFileName()).toString());
//                System.out.println("currentFileTrees:" + tree.getFileContent());
                // 如果当前提交有这个文件
                content = getString(currentFileTrees, tree);
//                System.out.println("content:" + content);
//                System.out.println("givenFileTrees:" + givenFileContent);
                // 如果给定提交有这个文件
                content1 = getString(givenFileTrees, tree);
//                System.out.println("content1" + content1);
                result += "<<<<<<< HEAD\n" + content + "=======\n" + content1 + ">>>>>>>\n";
                writeContents(join(CWD, tree.getFileName()), result);
            }
        }
    }

    private static String getString(Trees currentFileTrees, Tree tree) throws IOException {
        String res = "";
        if(currentFileTrees.Trees.stream().anyMatch(tree::diffName)){
            String currentFileContent = currentFileTrees.Trees.stream().filter(tree::diffName).collect(Collectors.toList()).get(0).getFileContent();
            extractFromBlob(join(BLOB_DIR, currentFileContent), join(CWD, tree.getFileName()));
            res = readContentsAsString(join(CWD, tree.getFileName()));
        }
        return res;
    }

    private static void dealWithFailureCase(String branchName) {
        if (Objects.requireNonNull(Stages.listFiles()).length != 0) {
            System.out.println("You have uncommitted changes");
        }
        if (branchName.equals(currentBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
        }
        if (!plainFilenamesIn(BRANCH_DIR).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        }
        if (branchName.equals("0xffffffff")) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first");
        }
    }

    private static void findModifiedFilesInGivenBranchAfterSplitPoint(Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String, Map<String, String>> sha1ToFileNameForOriginalBranch) throws IOException {
        Set<String> givenFileContent = getSet(sha1ToFileNameForGivenBranch);
        givenFileContent.removeAll(getSet(sha1ToFileNameForOriginalBranch));
        for (Map.Entry<String, Map<String, String>> entry : sha1ToFileNameForGivenBranch.entrySet()) {
            if (entry.getValue().keySet().stream().anyMatch(givenFileContent::contains)) {
                checkout(entry.getKey(), entry.getValue().keySet().iterator().next());
                // 签出在给定分支中被修改过的文件
            }
        }
    }

    private static void findFilesJustInGivenBranch(String commitOfSplitPoint, Map<String, Map<String, String>> sha1ToFileNameForGivenBranch) throws IOException {
        Set<String> givenFileContent = new HashSet<>();
        givenFileContent.add(readContentsAsString(join(TREE_DIR, readObject(join(COMMIT_DIR, commitOfSplitPoint), Commit.class).getTreeSha1())));
        String temp;
        for (Map.Entry<String, Map<String, String>> entry : sha1ToFileNameForGivenBranch.entrySet()) {
            temp = entry.getValue().keySet().iterator().next();
            if (!givenFileContent.contains(temp)) {
                // 签出文件
                checkout(entry.getKey(), temp);
                // 添加至暂存区
                addIntoStage(entry.getValue().get(temp), temp, 0);
            }
        }
    }

//    private static void rmInGivenBranchAndUnModifiedInOriginalBranch(String commitOfSplitPoint, Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String, Map<String, String>> sha1ToFileNameForOriginalBranch) {
//        String splitTreeFileContent = returnTreeFromCommit(commitOfSplitPoint).getFileContent();
//        if (!getSet(sha1ToFileNameForGivenBranch).contains(splitTreeFileContent) && getSet(sha1ToFileNameForOriginalBranch).contains(splitTreeFileContent)) {
//            restrictedDelete(join(Stages, splitTreeFileContent));
//        }
//    }

    private static void findConflictFileAndResolve(Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String, Map<String, String>> sha1ToFileNameForOriginalBranch, String givenBranchName) throws IOException {
        // 找到冲突文件
        Map<String, List<String>> duplicatesMap = Stream.concat(
                        sha1ToFileNameForGivenBranch.values().stream().flatMap(m -> m.entrySet().stream()),
                        sha1ToFileNameForOriginalBranch.values().stream().flatMap(m -> m.entrySet().stream()))
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
        // 找到未跟踪的文件
        Set<String> unTrackedFiles = new HashSet<>(Objects.requireNonNull(plainFilenamesIn(CWD)));
        if (!duplicatesMap.isEmpty()) {
            System.out.println("Encountered a merge conflict");
            for (Map.Entry<String, List<String>> entry : duplicatesMap.entrySet()) {
                if (entry.getValue().size() == 2) {
                    if (getSet(sha1ToFileNameForOriginalBranch).contains(entry.getValue().get(0))) {
                        if (unTrackedFiles.contains(entry.getKey())) {
                            dealWithFile("0xffffffff");
                        }
                        resolveConflict(entry.getKey(), entry.getValue().get(0), entry.getValue().get(1), currentBranchName, givenBranchName);
                    }
                }
            }
        }
    }

    private static void resolveConflict(String fileName, String fileCurrentContent, String fileGivenContent, String currentBranchName, String givenBranchName) {
        try (PrintWriter out = new PrintWriter(join(Stages, fileCurrentContent))) {
            out.println("<<<<<<< HEAD");
            out.println(readContentsAsString(join(BLOB_DIR, fileCurrentContent)));
            out.println("=======");
            out.print(readContentsAsString(join(BLOB_DIR, fileGivenContent)));
            add(fileName);
            commit("Merge" + givenBranchName + "into" + currentBranchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> getSet(Map<String, Map<String, String>> a) {
        return a.values().stream().flatMap(innerMap -> innerMap.keySet().stream()).collect(Collectors.toSet());
    }

//    private static Map<String, Map<String, String>> findFiles(String start, String commitOfSplitPoint) {
//        System.out.println("==============================");
//        Map<String, Map<String, String>> re = new HashMap<>();
//        String commit;
//        Tree tempTree;
//        Map<String, String> a = new HashMap<>();
//        for (commit = start; !Objects.equals(commit, commitOfSplitPoint); commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()) {
//            tempTree = returnTreeFromCommit(start);
//            System.out.println("fileName:" + tempTree.getFileName() + "===fileContent" + tempTree.getFileContent() );
//            a.put(tempTree.getFileName(), tempTree.getFileContent());
//            re.put(commit, a);
//            a.clear();
//        }
//        System.out.println("commitOfSplitPoint" + commitOfSplitPoint);
//        System.out.println(re);
//        System.out.println("==============================");
//        return re;
//    }

    private static void commitMessageOfMerge(int flag) {
        switch (flag) {
            case 1:
                System.out.println("Given branch is an ancestor of the current branch.");
                break;
            case 2:
                System.out.println("Current branch fast-forwarded.");
                break;
            case 3:
                System.out.println("First commit cannot be merged.");
                break;
            case 4:
                System.out.println("Current branch is up to date.");
                break;
            case 5:
                System.out.println("Given branch has no history.");
                break;
            case 6:
                System.out.println("Cannot merge a branch with itself");
                break;
        }
    }

    /**
     * 将指定文件的内容压缩后存储到Blob文件中。
     *
     * @param fileName 需要被压缩并存储的文件名称。
     * @param path     压缩后Blob文件的保存路径。
     * @throws IOException 如果读取文件或写入Blob文件时发生IO异常。
     */
    private static void addIntoBlob(String fileName, File path) throws IOException {
        //System.out.println(join(CWD,fileName));
        // 读取待压缩文件的内容
        byte[] input = readContents(join(CWD, fileName));
        // 创建压缩器，使用默认压缩级别和nowrap模式
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        // 创建输出流，用于接收经过压缩后的数据
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream(input.length);
        // 使用DeflaterOutputStream进行数据压缩
        try (DeflaterOutputStream dos = new DeflaterOutputStream(compressedStream, deflater)) {
            dos.write(input);
        }
        // 获取压缩后的字节数组
        byte[] byteArray = compressedStream.toByteArray();
        // 将压缩后的字节数组写入到指定路径的文件中
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addIntoStage(String fileName, String fileContent, int status) throws IOException {
        File tempStage = join(Stages, fileContent);
        writeObject(tempStage, new Stage(fileName, fileContent, status));
        tempStage.createNewFile();
    }

    private static void extractFromBlob(File compressedFile, File outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(compressedFile);
             InflaterInputStream iis = new InflaterInputStream(fis, new Inflater(true));
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
//        String content = new String(Files.readAllBytes(outputFile.toPath()));
//        String lastLine = content.substring(content.lastIndexOf("\n") + 1);
//        if(lastLine.isEmpty()){
//            content = content.substring(0, content.length() - 1);
//            Files.write(outputFile.toPath(), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
//        }
    }

    /**
     * 更新当前分支和HEAD指向的提交SHA1值。
     *
     * @param commitSha1 新的提交SHA1哈希值
     * @throws IOException 如果在更新文件或创建文件时发生错误
     */
    private static void updateCurrentBranchAndHEAD(String commitSha1) throws IOException {
        // 1. 获取当前活动分支的文件路径
        File updateCurrentBranch = join(BRANCH_DIR, currentBranchName);
        // 2. 写入新的commitSHA1到当前分支文件
        writeObject(join(updateCurrentBranch), commitSha1);
        // 3. 创建或更新当前分支文件，确保其存在
        updateCurrentBranch.createNewFile();
        // 4. 更新HEAD文件，将其指向新的commitSHA1
        writeObject(HEAD, commitSha1);
        // 5. 创建或更新HEAD文件，确保其存在
        HEAD.createNewFile();
    }

    private static void judgeVoidCommit(String message) {
        String tempHint = "No changes added to the commit.";
        int flag = 0;
        if (Objects.equals(message, "") || Stages.listFiles() == null) {
            if (Objects.equals(message, "")) {
                flag = 1;
            } else {
                flag = 2;
            }
        }
        if (flag != 0) {
            if (flag == 1) {
                tempHint = "Please enter a commit message";
            }
            System.out.println(tempHint);
        }
    }

    /**
     * 处理树结构。
     * 该方法首先从指定的阶段（Stages）中获取第一个文件名，然后将该文件名与阶段路径结合生成一个临时文件。
     * 接着，从该临时文件中读取对象并创建一个tree实例。最后，返回该tree实例的SHA1值。
     *
     * @return String 返回tree实例的SHA1值。
     */

    private static void printCaption(String message) {
        System.out.println("=== " + message + " ===");
    }

    private static void judgeIfBranchExistOrCurrent(String branchName) {
        if (Objects.equals(branchName, currentBranchName)) {
            System.out.println("Cannot remove the current branch.");
            exit(0);
        }
        if (!Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            exit(0);
        }
    }

    private static String getLatestCommit() {
        String latestCommit = readContentsAsString(HEAD);
        return latestCommit.substring(latestCommit.length() - 40);
    }

    private static byte[] sha11(Object a) {
        if (a == null) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(a);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}