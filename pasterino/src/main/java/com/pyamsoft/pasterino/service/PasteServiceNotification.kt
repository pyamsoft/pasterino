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

package com.pyamsoft.pasterino.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.pyamsoft.pasterino.R
import timber.log.Timber

object PasteServiceNotification {

  private const val ID = 1005
  private const val RC = 1005

  @JvmStatic
  internal fun start(context: Context) {
    if (PasteService.isRunning) {
      Timber.d("Start notification %d", ID)
      getNotificationManager(context).notify(ID, createNotification(context))
    }
  }

  @JvmStatic
  internal fun stop(context: Context) {
    Timber.d("Stop notification %d", ID)
    getNotificationManager(context).cancel(ID)
  }

  @JvmStatic
  @CheckResult
  private fun getNotificationManager(context: Context): NotificationManager {
    return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }

  @JvmStatic
  @CheckResult
  private fun createNotification(context: Context): Notification {
    val appContext = context.applicationContext
    val singlePasteIntent = Intent(appContext, SinglePasteService::class.java)
    val notificationChannelId = "pasterino_foreground"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      setupNotificationChannel(appContext, notificationChannelId)
    }
    return NotificationCompat.Builder(appContext, notificationChannelId)
        .apply {
          setContentTitle(appContext.getString(R.string.app_name))
          setSmallIcon(R.drawable.ic_paste_notification)
          setContentText("Pasterino Plzarino")
          setContentIntent(PendingIntent.getService(appContext, RC, singlePasteIntent, 0))
          setWhen(0)
          setOngoing(true)
          setAutoCancel(false)
          setNumber(0)
          priority = NotificationCompat.PRIORITY_MIN
          color = ContextCompat.getColor(appContext, R.color.green500)
        }
        .build()
  }

  @JvmStatic
  @RequiresApi(VERSION_CODES.O)
  private fun setupNotificationChannel(
    context: Context,
    notificationChannelId: String
  ) {
    val name = "Paste Service"
    val desc = "Notification related to the Pasterino service"
    val importance = NotificationManager.IMPORTANCE_MIN
    val notificationChannel = NotificationChannel(notificationChannelId, name, importance).apply {
      lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      description = desc
      enableLights(false)
      enableVibration(false)
    }

    Timber.d("Create notification channel with id: %s", notificationChannelId)
    val notificationManager: NotificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
  }
}
