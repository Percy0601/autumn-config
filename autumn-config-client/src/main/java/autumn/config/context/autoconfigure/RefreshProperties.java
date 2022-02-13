package autumn.config.context.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

import java.util.List;

@ConfigurationProperties("spring.cloud.refresh")
public class RefreshProperties {

    /**
     * Additional property sources to retain during a refresh. Typically only system
     * property sources are retained. This property allows property sources, such as
     * property sources created by EnvironmentPostProcessors to be retained as well.
     */
    private List<String> additionalPropertySourcesToRetain;

    public List<String> getAdditionalPropertySourcesToRetain() {
        return this.additionalPropertySourcesToRetain;
    }

    public void setAdditionalPropertySourcesToRetain(List<String> additionalPropertySourcesToRetain) {
        this.additionalPropertySourcesToRetain = additionalPropertySourcesToRetain;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("additionalPropertySourcesToRetain", additionalPropertySourcesToRetain).toString();

    }
}
