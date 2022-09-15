package adt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ArrayList<T> implements ListI<T>{

    private T[] array;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 1;

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int initialCapacity) {
        numberOfEntries = 0;
        array = (T[]) new Object[initialCapacity];
    }

    public T[] array(){
        return array;
    }
    private void expandArray(){
        array = Arrays.copyOf(array,array.length + 1);
    }

    private void shrinkArray(){
        array = Arrays.copyOf(array, array.length - 1);
    }

    @Override
    public boolean add(T newEntry) {

        if(isFull())
            expandArray();

        array[numberOfEntries] = newEntry;
        numberOfEntries++;
        return true;
    }

    @Override
    public boolean add(int index, T newEntry) {
        boolean isSuccessful = true;

        if ((index >= 0) && (index <= numberOfEntries)) {
            makeRoom(index);
            array[index] = newEntry;
            numberOfEntries++;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private void makeRoom(int index) {
        int newIndex = index - 1;
        int lastIndex = numberOfEntries - 1;

        for (int i = lastIndex; i >= newIndex; i--) {
            array[i + 1] = array[i];
        }
    }

    @Override
    public T remove(int index) {
        T result = null;

        if ((index >= 0) && (index < numberOfEntries)) {
            result = array[index];

            if (index < numberOfEntries - 1) {
                removeGap(index);
                shrinkArray();
            }
            else{
                shrinkArray();
            }


            numberOfEntries--;
        }

        return result;
    }

    private void removeGap(int index) {
        // move each entry to next lower position starting at entry after the
        // one removed and continuing until end of array
        int removedIndex = index;
        int lastIndex = numberOfEntries - 1;

        for (int i = removedIndex; i < lastIndex; i++) {
            array[i] = array[i + 1];
        }
    }

    @Override
    public void clear() {
        numberOfEntries = 0;
    }

    @Override
    public boolean replace(int index, T newEntry) {
        boolean isSuccessful = true;

        if ((index >= 0) && (index < numberOfEntries)) {
            array[index] = newEntry;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    @Override
    public T getEntry(int index) {
        T result = null;

        if ((index >= 0) && (index < numberOfEntries)) {
            result = array[index];
        }

        return result;
    }

    @Override
    public boolean contains(T anEntry) {
        return Arrays.stream(array).filter(e -> e.equals(anEntry)).findFirst().isPresent();
    }

    @Override
    public T search(T anEntry) {
        Optional<T> result = Arrays.stream(array).filter(e -> e.equals(anEntry)).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return numberOfEntries == array.length;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {
        Objects.requireNonNull(action);
        for (T t : array) {
            action.accept(t);
        }
    }

    @Override
    public Stream<T> stream(){
        return Arrays.stream(array);
    }


    private class ArrayListIterator implements Iterator<T>{

        int currIndex;

        ArrayListIterator(){
            currIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return currIndex < numberOfEntries;
        }

        @Override
        public T next() {
            return array[currIndex++];
        }
    }
}
