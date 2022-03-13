package autumn.config.build;

import autumn.config.foundation.internals.DefaultProviderManager;
import autumn.config.foundation.spi.ProviderManager;
import autumn.config.internals.ConfigFactoryManager;
import autumn.config.internals.ConfigManager;
import autumn.config.internals.DefaultConfigManager;
import autumn.config.spi.*;
import autumn.config.util.ConfigUtil;
import autumn.config.util.factory.DefaultPropertiesFactory;
import autumn.config.util.factory.PropertiesFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zhaobaoxin
 */
public class AutumnInjector {

  private static volatile Map<Class, List<Object>> injector;
  private static final Object lock = new Object();

  private static Map<Class, List<Object>> getInjector() {
    if (injector == null) {
      synchronized (lock) {
        injector = new ConcurrentHashMap<>();
      }
    }
    return injector;
  }

  public static <T> T getInstance(Class<T> clazz) {
    Map<Class, List<Object>> _m = getInjector();
    if(!_m.containsKey(clazz)) {
      // ConfigManager
      if(clazz.getName().equals(ConfigManager.class.getName())) {
        return makeInstance(clazz, DefaultConfigManager.class.getName());
      }

      // ConfigRegistry
      if(clazz.getName().equals(ConfigRegistry.class.getName())) {
        return makeInstance(clazz, DefaultConfigRegistry.class.getName());
      }

      // ConfigUtil
      if(clazz.getName().equals(ConfigUtil.class.getName())) {
        return makeInstance(clazz, ConfigUtil.class.getName());
      }

      // PropertiesFactory
      if(clazz.getName().equals(PropertiesFactory.class.getName())) {
        return makeInstance(clazz, DefaultPropertiesFactory.class.getName());
      }

      // ConfigFactoryManager
      if(clazz.getName().equals(ConfigFactoryManager.class.getName())) {
        return makeInstance(clazz, DefaultConfigFactoryManager.class.getName());
      }

      // ConfigFactory
      if(clazz.getName().equals(ConfigFactory.class.getName())) {
        return makeInstance(clazz, DefaultConfigFactory.class.getName());
      }

      // ProviderManager
      if(clazz.getName().equals(ProviderManager.class.getName())) {
        return makeInstance(clazz, DefaultProviderManager.class.getName());
      }
    }
    List<Object> instances = _m.get(clazz);
    return (T)instances.get(0);
  }

  public static <T> List<T> getInstances(Class<T> clazz) {
    Map<Class, List<Object>> _m = getInjector();
    List<Object> result = _m.get(clazz);
    if(null == result) {
      if(clazz.getName().equals(ProviderManager.class.getName())) {
        makeInstance(ProviderManager.class, DefaultProviderManager.class.getName());
      }
    }
    result = _m.get(clazz);

    return (List<T>)result;
  }

  public static <T> T getInstance(Class<T> clazz, String name) {
    Map<Class, List<Object>> _m = getInjector();
    if(!_m.containsKey(clazz)) {
      return makeInstance(clazz, name);
    }
    List<Object> instances = _m.get(clazz);
    return (T)instances.get(0);
  }

  private static <T> T makeInstance(Class<T> clazz, String className) {
    Map<Class, List<Object>> injector = getInjector();
    List<Object> instances = injector.get(clazz);
    if(null == instances) {
      instances = new ArrayList<>();
      injector.put(clazz, instances);
    }

    Object instance = instances.stream()
            .filter(it -> it.getClass().getName().equals(className))
            .findAny()
            .orElse(null);

    if(null != instance) {
      return (T)instance;
    }

    // ConfigManager
    if(className.equals(DefaultConfigManager.class.getName())) {
      DefaultConfigManager defaultConfigManager = new DefaultConfigManager();
      instances.add(defaultConfigManager);
      return (T)defaultConfigManager;
    }

    // ConfigRegistry
    if(className.equals(DefaultConfigRegistry.class.getName())) {
      ConfigRegistry defaultConfigRegistry = new DefaultConfigRegistry();
      instances.add(defaultConfigRegistry);
      return (T)defaultConfigRegistry;
    }

    // ConfigUtil
    if(className.equals(ConfigUtil.class.getName())) {
      ConfigUtil configUtil = new ConfigUtil();
      instances.add(configUtil);
      return (T)configUtil;
    }

    // PropertiesFactory
    if(className.equals(DefaultPropertiesFactory.class.getName())) {
      PropertiesFactory defaultPropertiesFactory = new DefaultPropertiesFactory();
      instances.add(defaultPropertiesFactory);
      return (T)defaultPropertiesFactory;
    }

    // ConfigFactoryManager
    if(className.equals(DefaultConfigFactoryManager.class.getName())) {
      ConfigFactoryManager defaultConfigFactoryManager = new DefaultConfigFactoryManager();
      instances.add(defaultConfigFactoryManager);
      return (T)defaultConfigFactoryManager;
    }

    // ConfigFactory
    if(className.equals(DefaultConfigFactory.class.getName())) {
      ConfigFactory defaultConfigFactory = new DefaultConfigFactory();
      instances.add(defaultConfigFactory);
      return (T)defaultConfigFactory;
    }

    // ProviderManager
    if(className.equals(DefaultProviderManager.class.getName())) {
      ProviderManager defaultProviderManager = new DefaultProviderManager();
      instances.add(defaultProviderManager);
      return (T)defaultProviderManager;
    }

    return null;
  }

}
