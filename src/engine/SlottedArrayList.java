package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class is similar to ArrayList,
 * except the index of added elements is constant,
 * and new items will be added to the first available slot
 */
public class SlottedArrayList<T> implements Iterable<T>{
    private Object[] list; //the actual list
    private final ArrayList<Integer> removedIndices; //every free space that comes before the write index
    private int writeIndex; //the farthest an item has been written
    private int totalItems;

    /**
     * creates a SlottedArrayList.
     */
    public SlottedArrayList(){
        list = new Object[8];
        removedIndices = new ArrayList<>();
    }

    /**
     * creates a SlottedArrayList.
     * @param initialCapacity the initial capacity of the list. An initial capacity of 0 is not allowed.
     */
    public SlottedArrayList(int initialCapacity){
        if(initialCapacity <= 0) throw new IllegalStateException("the initial capacity of a SlottedArrayList cannot be 0!");
        list = new Object[initialCapacity];
        removedIndices = new ArrayList<>();
    }

    public int add(T item){
        totalItems++;
        if(removedIndices.size() == 0){ // if there aren't any removed indices available
            if(writeIndex >= list.length-1) grow(); //grow the list if required
            list[writeIndex] = item; //and write the item
            return writeIndex++;
        } else {
            int removed = removedIndices.size()-1; //get the last item of removedIndices
            int index = removedIndices.get(removed);

            removedIndices.remove(removed); //remove the last item - the reason for the last item is for performance.
            if(list[index] != null) throw new IllegalStateException("Cannot add item: removed index already contains data, which should* be impossible.");

            list[index] = item;
            return index;
        }
    }
    private void grow(){
        list = Arrays.copyOf(list, (int)(list.length*1.5+1));
    }

    /**
     * "removes" the item at an index.
     * This method actually just sets that place in the list to null, and adds the index to removedIndices.
     * @param index the index to remove. If this index is already empty, nothing happens.
     */
    public void remove(int index){
        if(list[index] != null) {
            totalItems--;
            list[index] = null;
            removedIndices.add(index);
        }
    }
    public T get(int index){
        return (T)list[index];
    }

    public int size(){
        return totalItems;
    }

    public int capacity(){
        return list.length;
    }

    public Object[] getList() {
        return list;
    }

    @Override
    public Iterator iterator() {
        return new SlottedArrayListIterator<T> (list, totalItems);
    }
    static class SlottedArrayListIterator<E> implements Iterator<E>{
        int index;
        Object[] list;
        boolean hasNext;
        public SlottedArrayListIterator(Object[] list, int totalItems){
            this.list = list;
            if(totalItems > 0) hasNext = true;
            index = -1;
            incrementIndex();
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public E next() {
            // get the object to return
            // go to the next filled index - if there is no next filled index, set hasNext to false.
            // return the object
            Object returner = list[index];
            incrementIndex();
            return (E)returner;
        }

        private void incrementIndex(){
            do{
                index++;
                if(index >= list.length){
                    hasNext = false;
                    return;
                }
            }while(list[index]==null);
        }
    }
}
