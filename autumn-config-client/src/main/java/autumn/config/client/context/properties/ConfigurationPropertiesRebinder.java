package autumn.config.client.context.properties;

import autumn.config.client.context.environment.EnvironmentChangeEvent;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationPropertiesRebinder implements ApplicationContextAware,
        ApplicationListener<EnvironmentChangeEvent> {

    private ConfigurationPropertiesBeans beans;

    private ApplicationContext applicationContext;

    private Map<String, Exception> errors = new ConcurrentHashMap<>();

    public ConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
        this.beans = beans;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * A map of bean name to errors when instantiating the bean.
     * @return The errors accumulated since the latest destroy.
     */
    public Map<String, Exception> getErrors() {
        return this.errors;
    }

    @ManagedOperation
    public void rebind() {
        this.errors.clear();
        for (String name : this.beans.getBeanNames()) {
            rebind(name);
        }
    }

    @ManagedOperation
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
                this.errors.put(name, e);
                throw e;
            } catch (Exception e) {
                this.errors.put(name, e);
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
