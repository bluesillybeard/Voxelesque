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
        waitModifying();
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.iterating = true;
        waitModifying();
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
        modifying = true;
        waitIterating();
        list.add(e);
        modifying = false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        modifying = true;
        waitIterating();
        boolean out = list.remove(o);
        modifying = false;
        return out;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        modifying = true;
        waitIterating();
        boolean out = list.addAll(c);
        modifying = false;
        return out;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        modifying = true;
        waitIterating();
        boolean out = list.addAll(index, c);
        modifying = false;
        return out;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        modifying = true;
        waitIterating();
        boolean out = list.removeAll(c);
        modifying = false;
        return out;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        modifying = true;
        waitIterating();
        boolean out = list.removeIf(filter);
        modifying = false;
        return out;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        modifying = true;
        waitIterating();
        boolean out = list.retainAll(c);
        modifying = false;
        return out;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        modifying = true;
        waitIterating();
        list.replaceAll(operator);
        modifying = false;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        modifying = true;
        waitIterating();
        list.sort(c);
        modifying = false;
    }

    @Override
    public void clear() {
        modifying = true;
        waitIterating();
        list.clear();
        modifying = false;
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        modifying = true;
        waitIterating();
        E out = list.set(index, element);
        modifying = false;
        return out;
    }

    @Override
    public void add(int index, E element) {
        modifying = true;
        waitIterating();
        list.add(index, element);
        modifying = false;
    }

    @Override
    public E remove(int index) {
        modifying = true;
        waitIterating();
        E out = list.remove(index);
        modifying = false;
        return out;
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
        waitModifying();
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        this.iterating = true;
        waitModifying();
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        iterating = true;
        waitModifying();
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

    private void waitModifying(){
        while(modifying){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }
}
