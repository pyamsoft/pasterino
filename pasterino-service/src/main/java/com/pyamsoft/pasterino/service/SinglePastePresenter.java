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
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import timber.log.Timber;

class SinglePastePresenter extends SchedulerPresenter {

  @NonNull private final PasteServiceInteractor interactor;

  SinglePastePresenter(@NonNull PasteServiceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = Checker.checkNonNull(interactor);
  }

  @Override protected void onStop() {
    super.onStop();
  }

  /**
   * public
   */
  void postDelayedEvent(@NonNull SinglePasteCallback callback) {
    SinglePasteCallback pasteCallback = Checker.checkNonNull(callback);
    disposeOnStop(interactor.getPasteDelayTime()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pasteCallback::onPost,
            throwable -> Timber.e(throwable, "onError postDelayedEvent")));
  }

  interface SinglePasteCallback {

    void onPost(long delay);
  }
}
