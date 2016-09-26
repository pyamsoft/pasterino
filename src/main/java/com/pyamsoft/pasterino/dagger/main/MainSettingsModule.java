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

import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.app.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterino.app.main.MainSettingsPresenter;
import com.pyamsoft.pydroid.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class MainSettingsModule {

  @ActivityScope @Provides MainSettingsPresenter provideMainSettingsPresenter(
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs") Scheduler obsScheduler) {
    return new MainSettingsPresenterImpl(obsScheduler, subScheduler);
  }

  @ActivityScope @Provides MainSettingsPreferencePresenter provideMainSettingsPreferencePresenter(
      @NonNull MainSettingsPreferenceInteractor interactor,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs") Scheduler obsScheduler) {
    return new MainSettingsPreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @ActivityScope @Provides MainSettingsPreferenceInteractor provideMainSettingsPreferenceInteractor(
      @NonNull MainSettingsPreferenceInteractorImpl interactor) {
    return interactor;
  }
}
