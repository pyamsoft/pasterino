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

package com.pyamsoft.pasterino.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.ApplicationPreferences;

class PasterinoPreferencesImpl implements PasterinoPreferences {

  @NonNull private final String delayTime;
  @NonNull private final String delayTimeDefault;
  @NonNull private final ApplicationPreferences preferences;

  PasterinoPreferencesImpl(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    preferences = ApplicationPreferences.getInstance(appContext);
    delayTime = appContext.getString(R.string.delay_time_key);
    delayTimeDefault = appContext.getString(R.string.delay_time_default);
  }

  @Override public long getPasteDelayTime() {
    return Long.parseLong(preferences.get(delayTime, delayTimeDefault));
  }

  @Override public void clearAll() {
    preferences.clear(true);
  }
}
