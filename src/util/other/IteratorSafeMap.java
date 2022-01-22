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
 */
public class IteratorSafeMap<K, V> implements Map<K, V>{
    private Map<K, V> map;
    private boolean iterating;
    @Override
    public int size() {
        return map.size();
    }
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
    @Override
    public V get(Object key) {
        return map.get(key);
    }
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }
    @Override
    public Collection<V> values() {
        return map.values();
    }
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Cannot edit a read-only map");
    }
}
