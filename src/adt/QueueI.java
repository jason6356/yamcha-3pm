package adt;

public interface QueueI<T> extends Iterable{

    void enqueue(T entry);

    T dequeue();

    T getFront();

    boolean isEmpty();

    int size();

}
