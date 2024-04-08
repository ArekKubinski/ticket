/*
 * Copyright (c) 2024.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package retrofit.database

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.dao.NasdaqDao
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object RemoteDatabase {
	private const val API_NASDAQ = "https://api.nasdaq.com/"
	
	var errorHandlerMainActivityCallback: ((Throwable) -> Unit)? = null
	
	fun provideCoroutineContext() = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
		errorHandlerMainActivityCallback?.let { it(throwable) }
	}
	
	fun provideNasdaqDAO(): NasdaqDao {
		val httpClient = OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
		val retrofitBuilder = Retrofit.Builder()
			.baseUrl(API_NASDAQ)
			.addConverterFactory(GsonConverterFactory.create(Gson()))
			.callbackExecutor(Executors.newSingleThreadExecutor())
		val retrofit = retrofitBuilder.client(httpClient.build()).build()
		
		return retrofit.create(NasdaqDao::class.java)
	}
}