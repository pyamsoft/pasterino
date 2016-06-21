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

package com.pyamsoft.pasterino.app.service;

import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.dagger.service.PasteServiceInteractor;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Inject;

public final class SinglePastePresenter
    extends Presenter<SinglePastePresenter.SinglePasteProvider> {

  @NonNull private final PasteServiceInteractor interactor;

  @Inject public SinglePastePresenter(@NonNull PasteServiceInteractor interactor) {
    this.interactor = interactor;
  }

  public final void onPostDelayedEvent() {
    getView().postDelayedEvent(interactor.getPasteDelayTime());
  }

  public interface SinglePasteProvider {

    void postDelayedEvent(long delay);
  }
}
