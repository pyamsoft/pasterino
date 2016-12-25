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

package com.pyamsoft.pasterino.presenter.main;

import com.pyamsoft.pydroid.presenter.PresenterBase;

class MainSettingsPresenterImpl extends PresenterBase<MainSettingsPresenter.View>
    implements MainSettingsPresenter {

  MainSettingsPresenterImpl() {
  }

  @Override public void setFABFromState(boolean serviceRunning) {
    getView(view -> {
      if (serviceRunning) {
        view.onFABEnabled();
      } else {
        view.onFABDisabled();
      }
    });
  }

  @Override public void clickFabServiceRunning() {
    getView(View::onDisplayServiceInfo);
  }

  @Override public void clickFabServiceIdle() {
    getView(View::onCreateAccessibilityDialog);
  }
}