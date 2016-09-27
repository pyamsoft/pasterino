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

package com.pyamsoft.pasterino.dagger;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.PasterinoPreferences;
import com.pyamsoft.pasterino.dagger.main.MainSettingsModule;
import com.pyamsoft.pasterino.dagger.service.PasteServiceModule;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PasterinoModule {

  @NonNull private final Provider provider;

  public PasterinoModule(@NonNull Context context) {
    provider = new Provider(context);
  }

  @CheckResult @NonNull public final PasteServiceModule providePasteServiceModule() {
    return new PasteServiceModule(provider);
  }

  @CheckResult @NonNull public final MainSettingsModule provideMainSettingsModule() {
    return new MainSettingsModule(provider);
  }

  public static class Provider {

    @NonNull private final Context appContext;
    @NonNull private final PasterinoPreferences preferences;

    Provider(final @NonNull Context context) {
      appContext = context.getApplicationContext();
      preferences = new PasterinoPreferencesImpl(appContext);
    }

    @CheckResult @NonNull public final Context provideContext() {
      return appContext;
    }

    @CheckResult @NonNull public final PasterinoPreferences providePreferences() {
      return preferences;
    }

    @CheckResult @NonNull public final Scheduler provideSubScheduler() {
      return Schedulers.io();
    }

    @CheckResult @NonNull public final Scheduler provideObsScheduler() {
      return AndroidSchedulers.mainThread();
    }
  }
}
