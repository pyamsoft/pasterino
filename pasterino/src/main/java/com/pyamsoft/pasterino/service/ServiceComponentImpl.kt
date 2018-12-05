package com.pyamsoft.pasterino.service

import androidx.lifecycle.LifecycleOwner

class ServiceComponentImpl(
  private val owner: LifecycleOwner,
  private val pasteServiceModule: PasteServiceModule
) : ServiceComponent {

}
