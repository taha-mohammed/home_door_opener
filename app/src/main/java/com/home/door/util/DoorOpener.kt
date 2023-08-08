package com.home.door.util

import android.util.Log
import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import com.burgstaller.okhttp.CachingAuthenticatorDecorator
import com.burgstaller.okhttp.digest.CachingAuthenticator
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import com.home.door.data.room.DoorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.ConcurrentHashMap

object DoorOpener {
    internal suspend fun unlockDoor(door: DoorEntity): Result<Unit> = withContext(Dispatchers.IO) {

        val authenticator = DigestAuthenticator(Credentials(door.user, door.password))

        val authCache: Map<String, CachingAuthenticator> = ConcurrentHashMap()
        val client = OkHttpClient.Builder()
            .authenticator(CachingAuthenticatorDecorator(authenticator, authCache))
            .addInterceptor(AuthenticationCacheInterceptor(authCache))
            .build()

        val body = "<RemoteControlDoor><cmd>open</cmd></RemoteControlDoor>"
            .toRequestBody("application/xml".toMediaType())
        val request: Request = Request.Builder()
            .url("http://"+door.ip+"/ISAPI/AccessControl/RemoteControl/door/1")
            .put(body)
            .build()

        // Make the request
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e("TAG", "Response failed: " + response.message)
                return@withContext Result.failure(Throwable(response.message))
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error: " + e.message)
            return@withContext Result.failure(e)
        }
        Result.success(Unit)
    }
}