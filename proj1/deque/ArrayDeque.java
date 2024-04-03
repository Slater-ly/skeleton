package deque;

import java.util.Iterator;

public class ArrayDeque<Link> implements Deque<Link>, Iterable<Link> {
    Link[] items;
    int size = 0;
    int startIndex = -1;
    int endIndex = -1;
    boolean justAddFirst = true;

    /**
     * @return
     */
    @Override
    public Iterator<Link> iterator() {
        return new myIterator();
    }

    private class myIterator implements Iterator<Link> {
        private int count = 0;
        private int cou = 0;

        /**
         * @return
         */
        @Override
        public boolean hasNext() {
            count = count + 1;
            return !(size >= count);
        }

        /**
         * @return
         */
        @Override
        public Link next() {
            Link x;
            if (cou <= items.length - startIndex) {
                x = items[startIndex + cou];
            } else {
                x = items[cou - (items.length - startIndex)];
            }
            cou = cou + 1;
            return x;
        }
    }

    public ArrayDeque() {
        items = (Link[]) new Object[8];
    }

    private void recover(boolean FLAG) {
        boolean flag = false;

        int t = FLAG ? (size * 2) : size + 1;
        Link[] a = (Link[]) new Object[t];
        //
        if (startIndex < endIndex && startIndex != 0) {
            System.arraycopy(items, startIndex, a, 0, endIndex - startIndex + 1);
            flag = true;
        } else {
            if (!FLAG || endIndex + 1 == startIndex || endIndex + 1 == size) {
                endIndex = endIndex + 1;
            }
            if (endIndex == size && justAddFirst) {
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
            int temp = items.length - startIndex;
            System.arraycopy(items, startIndex, a, a.length - temp, temp);
            flag = true;
        }
        if (flag) {
            if (FLAG) {
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
        justAddFirst = false;
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
    public Link getFirst() {
        return items[startIndex];
    }

    /**
     *
     */
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
}
