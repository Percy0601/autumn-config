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

import autumn.config.build.AutumnInjector;
import autumn.config.core.ConfigConsts;
import autumn.config.core.utils.ClassLoaderUtil;
import autumn.config.enums.ConfigSourceType;
import autumn.config.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class LocalFileConfigRepository extends AbstractConfigRepository
    implements RepositoryChangeListener {
  private static final Logger logger = LoggerFactory.getLogger(LocalFileConfigRepository.class);
  private static final String CONFIG_DIR = "/config-cache";
  private final String m_namespace;
  private File m_baseDir;
  private final ConfigUtil m_configUtil;
  private volatile Properties m_fileProperties;
  private volatile ConfigRepository m_upstream;

  private volatile ConfigSourceType m_sourceType = ConfigSourceType.LOCAL;

  /**
   * Constructor.
   *
   * @param namespace the namespace
   */
  public LocalFileConfigRepository(String namespace) {
    this(namespace, null);
  }

  public LocalFileConfigRepository(String namespace, ConfigRepository upstream) {
    m_namespace = namespace;
    m_configUtil = AutumnInjector.getInstance(ConfigUtil.class);
    this.setLocalCacheDir(findLocalCacheDir(), false);
    this.setUpstreamRepository(upstream);
    this.trySync();
  }

  void setLocalCacheDir(File baseDir, boolean syncImmediately) {
    m_baseDir = baseDir;
    this.checkLocalConfigCacheDir(m_baseDir);
    if (syncImmediately) {
      this.trySync();
    }
  }

  private File findLocalCacheDir() {
    try {
      String defaultCacheDir = m_configUtil.getDefaultLocalCacheDir();
      Path path = Paths.get(defaultCacheDir);
      if (!Files.exists(path)) {
        Files.createDirectories(path);
      }
      if (Files.exists(path) && Files.isWritable(path)) {
        return new File(defaultCacheDir, CONFIG_DIR);
      }
    } catch (Throwable ex) {
      //ignore
    }

    return new File(ClassLoaderUtil.getClassPath(), CONFIG_DIR);
  }

  @Override
  public Properties getConfig() {
    if (m_fileProperties == null) {
      sync();
    }
    Properties result = propertiesFactory.getPropertiesInstance();
    result.putAll(m_fileProperties);
    return result;
  }

  @Override
  public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {
    if (upstreamConfigRepository == null) {
      return;
    }
    //clear previous listener
    if (m_upstream != null) {
      m_upstream.removeChangeListener(this);
    }
    m_upstream = upstreamConfigRepository;
    upstreamConfigRepository.addChangeListener(this);
  }

  @Override
  public ConfigSourceType getSourceType() {
    return m_sourceType;
  }

  @Override
  public void onRepositoryChange(String namespace, Properties newProperties) {
    if (newProperties.equals(m_fileProperties)) {
      return;
    }
    Properties newFileProperties = propertiesFactory.getPropertiesInstance();
    newFileProperties.putAll(newProperties);
    updateFileProperties(newFileProperties, m_upstream.getSourceType());
    this.fireRepositoryChange(namespace, newProperties);
  }

  @Override
  protected void sync() {
    //sync with upstream immediately
    boolean syncFromUpstreamResultSuccess = trySyncFromUpstream();

    if (syncFromUpstreamResultSuccess) {
      return;
    }

    Throwable exception = null;
    try {
      m_fileProperties = this.loadFromLocalCacheFile(m_baseDir, m_namespace);
      m_sourceType = ConfigSourceType.LOCAL;
    } catch (Throwable ex) {
      exception = ex;
      //ignore
    } finally {

    }

    if (m_fileProperties == null) {
      m_sourceType = ConfigSourceType.NONE;
    }
  }

  private boolean trySyncFromUpstream() {
    if (m_upstream == null) {
      return false;
    }
    try {
      updateFileProperties(m_upstream.getConfig(), m_upstream.getSourceType());
      return true;
    } catch (Throwable ex) {
      logger
          .warn("Sync config from upstream repository {} failed, reason: {}", m_upstream.getClass(), ex.toString());
    }
    return false;
  }

  private synchronized void updateFileProperties(Properties newProperties, ConfigSourceType sourceType) {
    this.m_sourceType = sourceType;
    if (newProperties.equals(m_fileProperties)) {
      return;
    }
    this.m_fileProperties = newProperties;
    persistLocalCacheFile(m_baseDir, m_namespace);
  }

  private Properties loadFromLocalCacheFile(File baseDir, String namespace) throws IOException {

    File file = assembleLocalCacheFile(baseDir, namespace);
    Properties properties = null;

    if (file.isFile() && file.canRead()) {
      InputStream in = null;

      try {
        in = new FileInputStream(file);
        properties = propertiesFactory.getPropertiesInstance();
        properties.load(in);
        logger.debug("Loading local config file {} successfully!", file.getAbsolutePath());
      } catch (IOException ex) {

      } finally {
        try {
          if (in != null) {
            in.close();
          }
        } catch (IOException ex) {
          // ignore
        }
      }
    } else {

    }

    return properties;
  }

  void persistLocalCacheFile(File baseDir, String namespace) {
    if (baseDir == null) {
      return;
    }
    File file = assembleLocalCacheFile(baseDir, namespace);
    OutputStream out = null;

    try {
      out = new FileOutputStream(file);
      m_fileProperties.store(out, "Persisted by DefaultConfig");

    } catch (IOException ex) {
      logger.warn("Persist local cache file {} failed, reason: {}.", file.getAbsolutePath(), ex.toString());
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException ex) {
          //ignore
        }
      }
    }
  }

  private void checkLocalConfigCacheDir(File baseDir) {
    if (baseDir.exists()) {
      return;
    }
    try {
      Files.createDirectory(baseDir.toPath());
    } catch (IOException ex) {
      logger.warn(
          "Unable to create local config cache directory {}, reason: {}. Will not able to cache config file.",
          baseDir.getAbsolutePath(), ex.toString());
    } finally {

    }
  }

  File assembleLocalCacheFile(File baseDir, String namespace) {
    String fileName =
        String.format("%s.properties", String.join(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR,
                Arrays.asList(m_configUtil.getAppId(), ConfigConsts.CLUSTER_NAME_DEFAULT, namespace))
        );
    ;

    return new File(baseDir, fileName);
  }
}
