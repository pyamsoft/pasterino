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
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pasterino.app.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterino.bus.ConfirmationDialogBus;
import com.pyamsoft.pasterino.model.event.ConfirmationEvent;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.Bus;
import com.pyamsoft.pydroid.tool.Offloader;
import timber.log.Timber;

class MainSettingsPreferencePresenterImpl
    extends PresenterBase<MainSettingsPreferencePresenter.MainSettingsView>
    implements MainSettingsPreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final MainSettingsPreferenceInteractor interactor;
  @NonNull private Offloader<Boolean> confirmedSubscription = new Offloader.Empty<>();
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

  private void unsubscribeConfirm() {
    if (!confirmedSubscription.isCancelled()) {
      confirmedSubscription.cancel();
    }
  }

  private void unregisterFromConfirmEventBus() {
    ConfirmationDialogBus.get().unregister(confirmBusSubscription);
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting void registerOnConfirmEventBus() {
    unregisterFromConfirmEventBus();
    confirmBusSubscription = ConfirmationDialogBus.get().register(item -> {
      unsubscribeConfirm();
      confirmedSubscription = interactor.clearAll()
          .result(item1 -> getView(MainSettingsView::onClearAll))
          .error(throwable -> Timber.e(throwable, "onError clearAll"))
          .execute();
    }, item -> Timber.e(item, "onError"));
  }
}
