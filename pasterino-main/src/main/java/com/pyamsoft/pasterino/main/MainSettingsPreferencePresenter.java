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
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

class MainSettingsPreferencePresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final MainSettingsPreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription clearSubscription;

  MainSettingsPreferencePresenter(@NonNull MainSettingsPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(clearSubscription);
  }

  public void clearAll(@NonNull ConfirmCallback callback) {
    callback.showConfirmDialog();
  }

  public void processClearRequest(@NonNull ClearRequestCallback callback) {
    SubscriptionHelper.unsubscribe(clearSubscription);
    clearSubscription = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(),
            throwable -> Timber.e(throwable, "onError clearAll"),
            () -> SubscriptionHelper.unsubscribe(clearSubscription));
  }

  interface ConfirmCallback {

    void showConfirmDialog();
  }

  interface ClearRequestCallback {

    void onClearAll();
  }
}
