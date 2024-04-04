package deque;

public interface Deque<T>  {
    void addFirst(T x);

    void addLast(T x);

    T removeFirst();

    T removeLast();

    T getFirst();

    T getLast();

    boolean isEmpty();

    int size();

    void printDeque();

    T get(int index);
}
