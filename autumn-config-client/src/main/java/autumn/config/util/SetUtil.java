package autumn.config.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SetUtil {

    public static <T> Set<T> intersection(Set<T> previousKeys, Set<T> currentKeys) {
        if(null == previousKeys || null == currentKeys) {
            return new HashSet<>();
        }

        if(previousKeys.isEmpty() || currentKeys.isEmpty()) {
            return new HashSet<>();
        }

        Set<T> result = previousKeys.stream()
                .filter(item -> currentKeys.contains(item))
                .collect(Collectors.toSet());

        return result;
    }

    public static <T> Set<T> difference(Set<T> previousKeys, Set<T> currentKeys) {
        if(null == previousKeys || previousKeys.isEmpty()) {
            return currentKeys;
        }

        if(currentKeys.isEmpty() || currentKeys.isEmpty()) {
            return previousKeys;
        }
        Set<T> r1 = previousKeys.stream()
                .filter(it -> !currentKeys.contains(it))
                .collect(Collectors.toSet());
        Set<T> r2 = currentKeys.stream()
                .filter(it -> !previousKeys.contains(it))
                .collect(Collectors.toSet());
        Set<T> result = new HashSet<>();
        result.addAll(r1);
        result.addAll(r2);
        return result;
    }

}
