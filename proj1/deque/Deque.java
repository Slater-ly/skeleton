package deque;

public interface Deque<Link> {
    public void addFirst(Link x);

    public void addLast(Link x);

    public Link removeFirst();

    public Link removeLast();

    public Link getFirst();

    public Link getLast();

    public boolean isEmpty();

    public int size();

    public void printDeque();

    public Link get(int index);
}
