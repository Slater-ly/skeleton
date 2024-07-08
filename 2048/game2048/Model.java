package game2048;

import java.lang.reflect.Parameter;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: zyl
 */
/*
* the sorted of the arrays.
* ↑ →
* */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    public static final int direction = 4;
    public static final int[][] directionList = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean OnSide(int num, int maxSize){
        return num  == 0  || num == maxSize - 1;
    }
    public boolean check(int c, int r){
        Tile ac = board.tile(c, r);
        return ac == null;
    }
    public boolean tilt(Side side) {
        /*make sure how many step should move */
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.s
        board.setViewingPerspective(side);
            /*
            30 31 32 33
            20 21 22 23
            10 11 12 13
            00 01 02 03
            ↑→
            *1.只要相同的两个数字中间没有其他的数字 这两个数字就可以向移动方向合并
            *3.开始时 反方向遍历 碰到null 若为连续null的第一个 则标记成目标点 且下一个非null一定会移动到这个点上
                              若非null 有目标则移动至目标 无目标则自己作为目标 然后往反方向遍历查看是否有可合并方块（中间为null的相同方块）若有则将这个点合并（同时这个点设为null）
                              如果没有则目标点下移一个(即为下一个方块可以移动到的地方)
                              对于每个非空方块，都应该往反方向遍历以寻找可合并方块。特别的，对于第一个非空方块，应该先移动至反方向首个空。

             * */
            int ct, rt, k;
            boolean etFlag = false, goalFlag = false, mergeFlag, moveFlag = true, ok = false;
            for(int c = board.size() - 1; c >= 0;c --){// 每一列需要重新设置的值ct.rt.
                ct = c;     rt = 3;
                for(int r = board.size() - 1; r >= 0; r --){
                    Tile t = board.tile(c, r);
                    if(t == null){
                        if(!etFlag){
                            etFlag = true;
                            goalFlag = true;
                            rt = r;
                        }
                    }
                    else{
                        k = r - 1;
                        if(goalFlag){// 连续0遇到第一个非0时，非0移动至第一个连续0
                            board.move(ct, rt, t);
                            goalFlag = false;
                            k = rt - 1;// 当移动至新位置的时候，应该从新位置开始往反方向遍历。
                            moveFlag = false;
                            changed = true;
                            //rt -= 1;
                        }
                        //当查询到数字的时候，往反方向遍历以查询是否有可合并的值。
                        mergeFlag = true;
                        for(int r1 = k; r1 >= 0; r1--){
                            Tile pt = board.tile(c, rt);// 当执行了一次合并的时候,如果原始方块得到了合并,再调用第二个方块时就会出错（move的机制问题,所以应该重新建）
                            Tile tt = board.tile(ct, r1);
                            if(tt != null && pt != null){
                                if(pt.value() == tt.value() && mergeFlag){// 如果中间只有空则可以合并 否则不能. eg: 2 0 2 0 -> 4 0 0 0/ 2 4 2 0不可合并
                                    if(!check(ct, rt)){// 如果目标位置有方块则应该更新目标点,如:4220 -> 4400,防止变成2200.
                                        if(moveFlag){//如果先移动了就不用更新目标点，未移动才要
                                            rt = r;
                                            moveFlag = false;
                                        }
                                    }
                                    System.out.println("t:" + t.value() + "--tt:" + tt.value());
                                    score += (tt.value() * 2);
                                    if (board.move(ct, rt, tt)){
                                        ok = true;
                                        break;//每次只能进行一次合并!
                                    }
                                    //tt.merge(ct, rt, t);
                                }
                                else{
                                    mergeFlag = false;// 出现不相等的非空方块,则意味这后续即使有等块也不能合并。
                                    if(check(c, rt) && (rt < r1)) {// 如果目标位置为空且空位可移,则直接移
                                        ok = true;
                                        board.move(c, rt, tt);
                                        break;// 不相等的时候，如果目标位置为空且不会超出上一个非空值，则可以直接移动。eg:2 0 4 8 -> 2 4 0 8
                                    }
                                    else{// 如果目标位置不为空或者目标位置不可移，则寻找能够移动到的位置
                                        int temp = rt;
                                        while (temp > r1){// eg: 0 2 0 4 -> 0 2 4 0
                                            if(check(c, temp)){
                                                board.move(c, temp, tt);
                                                ok = true;
                                                break;
                                            }
                                            temp --;
                                        }//也有不能移动的情况,eg: 0 2 4 0
                                    }
                                }
                            }
                            changed = true;
                        }
                    }
                    if(ok){
                        changed = true;
                        ok = false;
                    }
                    System.out.println("--------------------------");
                    for (int ra = board.size()- 1; ra >= 0; ra -= 1){
                        for (int ca = 0 ;ca < board.size(); ca += 1){
                            if(board.tile(ca, ra) != null){
                                System.out.print(board.tile(ca, ra).value());
                            }
                            else{
                                System.out.print("0");
                            }
                        }
                        System.out.println();
                    }
                    System.out.println("--------------------------");
                }
                etFlag = false;
                goalFlag = false;
            }
//            boolean goalFlag = false;
//            int cTemp = 0, rTemp = 0;
//            boolean emptyFlag = false, firstNoNull, changeFlag = true;
//            for (int c = board.size() - 1; c >= 0; c -= 1) {
//                if(changeFlag){// 每一列的方向值都应该更新
//                    cTemp = c;  rTemp = 3;
//                }
//                for (int r = board.size() - 1; r >= 0; r -= 1) {
//                    Tile t = board.tile(c, r);
//                    if(t == null && !emptyFlag){
//                        emptyFlag = true;
//                        goalFlag = true;
//                        cTemp = c;  rTemp = r;
//                    }
//                    if(t != null){
//                        if(goalFlag){
//                            board.move(cTemp, rTemp, t);
//                            goalFlag = false;
//                            changed = true;
//                        }
//                        firstNoNull = false;
//                        for(int z = r - 1; z >= 0; z -= 1) {
//                            Tile temp = board.tile(c, z);
//                            if (temp != null) {
//                                if (t.value() == temp.value() && !firstNoNull) {
//                                    Tile flg = board.tile(cTemp, rTemp);
//                                    if(flg != null){
//                                        cTemp = c;
//                                        rTemp = r;// 如果待移位有方块则不能移动至此位
//                                    }
//                                    System.out.println("value T is:" + t.value() + "    c:" + c + "  r:" + r);
//                                    System.out.println("value Temp is:" + temp.value() + "  c:" + c + " z:" + z);
//                                    System.out.println("________________________________");
//                                    System.out.println(c + ":" + cTemp + ":" + rTemp);
//                                    System.out.println("________________________________");
//                                    board.move(cTemp, rTemp, temp);
//                                    score += (t.value() * 2);
//                                    temp.merge(cTemp, rTemp, t);
//                                    if (rTemp > 0) {
//                                        Tile testGo1 = board.tile(cTemp, rTemp - 1);
//                                        if(testGo1 == null){
//                                            rTemp -= 1;
//                                        }
//                                        changeFlag = false;
//                                    }
//                                } else {
//                                    // 不相等的时候不可以直接移动方块!!!
//                                    firstNoNull = true;
//                                    Tile TestGo = board.tile(cTemp, rTemp);
//                                    if (TestGo == null){
//                                        if(c == cTemp){
//                                            System.out.println("readyToMove:" + cTemp + "::" + rTemp);
//                                            board.move(cTemp, rTemp, temp);
//                                        }
//                                    }
////                                    System.out.println("readyToMove:" + cTemp + "::" + rTemp);
////                                    board.move(cTemp, rTemp, temp);
//                                }
//                                changed = true;
//                            }
//                        }
//                    }
//
//                    for (int ra = board.size()- 1; ra >= 0; ra -= 1){
//                        for (int ca = 0 ;ca < board.size(); ca += 1){
//                            if(board.tile(ca, ra) != null){
//                                System.out.print(board.tile(ca, ra).value());
//                            }
//                            else{
//                                System.out.print("0");
//                            }
//                        }
//                        System.out.println();
//                    }
//                    System.out.println(c + ":" + cTemp + ":" + rTemp);
//                    System.out.println("________________________________");
//                }
//                emptyFlag = false;
//                goalFlag = false;
//            }
//        for (int c = board.size() - 1; c >= 0; c -= 1) {
//            for (int r = board.size() - 1; r >= 0; r -= 1) {
//                Tile t = board.tile(c, r);
//                if(t != null){
//                    board.move(c, 3, t);
//                    changed = true;
//                    score += 7;
//                }
//            }
//        }
        checkGameOver();
        if (changed) {
            setChanged();
        }
        board.setViewingPerspective(Side.NORTH);
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int maxSize = b.size();
        boolean flag = false;
        for (int col = 0; col < maxSize; col += 1) {
            for (int row = 0; row < maxSize; row += 1) {
                if(b.tile(col,row) == null){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int maxSize = b.size();
        boolean flag = false;
        for (int col = 0; col < maxSize; col += 1) {
            for (int row = 0; row < maxSize; row += 1) {
                if(b.tile(col,row) != null) {
                    if (b.tile(col, row).value() == MAX_PIECE) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
        // TODO: Fill in this function.
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    /*only move with all the */
    public static boolean atLeastOneMoveExists(Board b) {
        boolean flag = false;
        int maxSize = b.size();
        int tempCol, tempRow;
        if (emptySpaceExists(b)){
            flag = true;
        }
        else {
            for (int col = 0; col < maxSize; col += 1) {
                for (int row = 0; row < maxSize; row += 1) {
                    for(int compareRow = 0; compareRow < 4; compareRow += 1){
                        tempCol = col + directionList[compareRow][0];
                        tempRow = row + directionList[compareRow][1];
                        if((tempCol >= 0 && tempCol < maxSize) && (tempRow >= 0 && tempRow < maxSize)){
                            if(b.tile(tempCol,tempRow).value() == b.tile(col, row).value()){
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        // TODO: Fill in this function.
        return flag;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
