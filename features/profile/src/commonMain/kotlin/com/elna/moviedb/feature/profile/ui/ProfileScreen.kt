package com.elna.moviedb.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.camera
import com.elna.moviedb.resources.notifications
import com.elna.moviedb.resources.profile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import rememberCameraManager
import rememberGalleryManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    var checked by remember { mutableStateOf(true) }
    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.profile)) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(Res.string.profile)}: Elna",
                    modifier = Modifier.weight(1f)
                )
                Camera(imageBitmap)
                ProfileImage(imageBitmap)
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.notifications)
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    }
                )
            }
        }
    }
}

@Composable
private fun Camera(imageBitmap: MutableState<ImageBitmap?>) {
    val coroutineScope = rememberCoroutineScope()
    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            imageBitmap.value = it?.toImageBitmap()
        }
    }
    Button(
        modifier = Modifier.padding(end = 8.dp),
        onClick = {
            cameraManager.launch()
        },
        content = { Text(stringResource(Res.string.camera)) }
    )
}

@Composable
private fun ProfileImage(imageBitmap: MutableState<ImageBitmap?>) {
    val coroutineScope = rememberCoroutineScope()
    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            imageBitmap.value = it?.toImageBitmap()
        }
    }


    val contentScale = ContentScale.Crop

    val modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .clickable {
            galleryManager.launch()
        }

    val image = imageBitmap.value
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Image(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            modifier = modifier.background(Color.Gray),
            contentScale = contentScale
        )
    }
}