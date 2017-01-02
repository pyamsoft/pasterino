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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.databinding.FragmentMainBinding;
import com.pyamsoft.pasterino.presenter.main.MainSettingsPresenter;
import com.pyamsoft.pasterino.presenter.main.MainSettingsPresenterLoader;
import com.pyamsoft.pasterino.service.PasteService;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.design.fab.HideScrollFABBehavior;
import com.pyamsoft.pydroid.design.util.FABUtil;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;

public class MainSettingsFragment extends ActionBarFragment implements MainSettingsPresenter.View {

  @NonNull public static final String TAG = "MainSettingsFragment";
  @NonNull private static final String KEY_PRESENTER = "key_settings_presenter";
  @NonNull private final AsyncDrawable.Mapper drawableMap = new AsyncDrawable.Mapper();
  @SuppressWarnings("WeakerAccess") MainSettingsPresenter presenter;
  private long loadedKey;
  private FragmentMainBinding binding;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<MainSettingsPresenter>() {

              @NonNull @Override public PersistLoader<MainSettingsPresenter> createLoader() {
                return new MainSettingsPresenterLoader();
              }

              @Override public void onPersistentLoaded(@NonNull MainSettingsPresenter persist) {
                presenter = persist;
              }
            });
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentMainBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFAB();
    displayPreferenceFragment();
  }

  private void setupFAB() {
    binding.mainSettingsFab.setOnClickListener(view -> {
      if (PasteService.isRunning()) {
        presenter.clickFabServiceRunning();
      } else {
        presenter.clickFabServiceIdle();
      }
    });
    FABUtil.setupFABBehavior(binding.mainSettingsFab, new HideScrollFABBehavior(10));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    drawableMap.clear();
    binding.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
    Pasterino.getRefWatcher(this).watch(this);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey, MainSettingsPresenter.class);
    super.onSaveInstanceState(outState);
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(false);
    presenter.setFABFromState(PasteService.isRunning());
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  private void displayPreferenceFragment() {
    final FragmentManager fragmentManager = getChildFragmentManager();
    if (fragmentManager.findFragmentByTag(MainSettingsPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.fragment_container, new MainSettingsPreferenceFragment(),
              MainSettingsPreferenceFragment.TAG)
          .commit();
    }
  }

  @Override public void onFABEnabled() {
    final AsyncMap.Entry task =
        AsyncDrawable.load(R.drawable.ic_help_24dp).into(binding.mainSettingsFab);
    drawableMap.put("fab", task);
  }

  @Override public void onFABDisabled() {
    final AsyncMap.Entry task =
        AsyncDrawable.load(R.drawable.ic_service_start_24dp).into(binding.mainSettingsFab);
    drawableMap.put("fab", task);
  }

  @Override public void onCreateAccessibilityDialog() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new AccessibilityRequestDialog(),
        "accessibility");
  }

  @Override public void onDisplayServiceInfo() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new ServiceInfoDialog(),
        "servce_info");
  }
}
