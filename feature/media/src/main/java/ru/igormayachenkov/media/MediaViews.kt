package ru.igormayachenkov.media

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.roundToLong

private const val TAG = "myapp.MediaViews"

@Composable
fun MediaRow(context: Context, mediaList:List<AbstractFile>) {
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
fun FileThumb(context: Context, file: AbstractFile){

    var loadingState by remember {
        mutableStateOf<LoadingState>(
            if(file.isLoaded) LoadingState.Loaded else LoadingState.Unloaded
        )
    }

    when(loadingState){
        LoadingState.Unloaded -> Text(text = "u")
        is LoadingState.Error -> Text((loadingState as LoadingState.Error).error )
        LoadingState.Loading  -> Text("Loading...")
        is LoadingState.Loaded -> when(file){
            is BinaryFile -> ThumbBinary (file)
            is ImageFile  -> ThumbImage  (file)
        }
    }
    LaunchedEffect("once") {
        if(loadingState==LoadingState.Unloaded) {
            Log.w(TAG, "load media file=${file.file.name}")
            loadingState = LoadingState.Loading
            // Do load
            try {
                delay((Math.random() * 3000).roundToLong())
                //if(Math.random()<0.2) throw Exception("load error")

                FileFactory.loadFile(file)

                loadingState = LoadingState.Loaded
            } catch (e: Exception) {
                loadingState = LoadingState.Error(e.message ?: e.toString())
            }
        }
    }


}



@Composable
fun ThumbBinary(file: BinaryFile){
    Text(file.buffer!!.size.toString())
}

@Composable
fun ThumbImage(file: ImageFile){
    //Image(painter = painterResource(id = R.drawable.cat), contentDescription = "", Modifier.size(50.dp))
    Image(
        bitmap = file.bitmap!!.asImageBitmap(),
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
//            MediaFile(File("One")),
//            MediaFile(File("Two")),
//            MediaFile(File("Three")),
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MediaFilePreview_Preview() {
    val context = LocalContext.current
    FileThumb(context = context,
        //file = MediaFile(File("filename"))
        file = ImageFile(File("example")).apply { bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cat) }
    )

}