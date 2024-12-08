package fhnw.emoba.thatsapp.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import fhnw.emoba.thatsapp.model.ThatsAppModel




@Composable
fun PhotoBootUI(model: ThatsAppModel) {
    MaterialTheme(colorScheme = lightColorScheme(primary            = Color(0xFF2E78D7),
        primaryContainer   = Color(0xFF2E78D7),
        onPrimaryContainer = Color.White)
    ) {
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            topBar                       = { Bar(model) },
            content                      = { Body(model) },
            floatingActionButton         = { FAB(model) },
            floatingActionButtonPosition = FabPosition.Center,
        )

        Notification(model, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Bar(model: ThatsAppModel) {
    with(model) {
        TopAppBar(title  = { Text(title) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor    = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,),
        )
    }
}


@Composable
//public fun Body(model: ThatsAppModel, paddingValues: PaddingValues) {
public fun Body(model: ThatsAppModel) {
    with(model) {
        ConstraintLayout(modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()) {
            val margin = 10.dp

            val (messageBox, selfie) = createRefs()

            if (photo != null) {
                Photo(bitmap   = photo!!,
                      model    = model,
                      modifier = Modifier.constrainAs(selfie) {
                              top.linkTo(parent.top, margin)
                              start.linkTo(parent.start, margin)
                              end.linkTo(parent.end, margin)
                              width = Dimension.fillToConstraints})
            } else {
                MessageBox(Modifier.constrainAs(messageBox) {
                        centerTo(parent)
                    })
            }
        }
    }
}

@Composable
private fun Photo(bitmap: Bitmap, model: ThatsAppModel, modifier: Modifier){
    with(model){
        Image(bitmap             = bitmap.asImageBitmap(),
              contentDescription = "",
              modifier           = modifier.clickable(onClick = { println("Rotating not implemented yet") }))
    }
}

@Composable
private fun MessageBox(modifier: Modifier){
    Text(text     = "Take a Picture",
         style    = MaterialTheme.typography.titleSmall,
         modifier = modifier)
}

@Composable
private fun FAB(model: ThatsAppModel) {
    with(model) {
        FloatingActionButton(
            onClick         = { takePhoto() },
            containerColor = MaterialTheme.colorScheme.primary
        ) { Icon(Icons.Filled.CameraAlt, "") }
    }
}


@Composable
private fun Notification(model: ThatsAppModel, snackbarState: SnackbarHostState) {
    with(model){
        if (notificationMessage.isNotBlank()) {
            LaunchedEffect(snackbarState){
                snackbarState.showSnackbar(message     = notificationMessage,
                                           actionLabel = "OK")
                notificationMessage = ""
            }
        }
    }
}