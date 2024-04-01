package deque;

import org.apache.lucene.util.RamUsageEstimator;

public class ArrayDeque<Link> implements Deque<Link> {

    Link[] items;
    int size = 0;
    int startIndex = -1;
    int endIndex = -1;
    boolean JustAddFirst = true;

    public ArrayDeque() {
        items = (Link[]) new Object[8];
    }

    private void recover(boolean Flag) {
        boolean flag = false;

        int t = Flag ? (size * 2) : size + 1;
        Link[] a = (Link[]) new Object[t];
        //
        if (startIndex < endIndex && startIndex != 0) {
            System.arraycopy(items, startIndex, a, 0, endIndex - startIndex + 1);
            flag = true;
        } else {
            if (!Flag || endIndex + 1 == startIndex || endIndex + 1 == size) {
                endIndex = endIndex + 1;
            }
            if (endIndex == size && JustAddFirst) {
                System.arraycopy(items, 0, a, endIndex, endIndex);
                startIndex = endIndex;
                endIndex = a.length - 1;
                items = a;
                return;
            } else {
                System.arraycopy(items, 0, a, 0, endIndex);
            }
            endIndex = endIndex - 1;
        }
        if (startIndex != 0 && startIndex > endIndex) {
            System.arraycopy(items, startIndex, a, a.length - (items.length - startIndex), items.length - startIndex);
            flag = true;
        }
        if (flag) {
            if (Flag) {
                startIndex = size + startIndex;
            } else {
                if (startIndex > endIndex) {
                    if (a.length - (items.length - startIndex) > 0) {
                        startIndex = a.length - (items.length - startIndex);
                    }
                } else {
                    int temp = endIndex - startIndex;
                    startIndex = size - (endIndex - startIndex) - 1;
                    endIndex = temp;
                }
            }
        } else {
            if (startIndex != 0) {
                startIndex = size + startIndex;
            }
        }

        items = a;
    }

    @Override
    public void addFirst(Link x) {
        if (startIndex - 1 == endIndex || startIndex == 0) {
            recover(true);
        }
        if (startIndex == -1) {
            startIndex = items.length - 1;
        } else {
            if (endIndex == -1) {
                endIndex = items.length - 1;
            }
            startIndex = startIndex - 1;
        }
        items[startIndex] = x;
        size = size + 1;
    }

    /**
     *
     */
    @Override
    public void addLast(Link x) {
        JustAddFirst = false;
        if (endIndex + 1 == startIndex || endIndex >= items.length) {
            recover(true);
        }
        if (endIndex == -1) {
            if (startIndex == -1) {
                startIndex = 0;
            }
            endIndex = 0;
        } else if (endIndex == items.length - 1) {
            recover(true);
            endIndex = endIndex + 1;
        } else {
            endIndex = endIndex + 1;
        }
        if (endIndex == 1 && (startIndex == items.length - 1 || startIndex == -1)) {
            endIndex = 0;
        }
        items[endIndex] = x;
        size = size + 1;
    }

    /**
     *
     */
    @Override
    public Link removeFirst() {
        check();
        Link x = null;
        if (!isEmpty()) {
            if (startIndex == items.length && items[startIndex - 1] == null) {
                startIndex = 0;
            }
            x = getFirst();
            items[startIndex] = null;
            startIndex = startIndex + 1;
            size = size - 1;
        }
        return x;
    }

    /**
     *
     */
    @Override
    public Link removeLast() {
        check();
        Link x = null;
        if (!isEmpty()) {
            if (endIndex == 0 && items[endIndex] == null) {
                endIndex = items.length - 1;
            }
            x = getLast();
            items[endIndex] = null;
            endIndex = endIndex > 0 ? (endIndex - 1) : endIndex;
            size = size - 1;
        } else {
            startIndex = 0;
            endIndex = 0;
        }
        return x;
    }

    /**/
    private void check() {
        if (items.length > 4 * size && size > 2) {
            recover(false);
        }
    }

    /**
     *
     */
    @Override
    public Link getFirst() {
        return items[startIndex];
    }

    /**
     *
     */
    @Override
    public Link getLast() {
        return items[endIndex];
    }

    /**
     *
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *
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
        for (i = startIndex; i < items.length && items[i] != null; i = i + 1) {
            System.out.print(items[i] + " ");
        }
//        System.out.print(" ");
        if (endIndex != items.length - 1 && endIndex != size - 1) {
            for (i = 0; i <= endIndex; i = i + 1) {
                System.out.print(items[i] + " ");
            }
        }
        System.out.println();
    }

    @Override
    public Link get(int index) {
        Link x = null;
        int length = endIndex + items.length - startIndex;
        if (length > index) {
            if (startIndex == 0 && endIndex != 0) {
                x = items[index];
            } else {
                // 如果是
                if (index <= items.length - startIndex) {
                    x = items[startIndex + index];
                } else {
                    x = items[index - (items.length - startIndex)];
                }
            }
        }
        return x;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 25; ++i) {
            a.addFirst(i);
            a.addLast(i);
        }
        System.out.println(RamUsageEstimator.humanSizeOf(a));
        a.printDeque();
        for (int i = 0; i < 48; ++i) {
            System.out.println(a.removeLast());
            System.out.println(a.removeFirst());
            System.out.println(a.removeLast() + "..." + a.removeFirst());
        }
        System.out.println(a.size);

    }
}
