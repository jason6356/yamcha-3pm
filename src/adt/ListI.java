package adt;

import java.util.stream.Stream;

public interface ListI<T> extends Iterable{

    public boolean add(T newEntry);

    public boolean add(int index, T newEntry);

    public T remove(int index);

    public void clear();

    public boolean replace(int index, T newEntry);

    public T getEntry(int index);

    public boolean contains(T anEntry);

    public T search(T anEntry);

    public int getNumberOfEntries();

    public boolean isEmpty();

    public boolean isFull();

    public Stream<T> stream();
}
