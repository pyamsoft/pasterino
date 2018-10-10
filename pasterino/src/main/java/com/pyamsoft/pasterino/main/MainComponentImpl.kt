package com.pyamsoft.pasterino.main

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pydroid.loader.LoaderModule

class MainComponentImpl(
  private val owner: LifecycleOwner,
  private val loaderModule: LoaderModule,
  private val mainSettingsModule: MainModule,
  private val pasteServiceModule: PasteServiceModule
) : MainComponent {

  override fun inject(fragment: MainFragment) {
    fragment.imageLoader = loaderModule.provideImageLoader()
    fragment.pasteViewModel = pasteServiceModule.getViewModel(owner)
  }

  override fun inject(fragment: MainSettingsPreferenceFragment) {
    fragment.viewModel = mainSettingsModule.getViewModel(owner)
    fragment.publisher = pasteServiceModule.getPublisher()
  }

}