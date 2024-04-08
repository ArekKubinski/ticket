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

package repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.internal.http2.StreamResetException
import retrofit.dao.NasdaqDao
import retrofit.model.NasdaqResponse
import retrofit2.Response

@Suppress("RedundantSuspendModifier")
open class NasdaqRepository(
	private val nasdaqDao: NasdaqDao
) {
	
	suspend fun getStocks(done: ((response: NasdaqResponse?) -> Unit)? = null): NasdaqResponse? {
		val result: Response<NasdaqResponse>
		try {
			result = nasdaqDao.getStocks().execute()
		} catch (e: StreamResetException) {
			e.printStackTrace()
			return null
		}
		if (result.isSuccessful) {
			done?.invoke(result.body())
			return result.body()
		} else {
			val gson = Gson()
			val type = object : TypeToken<NasdaqResponse>() {}.type
			val errorResponse: NasdaqResponse? =
				gson.fromJson(result.errorBody()!!.charStream(), type)
			if (errorResponse != null) {
				done?.invoke(errorResponse)
				return errorResponse
			} else {
				done?.invoke(null)
				return null
			}
		}
	}
}