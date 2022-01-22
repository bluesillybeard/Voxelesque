package util.other;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class covers a map and makes it thread-save and iterator safe.
 *
 * Essentially, instead of throwing a ConcurrentModificationException when it's modified during iteration,
 * instead it waits until the iterating is finished, then makes the change and returns.
 * In other words, the thread modifying the map *waits* until the map is done being iterated.
 *
 * IMPORTANT: IF YOU ARE ITERATING THIS MAP, MAKE SURE TO CALL THE stopIterating() METHOD WHEN YOU ARE DONE ITERATING
 */
public class IteratorSafeMap<K, V> implements Map<K, V>{
    private final Map<K, V> map;
    private boolean iterating;
    //If there is an issue with starting an iterator while the map is being modified, add a modifying variable.

    public IteratorSafeMap(Map<K, V> map) {
        this.map = map;
    }

    public synchronized void stopIterating(){
        iterating = false;
    }

    @Override
    public synchronized int size() {
        return map.size();
    }
    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }
    @Override
    public synchronized boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    @Override
    public synchronized boolean containsValue(Object value) {
        return map.containsValue(value);
    }
    @Override
    public synchronized V get(Object key) {
        return map.get(key);
    }
    @Override
    public synchronized V put(K key, V value) {
        waitIterating();
        return map.put(key, value);
    }
    @Override
    public synchronized V remove(Object key) {
        waitIterating();
        return map.remove(key);
    }
    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        waitIterating();
        map.putAll(m);
    }
    @Override
    public synchronized void clear() {
        waitIterating();
        map.clear();
    }
    @Override
    public synchronized Set<K> keySet() {
        return map.keySet();
    }
    @Override
    public synchronized Collection<V> values() {
        return map.values();
    }
    @Override
    public synchronized Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
    @Override
    public synchronized V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
    @Override
    public synchronized void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }
    @Override
    public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        waitIterating();
        map.replaceAll(function);
    }
    @Override
    public synchronized V putIfAbsent(K key, V value) {
        waitIterating();
        return map.putIfAbsent(key, value);
    }
    @Override
    public synchronized boolean remove(Object key, Object value) {
        waitIterating();
        return map.remove(key, value);
    }
    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        waitIterating();
        return map.replace(key, oldValue, newValue);
    }
    @Override
    public synchronized V replace(K key, V value) {
        waitIterating();
        return map.replace(key, value);
    }
    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }
    @Override
    public synchronized V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }
    @Override
    public synchronized V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }
    @Override
    public synchronized V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        waitIterating();
        return map.merge(key, value, remappingFunction);
    }
    
    private void waitIterating(){
        while(iterating){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }
}
