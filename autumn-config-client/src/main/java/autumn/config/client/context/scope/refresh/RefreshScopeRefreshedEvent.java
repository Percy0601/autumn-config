package autumn.config.client.context.scope.refresh;

import org.springframework.context.ApplicationEvent;

public class RefreshScopeRefreshedEvent extends ApplicationEvent {

    /**
     * Default name for the refresh scope refreshed event.
     */
    public static final String DEFAULT_NAME = "__refreshAll__";

    private String name;

    public RefreshScopeRefreshedEvent() {
        this(DEFAULT_NAME);
    }

    public RefreshScopeRefreshedEvent(String name) {
        super(name);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
