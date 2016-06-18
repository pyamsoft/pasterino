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

package com.pyamsoft.pasterino.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import timber.log.Timber;

public final class SinglePasteService extends IntentService {

  private static final long NOTIFICATION_SHADE_DISMISS_DELAY = 600L;
  @NonNull private final Handler handler;

  public SinglePasteService() {
    super(SinglePasteService.class.getName());
    handler = new Handler(Looper.getMainLooper());
  }

  @Override protected void onHandleIntent(Intent intent) {
    Timber.d("Attempt single paste");
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(() -> PasteService.getInstance().pasteIntoTarget(),
        NOTIFICATION_SHADE_DISMISS_DELAY);
  }

  @Override public boolean onUnbind(Intent intent) {
    handler.removeCallbacksAndMessages(null);
    return super.onUnbind(intent);
  }
}
