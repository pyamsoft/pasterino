/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import android.support.v4.app.NotificationManagerCompat
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
    val appContext = context.applicationContext
    return appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }

  @JvmStatic
  @CheckResult private fun createNotification(context: Context): Notification {
    val appContext = context.applicationContext
    val singlePasteIntent = Intent(appContext, SinglePasteService::class.java)
    val notificationChannelId: String = "pasterino_foreground"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      setupNotificationChannel(appContext, notificationChannelId)
    }
    return NotificationCompat.Builder(appContext, notificationChannelId).setContentTitle(
        appContext.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_notification)
        .setContentText("Pasterino Plzarino")
        .setContentIntent(PendingIntent.getService(appContext, RC, singlePasteIntent, 0))
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setColor(ContextCompat.getColor(appContext, R.color.green500))
        .setNumber(0)
        .build()
  }

  @RequiresApi(VERSION_CODES.O) private fun setupNotificationChannel(context: Context,
      notificationChannelId: String) {
    val name = "Paste Service"
    val description = "Notification related to the Pasterino service"
    val importance = NotificationManagerCompat.IMPORTANCE_MIN
    val notificationChannel = NotificationChannel(notificationChannelId, name, importance)
    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    notificationChannel.description = description
    notificationChannel.enableLights(false)
    notificationChannel.enableVibration(false)

    Timber.d("Create notification channel with id: %s", notificationChannelId)
    val notificationManager: NotificationManager = context.applicationContext.getSystemService(
        Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
  }
}
