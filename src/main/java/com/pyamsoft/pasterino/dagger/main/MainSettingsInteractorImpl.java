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
import com.pyamsoft.pasterino.PasterinoPreferences;
import javax.inject.Inject;

final class MainSettingsInteractorImpl implements MainSettingsInteractor {

  @NonNull private final PasterinoPreferences preferences;

  @Inject MainSettingsInteractorImpl(@NonNull PasterinoPreferences preferences) {
    this.preferences = preferences;
  }
}
