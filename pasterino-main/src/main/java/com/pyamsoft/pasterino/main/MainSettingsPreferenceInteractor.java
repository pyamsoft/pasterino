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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.base.preference.ClearPreferences;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Single;

class MainSettingsPreferenceInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ClearPreferences preferences;

  MainSettingsPreferenceInteractor(@NonNull ClearPreferences preferences) {
    this.preferences = Checker.checkNonNull(preferences);
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<Boolean> clearAll() {
    return Single.fromCallable(() -> {
      // We must use Single or Observable here as in order for the Bus to work it needs to flow via onNext events
      preferences.clearAll();
      return Boolean.TRUE;
    });
  }
}
