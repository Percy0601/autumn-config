package autumn.config.client.context.properties;

import autumn.config.client.context.environment.EnvironmentChangeEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

public class ConfigurationPropertiesRebinder implements ApplicationContextAware,
        ApplicationListener<EnvironmentChangeEvent> {

    private ConfigurationPropertiesBeans beans;

    private ApplicationContext applicationContext;

    public ConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
        this.beans = beans;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void rebind() {
        for (String name : this.beans.getBeanNames()) {
            rebind(name);
        }
    }

    public boolean rebind(String name) {
        if (!this.beans.getBeanNames().contains(name)) {
            return false;
        }
        if (this.applicationContext != null) {
            try {
                Object bean = this.applicationContext.getBean(name);
                if (bean != null) {
                    this.applicationContext.getAutowireCapableBeanFactory().destroyBean(bean);
                    this.applicationContext.getAutowireCapableBeanFactory().initializeBean(bean, name);
                    return true;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Cannot rebind to " + name, e);
            }
        }
        return false;
    }

    public Set<String> getBeanNames() {
        return new HashSet<>(this.beans.getBeanNames());
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        if (this.applicationContext.equals(event.getSource())
                // Backwards compatible
                || event.getKeys().equals(event.getSource())) {
            rebind();
        }
    }
}
