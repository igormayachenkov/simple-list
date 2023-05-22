package ru.igormayachenkov.list.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.DataViewModel
import ru.igormayachenkov.list.ui.theme.ListTheme

private const val TAG = "myapp.DataScreen"

@Composable
fun DataScreen(dataViewModel: DataViewModel) {
    if(!dataViewModel.isVisible) return
    BackHandler(enabled = true, onBack = dataViewModel::hide)
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { dataViewModel.hide() },
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
                Text(text = "Bla bla bla...")
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
        DataScreen(dataViewModel = DataViewModel(isPreview = true))
    }
}