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

package com.pyamsoft.pasterinopresenter.main;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import timber.log.Timber;

class MainSettingsPreferencePresenterImpl
    extends PresenterBase<MainSettingsPreferencePresenter.MainSettingsView>
    implements MainSettingsPreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final MainSettingsPreferenceInteractor interactor;
  @NonNull private ExecutedOffloader confirmedSubscription = new ExecutedOffloader.Empty();

  MainSettingsPreferencePresenterImpl(@NonNull MainSettingsPreferenceInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
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

  @Override public void processClearRequest() {
    unsubscribeConfirm();
    confirmedSubscription = interactor.clearAll()
        .onError(throwable -> Timber.e(throwable, "onError clearAll"))
        .onResult(item -> getView(MainSettingsView::onClearAll))
        .execute();
  }
}
