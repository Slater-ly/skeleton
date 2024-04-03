package deque;

import java.util.*;

public class LinkedListDeque<Link> implements Iterable<Link>, Deque<Link> {
    /**
     *
     */
    @Override
    public Iterator<Link> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Link> {
        private int idx;
        private StuffNode p = null;

        private boolean flag = true;

        @Override
        public boolean hasNext() {
            return idx < size;
        }

        /**
         *
         */
        @Override
        public Link next() {
            if (flag) {
                p = sentinel;
                flag = false;
            }
            idx += 1;
            p = p.next;
            return p.item;
        }
    }


    private class StuffNode {
        private Link item;
        private StuffNode next, last;

        private StuffNode(Link f, StuffNode l, StuffNode n) {
            item = f;
            next = n;
            last = l;
        }
    }

    private final StuffNode sentinel;
    private int size;

    private void initialization() {
        sentinel.next = sentinel;
        sentinel.last = sentinel;
    }

    public LinkedListDeque() {
        sentinel = new StuffNode(null, null, null);
        initialization();
        size = 0;
    }

    public LinkedListDeque(Link x) {
        sentinel = new StuffNode(x, null, null);
        initialization();
        size = 1;
    }

    @Override
    public String toString() {
        return "SLList{" +
                "sentinel=" + sentinel +
                ", size=" + size +
                '}';
    }

    public void addFirst(Link x) {
        StuffNode newNode = new StuffNode(x, null, null);
        StuffNode front = sentinel.next;
        sentinel.next = newNode;
        newNode.last = sentinel;
        newNode.next = front;
        front.last = newNode;
        StuffNode f = sentinel.next;
        size = size + 1;
    }

    public void addLast(Link x) {
        StuffNode newNode = new StuffNode(x, null, null);
        StuffNode last = sentinel.last;
        newNode.next = sentinel;
        sentinel.last = newNode;
        last.next = newNode;
        newNode.last = last;
        size = size + 1;
    }

    public Link removeFirst() {
        Link x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.next.last = sentinel;
        size = size > 0 ? (size - 1) : 0;
        return x;
    }

    public Link removeLast() {
        Link x = sentinel.last.item;
        sentinel.last.last.next = sentinel;
        sentinel.last = sentinel.last.last;
        size = size > 0 ? (size - 1) : 0;
        return x;
    }

    public Link getFirst() {
        return sentinel.next.item;
    }

    public Link getLast() {
        return sentinel.last.item;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int getSize() {
        return size;
    }

    public int size() {
        return getSize();
    }

    public void printDeque() {
        for (StuffNode p = sentinel.next; p.item != null; p = p.next) {
            System.out.print(p.item + " ");
            System.out.println(p.next.next.next);
        }
        System.out.println();

    }

    public Link get(int index) {
        int countT = 0;
        Link RE = null;
        StuffNode p = null;
        if (index * 2 < size) {
            p = sentinel.next;
        } else {
            p = sentinel.last;
        }

        for (; p.item != null; p = p.next) {
            if (countT == index) {
                RE = p.item;
                break;
            }
            countT += 1;
        }
        return RE;
    }

    private Link getRecursive(int index) {
        return getRecursiveHelper(sentinel.next, index);
    }

    private Link getRecursiveHelper(StuffNode p, int index) {
        if (p == null || p.item == null) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(p.next, index - 1);
    }
}
