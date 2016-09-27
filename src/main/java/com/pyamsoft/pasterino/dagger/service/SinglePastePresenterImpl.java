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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pasterino.app.service.SinglePastePresenter;
import com.pyamsoft.pydroid.presenter.PresenterBase;

class SinglePastePresenterImpl extends PresenterBase<SinglePastePresenter.SinglePasteProvider>
    implements SinglePastePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final PasteServiceInteractor interactor;
  @Nullable private AsyncTask<Void, Void, Long> pasteTime;

  SinglePastePresenterImpl(@NonNull PasteServiceInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubPasteTime();
  }

  @Override public void onPostDelayedEvent() {
    unsubPasteTime();
    pasteTime = interactor.getPasteDelayTime(
        item -> getView((BoundView<SinglePasteProvider>) view -> view.postDelayedEvent(item)));
  }

  private void unsubPasteTime() {
    if (pasteTime != null) {
      if (!pasteTime.isCancelled()) {
        pasteTime.cancel(true);
      }
    }
  }
}
