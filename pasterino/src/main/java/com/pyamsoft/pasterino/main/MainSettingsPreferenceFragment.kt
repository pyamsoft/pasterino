/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pasterino.main

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v7.preference.Preference
import android.view.View
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.PasteServicePublisher
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.DialogUtil
import timber.log.Timber

class MainSettingsPreferenceFragment : ActionBarSettingsPreferenceFragment(), MainSettingsPreferencePresenter.View {

  override fun provideBoundPresenters(): List<Presenter<*>> =
      super.provideBoundPresenters() + listOf(presenter)

  internal lateinit var presenter: MainSettingsPreferencePresenter
  internal lateinit var publisher: PasteServicePublisher

  override val isLastOnBackStack: AboutLibrariesFragment.BackStackState = AboutLibrariesFragment.BackStackState.LAST

  override val rootViewContainer: Int = R.id.main_container

  override val preferenceXmlResId: Int = R.xml.preferences

  override val applicationName: String
    get() = getString(R.string.app_name)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtain<PasterinoComponent>(context!!.applicationContext).inject(this)
    presenter.bind(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val explain: Preference = findPreference(getString(R.string.explain_key))
    explain.setOnPreferenceClickListener {
      DialogUtil.guaranteeSingleDialogFragment(activity, HowToDialog(), "howto")
      return@setOnPreferenceClickListener true
    }
  }

  override fun onClearAll() {
    context?.let {
      PasteServiceNotification.stop(it)
      SinglePasteService.stop(it)
      try {
        publisher.publish(ServiceEvent(ServiceEvent.Type.FINISH))
      } catch (e: NullPointerException) {
        Timber.e(e, "Expected exception when Service is NULL")
      }

      Timber.d("Clear application data")
      val activityManager = it.applicationContext
          .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this).watch(this)
  }

  override fun onClearAllClicked() {
    DialogUtil.guaranteeSingleDialogFragment(activity, ConfirmationDialog(), "confirm")
  }

  companion object {

    const val TAG = "MainSettingsPreferenceFragment"
  }
}
