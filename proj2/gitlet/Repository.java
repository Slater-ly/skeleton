package gitlet;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 * When init ,creat a new folder .gitlet
 *  @author TODO
 */
public class Repository {
    // TODO:4.30:修改commit的bug
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "com");
    public static final File BLOB_DIR = join(OBJECT_DIR, "blo");
    public static final File TREE_DIR = join(OBJECT_DIR, "tre");
    public static final File Stages = join(GITLET_DIR, "stage");
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final  File MAPBRANCH_DIR = join(BRANCH_DIR,"mapBranch");
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
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        } else {
            Commit initial = new Commit("initial commit", null, null);
            initial.setTimestamp("00:00:00 CST, Thursday, 1 January 1970");
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
        if(Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).contains(branchName)){
            throw new GitletException("A branch with that name already exists.");
        }
        File temp1 = join(MAPBRANCH_DIR, branchName);
        File temp = join(BRANCH_DIR, branchName);
        String tempCommit = readContentsAsString(HEAD);
        System.out.println("HEAD Commit" + tempCommit);
        // 创建分支时,应该让其指向最新的commit
        tempCommit = tempCommit.substring(tempCommit.length() - 40);
        System.out.println("HEAD Commit" + tempCommit);
        writeObject(temp, tempCommit);
        writeObject(temp1, tempCommit);
        temp.createNewFile();
        temp1.createNewFile();
    }
    public static void add(String fileName) throws IOException {
        if(!Objects.requireNonNull(plainFilenamesIn(CWD)).contains(fileName)){
            throw new GitletException("File does not exist.");
        }
        else {
            File fileContent = join(CWD, fileName);
            String fileSha1 = sha1(readContentsAsString(fileContent));
            // 检验是否有内容相同的文件存在
            List<String>  a = plainFilenamesIn(BLOB_DIR);
            if(a != null){
                if(! a.contains(fileSha1)){
                    // 如果没有，则添加一条blob
                    addIntoBlob(fileName, join(BLOB_DIR, fileSha1));
                    // 添加至暂存区文件夹
                    addIntoStage(fileName, fileSha1, 0);
                }
            }
        }
    }
    // TODO: 5.8: 修复tree的bug 检查并修复bug
    private static String dealWithTree(){
        // 获取并校验阶段文件内容
        String fileContent = Objects.requireNonNull(plainFilenamesIn(Stages)).get(0);
        // 结合阶段路径和文件名生成临时文件
        File tempStage = join(Stages, fileContent);
        // 从临时文件中读取Stage对象
        Stage stageTemp = readObject(tempStage, Stage.class);
        // 创建tree对象
        Tree tempTree = new Tree(stageTemp.getFileName(), fileContent,"blob");
        Trees tempTrees = new Trees();
        // 如果不是空仓库则应该获取之前的仓库状态
        Commit tempCommit = readObject(join(COMMIT_DIR, getLatestCommit()), Commit.class);
        if(tempCommit.getParent() != null){
            tempTrees = readObject(join(TREE_DIR, tempCommit.getTreeSha1()), Trees.class);
        }
        tempTrees.Trees.add(tempTree);
        String treeSha1 = sha1(sha11(tempTrees));
        writeObject(join(TREE_DIR, treeSha1), tempTrees);
        return treeSha1;
    }
    public static void commit(String message) throws IOException {
        // 此时头指针已经更新但是master指针还没更新
        judgeVoidCommit(message);
        Commit tempCommit = new Commit(message,null, dealWithTree());
        System.out.println(Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).size());
        // 将commit添加到文件夹内
        if(Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).size() != 0){
            tempCommit.setParent(getLatestCommit());
        }
        String sha1 = sha1(sha11(tempCommit));
        File com = join(COMMIT_DIR, sha1);
        writeObject(com, tempCommit);
        com.createNewFile();
        // 添加完之后应该删除掉暂存区内的内容
        File readyToDelete = join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0));
        if(readObject(readyToDelete, Stage.class).getFileStatus() == 1){
            restrictedDelete(join(CWD, readObject(readyToDelete, Stage.class).getFileName()));
        }
        restrictedDelete(readyToDelete);
        // 更新指针
        updateCurrentBranchAndHEAD(sha1);
    }
    public static void rm(String fileName) throws IOException {
        //
        String latestCommit = getLatestCommit();
        String substring = latestCommit.substring(latestCommit.length() - 40);
        Commit tempCommit = readObject(join(COMMIT_DIR, substring), Commit.class);
        Trees tempTree = readObject(join(TREE_DIR, tempCommit.getTreeSha1()), Trees.class);
        System.out.println(plainFilenamesIn(Stages));
        System.out.println(Stages.length());
        if(Objects.requireNonNull(plainFilenamesIn(Stages)).size() == 0 && tempTree.Trees.stream().noneMatch(x -> x.getFileName().equals(fileName))){
            System.out.println("No reason to remove the file");
        }
        if(Stages.length() != 0){
            File tempStagePath = join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0));
            Stage tempStage = readObject(tempStagePath, Stage.class);
            if(fileName.equals(tempStage.getFileName()) && tempStage.getFileStatus() == 0){
//                restictedDelete(tempStagePath);
                tempStage.setFileStatus(1);
                writeObject(tempStagePath, tempStage);
                tempStagePath.createNewFile();
            }
        }
        Optional<String> fileContents = tempTree.Trees.stream().filter(x -> x.getFileName().equals(fileName)).map(Tree::getFileContent).findFirst();
        if(fileContents.isPresent()){
            String fileContent = fileContents.get();
            // 此时应该更新一条待删除的commit
            if(Objects.requireNonNull(plainFilenamesIn(CWD)).contains(fileName)){
                addIntoStage(fileName, fileContent, 1);
//                System.out.println("successful set the status");
            }
        }
    }
    public static void globalLog(){
        List<String> strings = plainFilenamesIn(COMMIT_DIR);
        if (strings != null && !strings.isEmpty()) {
            strings.forEach(as -> showCommit(readObject(join(COMMIT_DIR, as), Commit.class), as));
        }
    }
    public static void Log() {
        // 待完成: 合并分支时显示两个parent
        String currentBranch = readContentsAsString(join(BRANCH_DIR, currentBranchName));
        System.out.println(currentBranch);
        currentBranch  = currentBranch.substring(currentBranch.length() - 40);
        Commit commit = readObject(join(COMMIT_DIR, currentBranch), Commit.class);
        while(commit.getParent() != null){
            showCommit(commit, currentBranch);
            currentBranch = commit.getParent();
            commit = readObject(join(COMMIT_DIR, currentBranch), Commit.class);
        }
        showCommit(commit, currentBranch);
    }
    private static void showCommit(Commit commit, String commitName){
        System.out.println("===");
        System.out.println("commit " + commitName);
        System.out.println("Date " + commit.getTimestamp());
        System.out.println(commit.getMessage());
    }
    public static void find(String message){
        List<String> strings = plainFilenamesIn(COMMIT_DIR);
        Commit commit;
        boolean flag = false;
        for(String s: strings){
            commit = readObject(join(COMMIT_DIR, s), Commit.class);
            if(commit.getMessage().equals(message)){
                flag = true;
                System.out.println(s);
            }
        }
        if(!flag){
            System.out.println("Found no commit with that message.");
        }
    }
    public static void status(){
        printCaption("Branch");
        System.out.println("*" + currentBranchName);
        for(String s: Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR))){
            if(!s.equals(currentBranchName)){
                System.out.println(s);
            }
        }
        Stage stage;
        printCaption("Staged Files");
        List<String> removeStage = new ArrayList<>();
        for(String s: Objects.requireNonNull(plainFilenamesIn(Stages))){
            stage = readObject(join(Stages, s), Stage.class);
            if(stage.getFileStatus() == 0){
                System.out.println(stage.getFileName());
            }
            else{
                removeStage.add(s);
            }
        }
        printCaption("Removed Files");
        for (String s: removeStage){
            stage = readObject(join(Stages, s), Stage.class);
            if(stage.getFileStatus() == 1){
                System.out.println(stage.getFileName());
            }
        }

    }
    /* TODO:
        head指针指向当前的分支 而分支则指向最新提交. 当没有自己创建分支的时候，head指针指向的分支则默认为master √
        分支实际上只是对最新提交的引用 既有最新提交的时候 应该同步更新分支内的内容  √
    */
    public static void checkout(String... args) throws IOException {
        int length = args.length;
        System.out.println(Arrays.toString(args));
        switch (length){
            case 2:
                dealWithBranch(args[1]);
                break;
            case 3:
                dealWithFile(args[2]);
                break;
            case 4:
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
    private static Tree returnTreeFromCommit(String commitId){
        Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        Commit parentCommit = readObject(join(COMMIT_DIR, commit.getParent()), Commit.class);
        Trees tempTrees = readObject(join(TREE_DIR, commit.getTreeSha1()), Trees.class);
        Trees tempParentTrees = readObject(join(TREE_DIR, parentCommit.getTreeSha1()), Trees.class);
        List<Tree> tempTreeList = new ArrayList<>(tempTrees.Trees);
        tempTreeList.removeAll(tempParentTrees.Trees);
        return tempTreeList.get(0);
    }
    private static void dealWithBranch(String name) throws IOException {
        if(!plainFilenamesIn(BRANCH_DIR).contains(name)){
            judgeVoidCmdInCheckout(3);
        }
        String branchToCommit = readContentsAsString(join(BRANCH_DIR, name));
        // 检查当前HEAD指向的内容是否与指定分支的内容相同
        String temp = readContentsAsString(HEAD).substring(readContentsAsString(HEAD).length() - 40);
        String tempBranch = readContentsAsString(join(BRANCH_DIR, name)).substring(readContentsAsString(join(BRANCH_DIR, name)).length() - 40);
        if (temp.equals(tempBranch) && currentBranchName.equals(name)) {
            judgeVoidCmdInCheckout(4);
        }
        else {

            restrictedDelete(join(CWD, returnTreeFromCommit(temp).getFileName()));
            Tree tempTree = returnTreeFromCommit(tempBranch);
            //System.out.println("fileContent:" + tempTree.getFileContent() + "===" + "filename:" + tempTree.getFileName());
            // 将切换分支的文件拿到工作目录
            extractFromBlob(join(BLOB_DIR, tempTree.getFileContent()), join(CWD, tempTree.getFileName()));
//            writeDecompress(readContents(join(BLOB_DIR, tempTree.getFileContent())), join(CWD, tempTree.getFileName()));
            // 更新HEAD指向的分支内容为指定的分支内容,同时切换分支
            writeObject(HEAD, branchToCommit);
            restrictedDelete(Stages);
        }
        join(CURRENTBRANCH_DIR,currentBranchName).renameTo(join(CURRENTBRANCH_DIR,name));
    }
    private static Trees returnTreesFromCommit(String commitId){
        return readObject(join(TREE_DIR, readObject(join(COMMIT_DIR, commitId), Commit.class).getTreeSha1()), Trees.class);
    }
    private static String returnOnlyFileContentFromTree(String commitId,String fileName){
        Trees tempTrees = returnTreesFromCommit(commitId);
        String fileContent = null;
        Optional<String> fileContents = tempTrees.Trees.stream().filter(x -> x.getFileName().equals(fileName)).map(Tree::getFileContent).findFirst();
        if(fileContents.isPresent()){
            fileContent = fileContents.get();
            // 此时应该更新一条待删除的commit
        }
        return fileContent;
    }
    /*TODO:根据Commit 的tree 找到文件，然后通过父提交往前回溯找到tree中是否有此文件名的文件 拿到blob 最后将blob的内容写入到当前目录*/
    /**
     * 处理指定名称的文件。
     * @param name 需要处理的文件名称。
     * @throws IOException 如果读取或写入文件时发生错误。
     */
    private static void dealWithFile(String name) throws IOException {
        String CurrentCommit = readContentsAsString(HEAD);
        CurrentCommit = CurrentCommit.substring(CurrentCommit.length() - 40);
        Trees tempTrees = returnTreesFromCommit(CurrentCommit);
        if(tempTrees.Trees.stream().noneMatch(x -> x.getFileName().equals(name))){
            extractFromBlob(join(BLOB_DIR, returnOnlyFileContentFromTree(CurrentCommit, name)), join(CWD, name));
        }
        else {
            judgeVoidCmdInCheckout(1);
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
        // 检查提交ID是否存在于提交目录中
        if(!Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).contains(commitId)){
            judgeVoidCmdInCheckout(2);
        }
        // 读取指定提交对象
        Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        // 如果提交对象中的树SHA1不为空，则尝试读取对应的树对象
        if(commit.getTreeSha1() != null){
            Tree tempTree = returnTreeFromCommit(commitId);
            // 在树对象中查找指定字段名的文件

            if(tempTree.getFileName().equals(fileName)){
                // 如果找到，将文件内容解压并写入当前工作目录
//                writeDecompress(readContents(join(BLOB_DIR, tempTree.getFileContent())), join(CWD, fileName));
                extractFromBlob(join(BLOB_DIR, tempTree.getFileContent()), join(CWD, tempTree.getFileName()));
            }
            else{
                // 如果未找到指定字段名的文件，判断为无效命令
                judgeVoidCmdInCheckout(1);
            }
        }
    }
    private static void judgeVoidCmdInCheckout(int kind){
        switch (kind){
            case 1:
                System.out.println("File does not exist in that commit.");
            case 2:
                System.out.println("No commit with that id exists.");
            case 3:
                System.out.println("No such branch exists.");
            case 4:
                System.out.println("No need to checkout the current branch.");
            case 5:
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
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
        if(Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).contains(commitID)){
            throw new GitletException("No commit with that id exists.");
        }
        if(TREE_DIR.listFiles() != null){
            Tree tempTree;
            tempTree = returnTreeFromCommit(commitID); // 从文件中读取tree对象
            restrictedDelete(CWD);
            // 检出文件
            checkOutFileWithCommit(commitID, tempTree.getFileName());
            // 清空暂存区
            restrictedDelete(join(Stages, Objects.requireNonNull(plainFilenamesIn(Stages)).get(0)));
            // 移动分支头
            updateCurrentBranchAndHEAD(commitID);
        }
    }
    /*
    The split point is a latest common ancestor of the current and given branch heads: - A common ancestor is a commit to which there is a
    path (of 0 or more parent pointers) from both branch heads. - A latest common ancestor is a common ancestor that is not an ancestor of
    any other common ancestor. For example, although the leftmost commit in the diagram above is a common ancestor of master and branch,
    it is also an ancestor of the commit immediately to its right, so it is not a latest common ancestor. If the split point is the same
    commit as the given branch, then we do nothing; the merge is complete, and the operation ends with the message Given branch is an ancestor
    of the current branch. If the split point is the current branch, then the effect is to check out the given branch, and the operation ends
     after printing the message Current branch fast-forwarded. Otherwise, we continue with the steps below.
Any files that have been modified in the given branch since the split point, but not modified in the current branch since the split point
 should be changed to their versions in the given branch (checked out from the commit at the front of the given branch). These files should
  then all be automatically staged. To clarify, if a file is “modified in the given branch since the split point” this means the version of
  the file as it exists in the commit at the front of the given branch has different content from the version of the file at the split point.
   Remember: blobs are content addressable!

Any files that have been modified in the current branch but not in the given branch since the split point should stay as they are.

Any files that have been modified in both the current and given branch in the same way (i.e., both files now have the same content or
 were both removed) are left unchanged by the merge. If a file was removed from both the current and given branch, but a file of the
 same name is present in the working directory, it is left alone and continues to be absent (not tracked nor staged) in the merge.

Any files that were not present at the split point and are present only in the current branch should remain as they are.

Any files that were not present at the split point and are present only in the given branch should be checked out and staged.

Any files present at the split point, unmodified in the current branch, and absent in the given branch should be removed (and untracked).

Any files present at the split point, unmodified in the given branch, and absent in the current branch should remain absent.

Any files modified in different ways in the current and given branches are in conflict. “Modified in different ways” can mean that
the contents of both are changed and different from other, or the contents of one are changed and the other file is deleted, or
the file was absent at the split point and has different contents in the given and current branches. In this case, replace the
contents of the conflicted file with
    * */
    /*
    * 如何确定是否存在承接关系呢？
    * 维护每个分支与头部提交的对应关系
    * */
    public static void merge(String branchName) throws IOException {
        // 当前分支的commit
        String currentBranchCommit = readContentsAsString(HEAD).substring(readContentsAsString(HEAD).length() - 40);
        int flagChangeIfOut = 0;
        String givenBranchCommit = readContentsAsString(join(BRANCH_DIR, branchName));
        String commit;
        for(commit = currentBranchCommit;;commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()){
            if(commit.equals(givenBranchCommit)){
                commitMessageOfMerge(1);
                flagChangeIfOut = 1;
                break;
            }
        }
        for(commit = givenBranchCommit;;commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()){
            if(commit.equals(currentBranchCommit)){
                checkout(branchName);
                commitMessageOfMerge(2);
                flagChangeIfOut = 2;
                break;
            }
        }
        if(flagChangeIfOut != 0){
            System.out.println("s");
            findModifiedFiles(commit, branchName);
        }
    }
    private static void findModifiedFiles(String commitOfSplitPoint, String branchName) throws IOException {
        dealWithFailureCase(branchName);
        Map<String, Map<String, String>> sha1ToFileNameForGivenBranch = findFiles(readContentsAsString(join(BRANCH_DIR, branchName)), commitOfSplitPoint);
        Map<String, Map<String, String>> sha1ToFileNameForOriginalBranch = findFiles(readContentsAsString(HEAD), commitOfSplitPoint);
        //在给定分支中修改过、但在当前分支中没有修改过的文件
        findModifiedFilesInGivenBranchAfterSplitPoint(sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch);
        // 在分割点不存在且只存在于给定分支中的文件
        findFilesJustInGivenBranch(commitOfSplitPoint, sha1ToFileNameForGivenBranch);
        //任何在分割点存在、在当前分支中未被修改、但在给定分支中不存在的文件
        rmInGivenBranchAndUnModifiedInOriginalBranch(commitOfSplitPoint, sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch);
        // 找到冲突文件
        findConflictFileAndResolve(sha1ToFileNameForGivenBranch, sha1ToFileNameForOriginalBranch, branchName);

    }
    private static void dealWithFailureCase(String branchName){
        if(Stages.listFiles() != null){
            System.out.println("You have uncommitted changes");
        }
        if(branchName.equals(currentBranchName)){
            System.out.println("Cannot merge a branch with itself.");
        }
        if(!plainFilenamesIn(BRANCH_DIR).contains(branchName)){
            System.out.println("A branch with that name does not exist.");
        }
        if(branchName.equals("0xffffffff")){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first");
        }
    }
    private static void findModifiedFilesInGivenBranchAfterSplitPoint(Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String,Map<String, String>> sha1ToFileNameForOriginalBranch) throws IOException {
        Set<String> givenFileContent = getSet(sha1ToFileNameForGivenBranch);
        givenFileContent.removeAll(getSet(sha1ToFileNameForOriginalBranch));
        for(Map.Entry<String,Map<String,String>> entry: sha1ToFileNameForGivenBranch.entrySet()){
            if(entry.getValue().keySet().stream().anyMatch(givenFileContent::contains)){
                checkout(entry.getKey(), entry.getValue().keySet().iterator().next());
                // 签出在给定分支中被修改过的文件
             }
        }
    }
    private static void findFilesJustInGivenBranch(String commitOfSplitPoint, Map<String, Map<String, String>> sha1ToFileNameForGivenBranch) throws IOException {
        Set<String> givenFileContent = new HashSet<>();
        givenFileContent.add(readContentsAsString(join(TREE_DIR,readObject(join(COMMIT_DIR, commitOfSplitPoint), Commit.class).getTreeSha1())));
        String temp;
        for(Map.Entry<String,Map<String,String>> entry: sha1ToFileNameForGivenBranch.entrySet()){
            temp = entry.getValue().keySet().iterator().next();
            if(!givenFileContent.contains(temp)){
                // 签出文件
                checkout(entry.getKey(), temp);
                // 添加至暂存区
                addIntoStage(entry.getValue().get(temp), temp, 0);
            }
        }
    }
    private static void rmInGivenBranchAndUnModifiedInOriginalBranch(String commitOfSplitPoint, Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String,Map<String, String>> sha1ToFileNameForOriginalBranch){
         String splitTreeFileContent = returnTreeFromCommit(commitOfSplitPoint).getFileContent();
         if(!getSet(sha1ToFileNameForGivenBranch).contains(splitTreeFileContent) && getSet(sha1ToFileNameForOriginalBranch).contains(splitTreeFileContent)){
             restrictedDelete(join(Stages, splitTreeFileContent));
         }
    }
    private static void findConflictFileAndResolve(Map<String, Map<String, String>> sha1ToFileNameForGivenBranch, Map<String,Map<String, String>> sha1ToFileNameForOriginalBranch, String givenBranchName) throws IOException {
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
        if(!duplicatesMap.isEmpty()){
            System.out.println("Encountered a merge conflict");
            for(Map.Entry<String, List<String>> entry: duplicatesMap.entrySet()){
                if(entry.getValue().size() == 2){
                    if(getSet(sha1ToFileNameForOriginalBranch).contains(entry.getValue().get(0))){
                        if (unTrackedFiles.contains(entry.getKey())){
                            dealWithFile("0xffffffff");
                        }
                        resolveConflict(entry.getKey(), entry.getValue().get(0), entry.getValue().get(1),currentBranchName, givenBranchName);
                    }
                }
            }
        }
    }
    private static void resolveConflict(String fileName, String fileCurrentContent, String fileGivenContent, String currentBranchName, String givenBranchName){
        try(PrintWriter out = new PrintWriter(join(Stages, fileCurrentContent))) {
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
    private static Set<String> getSet(Map<String, Map<String, String>> a){
        return a.values().stream().flatMap(innerMap -> innerMap.keySet().stream()).collect(Collectors.toSet());
    }
    private static Map<String, Map<String,String>> findFiles(String start, String commitOfSplitPoint){
        Map<String,Map<String, String>> re  = new HashMap<>();
        String commit;
        Tree tempTree;
        Map<String,String> a = new HashMap<>();
        for(commit = start; !Objects.equals(commit, commitOfSplitPoint); commit = readObject(join(COMMIT_DIR, commit), Commit.class).getParent()){
            tempTree = returnTreeFromCommit(start);
            a.put(tempTree.getFileName(), tempTree.getFileContent());
            re.put(commit,a);
            a.clear();
        }
        return re;
    }

    private static void commitMessageOfMerge(int flag){
        switch (flag){
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
        }
    }
    /**
     * 将指定文件的内容压缩后存储到Blob文件中。
     * @param fileName 需要被压缩并存储的文件名称。
     * @param path 压缩后Blob文件的保存路径。
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
        try(FileOutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(byteArray);
        }   catch (IOException e){
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
    }
    /**
     * 更新当前分支和HEAD指向的提交SHA1值。
     *
     * @param commitSha1 新的提交SHA1哈希值
     * @throws IOException 如果在更新文件或创建文件时发生错误
     */
    private static void updateCurrentBranchAndHEAD(String commitSha1) throws IOException {
        System.out.println("Updating branch " + currentBranchName + " to commit " + commitSha1);
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

    private static void judgeVoidCommit(String message){
        String tempHint = "No changes added to the commit.";
        int flag = 0;
        if(Objects.equals(message, "") || Stages.listFiles() == null) {
            if(Objects.equals(message, "")){
                flag = 1;
            }
            else {
                flag = 2;
            }
        }
        if(flag != 0){
            if(flag == 1){
                tempHint = "Please enter a commit message";
            }
            throw new GitletException(tempHint);
        }
    }
    /**
     * 处理树结构。
     * 该方法首先从指定的阶段（Stages）中获取第一个文件名，然后将该文件名与阶段路径结合生成一个临时文件。
     * 接着，从该临时文件中读取对象并创建一个tree实例。最后，返回该tree实例的SHA1值。
     *
     * @return String 返回tree实例的SHA1值。
     */

    private static void printCaption(String message){System.out.println("===" + message + "===");}
    private static void judgeIfBranchExistOrCurrent(String branchName){
        if(Objects.equals(branchName, currentBranchName)){
            throw new GitletException("Cannot remove the current branch.");
        }
        if(!Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).contains(branchName)){
            throw new GitletException("A branch with that name does not exist.");
        }
    }
    private static String getLatestCommit(){
        String latestCommit = readContentsAsString(HEAD);
        System.out.println(latestCommit);
        return latestCommit.substring(latestCommit.length() - 40);
    }

    private static byte[] sha11(Object a){
        if(a == null){
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
    public static void test(){
        System.out.println("==========================");
        for(String s: plainFilenamesIn(BRANCH_DIR)){
            System.out.println("branchName:" + s + " " + "branchContent:" + readContentsAsString(join(BRANCH_DIR, s)));
        }
        System.out.println("HEAD:" + readContentsAsString(HEAD));
        System.out.println("==========================");
    }
}
