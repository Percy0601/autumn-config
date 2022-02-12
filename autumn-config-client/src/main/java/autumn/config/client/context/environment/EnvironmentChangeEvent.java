package autumn.config.client.context.environment;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class EnvironmentChangeEvent extends ApplicationEvent {

    private Set<String> keys;

    public EnvironmentChangeEvent(Object context, Set<String> keys) {
        super(context);
        this.keys = keys;
    }

    /**
     * @return The keys.
     */
    public Set<String> getKeys() {
        return this.keys;
    }
}
