package util.other;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * This class covers a List and makes it thread-save and iterator safe.
 *
 * Essentially, instead of throwing a ConcurrentModificationException when it's modified during iteration,
 * instead it waits until the iterating is finished, then makes the change and returns.
 * In other words, the thread modifying the List *waits* until the map is done being iterated.
 *
 * IMPORTANT: IF YOU ARE ITERATING THIS LIST, MAKE SURE TO CALL stopIterating() WHEN YOU ARE DONE ITERATING.
 * the forEach() method is automatically accounted for.
 */
public class IteratorSafeList<E> implements List<E> {
    private final List<E> list;
    private boolean iterating;
    private boolean modifying;

    public IteratorSafeList(List<E> list) {
        this.list = list;
    }

    public void stopIterating(){
        iterating = false;
    }
    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        this.iterating = true;
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.iterating = true;
        list.forEach(action);
        this.iterating = false;
    }


    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return list.toArray(generator);
    }

    @Override
    public boolean add(E e) {
        waitIterating();
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        waitIterating();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        waitIterating();
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        waitIterating();
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        waitIterating();
        return list.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        waitIterating();
        return list.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        waitIterating();
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        waitIterating();
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        waitIterating();
        list.sort(c);
    }

    @Override
    public void clear() {
        waitIterating();
        list.clear();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        waitIterating();
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        waitIterating();
        list.add(index, element);
    }

    @Override
    public E remove(int index) {
        waitIterating();
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        this.iterating = true;
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        this.iterating = true;
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        iterating = true;
        return list.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

    private void waitIterating(){
        while(iterating){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }
}
