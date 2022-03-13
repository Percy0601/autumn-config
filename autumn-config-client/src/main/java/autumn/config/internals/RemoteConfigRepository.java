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

import autumn.config.enums.ConfigSourceType;

import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class RemoteConfigRepository extends AbstractConfigRepository {

  /**
   * Constructor.
   *
   * @param namespace the namespace
   */
  public RemoteConfigRepository(String namespace) {

  }

  @Override
  protected void sync() {

  }

  @Override
  public Properties getConfig() {
    return null;
  }

  @Override
  public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {

  }

  @Override
  public ConfigSourceType getSourceType() {
    return null;
  }
}
