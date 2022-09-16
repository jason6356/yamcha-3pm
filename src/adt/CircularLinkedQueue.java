package adt;

import java.util.Iterator;

public class CircularLinkedQueue<T> implements QueueI<T> {

    private Node front;
    private Node rear;

    private int size;

    public CircularLinkedQueue(){
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    @Override
    public void enqueue(T entry) {
        Node temp = new Node();
        temp.setData(entry);
        if(front == null) {
            front = temp;
            rear = front;
        }
        else
            rear.next = temp;

        rear = temp;
        temp.next = front;
        size++;
    }

    @Override
    public T dequeue() {
        if(isEmpty()){
            return ((T) "Queue is Empty");
        }

        T result;
        if(front == rear){
            result = front.getData();
            front = null;
            rear = null;
        }
        else{
            Node temp = front;
            result = temp.getData();
            front = front.next;
            rear.next = front;
        }
        size--;
        return result;
    }

    @Override
    public T getFront() {
        if(isEmpty()){
            return ((T) "Queue is Empty");
        }

        return front.getData();
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }

    @Override
    public int size() {
        return 0;
    }

    public void setFront(Node front) {
        this.front = front;
    }

    public Node getRear() {
        return rear;
    }

    public void setRear(Node rear) {
        this.rear = rear;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public Iterator iterator() {
        return new QueueIterator();
    }

    private class Node {

        T data;
        Node next;

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

        public Node(){
            this.data = null;
            this.next = null;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    private class QueueIterator implements Iterator<T>{

        int curr;
        Node currNode;

        QueueIterator(){
            this.curr = 0;
            this.currNode = front;
        }
        @Override
        public boolean hasNext() {
            return curr < size;
        }

        @Override
        public T next() {

            T result = currNode.getData();

            currNode = currNode.next;
            curr++;
            return result;
        }

    }

}
