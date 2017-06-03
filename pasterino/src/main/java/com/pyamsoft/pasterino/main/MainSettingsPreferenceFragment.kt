/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.DialogUtil
import timber.log.Timber

class MainSettingsPreferenceFragment : ActionBarSettingsPreferenceFragment() {

  internal lateinit var presenter: MainSettingsPreferencePresenter

  override val isLastOnBackStack: AboutLibrariesFragment.BackStackState
    get() = AboutLibrariesFragment.BackStackState.LAST

  override val rootViewContainer: Int
    get() = R.id.main_container

  override val applicationName: String
    get() = getString(R.string.app_name)

  override val preferenceXmlResId: Int
    get() = R.xml.preferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.get().provideComponent().plusMainComponent().inject(this)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val explain = findPreference(getString(R.string.explain_key))
    explain.setOnPreferenceClickListener { _ ->
      DialogUtil.guaranteeSingleDialogFragment(activity, HowToDialog(), "howto")
      true
    }
  }

  override fun onStart() {
    super.onStart()
    presenter.registerOnEventBus {
      PasteServiceNotification.stop(context)
      SinglePasteService.stop(context)
      try {
        PasteService.finish()
      } catch (e: NullPointerException) {
        Timber.e(e, "Expected exception when Service is NULL")
      }

      Timber.d("Clear application data")
      val activityManager = context.applicationContext
          .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
    Pasterino.getRefWatcher(this).watch(this)
  }

  override fun onClearAllClicked() {
    DialogUtil.guaranteeSingleDialogFragment(activity, ConfirmationDialog(), "confirm")
  }

  companion object {

    const val TAG = "MainSettingsPreferenceFragment"
  }
}
