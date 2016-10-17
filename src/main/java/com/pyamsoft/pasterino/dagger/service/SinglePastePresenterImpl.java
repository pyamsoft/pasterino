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

package com.pyamsoft.pasterino.dagger.service;

import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.app.service.SinglePastePresenter;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import timber.log.Timber;

class SinglePastePresenterImpl extends PresenterBase<SinglePastePresenter.SinglePasteProvider>
    implements SinglePastePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final PasteServiceInteractor interactor;
  @NonNull private ExecutedOffloader pasteTime = new ExecutedOffloader.Empty();

  SinglePastePresenterImpl(@NonNull PasteServiceInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubPasteTime();
  }

  @Override public void onPostDelayedEvent() {
    unsubPasteTime();
    pasteTime = interactor.getPasteDelayTime()
        .onError(throwable -> Timber.e(throwable, "onError onPostDelayedEvent"))
        .onResult(delay -> getView(view -> view.postDelayedEvent(delay)))
        .execute();
  }

  private void unsubPasteTime() {
    if (!pasteTime.isCancelled()) {
      pasteTime.cancel();
    }
  }
}
