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

import autumn.config.PropertiesCompatibleConfigFile;
import autumn.config.core.enums.ConfigFileFormat;
import autumn.config.core.utils.PropertiesUtil;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a config file that is of the file format `.properties`
 *
 * @author Jason Song(song_s@ctrip.com)
 * @author Diego Krupitza(info@diegokrupitza.com)
 */
public class PropertiesConfigFile extends AbstractConfigFile implements
        PropertiesCompatibleConfigFile {

  protected AtomicReference<String> m_contentCache;

  public PropertiesConfigFile(String namespace,
                              ConfigRepository configRepository) {
    super(namespace, configRepository);
    m_contentCache = new AtomicReference<>();
  }

  @Override
  protected void update(Properties newProperties) {
    m_configProperties.set(newProperties);
    m_contentCache.set(null);
  }

  @Override
  public String getContent() {
    if (m_contentCache.get() == null) {
      m_contentCache.set(doGetContent());
    }
    return m_contentCache.get();
  }

  String doGetContent() {
    if (!this.hasContent()) {
      return null;
    }

    try {
      return PropertiesUtil.toString(m_configProperties.get());
    } catch (Throwable ex) {
      return null;
    }
  }

  @Override
  public boolean hasContent() {
    return m_configProperties.get() != null && !m_configProperties.get().isEmpty();
  }

  @Override
  public ConfigFileFormat getConfigFileFormat() {
    return ConfigFileFormat.Properties;
  }

  @Override
  public Properties asProperties() {
      return this.hasContent() ? m_configProperties.get() : propertiesFactory.getPropertiesInstance();
  }
}
