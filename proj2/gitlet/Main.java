package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static gitlet.GitletException.*;
import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.plainFilenamesIn;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     我应该如何界定是否在暂存区内:add了但还没commit就是暂存区 add的标准就是 文件内容不同(即sha1编码不同)
     清空暂存区的标准是什么:commit了所有暂存区内的文件之后
     怎样定义暂存区:新建一个暂存区类 改变暂存区的状态
     */
    public static void main(String[] args) throws GitletException, IOException {
        // TODO: what if args is empty?
        if (args == null) {
            throw new GitletException("Please enter a command.");
        } else {
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    // TODO: handle the `init` command
                    Repository.init();
                    break;
                case "add":
                    Repository.add(args[1]);

                    // TODO: handle the `add [filename]` command
                    break;
                // TODO: FILL THE REST IN
                case "commit":
                    Repository.commit(args[1]);
                    break;
                case "rm":
                    Repository.rm(args[1]);
                    break;
                case "log":
                    Repository.Log();
                    break;
                case "global-log":
                    Repository.globalLog();
                case "find":
                    Repository.find(args[1]);
                    break;
                case "status":
                    Repository.status();
                    break;
                case "checkout":
                    Repository.checkout(args);
                    break;
                case "branch":
                    Repository.branch(args[1]);
                    break;
                case "rm-branch":
                    Repository.rmBranch(args[1]);
                    break;
                case "reset":
                    Repository.reset(args[1]);
                    break;
                 case "merge":
                     Repository.merge(args[1]);
                     break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
            Repository.test();
        }
    }

}

