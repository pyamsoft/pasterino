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

package com.pyamsoft.pasterino.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.pasterino.Pasterino;

public class ConfirmationDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setMessage(
        "Really clear all application settings?\n\nYou will have to manually restart the Accessibility Service component of Pasterino")
        .setPositiveButton("Yes", (dialogInterface, i) -> {
          dialogInterface.dismiss();
          sendConfirmationEvent();
        })
        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }

  @SuppressWarnings("WeakerAccess") void sendConfirmationEvent() {
    final FragmentManager fragmentManager = getFragmentManager();
    final Fragment settingsPreferenceFragment =
        fragmentManager.findFragmentByTag(MainSettingsPreferenceFragment.TAG);
    if (settingsPreferenceFragment instanceof MainSettingsPreferenceFragment) {
      ((MainSettingsPreferenceFragment) settingsPreferenceFragment).processClearRequest();
    } else {
      throw new ClassCastException("Fragment is not SettingsPreferenceFragment");
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Pasterino.getRefWatcher(this).watch(this);
  }
}
