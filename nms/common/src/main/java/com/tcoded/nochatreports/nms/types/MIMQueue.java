package com.tcoded.nochatreports.nms.types;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MIMQueue<T> implements Queue<T> {

    private final Queue<T> parent;

    private final Function<T, T> addFunction;
    private final Consumer<T> removeFunction;

    public MIMQueue(Queue<T> parent, Function<T, T> addFunction, Consumer<T> removeFunction) {
        this.parent = parent;
        this.addFunction = addFunction == null ? t -> t : addFunction;
        this.removeFunction = removeFunction == null ? t -> {} : removeFunction;
    }
    
    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return parent.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return parent.iterator();
    }

    @Override
    public Object[] toArray() {
        return parent.toArray();
    }

    @Override
    public boolean add(T o) {
        return parent.add(addFunction.apply(o));
    }

    @Override
    public boolean offer(T t) {
        return parent.add(addFunction.apply(t));
    }

    @Override
    public T remove() {
        T removed = parent.remove();
        removeFunction.accept(removed);
        return removed;
    }

    @Override
    public T poll() {
        T removed = parent.poll();
        if (removed != null) removeFunction.accept(removed);
        return removed;
    }

    @Override
    public T element() {
        return parent.element();
    }

    @Override
    public T peek() {
        return parent.peek();
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = parent.remove(o);
        if (removed) removeFunction.accept((T) o);
        return removed;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(addFunction::apply);
        return parent.addAll(c);
    }

    @Override
    public void clear() {
        List<T> copy = new ArrayList<>(parent);
        parent.clear();
        copy.forEach(removeFunction);
    }

    @Override
    public boolean retainAll(Collection c) {
        List<T> copy = new ArrayList<>(parent);
        copy.removeAll(c);
        boolean removed = parent.retainAll(c);
        if (removed) copy.forEach(removeFunction);
        return removed;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean removed = parent.removeAll(c);
        if (removed) c.forEach(removeFunction);
        return removed;
    }

    @Override
    public boolean containsAll(Collection c) {
        return parent.containsAll(c);
    }

    @Override
    public <K> K[] toArray(K[] a) {
        return parent.toArray(a);
    }
}
