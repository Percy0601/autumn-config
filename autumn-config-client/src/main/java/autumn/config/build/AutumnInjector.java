package autumn.config.build;

import autumn.config.internals.ConfigFactoryManager;
import autumn.config.internals.ConfigManager;
import autumn.config.internals.DefaultConfigManager;
import autumn.config.spi.ConfigRegistry;
import autumn.config.spi.DefaultConfigFactoryManager;
import autumn.config.spi.DefaultConfigRegistry;
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

    }
    List<Object> instances = _m.get(clazz);
    return (T)instances.get(0);
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
    // ConfigManager
    if(clazz.getName().equals(ConfigManager.class.getName())) {
      List<Object> instances = new ArrayList<>();
      DefaultConfigManager defaultConfigManager = new DefaultConfigManager();
      instances.add(defaultConfigManager);
      injector.put(clazz, instances);
      return (T)defaultConfigManager;
    }

    // ConfigRegistry
    if(clazz.getName().equals(ConfigRegistry.class.getName())) {
      List<Object> instances = new ArrayList<>();
      ConfigRegistry defaultConfigRegistry = new DefaultConfigRegistry();
      instances.add(defaultConfigRegistry);
      injector.put(clazz, instances);
      return (T)defaultConfigRegistry;
    }

    // ConfigUtil
    if(clazz.getName().equals(ConfigUtil.class.getName())) {
      List<Object> instances = new ArrayList<>();
      ConfigUtil configUtil = new ConfigUtil();
      instances.add(configUtil);
      injector.put(clazz, instances);
      return (T)configUtil;
    }

    // PropertiesFactory
    if(clazz.getName().equals(PropertiesFactory.class.getName())) {
      List<Object> instances = new ArrayList<>();
      PropertiesFactory defaultPropertiesFactory = new DefaultPropertiesFactory();
      instances.add(defaultPropertiesFactory);
      injector.put(clazz, instances);
      return (T)defaultPropertiesFactory;
    }

    // ConfigFactoryManager
    if(clazz.getName().equals(ConfigFactoryManager.class.getName())) {
      List<Object> instances = new ArrayList<>();
      ConfigFactoryManager defaultConfigFactoryManager = new DefaultConfigFactoryManager();
      instances.add(defaultConfigFactoryManager);
      injector.put(clazz, instances);
      return (T)defaultConfigFactoryManager;
    }



    return null;
  }

}
