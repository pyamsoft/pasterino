/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.main

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.material.R as R2
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.databinding.ToolbarBinding
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.ui.app.ToolbarActivityProvider
import com.pyamsoft.pydroid.ui.privacy.addPrivacy
import com.pyamsoft.pydroid.ui.privacy.removePrivacy
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets
import javax.inject.Inject

internal class ActivityToolbarView
@Inject
internal constructor(
    theming: ThemeProvider,
    toolbarActivityProvider: ToolbarActivityProvider,
    parent: ViewGroup
) : BaseUiView<UnitViewState, UnitViewEvent, ToolbarBinding>(parent) {

  override val viewBinding = ToolbarBinding::inflate

  override val layoutRoot by boundView { appbar }

  init {
    doOnInflate {
      setupToolbar(toolbarActivityProvider, theming)

      layoutRoot.doOnApplyWindowInsets { v, insets, padding ->
        v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
      }
    }

    doOnTeardown {
      toolbarActivityProvider.setToolbar(null)
      binding.toolbar.removePrivacy()
    }
  }

  private fun setupToolbar(
      toolbarActivityProvider: ToolbarActivityProvider,
      theming: ThemeProvider
  ) {
    val theme =
        if (theming.isDarkTheme()) {
          R2.style.ThemeOverlay_MaterialComponents
        } else {
          R2.style.ThemeOverlay_MaterialComponents_Light
        }

    binding.toolbar.apply {
      popupTheme = theme
      toolbarActivityProvider.setToolbar(this)
      setTitle(R.string.app_name)
      ViewCompat.setElevation(this, 0F)
    }

    binding.toolbar.addPrivacy(
        viewScope, Pasterino.PRIVACY_POLICY_URL, Pasterino.TERMS_CONDITIONS_URL)
  }
}
