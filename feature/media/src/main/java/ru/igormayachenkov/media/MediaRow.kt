package ru.igormayachenkov.media

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MediaRow(context: Context, mediaList:List<MediaFile>) {
    Text("MediaRow")
    mediaList.apply {
        Row {
            forEachIndexed { index, mediaFile ->
                if(index>0) Spacer(modifier = Modifier.width(5.dp))
                MediaFilePreview(media = mediaFile, context = context)
            }
        }
    }
}


@Composable
fun MediaFilePreview(context: Context, media: MediaFile){
    val mediaState by media.state
    //var media by remember { mutableStateOf(0) }

//    val mediaFlow = remember { MutableStateFlow(0) }
//    val media by mediaFlow.collectAsState()
    // TODO instead of remember use getMediaForItem(item.id)
    Text(text = "${when(mediaState){
        MediaState.Empty -> ""
        is MediaState.Error -> (mediaState as MediaState.Error).error
        MediaState.Loading -> "Loading..."
        is MediaState.Success -> (mediaState as MediaState.Success).content
    }}")
    LaunchedEffect(media) {
//        Log.w(TAG, "load item's media #${item.id}")
//        delay((Math.random()*3000).roundToLong())
//        media++
        //mediaFlow.emit(13)
        media.load(context)
    }
}
