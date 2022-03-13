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

import autumn.config.ConfigFileChangeListener;
import autumn.config.PropertiesCompatibleConfigFile;
import autumn.config.enums.ConfigSourceType;
import autumn.config.model.ConfigFileChangeEvent;

import java.util.Properties;

public class PropertiesCompatibleFileConfigRepository extends AbstractConfigRepository implements
        ConfigFileChangeListener {
  private final PropertiesCompatibleConfigFile configFile;
  private volatile Properties cachedProperties;

  public PropertiesCompatibleFileConfigRepository(PropertiesCompatibleConfigFile configFile) {
    this.configFile = configFile;
    this.configFile.addChangeListener(this);
    this.trySync();
  }

  @Override
  protected synchronized void sync() {
    Properties current = configFile.asProperties();

    if (cachedProperties != current) {
      cachedProperties = current;
      this.fireRepositoryChange(configFile.getNamespace(), cachedProperties);
    }
  }

  @Override
  public Properties getConfig() {
    if (cachedProperties == null) {
      sync();
    }
    return cachedProperties;
  }

  @Override
  public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {
    //config file is the upstream, so no need to set up extra upstream
  }

  @Override
  public ConfigSourceType getSourceType() {
    return configFile.getSourceType();
  }

  @Override
  public void onChange(ConfigFileChangeEvent changeEvent) {
    this.trySync();
  }
}
