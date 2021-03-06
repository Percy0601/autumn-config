package autumn.config.context.scope.refresh;

import autumn.config.context.scope.GenericScope;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

public class RefreshScope extends GenericScope
        implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, Ordered {

    private ApplicationContext context;

    private BeanDefinitionRegistry registry;

    private int order = Ordered.LOWEST_PRECEDENCE - 100;

    /**
     * Creates a scope instance and gives it the default name: "refresh".
     */
    public RefreshScope() {
        super.setName("refresh");
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
        super.postProcessBeanDefinitionRegistry(registry);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start(event);
    }

    public void start(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.context && this.registry != null) {
            eagerlyInitialize();
        }
    }

    private void eagerlyInitialize() {
        for (String name : this.context.getBeanDefinitionNames()) {
            BeanDefinition definition = this.registry.getBeanDefinition(name);
            if (this.getName().equals(definition.getScope()) && !definition.isLazyInit()) {
                Object bean = this.context.getBean(name);
                if (bean != null) {
                    bean.getClass();
                }
            }
        }
    }

    public boolean refresh(String name) {
        if (!ScopedProxyUtils.isScopedTarget(name)) {
            // User wants to refresh the bean with this name but that isn't the one in the
            // cache...
            name = ScopedProxyUtils.getTargetBeanName(name);
        }
        // Ensure lifecycle is finished if bean was disposable
        if (super.destroy(name)) {
            this.context.publishEvent(new RefreshScopeRefreshedEvent(name));
            return true;
        }
        return false;
    }

    public void refreshAll() {
        super.destroy();
        this.context.publishEvent(new RefreshScopeRefreshedEvent());
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
