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
        this.list = new Object[8];
        this.removedIndices = new ArrayList<>();
    }

    /**
     * creates a SlottedArrayList.
     * @param initialCapacity the initial capacity of the list. An initial capacity of 0 is not allowed.
     */
    public SlottedArrayList(int initialCapacity){
        if(initialCapacity <= 0) throw new IllegalStateException("the initial capacity of a SlottedArrayList cannot be 0!");
        this.list = new Object[initialCapacity];
        this.removedIndices = new ArrayList<>();
    }

    public int add(T item){
        this.totalItems++;
        if(this.removedIndices.size() == 0){ // if there aren't any removed indices available
            if(this.writeIndex >= this.list.length) grow(); //grow the list if required
            this.list[this.writeIndex] = item; //and write the item
            return this.writeIndex++;
        } else {
            int removed = this.removedIndices.size()-1; //get the last item of removedIndices
            int index = this.removedIndices.get(removed);

            this.removedIndices.remove(removed); //remove the removed index, because it now has data in it.
            if(this.list[index] != null) throw new IllegalStateException("Cannot add item: removed index already contains data, which should* be impossible.");

            this.list[index] = item;
            return index;
        }
    }
    private void grow(){
        this.list = Arrays.copyOf(this.list, (int)(this.list.length*1.5+1));
    }

    /**
     * "removes" the item at an index.
     * This method actually just sets that place in the list to null, and adds the index to removedIndices.
     * @param index the index to remove. If this index is already empty, nothing happens.
     */
    public void remove(int index){
        if(this.list[index] != null) {
            this.totalItems--;
            this.list[index] = null;
            this.removedIndices.add(index);
        }
    }
    public T get(int index){
        return (T) this.list[index];
    }

    public int size(){
        return this.totalItems;
    }

    public int capacity(){
        return this.list.length;
    }

    public Object[] getList() {
        return this.list;
    }

    @Override
    public Iterator iterator() {
        return new SlottedArrayListIterator<T> (this.list, this.totalItems);
    }
    static class SlottedArrayListIterator<E> implements Iterator<E>{
        int index;
        Object[] list;
        boolean hasNext;
        public SlottedArrayListIterator(Object[] list, int totalItems){
            this.list = list;
            if(totalItems > 0) this.hasNext = true;
            this.index = -1;
            incrementIndex();
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override
        public E next() {
            // get the object to return
            // go to the next filled index - if there is no next filled index, set hasNext to false.
            // return the object
            Object returner = this.list[this.index];
            incrementIndex();
            return (E)returner;
        }

        private void incrementIndex(){
            do{
                this.index++;
                if(this.index >= this.list.length){
                    this.hasNext = false;
                    return;
                }
            }while(this.list[this.index]==null);
        }
    }
}
