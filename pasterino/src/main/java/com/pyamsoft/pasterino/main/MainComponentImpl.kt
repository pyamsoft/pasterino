package com.pyamsoft.pasterino.main

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.service.PasteServiceModule

class MainComponentImpl(
  private val owner: LifecycleOwner,
  private val mainSettingsModule: MainModule,
  private val pasteServiceModule: PasteServiceModule
) : MainComponent {

  override fun inject(fragment: MainSettingsPreferenceFragment) {
    fragment.viewModel = mainSettingsModule.getViewModel(owner)
    fragment.publisher = pasteServiceModule.getPublisher()
  }

}
