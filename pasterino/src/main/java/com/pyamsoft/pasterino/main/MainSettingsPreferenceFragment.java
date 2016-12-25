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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.view.View;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.service.PasteService;
import com.pyamsoft.pasterino.service.PasteServiceNotification;
import com.pyamsoft.pasterino.service.SinglePasteService;
import com.pyamsoft.pasterinopresenter.main.MainSettingsPreferencePresenter;
import com.pyamsoft.pasterinopresenter.main.MainSettingsPreferencePresenterLoader;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment;
import timber.log.Timber;

public class MainSettingsPreferenceFragment extends ActionBarSettingsPreferenceFragment
    implements MainSettingsPreferencePresenter.MainSettingsView {

  @NonNull public static final String TAG = "MainSettingsPreferenceFragment";
  @NonNull private static final String KEY_PRESENTER = "key_main_presenter";
  @SuppressWarnings("WeakerAccess") MainSettingsPreferencePresenter presenter;
  private long loadedKey;

  @CheckResult @NonNull MainSettingsPreferencePresenter getPresenter() {
    if (presenter == null) {
      throw new NullPointerException("Presenter is NULL");
    }

    return presenter;
  }

  @NonNull @Override protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.LAST;
  }

  @Override protected int getRootViewContainer() {
    return R.id.main_container;
  }

  @NonNull @Override protected String getApplicationName() {
    return getString(R.string.app_name);
  }

  @Override protected int getPreferenceXmlResId() {
    return R.xml.preferences;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<MainSettingsPreferencePresenter>() {
              @NonNull @Override
              public PersistLoader<MainSettingsPreferencePresenter> createLoader() {
                return new MainSettingsPreferencePresenterLoader();
              }

              @Override
              public void onPersistentLoaded(@NonNull MainSettingsPreferencePresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference explain = findPreference(getString(R.string.explain_key));
    explain.setOnPreferenceClickListener(preference -> {
      AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new HowToDialog(), "howto");
      return true;
    });
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
    Pasterino.getRefWatcher(this).watch(this);
  }

  @Override protected boolean onClearAllPreferenceClicked() {
    presenter.clearAll();
    return super.onClearAllPreferenceClicked();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_PRESENTER, loadedKey, MainSettingsPreferencePresenter.class);
    super.onSaveInstanceState(outState);
  }

  @Override public void showConfirmDialog() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new ConfirmationDialog(),
        "confirm");
  }

  @Override public void onClearAll() {
    PasteServiceNotification.stop(getContext());
    SinglePasteService.stop(getContext());
    try {
      PasteService.finish();
    } catch (NullPointerException e) {
      Timber.e(e, "Expected exception when Service is NULL");
    }

    final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
        .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.clearApplicationUserData();
  }
}
