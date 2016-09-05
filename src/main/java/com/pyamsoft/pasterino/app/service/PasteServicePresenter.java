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
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;
import com.pyamsoft.pydroid.dagger.presenter.Presenter;

public interface PasteServicePresenter
    extends Presenter<PasteServicePresenter.PasteServiceProvider> {

  void pasteClipboardIntoFocusedView(@Nullable AccessibilityNodeInfo target);

  interface PasteServiceProvider {

    void onPaste(@NonNull AccessibilityNodeInfo target);

    void stopPasteService();
  }
}
