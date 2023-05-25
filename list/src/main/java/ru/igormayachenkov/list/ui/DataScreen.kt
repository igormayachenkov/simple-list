package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.data.Statistics
import ru.igormayachenkov.list.ui.theme.ListTheme

private const val TAG = "myapp.DataScreen"

@Composable
fun DataScreen(
    dataViewModel: DataViewModel= viewModel(),
    onHide:()->Unit
) {
    val statistics by dataViewModel.statistics.collectAsState()

    BackHandler(enabled = true, onBack = onHide)

    LaunchedEffect(key1 = null){
        Log.d(TAG,"onStart")
        dataViewModel.calculateStatistics()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onHide() },
        color = Color(0x80000040)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight(align = Alignment.Top),
        ) {
            Column(
                Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                //.padding(all = 16.dp)
            ) {
                Text("Your data status", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                // STATISTICS
                when(statistics){
                    Statistics.Loading->Text(text = "Loading...")
                    is Statistics.Error->Text(text = "Error: ${(statistics as Statistics.Error).message}", color = MaterialTheme.colors.error)
                    is Statistics.Success->{
                        val (nLists,nItems) = statistics as Statistics.Success
                        Row(Modifier.fillMaxWidth()) {
                            Text("number of lists:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
                            Text("$nLists")
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Text("number of items:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
                            Text("$nItems")
                        }
                    }
                }

                // ACTIONS
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = dataViewModel::save) {
                    Text("Save all in the archive")
                }
            }
        }
    }
}


@Preview(showBackground = false)
@Composable
fun DataScreenPreview() {
    ListTheme(darkTheme = true) {
        DataScreen(onHide = {})
    }
}