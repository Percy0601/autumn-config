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
package autumn.config.util.factory;

import autumn.config.build.ApplicationContextAwareUtil;
import autumn.config.util.ConfigUtil;
import autumn.config.util.OrderedProperties;


import java.util.Properties;

/**
 * Default PropertiesFactory implementation.
 *
 * @author songdragon@zts.io
 */
public class DefaultPropertiesFactory implements PropertiesFactory {

  private ConfigUtil m_configUtil;

  public DefaultPropertiesFactory() {
    m_configUtil = ApplicationContextAwareUtil.getBean(ConfigUtil.class);
  }

  @Override
  public Properties getPropertiesInstance() {
    if (m_configUtil.isPropertiesOrderEnabled()) {
      return new OrderedProperties();
    } else {
      return new Properties();
    }
  }
}
