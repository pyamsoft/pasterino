package com.pyamsoft.pasterino.service

import androidx.lifecycle.LifecycleOwner

class ServiceComponentImpl(
  private val owner: LifecycleOwner,
  private val pasteServiceModule: PasteServiceModule
) : ServiceComponent {

  override fun inject(service: PasteService) {
    service.viewModel = pasteServiceModule.getViewModel(owner)
  }

  override fun inject(service: SinglePasteService) {
    service.viewModel = pasteServiceModule.getViewModel(owner)
    service.publisher = pasteServiceModule.getPublisher()
  }

}
