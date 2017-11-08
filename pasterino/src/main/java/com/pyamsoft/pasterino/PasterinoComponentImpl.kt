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

package com.pyamsoft.pasterino

import com.pyamsoft.pasterino.base.PasterinoModule
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainFragment
import com.pyamsoft.pasterino.main.MainSettingsModule
import com.pyamsoft.pasterino.main.MainSettingsPreferenceFragment
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pasterino.service.SinglePasteService

internal class PasterinoComponentImpl internal constructor(
    private val module: PasterinoModule) : PasterinoComponent {

  private val mainSettingsModule: MainSettingsModule = MainSettingsModule(module)
  private val pasteServiceModule: PasteServiceModule = PasteServiceModule(module)

  override fun inject(fragment: MainFragment) {
    fragment.imageLoader = module.provideImageLoader()
  }

  override fun inject(service: SinglePasteService) {
    service.presenter = pasteServiceModule.getSinglePresenter()
    service.publisher = pasteServiceModule.getPasteServicePublisher()
  }

  override fun inject(service: PasteService) {
    service.presenter = pasteServiceModule.getPasteServicePresenter()
  }

  override fun inject(fragment: MainSettingsPreferenceFragment) {
    fragment.presenter = mainSettingsModule.getSettingsPreferencePresenter()
    fragment.publisher = pasteServiceModule.getPasteServicePublisher()
  }

  override fun inject(dialog: ConfirmationDialog) {
    dialog.publisher = mainSettingsModule.getSettingsPreferencePublisher()
  }

}