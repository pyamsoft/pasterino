/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.main;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.base.PasterinoModule;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Scheduler;

public class MainSettingsModule {

  @NonNull private final MainSettingsPreferenceInteractor interactor;
  @NonNull private final Scheduler obsScheduler;
  @NonNull private final Scheduler subScheduler;

  public MainSettingsModule(@NonNull PasterinoModule module) {
    module = Checker.checkNonNull(module);
    interactor = new MainSettingsPreferenceInteractor(module.provideClearPreferences());
    obsScheduler = module.provideObsScheduler();
    subScheduler = module.provideSubScheduler();
  }

  @NonNull @CheckResult MainSettingsPreferencePresenter getSettingsPreferencePresenter() {
    return new MainSettingsPreferencePresenter(interactor, obsScheduler, subScheduler);
  }
}
