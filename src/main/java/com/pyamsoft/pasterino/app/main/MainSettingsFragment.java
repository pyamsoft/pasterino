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

package com.pyamsoft.pasterino.app.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.app.notification.PasteServiceNotification;
import com.pyamsoft.pasterino.dagger.main.DaggerMainSettingsComponent;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public final class MainSettingsFragment extends PreferenceFragmentCompat
    implements MainSettingsPresenter.MainSettingsView {

  @Inject MainSettingsPresenter presenter;

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    DaggerMainSettingsComponent.builder()
        .pasterinoComponent(Pasterino.getInstance().getPasterinoComponent())
        .build()
        .inject(this);

    addPreferencesFromResource(R.xml.preferences);

    final Preference explain = findPreference(getString(R.string.explain_key));
    explain.setOnPreferenceClickListener(preference -> {
      AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new HowToDialog(), "howto");
      return true;
    });

    final Preference resetAll = findPreference(getString(R.string.clear_all_key));
    resetAll.setOnPreferenceClickListener(preference -> {
      Timber.d("Reset settings onClick");
      assert presenter != null;
      presenter.clearAll();
      return true;
    });

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    upgradeInfo.setOnPreferenceClickListener(preference -> {
      final FragmentActivity activity = getActivity();
      if (activity instanceof RatingDialog.ChangeLogProvider) {
        final RatingDialog.ChangeLogProvider provider = (RatingDialog.ChangeLogProvider) activity;
        RatingDialog.showRatingDialog(activity, provider, true);
      } else {
        throw new ClassCastException("Activity is not a change log provider");
      }
      return true;
    });
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    presenter.bindView(this);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    presenter.unbindView();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override public void showConfirmDialog() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new ConfirmationDialog(),
        "confirm");
  }

  @Override public void onClearAll() {
    PasteServiceNotification.stop(getContext());
    android.os.Process.killProcess(android.os.Process.myPid());
  }
}
