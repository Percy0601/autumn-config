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
package autumn.config.foundation.internals;

import autumn.config.build.AutumnInjector;
import autumn.config.core.spi.Ordered;

import java.util.*;

public class ServiceBootstrap {

  public static <S> S loadFirst(Class<S> clazz) {
    S s = AutumnInjector.getInstance(clazz);
    return s;
  }


  public static <S extends Ordered> List<S> loadAllOrdered(Class<S> clazz) {
    List<S> candidates = AutumnInjector.getInstances(clazz);

    Collections.sort(candidates, new Comparator<S>() {
      @Override
      public int compare(S o1, S o2) {
        // the smaller order has higher priority
        return Integer.compare(o1.getOrder(), o2.getOrder());
      }
    });

    return candidates;
  }

  public static <S extends Ordered> S loadPrimary(Class<S> clazz) {
    List<S> candidates = loadAllOrdered(clazz);

    if (candidates.isEmpty()) {
      throw new IllegalStateException(String.format(
          "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
          clazz.getName()));
    }


    return candidates.get(0);
  }
}
