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
import android.text.Spannable;
import com.pyamsoft.pasterino.BuildConfig;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.app.service.PasteService;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.StringUtil;

public final class MainActivity extends DonationActivityBase
    implements RatingDialog.ChangeLogProvider {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_Pasterino_Light);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    RatingDialog.showRatingDialog(this, this);

    if (PasteService.isRunning()) {
      showMainFragment();
    } else {
      showAccessibilityRequestFragment();
    }
  }

  private void showAccessibilityRequestFragment() {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main_container, new AccessibilityRequestFragment())
        .commit();
  }

  private void showMainFragment() {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main_container, new MainSettingsFragment())
        .commit();
  }

  @NonNull @Override protected String getPlayStoreAppPackage() {
    return getPackageName();
  }

  @NonNull @Override public Spannable getChangeLogText() {
    // The changelog text
    final String title = "What's New in Version " + BuildConfig.VERSION_NAME;
    final String line1 = "BUGFIX: Code cleanup and general bugfixes";
    final String line2 = "FEATURE: This change log screen";

    // Turn it into a spannable
    final Spannable spannable = StringUtil.createBuilder(title, "\n\n", line1, "\n\n", line2);

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

  @NonNull @Override public String getChangeLogPackageName() {
    return getPackageName();
  }

  @Override public int getChangeLogVersion() {
    return BuildConfig.VERSION_CODE;
  }
}
