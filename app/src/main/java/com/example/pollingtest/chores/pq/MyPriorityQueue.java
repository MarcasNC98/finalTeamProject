package com.example.pollingtest.chores.pq;

import com.example.pollingtest.chores.ChoreWithID;

import java.util.ArrayList;

public class MyPriorityQueue implements PQInterface{
    private final ArrayList<PQElement> thePQueue;


    public MyPriorityQueue() {
        this.thePQueue = new ArrayList<>();
    }

    private int findInsertPosition(int newkey) {
        boolean found = false;
        PQElement elem;
        int position = 0;

        while (position < thePQueue.size() && !found) {
            elem = thePQueue.get(position);

            if (elem.getKey() > newkey) {
                position = position + 1;
            } else {
                found = true;
            }
        }
        return position;
    }

    @Override
    public void enqueue(int key, Object element) {
        int index;

        PQElement elem = new PQElement(key, (ChoreWithID) element);

        index = findInsertPosition(key);

        if (index == size()) {
            thePQueue.add(elem);
        } else {
            thePQueue.add(index, elem);
        }
    }

    @Override
    public int size() {
        return thePQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return thePQueue.isEmpty();
    }

    @Override
    public Object dequeue() {
        return thePQueue.remove(0);
    }

    @Override
    public Object getTop() {
        return thePQueue.get(0);
    }

    @Override
    public ArrayList<ChoreWithID> getChores() {//Returns an array list of organised chores by their priority
        ArrayList<ChoreWithID> chores = new ArrayList<>();
        for (int i = 0; i<thePQueue.size();i++) {
            chores.add(thePQueue.get(i).getChore());
        }
        return chores;
    }


}
