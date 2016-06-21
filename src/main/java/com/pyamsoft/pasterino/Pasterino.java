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

import android.os.StrictMode;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pasterino.dagger.DaggerPasterinoComponent;
import com.pyamsoft.pasterino.dagger.PasterinoComponent;
import com.pyamsoft.pasterino.dagger.PasterinoModule;
import com.pyamsoft.pydroid.base.app.ApplicationBase;
import com.pyamsoft.pydroid.crash.CrashHandler;

public final class Pasterino extends ApplicationBase {

  private volatile static Pasterino instance = null;
  private PasterinoComponent pasterinoComponent;

  @NonNull @CheckResult public synchronized static Pasterino getInstance() {
    if (instance == null) {
      throw new NullPointerException("Pasterino instance is NULL");
    } else {
      //noinspection ConstantConditions
      return instance;
    }
  }

  public synchronized static void setInstance(@Nullable Pasterino instance) {
    Pasterino.instance = instance;
  }

  @NonNull @CheckResult public final PasterinoComponent getPasterinoComponent() {
    if (pasterinoComponent == null) {
      throw new NullPointerException("PasterinoComponent is NULL");
    } else {
      return pasterinoComponent;
    }
  }

  @Override public void onCreate() {
    super.onCreate();

    if (buildConfigDebug()) {
      new CrashHandler(getApplicationContext(), this).register();
      setStrictMode();
    }

    pasterinoComponent = DaggerPasterinoComponent.builder()
        .pasterinoModule(new PasterinoModule(getApplicationContext()))
        .build();

    // Set instance
    setInstance(this);
  }

  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  @Override protected boolean buildConfigDebug() {
    return BuildConfig.DEBUG;
  }

  @NonNull @Override public String appName() {
    return getString(R.string.app_name);
  }

  @NonNull @Override public String buildConfigApplicationId() {
    return BuildConfig.APPLICATION_ID;
  }

  @NonNull @Override public String buildConfigVersionName() {
    return BuildConfig.VERSION_NAME;
  }

  @Override public int buildConfigVersionCode() {
    return BuildConfig.VERSION_CODE;
  }

  @NonNull @Override public String getApplicationPackageName() {
    return getPackageName();
  }
}
