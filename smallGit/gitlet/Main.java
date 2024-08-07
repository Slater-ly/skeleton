package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static gitlet.GitletException.*;
import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.plainFilenamesIn;

/**
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     * 我应该如何界定是否在暂存区内:add了但还没commit就是暂存区 add的标准就是 文件内容不同(即sha1编码不同)
     * 清空暂存区的标准是什么:commit了所有暂存区内的文件之后
     */
    public static void main(String[] args){
        // TODO: what if args is empty?
        if (args == null) {
            throw new GitletException("Please enter a command.");
        } else {
            if(args.length == 0) {
                System.out.println("Please enter a command.");
                System.exit(0);
            }
            else {
                String firstArg = args[0];
                switch (firstArg) {
                    case "init":
                        // TODO: handle the `init` command
                        try {
                            Repository.init();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "add":
                        try {
                            Repository.add(args[1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // TODO: handle the `add [filename]` command
                        break;
                    // TODO: FILL THE REST IN
                    case "commit":
                        if(args.length == 1){
                            System.out.println("Please enter a commit message.");
                        }
                        else{
                            Repository.commit(args[1], false);
                        }
                        break;
                    case "rm":
                        Repository.rm(args[1]);
                        break;
                    case "log":
                        Repository.log();
                        break;
                    case "global-log":
                        Repository.globalLog();
                        break;
                    case "find":
                        Repository.find(args[1]);
                        break;
                    case "status":
                        Repository.status();
                        break;
                    case "checkout":
                        try {
                            Repository.checkout(args);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "branch":
                        Repository.branch(args[1]);
                        break;
                    case "rm-branch":
                        Repository.rmBranch(args[1]);
                        break;
                    case "reset":
                        try {
                            Repository.reset(args[1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "merge":
                        try {
                            Repository.merge(args[1]);
                        }
                        catch (IOException e){
                            throw new RuntimeException(e);
                        }
                        break;
                    default:
                        System.out.println("No command with that name exists.");
                }
            }
        }
    }
}

