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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.dagger.service.DaggerPasteServiceComponent;
import javax.inject.Inject;
import timber.log.Timber;

public final class SinglePasteService extends Service
    implements SinglePastePresenter.SinglePasteProvider {

  @NonNull private final Handler handler;
  @Inject SinglePastePresenter presenter;

  public SinglePasteService() {
    handler = new Handler(Looper.getMainLooper());
  }

  @Override public void onCreate() {
    super.onCreate();
    Timber.d("onCreate");
    DaggerPasteServiceComponent.builder()
        .pasterinoComponent(Pasterino.getInstance().getPasterinoComponent())
        .build()
        .inject(this);

    presenter.bindView(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");
    handler.removeCallbacksAndMessages(null);
    presenter.unbindView();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("Attempt single paste");
    presenter.onPostDelayedEvent();
    return START_NOT_STICKY;
  }

  @Override public void postDelayedEvent(long delay) {
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(() -> {
      PasteService.getInstance().pasteIntoCurrentFocus();
      stopSelf();
    }, delay);
  }
}
