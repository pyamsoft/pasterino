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

package com.pyamsoft.pasterino.dagger.main;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.app.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterino.app.main.MainSettingsPresenter;
import com.pyamsoft.pasterino.dagger.PasterinoModule;

public class MainSettingsModule {

  @NonNull private final MainSettingsPreferenceInteractor interactor;
  @NonNull private final MainSettingsPresenter settingsPresenter;
  @NonNull private final MainSettingsPreferencePresenter settingsPreferencePresenter;

  public MainSettingsModule(@NonNull PasterinoModule.Provider provider) {
    interactor = new MainSettingsPreferenceInteractorImpl(provider.providePreferences());
    settingsPresenter = new MainSettingsPresenterImpl(provider.provideObsScheduler(),
        provider.provideSubScheduler());
    settingsPreferencePresenter =
        new MainSettingsPreferencePresenterImpl(interactor, provider.provideObsScheduler(),
            provider.provideSubScheduler());
  }

  @NonNull @CheckResult public MainSettingsPreferencePresenter getSettingsPreferencePresenter() {
    return settingsPreferencePresenter;
  }

  @NonNull @CheckResult public MainSettingsPresenter getSettingsPresenter() {
    return settingsPresenter;
  }
}
