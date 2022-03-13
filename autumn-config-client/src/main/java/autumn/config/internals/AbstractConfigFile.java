/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package autumn.config.internals;

import autumn.config.ConfigFile;
import autumn.config.ConfigFileChangeListener;
import autumn.config.build.AutumnInjector;
import autumn.config.core.utils.AutumnThreadFactory;
import autumn.config.enums.ConfigSourceType;
import autumn.config.enums.PropertyChangeType;
import autumn.config.model.ConfigFileChangeEvent;
import autumn.config.util.factory.PropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public abstract class AbstractConfigFile implements ConfigFile, RepositoryChangeListener {
  private static final Logger logger = LoggerFactory.getLogger(AbstractConfigFile.class);
  private static ExecutorService m_executorService;
  protected final ConfigRepository m_configRepository;
  protected final String m_namespace;
  protected final AtomicReference<Properties> m_configProperties;
  private final ConcurrentLinkedQueue<ConfigFileChangeListener> m_listeners = new ConcurrentLinkedQueue<>();
  protected final PropertiesFactory propertiesFactory;

  private volatile ConfigSourceType m_sourceType = ConfigSourceType.NONE;

  static {
    m_executorService = Executors.newCachedThreadPool(AutumnThreadFactory
        .create("ConfigFile", true));
  }

  public AbstractConfigFile(String namespace, ConfigRepository configRepository) {
    m_configRepository = configRepository;
    m_namespace = namespace;
    m_configProperties = new AtomicReference<>();
    propertiesFactory = AutumnInjector.getInstance(PropertiesFactory.class);
    initialize();
  }

  private void initialize() {
    try {
      m_configProperties.set(m_configRepository.getConfig());
      m_sourceType = m_configRepository.getSourceType();
    } catch (Throwable ex) {
      logger.warn("Init Apollo Config File failed - namespace: {}, reason: {}.",
          m_namespace, ex.toString());
    } finally {
      //register the change listener no matter config repository is working or not
      //so that whenever config repository is recovered, config could get changed
      m_configRepository.addChangeListener(this);
    }
  }

  @Override
  public String getNamespace() {
    return m_namespace;
  }

  protected abstract void update(Properties newProperties);

  @Override
  public synchronized void onRepositoryChange(String namespace, Properties newProperties) {
    if (newProperties.equals(m_configProperties.get())) {
      return;
    }
    Properties newConfigProperties = propertiesFactory.getPropertiesInstance();
    newConfigProperties.putAll(newProperties);

    String oldValue = getContent();

    update(newProperties);
    m_sourceType = m_configRepository.getSourceType();

    String newValue = getContent();

    PropertyChangeType changeType = PropertyChangeType.MODIFIED;

    if (oldValue == null) {
      changeType = PropertyChangeType.ADDED;
    } else if (newValue == null) {
      changeType = PropertyChangeType.DELETED;
    }

    this.fireConfigChange(new ConfigFileChangeEvent(m_namespace, oldValue, newValue, changeType));
  }

  @Override
  public void addChangeListener(ConfigFileChangeListener listener) {
    if (!m_listeners.contains(listener)) {
      m_listeners.add(listener);
    }
  }

  @Override
  public boolean removeChangeListener(ConfigFileChangeListener listener) {
    return m_listeners.remove(listener);
  }

  @Override
  public ConfigSourceType getSourceType() {
    return m_sourceType;
  }

  private void fireConfigChange(final ConfigFileChangeEvent changeEvent) {
    for (final ConfigFileChangeListener listener : m_listeners) {
      m_executorService.submit(new Runnable() {
        @Override
        public void run() {
          String listenerName = listener.getClass().getName();
          try {
            listener.onChange(changeEvent);
          } catch (Throwable ex) {
            logger.error("Failed to invoke config file change listener {}", listenerName, ex);
          } finally {
          }
        }
      });
    }
  }
}
