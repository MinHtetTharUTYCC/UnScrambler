package com.example.unscrambler.dictionary


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscrambler.GameUiState
import com.example.unscrambler.GameViewModel
import com.example.unscrambler.UnScramblerTheme
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Dictionary(
    gameViewModel: GameViewModel,
    onDefOff: ()-> Unit
){

    var word by remember { mutableStateOf(gameViewModel.currentWord) }
    var definitions by remember { mutableStateOf<List<Meaning>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val client = OkHttpClient()
    val gson = Gson()

    // Function to fetch data
    fun fetchData() {
        isLoading = true
        error = null
        val request = Request.Builder()
            .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
            .build()

        scope.launch(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error = "Word not found"
                    } else {
                        val responseBody = response.body?.string()
                        val jsonArray = gson.fromJson(responseBody, JsonArray::class.java)

                        val meanings = jsonArray.flatMap { jsonElement ->
                            val meaningsArray = jsonElement
                                .asJsonObject
                                .getAsJsonArray("meanings")

                            meaningsArray.map { meaningElement ->
                                val partOfSpeech = meaningElement
                                    .asJsonObject
                                    .get("partOfSpeech")
                                    .asString

                                val definitionsAndExamples = meaningElement
                                    .asJsonObject
                                    .getAsJsonArray("definitions")
                                    .flatMap { definitionElement ->
                                        val definition = definitionElement
                                            .asJsonObject
                                            .get("definition")
                                            ?.asString

                                        val example = definitionElement
                                            .asJsonObject
                                            .get("example")
                                            ?.asString

                                        Log.d("examples:",example.toString())

                                        listOf(Pair(definition,example))
                                    }

                                Meaning(partOfSpeech, definitionsAndExamples)

                            }
                        }

                        definitions = meanings
                    }
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                Log.d("err:",error.toString())
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = word)

                }
            )
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ){

            LaunchedEffect(true) {
                // This code block will be executed when the Composable is first launched or recomposed.
                // You can put your fetchData() call here to trigger the network request.
                fetchData()
            }

            Text(
                text = word,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            error?.let { errorMsg ->
                Text("Error: $errorMsg", color = Color.Red)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val groupedDefinitions = definitions.groupBy { it.partOfSpeech }

                groupedDefinitions.forEach { (partOfSpeech, meanings) ->
                    item {
                        Text(
                            text = "Part of Speech: ${partOfSpeech.capitalize()}" ,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(meanings) { meaning ->
                        DefinitionCard(meaning)
                    }
                }
            }
        }
    }



}



data class Meaning(val partOfSpeech: String, val definitionsAndExamples: List<Pair<String?, String?>>)

@Composable
fun DefinitionCard(meaning: Meaning) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
//            Text("Part of Speech: ${meaning.partOfSpeech}", fontWeight = FontWeight.Bold)


            for ((definition, example) in meaning.definitionsAndExamples) {
                if (definition != null) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("$definition", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.padding(8.dp))

                if (example != null) {
                    Row(
                    ){
                        Text(text = "Example: ")
                        Text(text = example)
                    }
                }
                else Text(text = "Example: unavailable")
                Spacer(modifier = Modifier.padding(4.dp))
                Divider()
            }

            if (meaning.definitionsAndExamples.isEmpty()) {
                Text(text = "No definitions or examples available")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryPreview(){
    UnScramblerTheme {

        val gameViewModel: GameViewModel = viewModel()
        val gameUiState by gameViewModel.uiState.collectAsState()

        com.example.unscrambler.dictionary.Dictionary(
            gameViewModel = GameViewModel(),
            onDefOff = {})
    }
}

