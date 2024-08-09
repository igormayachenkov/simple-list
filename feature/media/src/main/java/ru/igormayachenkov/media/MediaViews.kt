package ru.igormayachenkov.media

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import java.io.File

@Composable
fun MediaRow(context: Context, mediaList:List<IFile>) {
    mediaList.apply {
        Row {
            forEachIndexed { index, file ->
                if(index>0) Spacer(modifier = Modifier.width(5.dp))
                FileThumb(file = file, context = context)
            }
        }
    }
}


@Composable
fun FileThumb(context: Context, file: IFile){
    val loadingState by file.loadingState.collectAsState()

    when(loadingState){
        LoadingState.Unloaded -> Text(text = "u")
        is LoadingState.Error -> Text((loadingState as LoadingState.Error).error )
        LoadingState.Loading  -> Text("Loading...")
        is LoadingState.Success -> when(val content = (loadingState as LoadingState.Success).content){
            is FileContent.Binary -> ThumbBinary (content = content)
            is FileContent.Image  -> ThumbImage  (content = content)
        }
    }

    LaunchedEffect("once") {
//        Log.w(TAG, "load item's media #${item.id}")
//        delay((Math.random()*3000).roundToLong())
//        media++
        //mediaFlow.emit(13)
       file.load(context)
    }
}
@Composable
fun ThumbBinary(content: FileContent.Binary){
    Text(content.size.toString())
}

@Composable
fun ThumbImage(content: FileContent.Image){
    //Image(painter = painterResource(id = R.drawable.cat), contentDescription = "", Modifier.size(50.dp))
    Image(
        bitmap = content.bitmap.asImageBitmap(),
        contentDescription = "",
        modifier = Modifier.size(50.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun MediaRow_Preview() {
    val context = LocalContext.current
    MediaRow(
        context = context,
        mediaList = listOf(
            MediaFile(File("One")),
            MediaFile(File("Two")),
            MediaFile(File("Three")),
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MediaFilePreview_Preview() {
    val context = LocalContext.current
    FileThumb(context = context,
        //file = MediaFile(File("filename"))
        file = MockImageFile(BitmapFactory.decodeResource(context.resources, R.drawable.cat))
    )

}