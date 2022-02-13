package autumn.config.context.scope;

import java.util.Collection;

public interface ScopeCache {

    /**
     * Removes the object with this name from the cache.
     * @param name The object name.
     * @return The object removed, or null if there was none.
     */
    Object remove(String name);

    /**
     * Clears the cache and returns all objects in an unmodifiable collection.
     * @return All objects stored in the cache.
     */
    Collection<Object> clear();

    /**
     * Gets the named object from the cache.
     * @param name The name of the object.
     * @return The object with that name, or null if there is none.
     */
    Object get(String name);

    /**
     * Put a value in the cache if the key is not already used. If one is already present
     * with the name provided, it is not replaced, but is returned to the caller.
     * @param name The key.
     * @param value The new candidate value.
     * @return The value that is in the cache at the end of the operation.
     */
    Object put(String name, Object value);
}
