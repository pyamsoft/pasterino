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

package com.pyamsoft.pasterino.main;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.Presenter;

class MainSettingsPresenter extends Presenter<Presenter.Empty> {

  MainSettingsPresenter() {
  }

  public void setFABFromState(boolean serviceRunning, @NonNull FABStateCallback callback) {
    if (serviceRunning) {
      callback.onFABEnabled();
    } else {
      callback.onFABDisabled();
    }
  }

  public void clickFabServiceRunning(@NonNull DisplayServiceCallback callback) {
    callback.onDisplayServiceInfo();
  }

  public void clickFabServiceIdle(@NonNull AccessibilityRequestCallback callback) {
    callback.onCreateAccessibilityDialog();
  }

  interface FABStateCallback {

    void onFABEnabled();

    void onFABDisabled();
  }

  interface DisplayServiceCallback {

    void onDisplayServiceInfo();
  }

  interface AccessibilityRequestCallback {

    void onCreateAccessibilityDialog();
  }
}
