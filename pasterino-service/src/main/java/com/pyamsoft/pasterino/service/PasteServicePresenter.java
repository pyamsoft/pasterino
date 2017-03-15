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

import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.model.ServiceEvent;
import com.pyamsoft.pydroid.bus.EventBus;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

class PasteServicePresenter extends SchedulerPresenter<Object> {

  @NonNull private Disposable finishDisposable = Disposables.empty();

  PasteServicePresenter(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    finishDisposable = DisposableHelper.dispose(finishDisposable);
  }

  public void registerOnBus(@NonNull ServiceCallback callback) {
    finishDisposable = DisposableHelper.dispose(finishDisposable);
    finishDisposable = EventBus.get()
        .listen(ServiceEvent.class)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(serviceEvent -> {
          switch (serviceEvent.type()) {
            case FINISH:
              callback.onServiceFinishRequested();
              break;
            case PASTE:
              callback.onPasteRequested();
              break;
            default:
              throw new IllegalArgumentException(
                  "Invalid ServiceEvent.Type: " + serviceEvent.type());
          }
        }, throwable -> Timber.e(throwable, "onError event bus"));
  }

  public interface ServiceCallback {

    void onServiceFinishRequested();

    void onPasteRequested();
  }
}
