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
import com.pyamsoft.pasterino.app.main.MainSettingsPresenter;
import com.pyamsoft.pasterino.app.service.PasteService;
import com.pyamsoft.pydroid.dagger.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;

class MainSettingsPresenterImpl extends SchedulerPresenter<MainSettingsPresenter.View>
    implements MainSettingsPresenter {

  @Inject MainSettingsPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override public void setFABFromState() {
    if (PasteService.isRunning()) {
      getView().onFABEnabled();
    } else {
      getView().onFABDisabled();
    }
  }

  @Override public void clickFab() {
    if (PasteService.isRunning()) {
      getView().onDisplayServiceInfo();
    } else {
      getView().onCreateAccessibilityDialog();
    }
  }
}