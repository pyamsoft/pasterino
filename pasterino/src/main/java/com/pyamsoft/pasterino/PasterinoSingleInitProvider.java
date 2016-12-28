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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pasterino.presenter.Injector;
import com.pyamsoft.pasterino.presenter.PasterinoModule;
import com.pyamsoft.pydroid.BuildConfigChecker;
import com.pyamsoft.pydroid.IPYDroidApp;
import com.pyamsoft.pydroid.SingleInitContentProvider;
import com.pyamsoft.pydroid.ui.UiLicenses;

public class PasterinoSingleInitProvider extends SingleInitContentProvider
    implements IPYDroidApp<PasterinoModule> {

  @Nullable private PasterinoModule pasterinoModule;

  @NonNull @Override protected BuildConfigChecker initializeBuildConfigChecker() {
    return new BuildConfigChecker() {
      @Override public boolean isDebugMode() {
        return BuildConfig.DEBUG;
      }
    };
  }

  @Override protected void onInstanceCreated(@NonNull Context context) {
    Injector.set(pasterinoModule);
  }

  @Override protected void onFirstCreate(@NonNull Context context) {
    super.onFirstCreate(context);
    pasterinoModule = new PasterinoModule(context);
  }

  @Nullable @Override public String provideGoogleOpenSourceLicenses(@NonNull Context context) {
    return null;
  }

  @Override public void insertCustomLicensesIntoMap() {
    UiLicenses.addLicenses();
  }

  @NonNull @Override public PasterinoModule provideComponent() {
    if (pasterinoModule == null) {
      throw new NullPointerException("Pasterino module is NULL");
    }

    return pasterinoModule;
  }
}
