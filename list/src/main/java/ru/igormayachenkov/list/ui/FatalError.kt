package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun FatalError(e:Exception) {

        Card( modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        ){
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text(text="FATAL ERROR", style = MaterialTheme.typography.h5)
                Text(text="${e.message}", style = MaterialTheme.typography.body1)
                Text(text="${e.stackTraceToString()}", style = MaterialTheme.typography.body2)

            }
        }
}

@Preview(showBackground = false)
@Composable
private fun FatalErrorPreview() {
    ListTheme(darkTheme = true) {
        FatalError(Exception("The exception message"))
    }
}