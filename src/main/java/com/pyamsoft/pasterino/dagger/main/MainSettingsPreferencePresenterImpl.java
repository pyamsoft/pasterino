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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.AsyncTaskCompat;
import com.pyamsoft.pasterino.app.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterino.bus.ConfirmationDialogBus;
import com.pyamsoft.pasterino.model.event.ConfirmationEvent;
import com.pyamsoft.pydroid.Bus;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import timber.log.Timber;

class MainSettingsPreferencePresenterImpl
    extends PresenterBase<MainSettingsPreferencePresenter.MainSettingsView>
    implements MainSettingsPreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final MainSettingsPreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @Nullable AsyncTask confirmedSubscription;
  @Nullable private Bus.Event<ConfirmationEvent> confirmBusSubscription;

  MainSettingsPreferencePresenterImpl(@NonNull MainSettingsPreferenceInteractor interactor) {
    this.interactor = interactor;
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

  @SuppressWarnings("WeakerAccess") void unsubscribeConfirm() {
    if (confirmedSubscription != null) {
      if (!confirmedSubscription.isCancelled()) {
        confirmedSubscription.cancel(true);
      }
    }
  }

  private void unregisterFromConfirmEventBus() {
    ConfirmationDialogBus.get().unregister(confirmBusSubscription);
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting void registerOnConfirmEventBus() {
    unregisterFromConfirmEventBus();
    confirmBusSubscription = ConfirmationDialogBus.get().register(item -> {
      unsubscribeConfirm();
      confirmedSubscription = AsyncTaskCompat.executeParallel(
          interactor.clearAll(item1 -> getView(MainSettingsView::onClearAll)));
    }, item -> Timber.e(item, "onError"));
  }
}
