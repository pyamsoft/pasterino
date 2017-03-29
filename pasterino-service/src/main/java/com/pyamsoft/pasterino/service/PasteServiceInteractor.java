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

package com.pyamsoft.pasterino.service;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.base.PasterinoPreferences;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Observable;

class PasteServiceInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PasterinoPreferences preferences;

  PasteServiceInteractor(@NonNull PasterinoPreferences preferences) {
    this.preferences = Checker.checkNonNull(preferences);
  }

  @NonNull @CheckResult public Observable<Long> getPasteDelayTime() {
    return Observable.fromCallable(preferences::getPasteDelayTime);
  }
}
