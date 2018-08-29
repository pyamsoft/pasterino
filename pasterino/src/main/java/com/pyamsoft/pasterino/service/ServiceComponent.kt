package com.pyamsoft.pasterino.service

interface ServiceComponent {

  fun inject(service: PasteService)

  fun inject(service: SinglePasteService)

}
