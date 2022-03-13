package autumn.config.internals;

import autumn.config.Config;
import autumn.config.ConfigChangeListener;
import autumn.config.build.AutumnInjector;
import autumn.config.core.utils.AutumnThreadFactory;
import autumn.config.enums.PropertyChangeType;
import autumn.config.model.ConfigChange;
import autumn.config.model.ConfigChangeEvent;
import autumn.config.util.ConfigUtil;
import autumn.config.util.SetUtil;
import autumn.config.util.factory.PropertiesFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public abstract class AbstractConfig implements Config {

    private static final ExecutorService m_executorService;

    private final ConcurrentLinkedQueue<ConfigChangeListener> m_listeners = new ConcurrentLinkedQueue<>();
    private final Map<ConfigChangeListener, Set<String>> m_interestedKeys = new ConcurrentHashMap<>();
    private final Map<ConfigChangeListener, Set<String>> m_interestedKeyPrefixes = new ConcurrentHashMap<>();
    private final ConfigUtil m_configUtil;

    private final AtomicLong m_configVersion; //indicate config version

    protected PropertiesFactory propertiesFactory;

    static {
        m_executorService = Executors.newCachedThreadPool(AutumnThreadFactory
                .create("Config", true));
    }

    public AbstractConfig() {
        m_configUtil = AutumnInjector.getInstance(ConfigUtil.class);
        m_configVersion = new AtomicLong();
        propertiesFactory = AutumnInjector.getInstance(PropertiesFactory.class);
    }

    @Override
    public void addChangeListener(ConfigChangeListener listener) {
        addChangeListener(listener, null);
    }

    @Override
    public void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys) {
        addChangeListener(listener, interestedKeys, null);
    }

    @Override
    public void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys, Set<String> interestedKeyPrefixes) {
        if (!m_listeners.contains(listener)) {
            m_listeners.add(listener);
            if (interestedKeys != null && !interestedKeys.isEmpty()) {
                m_interestedKeys.put(listener, new HashSet<>(interestedKeys));
            }
            if (interestedKeyPrefixes != null && !interestedKeyPrefixes.isEmpty()) {
                m_interestedKeyPrefixes.put(listener, new HashSet<>(interestedKeyPrefixes));
            }
        }
    }

    @Override
    public boolean removeChangeListener(ConfigChangeListener listener) {
        m_interestedKeys.remove(listener);
        m_interestedKeyPrefixes.remove(listener);
        return m_listeners.remove(listener);
    }

    @Override
    public Integer getIntProperty(String key, Integer defaultValue) {

        return defaultValue;
    }

    @Override
    public Long getLongProperty(String key, Long defaultValue) {

        return defaultValue;
    }

    @Override
    public Short getShortProperty(String key, Short defaultValue) {

        return defaultValue;
    }

    @Override
    public Float getFloatProperty(String key, Float defaultValue) {

        return defaultValue;
    }

    @Override
    public Double getDoubleProperty(String key, Double defaultValue) {

        return defaultValue;
    }

    @Override
    public Byte getByteProperty(String key, Byte defaultValue) {

        return defaultValue;
    }

    @Override
    public Boolean getBooleanProperty(String key, Boolean defaultValue) {

        return defaultValue;
    }

    @Override
    public String[] getArrayProperty(String key, final String delimiter, String[] defaultValue) {

        return defaultValue;
    }

    @Override
    public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType, T defaultValue) {

        return defaultValue;
    }

    @Override
    public Date getDateProperty(String key, Date defaultValue) {


        return defaultValue;
    }

    @Override
    public Date getDateProperty(String key, String format, Date defaultValue) {


        return defaultValue;
    }

    @Override
    public Date getDateProperty(String key, String format, Locale locale, Date defaultValue) {


        return defaultValue;
    }

    @Override
    public long getDurationProperty(String key, long defaultValue) {


        return defaultValue;
    }

    /**
     * Clear config cache
     */
    protected void clearConfigCache() {

    }

    /**
     * @param changes map's key is config property's key
     */
    protected void fireConfigChange(String namespace, Map<String, ConfigChange> changes) {
        final Set<String> changedKeys = changes.keySet();
        final List<ConfigChangeListener> listeners = this.findMatchedConfigChangeListeners(changedKeys);

        // notify those listeners
        for (ConfigChangeListener listener : listeners) {
            Set<String> interestedChangedKeys = resolveInterestedChangedKeys(listener, changedKeys);
            InterestedConfigChangeEvent interestedConfigChangeEvent = new InterestedConfigChangeEvent(
                    namespace, changes, interestedChangedKeys);
            this.notifyAsync(listener, interestedConfigChangeEvent);
        }
    }

    /**
     * Fire the listeners by event.
     */
    protected void fireConfigChange(final ConfigChangeEvent changeEvent) {
        final List<ConfigChangeListener> listeners = this
                .findMatchedConfigChangeListeners(changeEvent.changedKeys());

        // notify those listeners
        for (ConfigChangeListener listener : listeners) {
            this.notifyAsync(listener, changeEvent);
        }
    }

    private List<ConfigChangeListener> findMatchedConfigChangeListeners(Set<String> changedKeys) {
        final List<ConfigChangeListener> configChangeListeners = new ArrayList<>();
        for (ConfigChangeListener configChangeListener : this.m_listeners) {
            // check whether the listener is interested in this change event
            if (this.isConfigChangeListenerInterested(configChangeListener, changedKeys)) {
                configChangeListeners.add(configChangeListener);
            }
        }
        return configChangeListeners;
    }

    private void notifyAsync(final ConfigChangeListener listener, final ConfigChangeEvent changeEvent) {
        Runnable runnable = () -> {
            // String listenerName = listener.getClass().getName();
            listener.onChange(changeEvent);
        };
        m_executorService.submit(runnable);
    }

    private boolean isConfigChangeListenerInterested(ConfigChangeListener configChangeListener, Set<String> changedKeys) {
        Set<String> interestedKeys = m_interestedKeys.get(configChangeListener);
        Set<String> interestedKeyPrefixes = m_interestedKeyPrefixes.get(configChangeListener);

        if ((interestedKeys == null || interestedKeys.isEmpty())
                && (interestedKeyPrefixes == null || interestedKeyPrefixes.isEmpty())) {
            return true; // no interested keys means interested in all keys
        }

        if (interestedKeys != null) {
            for (String interestedKey : interestedKeys) {
                if (changedKeys.contains(interestedKey)) {
                    return true;
                }
            }
        }

        if (interestedKeyPrefixes != null) {
            for (String prefix : interestedKeyPrefixes) {
                for (final String changedKey : changedKeys) {
                    if (changedKey.startsWith(prefix)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Set<String> resolveInterestedChangedKeys(ConfigChangeListener configChangeListener, Set<String> changedKeys) {
        Set<String> interestedChangedKeys = new HashSet<>();

        if (this.m_interestedKeys.containsKey(configChangeListener)) {
            Set<String> interestedKeys = this.m_interestedKeys.get(configChangeListener);
            for (String interestedKey : interestedKeys) {
                if (changedKeys.contains(interestedKey)) {
                    interestedChangedKeys.add(interestedKey);
                }
            }
        }

        if (this.m_interestedKeyPrefixes.containsKey(configChangeListener)) {
            Set<String> interestedKeyPrefixes = this.m_interestedKeyPrefixes.get(configChangeListener);
            for (String interestedKeyPrefix : interestedKeyPrefixes) {
                for (String changedKey : changedKeys) {
                    if (changedKey.startsWith(interestedKeyPrefix)) {
                        interestedChangedKeys.add(changedKey);
                    }
                }
            }
        }

        return Collections.unmodifiableSet(interestedChangedKeys);
    }

    List<ConfigChange> calcPropertyChanges(String namespace, Properties previous,
                                           Properties current) {
        if (previous == null) {
            previous = propertiesFactory.getPropertiesInstance();
        }

        if (current == null) {
            current = propertiesFactory.getPropertiesInstance();
        }

        Set<String> previousKeys = previous.stringPropertyNames();
        Set<String> currentKeys = current.stringPropertyNames();

        Set<String> commonKeys = SetUtil.intersection(previousKeys, currentKeys);
        Set<String> newKeys = SetUtil.difference(currentKeys, commonKeys);
        Set<String> removedKeys = SetUtil.difference(previousKeys, commonKeys);

        List<ConfigChange> changes = new ArrayList<>();

        for (String newKey : newKeys) {
            changes.add(new ConfigChange(namespace, newKey, null, current.getProperty(newKey),
                    PropertyChangeType.ADDED));
        }

        for (String removedKey : removedKeys) {
            changes.add(new ConfigChange(namespace, removedKey, previous.getProperty(removedKey), null,
                    PropertyChangeType.DELETED));
        }

        for (String commonKey : commonKeys) {
            String previousValue = previous.getProperty(commonKey);
            String currentValue = current.getProperty(commonKey);
            if (Objects.equals(previousValue, currentValue)) {
                continue;
            }
            changes.add(new ConfigChange(namespace, commonKey, previousValue, currentValue,
                    PropertyChangeType.MODIFIED));
        }

        return changes;
    }
}