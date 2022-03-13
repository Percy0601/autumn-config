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
package autumn.config.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ClassLoaderUtil {
  private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);

  private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
  private static String classPath = "";

  static {
    if (loader == null) {
      logger.warn("Using system class loader");
      loader = ClassLoader.getSystemClassLoader();
    }

    try {
      URL url = loader.getResource("");
      // get class path
      if (url != null) {
        classPath = url.getPath();
        classPath = URLDecoder.decode(classPath, StandardCharsets.UTF_8);
      }

      // 如果是jar包内的，则返回当前路径
      if (!StringUtils.hasLength(classPath) || classPath.contains(".jar!")) {
        classPath = System.getProperty("user.dir");
      }
    } catch (Throwable ex) {
      classPath = System.getProperty("user.dir");
      logger.warn("Failed to locate class path, fallback to user.dir: {}", classPath, ex);
    }
  }

  public static ClassLoader getLoader() {
    return loader;
  }

  public static String getClassPath() {
    return classPath;
  }

}
