/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import androidx.preference.Preference
import android.view.View
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.PasteServicePublisher
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.ui.app.fragment.SettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber

class MainSettingsPreferenceFragment : SettingsPreferenceFragment(),
    MainSettingsPreferencePresenter.View {

  internal lateinit var presenter: MainSettingsPreferencePresenter
  internal lateinit var publisher: PasteServicePublisher

  override val rootViewContainer: Int = R.id.main_container

  override val preferenceXmlResId: Int = R.xml.preferences

  override val applicationName: String
    get() = getString(R.string.app_name)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .inject(this)
    presenter.bind(viewLifecycleOwner, this)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val explain: Preference = findPreference(getString(R.string.explain_key))
    explain.setOnPreferenceClickListener {
      HowToDialog().show(requireActivity(), "howto")
      return@setOnPreferenceClickListener true
    }
  }

  override fun onClearAll() {
    requireContext().also {
      PasteServiceNotification.stop(it)
      SinglePasteService.stop(it)
      try {
        publisher.publish(ServiceEvent(ServiceEvent.Type.FINISH))
      } catch (e: NullPointerException) {
        Timber.e(e, "Expected exception when Service is NULL")
      }

      Timber.d("Clear application data")
      val activityManager = it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this)
        .watch(this)
  }

  override fun onClearAllClicked() {
    ConfirmationDialog().show(requireActivity(), "confirm")
  }

  companion object {

    const val TAG = "MainSettingsPreferenceFragment"
  }
}
