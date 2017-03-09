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
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

class MainSettingsPreferencePresenter extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final MainSettingsPreferenceInteractor interactor;
  @NonNull private Disposable clearDisposable = Disposables.empty();

  MainSettingsPreferencePresenter(@NonNull MainSettingsPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    clearDisposable = DisposableHelper.unsubscribe(clearDisposable);
  }

  public void processClearRequest(@NonNull ClearRequestCallback callback) {
    clearDisposable = DisposableHelper.unsubscribe(clearDisposable);
    clearDisposable = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(),
            throwable -> Timber.e(throwable, "onError clearAll"));
  }

  interface ClearRequestCallback {

    void onClearAll();
  }
}
