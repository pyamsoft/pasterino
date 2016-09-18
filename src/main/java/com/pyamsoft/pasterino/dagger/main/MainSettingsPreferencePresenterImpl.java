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
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pasterino.app.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterino.bus.ConfirmationDialogBus;
import com.pyamsoft.pydroid.dagger.presenter.PresenterBase;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class MainSettingsPreferencePresenterImpl
    extends PresenterBase<MainSettingsPreferencePresenter.MainSettingsView>
    implements MainSettingsPreferencePresenter {

  @NonNull private final MainSettingsPreferenceInteractor interactor;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription confirmBusSubscription = Subscriptions.empty();
  @NonNull private Subscription confirmedSubscription = Subscriptions.empty();

  @Inject MainSettingsPreferencePresenterImpl(@NonNull MainSettingsPreferenceInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    this.interactor = interactor;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @Override protected void onBind() {
    super.onBind();
    registerOnConfirmEventBus();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterFromConfirmEventBus();
    unsubscribeConfirm();
  }

  @Override public void clearAll() {
    getView(MainSettingsView::showConfirmDialog);
  }

  void unsubscribeConfirm() {
    if (!confirmedSubscription.isUnsubscribed()) {
      confirmedSubscription.unsubscribe();
    }
  }

  private void unregisterFromConfirmEventBus() {
    if (!confirmBusSubscription.isUnsubscribed()) {
      confirmBusSubscription.unsubscribe();
    }
  }

  @VisibleForTesting void registerOnConfirmEventBus() {
    unregisterFromConfirmEventBus();
    confirmBusSubscription = ConfirmationDialogBus.get().register().subscribe(confirmationEvent -> {
      unsubscribeConfirm();
      confirmedSubscription = interactor.clearAll()
          .subscribeOn(ioScheduler)
          .observeOn(mainScheduler)
          .subscribe(aBoolean -> {
            getView(MainSettingsView::onClearAll);
          }, throwable -> {
            Timber.e(throwable, "onError");
          });
    }, throwable -> {
      Timber.e(throwable, "onError");
    });
  }
}
