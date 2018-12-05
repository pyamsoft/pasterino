package com.pyamsoft.pasterino.main

import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pasterino.service.PasteServiceModule

class MainComponentImpl(
  private val owner: LifecycleOwner,
  private val preferenceScreen: PreferenceScreen,
  private val tag: String,
  private val mainSettingsModule: MainModule,
  private val pasteServiceModule: PasteServiceModule
) : MainComponent {

  override fun inject(fragment: MainSettingsPreferenceFragment) {
    fragment.viewModel = mainSettingsModule.getViewModel(tag)
    fragment.publisher = pasteServiceModule.getPublisher()
    fragment.settingsView = SettingsViewImpl(owner, preferenceScreen)
  }

}
