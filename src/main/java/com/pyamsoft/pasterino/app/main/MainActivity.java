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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.pasterino.BuildConfig;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pydroid.lib.AboutLibrariesFragment;
import com.pyamsoft.pydroid.app.activity.DonationActivity;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.StringUtil;

public class MainActivity extends DonationActivity implements RatingDialog.ChangeLogProvider {

  @BindView(R.id.main_toolbar) Toolbar toolbar;
  Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_Pasterino_Light);
    super.onCreate(savedInstanceState);

    unbinder = ButterKnife.bind(this);
    setupAppBar();
  }

  @Override protected int bindActivityToView() {
    setContentView(R.layout.activity_main);
    return R.id.ad_view;
  }

  @NonNull @Override protected String provideAdViewUnitId() {
    return getString(R.string.banner_ad_id);
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    RatingDialog.showRatingDialog(this, this);
  }

  @Override protected void onStart() {
    super.onStart();
    showMainFragment();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  @Override public void onBackPressed() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final int backStackCount = fragmentManager.getBackStackEntryCount();
    if (backStackCount > 0) {
      fragmentManager.popBackStack();
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    switch (itemId) {
      case android.R.id.home:
        onBackPressed();
        handled = true;
        break;
      default:
        handled = false;
    }
    return handled || super.onOptionsItemSelected(item);
  }

  void setupAppBar() {
    toolbar.setTitle(getString(R.string.app_name));
    setSupportActionBar(toolbar);
  }

  void showMainFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(MainSettingsPreferenceFragment.TAG) == null
        && fragmentManager.findFragmentByTag(AboutLibrariesFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.main_container, new MainSettingsFragment(), MainSettingsFragment.TAG)
          .commit();
    }
  }

  @NonNull @Override public Spannable getChangeLogText() {
    // The changelog text
    final String title = "What's New in Version " + BuildConfig.VERSION_NAME;
    final String line1 = "FEATURE: Added advertisements (can be disabled in settings)";
    final String line2 = "BUGFIX: Less memory usage";

    // Turn it into a spannable
    final Spannable spannable = StringUtil.createLineBreakBuilder(title, line1, line2);

    int start = 0;
    int end = title.length();
    final int largeSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int largeColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceSmall);
    final int smallColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceSmall);

    StringUtil.boldSpan(spannable, start, end);
    StringUtil.sizeSpan(spannable, start, end, largeSize);
    StringUtil.colorSpan(spannable, start, end, largeColor);

    start += end + 2;
    end += 2 + line1.length() + 2 + line2.length();

    StringUtil.sizeSpan(spannable, start, end, smallSize);
    StringUtil.colorSpan(spannable, start, end, smallColor);

    return spannable;
  }

  @Override public int getChangeLogIcon() {
    return R.mipmap.ic_launcher;
  }

  @NonNull @Override public String provideApplicationName() {
    return "Pasterino";
  }

  @Override public int getCurrentApplicationVersion() {
    return BuildConfig.VERSION_CODE;
  }
}
