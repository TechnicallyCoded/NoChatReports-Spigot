package com.tcoded.nochatreports.nms.types;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MIMList <T> implements List<T> {
    
    private final List<T> parent;

    private final Function<T, T> addFunction;
    private final Consumer<T> removeFunction;
    
    public MIMList(List<T> parent, Function<T, T> addFunction, Consumer<T> removeFunction) {
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
    public boolean addAll(int index, Collection<? extends T> c) {
        c.forEach(addFunction::apply);
        return parent.addAll(index, c);
    }

    @Override
    public void clear() {
        List<T> copy = new ArrayList<>(parent);
        parent.clear();
        copy.forEach(removeFunction);
    }

    @Override
    public T get(int index) {
        return parent.get(index);
    }

    @Override
    public T set(int index, T element) {
        return parent.set(index, addFunction.apply(element));
    }

    @Override
    public void add(int index, T element) {
        parent.add(index, addFunction.apply(element));
    }

    @Override
    public T remove(int index) {
        T removed = parent.remove(index);
        removeFunction.accept(removed);
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        return parent.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return parent.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return parent.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return parent.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return parent.subList(fromIndex, toIndex);
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
