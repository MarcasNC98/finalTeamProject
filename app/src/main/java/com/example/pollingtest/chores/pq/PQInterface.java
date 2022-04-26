package com.example.pollingtest.chores.pq;

public interface PQInterface {
    public void enqueue(int key, Object element);
    public int size();
    public boolean isEmpty();
    public Object dequeue();
    public Object getTop();
    public Object getChores();
}
