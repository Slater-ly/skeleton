package deque;

public interface Deque<Link>  {
    void addFirst(Link x);

    void addLast(Link x);

    Link removeFirst();

    Link removeLast();

    Link getFirst();

    Link getLast();

    boolean isEmpty();

    int size();

    void printDeque();

    Link get(int index);
}
