package deque;

import org.apache.lucene.util.RamUsageEstimator;

public class ArrayDeque<Link> implements Deque<Link>{

    Link[] items;
    int size = 0;
    int startIndex = -1;
    int endIndex = -1;
    boolean JustAddFirst = true;
    /**
     常规逻辑: 尾部从数组头部插入 头部则从尾部逆向插入 即:
     L1 L2 L3 F4 F3 F2 F1
     正确顺序是:F4->F1->L1->L3
     当扩容时 只针对尾部扩容 即将后面的半段往后偏移 此时要更新头指针坐标
     极端情况下 比如只从头部插入或者只从尾部插入
     1.若是只从头部插入 最后应该进行的扩容操作只执行第二阶段
     2.若是只从尾部插入 最后应该进行的扩容操作只执行第一阶段

     */
    public ArrayDeque(){
        items =(Link[]) new Object[8];
    }
//    private void resize(int cap){
//        Link[] a = (Link[]) new Object[cap];
//        System.arraycopy(items, 0, a, 0, size);
//        items = a;
//    }
    private void recover(boolean Flag){
        boolean changed = false;
        boolean flag = false;
/*        arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);
    将数组src从srcPos开始，复制到dest从destPos开始，一共length个
    当内存
  */
        int t = Flag ? (size * 2) : size + 1;
        Link[] a = (Link[]) new Object[t];
        //
        if(startIndex < endIndex && startIndex != 0){
            System.arraycopy(items, startIndex, a, 0, endIndex - startIndex + 1);
            flag = true;
        }
        else {
            // endIndex + 1 == size的情况 就是只从尾端插入 这时候 则应该只执行第二阶段
            if(!Flag || endIndex + 1 == startIndex || endIndex + 1 == size){
                endIndex = endIndex + 1;
            }
            if(endIndex == size && JustAddFirst){
                System.arraycopy(items, 0, a, endIndex, endIndex);
                startIndex = endIndex;
                endIndex = a.length - 1;
                items = a;
                return;
            }
            else{
                System.arraycopy(items, 0, a, 0, endIndex);
            }
            endIndex --;
        }
//        if(endIndex >= a.length){
//            endIndex = endIndex - 1;
//        }
        // flag界定是否
        // 将尾部插入的部分移入到新数组中 界定一个状态 减小之后应该更
        if(startIndex != 0 && startIndex > endIndex){
            System.arraycopy(items, startIndex, a, a.length - (items.length - startIndex), items.length - startIndex);
            flag = true;
        }
        if(flag){
            if(Flag){
                startIndex = size + startIndex;
            }
            else{
                if(startIndex > endIndex){
                    startIndex = a.length - (items.length - startIndex) > 0 ? a.length - (items.length - startIndex) : startIndex;
                }
                else {
                    int temp = endIndex - startIndex;
                    startIndex = size - (endIndex - startIndex) - 1;
                    endIndex = temp;
                }
//                changed = true;
            }
        }
        else {
            if(startIndex != 0){
                startIndex = size + startIndex;
            }
        }
//        if(flag){
//            if(Flag){
//                startIndex = size;
//            }
//            else{
//                startIndex = 0;
//                endIndex = 0;
//            }
//            /*如果尾部有数据才将尾部移动至新数组 如果无 则应该及时更新flag*/
//        }
        items = a;
    }
    @Override
    public void addFirst(Link x) {
        if(startIndex - 1 == endIndex || startIndex == 0){
            recover(true);
        }
        if(startIndex == -1){
            startIndex = items.length - 1;
        }
        else {
            if(endIndex == -1){
                endIndex = items.length - 1;
            }
            startIndex = startIndex - 1;
        }

        items[startIndex] = x;
        size = size + 1;
    }

    /**
     */
    @Override
    public void addLast(Link x) {
        JustAddFirst = false;
//        System.out.println(startIndex + "wocao" + endIndex);
        if(endIndex + 1 == startIndex || endIndex >= items.length){
            recover(true);
        }
        if(endIndex == -1){
            if(startIndex == - 1){
                startIndex = 0;
            }
            endIndex = 0;
        }
        else if(endIndex == items.length - 1){
            recover(true);
            endIndex = endIndex + 1;
//            endIndex = 0;
        }
        else {
            endIndex = endIndex + 1;
        }
        if(endIndex == 1 && (startIndex == items.length - 1 || startIndex == -1)){
            endIndex = 0;
        }
        items[endIndex] = x;
        size = size + 1;
    }

    /**
     */
    @Override
    public Link removeFirst() {
        check();
        Link x = null;
        if(!isEmpty()){
            x = getFirst();
            items[startIndex] = null;
            startIndex = startIndex + 1;
            size = size - 1;
        }
        return x;
    }
    /**
     */
    @Override
    public Link removeLast() {
        check();
        Link x = null;
        if(!isEmpty()){
            x = getLast();
            items[endIndex] = null;
            endIndex = endIndex > 0 ? (endIndex - 1) : endIndex;
            size = size - 1;
        }
        else {
            startIndex = 0;
            endIndex = 0;
//            System.gc();
//            items = null;
        }
        return x;
    }
    /**/
    private void check(){
        if(items.length > 4 * size && size > 2){
            recover(false);
        }
    }

    /**
     */
    @Override
    public Link getFirst() {
        return items[startIndex];
   }

    /**
     */
    @Override
    public Link getLast() {
        return items[endIndex];
    }

    /**
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     */
    @Override
    public int size() {
        return size;
    }

    /**
     *
     */
    @Override
    public void printDeque() {
        int i;
        for(i = startIndex; i <= items.length && items[i] != null; i = i + 1){
            System.out.print(items[i] + " ");
        }
        i = 0;
//        System.out.print(" ");
        for(i = 0; i <= endIndex; i = i + 1){
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    /**
     */
    @Override
    public Link get(int index) {
        return items[index];
    }

    public static void main(String[] args) {
        //java.util.Deque<Integer> as = new java.util.ArrayDeque<>();
        /*        头尾指针一开始都为0.当插入的时候，头指针为items.length - 1 每次都--（往前排列）尾指针则与数组顺序一致。
                  尾指针则是++ 到时候遍历则从 头指针开始到数组末尾 然后再回到开头
        * */
        ArrayDeque<Integer> a = new ArrayDeque<>();
        long start = System.currentTimeMillis();
        for(int i = 0; i < 25; ++ i){
            a.addFirst(i);
//            a.addLast(i);
        }
//        for(int i = 0; i < 25; ++ i){
//            a.addLast(i);
//            a.addFirst(i);
//        }
        System.out.println(RamUsageEstimator.humanSizeOf(a));
        long start1 = System.currentTimeMillis();
        for(int i = 0; i < 50; ++ i){
//            System.out.println(i + "s");
            System.out.println(a.removeLast());
//            System.out.println(a.removeFirst());
//            System.out.println(a.removeLast() + "..." + a.removeFirst());
//            as.removeFirst();
        }
//        System.out.println(RamUsageEstimator.humanSizeOf(a));
//        a.printDeque();

//此处写要测试的代码
        long end = System.currentTimeMillis();
        System.out.println("共耗时"+(start1 - start)+"毫秒");
        System.out.println("共耗时"+(end-start1)+"毫秒");
        System.out.println(a.size);

    }
}
