/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.dagger.DaggerPasterinoComponent;
import com.pyamsoft.pasterino.dagger.PasterinoComponent;
import com.pyamsoft.pasterino.dagger.PasterinoModule;

public class Singleton {

  private Singleton() {
    throw new RuntimeException("No instances");
  }

  public static final class Dagger {

    private static volatile Dagger instance = null;
    @NonNull private final PasterinoComponent component;

    private Dagger(@NonNull Context context) {
      component = DaggerPasterinoComponent.builder()
          .pasterinoModule(new PasterinoModule(context.getApplicationContext()))
          .build();
    }

    @CheckResult @NonNull public static PasterinoComponent with(@NonNull Context context) {
      if (instance == null) {
        synchronized (Dagger.class) {
          if (instance == null) {
            instance = new Dagger(context.getApplicationContext());
          }
        }
      }

      if (instance == null) {
        throw new NullPointerException("Dagger instance is NULL");
      } else {
        return instance.component;
      }
    }
  }
}