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
import com.pyamsoft.pydroid.lib.PYDroidApplication;

public class Pasterino extends PYDroidApplication implements IPasterino<PasterinoComponent> {

  private PasterinoComponent component;

  @NonNull @CheckResult public static IPasterino<PasterinoComponent> get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof IPasterino) {
      return Pasterino.class.cast(appContext);
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPasterino");
    }
  }

  @Override protected void onFirstCreate() {
    super.onFirstCreate();
    component = DaggerPasterinoComponent.builder()
        .pasterinoModule(new PasterinoModule(getApplicationContext()))
        .build();
  }

  @NonNull @Override public PasterinoComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("Pasterino component is NULL");
    }
    return component;
  }
}
