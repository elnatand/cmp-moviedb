package com.example.moviedb.feature.profile.ui

import CameraFactory
import ImagePickerFactory
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.model.Strings
import getPlatformContext
import rememberBitmapFromBytes

@Composable
fun ProfileRoute() {
    ProfileScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    var checked by remember { mutableStateOf(true) }
    val selectedBytes = remember { mutableStateOf(ByteArray(0)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = Strings.profile.get()) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Camera(selectedBytes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${Strings.user_name.get()}: Elna",
                    modifier = Modifier.weight(1f)
                )
                ProfileImage(selectedBytes)
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.notifications.get()
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
private fun Camera(selectedBytes: MutableState<ByteArray>) {
    val camera = CameraFactory(context = getPlatformContext()).createCamera()
    camera.RegisterCamera { bytes: ByteArray ->
        selectedBytes.value = bytes
    }
    Button(
        onClick = {
            camera.openCamera()
        },
        content = { Text("Open Camera") }
    )
}

@Composable
private fun ProfileImage(selectedBytes: MutableState<ByteArray>) {
    val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
    imagePicker.RegisterPicker { bytes: ByteArray ->
        selectedBytes.value = bytes
    }
    val contentScale = ContentScale.Crop

    val modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .clickable {
            imagePicker.pickImage()
        }

    if (selectedBytes.value.isNotEmpty()) {
        Image(
            bitmap = rememberBitmapFromBytes(selectedBytes.value),
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