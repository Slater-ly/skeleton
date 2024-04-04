package deque;

import java.util.*;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    /**
     *
     */
    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<T> {
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
        public T next() {
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
        private final T item;
        private StuffNode next, last;

        private StuffNode(T f, StuffNode l, StuffNode n) {
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

    public LinkedListDeque(T x) {
        sentinel = new StuffNode(x, null, null);
        initialization();
        size = 1;
    }

    public void addFirst(T x) {
        StuffNode newNode = new StuffNode(x, null, null);
        StuffNode front = sentinel.next;
        sentinel.next = newNode;
        newNode.last = sentinel;
        newNode.next = front;
        front.last = newNode;
        size = size + 1;
    }

    public void addLast(T x) {
        StuffNode newNode = new StuffNode(x, null, null);
        StuffNode last = sentinel.last;
        sentinel.last = newNode;
        newNode.next = sentinel;
        newNode.last = last;
        last.next = newNode;
        size = size + 1;
    }

    public T removeFirst() {
        T x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        if (sentinel.next != sentinel) {
            sentinel.next.last = sentinel;
        } else {
            sentinel.last = sentinel;
        }
        size = size > 0 ? (size - 1) : 0;
        return x;
    }


    public T removeLast() {
        T x = sentinel.last.item;
        sentinel.last = sentinel.last.last;
        sentinel.last.last.next = sentinel;
        if (sentinel.next != null) {
            sentinel.next.last = sentinel;
        }
        size = size > 0 ? (size - 1) : 0;
        return x;
    }

    public T getFirst() {
        return sentinel.next.item;
    }

    public T getLast() {
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
            //System.out.println(p.next.next.next);
        }
        System.out.println();
    }

    public T get(int index) {
        int countT = 0;
        T result = null;
        StuffNode p = null;
        p = sentinel.next;
        for (; p.item != null; p = p.next) {
            if (countT == index) {
                result = p.item;
                break;
            }
            countT += 1;
        }
        return result;
    }

    public T getRecursive(int index) {
        return getRecursiveHelper(sentinel.next, index);
    }

    private T getRecursiveHelper(StuffNode p, int index) {
        if (p == null || p.item == null) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(p.next, index - 1);
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> a = new LinkedListDeque<>();
//        a.addLast(0);
//        a.addLast(1);
//        System.out.println(a.removeFirst());
//        a.addLast(4);
//        a.addLast(5);
//        System.out.println(a.removeFirst());
//        a.printDeque();
//
//
//
//        a.addFirst(0);
//        a.addFirst(1);
//        System.out.println(a.removeLast());
//        a.printDeque();
//        a.addFirst(4);
//        a.addFirst(5);
//        a.printDeque();
//        System.out.println(a.removeLast());
//
//
//
//        a.printDeque();
        for (int i = 0; i < 50; i++) {
            a.addLast(i);
//            a.addLast(i);
        }

        for (int i = 0; i < 50; i++) {
//            System.out.println(a.getRecursive(i));
            System.out.println(a.get(i));
//            a.addLast(i);
        }
        a.printDeque();
//        System.out.println("------------------");
//        Iterator<Integer> it = a.iterator();
//        while (it.hasNext()){
//            System.out.println(it.next());
//        }
    }
}
