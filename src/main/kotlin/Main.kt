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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import kong.unirest.core.Unirest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import remote.model.NasdaqResponse
import repository.NasdaqRepository
import useCase.FetchNasdaqStocksUseCase
import yahoofinance.YahooFinance


@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

private val coroutineExceptionHandler =
    CoroutineExceptionHandler { coroutineContext, throwable ->
        println(throwable.message)
    }

fun main() = application {
    val ioScope = rememberCoroutineScope { Dispatchers.IO + coroutineExceptionHandler }
    val fetch = remember { FetchNasdaqStocksUseCase(NasdaqRepository()) }
    val stocks = remember { ioScope.launch {
        val i = fetch.invoke()
        println(i.data.totalRecords)
        for (j in i.data.table.rows) {
            println(j.symbol)
        }
        val response = Unirest.get("https://api.nasdaq.com/api/screener/stocks?limit=10000")
            .asObject(NasdaqResponse::class.java).body
        val y = YahooFinance.get("WIA")
        println(y)
        exitApplication()
    } }
//    Window(onCloseRequest = ::exitApplication) {
//        App()
//    }
}
