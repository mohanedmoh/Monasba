package com.savvy.monasbat;

import java.util.LinkedList;
import java.util.List;

public class LIFOqueue {
    public LinkedList<Object> Firstlist = new LinkedList<Object>();
    public LinkedList<Object> Scndlist = new LinkedList<Object>();

    public LIFOqueue(List<Object> Firstlist) {
        System.out.println("Size motherfucker=" + Firstlist.size());
        for (int x = Firstlist.size() - 1; x >= 0; x--) {
            push(Firstlist.get(x));
        }
    }

    public void push(Object item) {
        Firstlist.addFirst(item);
        System.out.println("Stacked: to one " + item);
    }

    public Object pop() {
        //System.out.println("Destacked: from one " + Firstlist.getFirst());
        Scndpush(Firstlist.getFirst());
        return Firstlist.removeFirst();
    }

    public Object peek() {
        return Firstlist.getFirst();
    }

    public int size() {
        return Firstlist.size();
    }

    public boolean isEmpty() {
        return Firstlist.isEmpty();
    }

    public void Scndpush(Object item) {
        Scndlist.addFirst(item);
        System.out.println("Stacked: to two" + item);
    }

    public Object scndpop() {
        // System.out.println("Destacked: from two" + Scndlist.getFirst());
        push(Scndlist.getFirst());
        return Scndlist.removeFirst();
    }

    public Object scndpeek() {
        return Scndlist.getFirst();
    }

    public int scndsize() {
        return Scndlist.size();
    }

    public boolean scndisEmpty() {
        return Scndlist.isEmpty();
    }

}
