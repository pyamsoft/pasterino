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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.app.fragment.SettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.popHide
import com.pyamsoft.pydroid.ui.util.popShow
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.widget.HideOnScrollListener
import timber.log.Timber

class MainSettingsPreferenceFragment : SettingsPreferenceFragment() {

  internal lateinit var viewModel: MainViewModel
  internal lateinit var publisher: Publisher<ServiceEvent>
  internal lateinit var theming: Theming

  private var hideOnScrollListener: HideOnScrollListener? = null

  override val rootViewContainer: Int = R.id.main_container

  override val preferenceXmlResId: Int = R.xml.preferences

  override val applicationName: String
    get() = getString(R.string.app_name)

  override val bugreportUrl: String = "https://github.com/pyamsoft/pasterino/issues"

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusMainComponent(viewLifecycleOwner)
        .inject(this)

    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupExplainButton()
    setupDarkTheme()
    attachOnScrollListener()
    viewModel.onClearAllEvent { onClearAll() }
  }

  private fun setupExplainButton() {
    val explain = findPreference(getString(R.string.explain_key))
    explain.setOnPreferenceClickListener {
      HowToDialog().show(requireActivity(), "howto")
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupDarkTheme() {
    val darkTheme = findPreference(getString(R.string.dark_mode_key))
    darkTheme.setOnPreferenceChangeListener { _, newValue ->
      if (newValue is Boolean) {
        theming.setDarkTheme(newValue)
        requireActivity().recreate()
        return@setOnPreferenceChangeListener true
      } else {
        return@setOnPreferenceChangeListener false
      }
    }
  }

  private fun attachOnScrollListener() {
    val mainFragment = requireActivity().supportFragmentManager.findFragmentByTag(MainFragment.TAG)
    if (mainFragment is MainFragment) {
      val fab = mainFragment.getFloatingActionButton()
      val listener = HideOnScrollListener.withView(fab) {
        if (it) {
          fab.popShow()
        } else {
          fab.popHide()
        }
      }

      listView.addOnScrollListener(listener)
      hideOnScrollListener = listener
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    hideOnScrollListener?.also { listView?.removeOnScrollListener(it) }
    hideOnScrollListener = null
  }

  private fun onClearAll() {
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

  override fun onClearAllClicked() {
    ConfirmationDialog().show(requireActivity(), "confirm")
  }

  companion object {

    const val TAG = "MainSettingsPreferenceFragment"
  }
}
