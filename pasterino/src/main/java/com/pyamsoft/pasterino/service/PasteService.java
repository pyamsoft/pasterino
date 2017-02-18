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

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import timber.log.Timber;

public class PasteService extends AccessibilityService {

  private static PasteService instance = null;

  @NonNull @CheckResult public static synchronized PasteService getInstance() {
    if (instance == null) {
      throw new NullPointerException("PasteService instance is NULL");
    } else {
      //noinspection ConstantConditions
      return instance;
    }
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess")
  static synchronized void setInstance(@Nullable PasteService instance) {
    PasteService.instance = instance;
  }

  public static void finish() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      getInstance().disableSelf();
    }
  }

  @CheckResult public static boolean isRunning() {
    return instance != null;
  }

  public final void pasteIntoCurrentFocus() {
    final AccessibilityNodeInfo info =
        getRootInActiveWindow().findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
    if (info != null && info.isEditable()) {
      Timber.d("Perform paste on target: %s", info.getViewIdResourceName());
      info.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);
      Toast.makeText(getApplicationContext(), "Pasting text into current input focus.",
          Toast.LENGTH_SHORT).show();
    } else {
      Timber.e("No editable target to paste into");
    }
  }

  @Override public void onAccessibilityEvent(AccessibilityEvent event) {
    Timber.d("onAccessibilityEvent");
  }

  @Override public void onInterrupt() {
    Timber.e("onInterrupt");
  }

  @Override protected void onServiceConnected() {
    super.onServiceConnected();
    Timber.d("onServiceConnected");

    setInstance(this);
    PasteServiceNotification.start(this);
  }

  @Override public boolean onUnbind(Intent intent) {
    Timber.d("onUnbind");
    PasteServiceNotification.stop(this);
    setInstance(null);
    return super.onUnbind(intent);
  }
}
