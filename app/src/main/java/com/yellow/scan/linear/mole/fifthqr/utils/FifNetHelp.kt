package com.yellow.scan.linear.mole.fifthqr.utils

import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class FifNetHelp {
    private val client = OkHttpClient.Builder()
        .hostnameVerifier { _, _ -> true }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    interface Callback {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }

    fun executeMapRequest(url: String, map: Map<String, Any>, callback: Callback) {
        val urlBuilder = buildUrlWithQueryParameters(url, map)
        val request = buildRequest(urlBuilder)

        enqueueRequest(request, callback)
    }

    private fun buildUrlWithQueryParameters(url: String, map: Map<String, Any>): HttpUrl.Builder {
        val urlBuilder = url.toHttpUrl().newBuilder()
        map.forEach { entry ->
            urlBuilder.addEncodedQueryParameter(
                entry.key,
                encodeParameterValue(entry.value.toString())
            )
        }
        return urlBuilder
    }

    private fun buildRequest(urlBuilder: HttpUrl.Builder): Request {
        return Request.Builder()
            .get()
            .url(urlBuilder.build())
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()
    }

    private fun enqueueRequest(request: Request, callback: Callback) {
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    callback.onSuccess(responseBody)
                } else {
                    callback.onFailure(responseBody.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Network error")
            }
        })
    }

    private fun encodeParameterValue(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }


    fun postPutData(url: String, body: Any, callback: Callback) {
        val requestBody = createRequestBody(body)
        val request = buildRequest(url, requestBody)

        enqueueRequestPost(request, callback)
    }

    private fun createRequestBody(body: Any): RequestBody {
        return RequestBody.create("application/json".toMediaTypeOrNull(), body.toString())
    }

    private fun buildRequest(url: String, requestBody: RequestBody): Request {
        return Request.Builder()
            .url(url)
            .post(requestBody)
            .tag(requestBody)
            .build()
    }

    private fun enqueueRequestPost(request: Request, callback: Callback) {
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    callback.onSuccess(responseBody)
                } else {
                    callback.onFailure(responseBody.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Network error")
            }
        })
    }
}