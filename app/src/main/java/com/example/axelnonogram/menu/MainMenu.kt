package com.example.axelnonogram.menu
//
//
//import android.R
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.pointer.consumeAllChanges
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.ui.unit.sp
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material3.TextButton
//import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
//import androidx.compose.material.icons.filled.CropSquare
//import androidx.compose.material.icons.outlined.DisabledByDefault
//import androidx.compose.material.icons.filled.Exposure
//import androidx.compose.material.icons.filled.OpenWith
//import androidx.compose.material.icons.filled.Redo
//import androidx.compose.material.icons.filled.Square
//import androidx.compose.material.icons.filled.Undo
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.geometry.Offset
//import kotlin.math.min
//import kotlin.math.max
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.unit.Dp
//import java.nio.file.WatchEvent
//
//
//
//
//@Composable
//fun MainMenu(){
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//        Button(
//            modifier =
//                Modifier.height(20.dp),
//            onClick = TODO(),
//
//            shape = TODO(),
//            colors = TODO(),
//
//        ) { }
//    }
//
//}
//
//
//@Composable
//fun MenuButtons(
//    text: String,
//    arguments: Any = false,
//    composable: @Composable () -> Unit
//) {
//
//}



//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun MenuScreen() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF7FCCE3)) // Light blue background color
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Top bar with Sign in button and counter
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Button(
//                    onClick = { /* Sign in action */ },
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(16.dp))
//                        .height(40.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF7FCCE3),
//                        contentColor = Color.Black
//                    )
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(id = android.R.drawable.ic_menu_view),
//                            contentDescription = "Eye icon",
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Sign in")
//                    }
//                }
//
//                Text(
//                    text = "53 / 1001",
//                    color = Color.Black
//                )
//            }
//
//            // Scrollable menu buttons
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Spacer(modifier = Modifier.height(32.dp)) // Space at the top
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_compass,
//                    text = "Continue",
//                    subtext = "???",
//                    percentage = "25%"
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_media_play,
//                    text = "Tutorial"
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_gallery,
//                    text = "Black-and-White Nonograms"
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_gallery,
//                    text = "Colored Nonograms"
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_mapmode,
//                    text = "Sent by Users"
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_edit,
//                    text = "My Nonograms"
//                )
//
//                // Bookmarks label
//                Text(
//                    text = "Bookmarks",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    textAlign = TextAlign.Center,
//                    color = Color.Black
//                )
//
//                MenuButton(
//                    icon = android.R.drawable.ic_menu_sort_by_size,
//                    text = "Weight Categories"
//                )
//
//                // Add more buttons as needed
//                Spacer(modifier = Modifier.height(32.dp)) // Space at the bottom
//            }
//        }
//    }
//}
//
//@Composable
//fun MenuButton(
//    icon: Int,
//    text: String,
//    subtext: String? = null,
//    percentage: String? = null
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Icon on the left
//            Image(
//                painter = painterResource(id = icon),
//                contentDescription = null,
//                modifier = Modifier.size(32.dp)
//            )
//
//            // Text in the center
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 8.dp)
//            ) {
//                if (subtext != null) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = text,
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Text(
//                            text = subtext,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
//                } else {
//                    Text(
//                        text = text,
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//
//            // Percentage on the right (if provided)
//            if (percentage != null) {
//                Text(
//                    text = percentage,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun MenuScreenPreview() {
//    MaterialTheme {
//        MenuScreen()
//    }
//}

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuOption(
    val text: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(
    option: MenuOption,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = option.onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Text centered in the button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            Text(
                text = option.text,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MenuButtonList(
    options: List<MenuOption>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF7FCCE6)) // Light blue background similar to the image
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.85f) // Set width to 85% of screen width
                .verticalScroll(rememberScrollState()) // Make it scrollable
                .padding(vertical = 16.dp)
        ) {
            options.forEach { option ->
                MenuButton(option = option)
            }
        }
    }
}



//
//@Preview(showBackground = true)
//@Composable
//fun MenuPreview() {
//    // Sample menu options for preview
//    val sampleOptions = listOf(
//        MenuOption("Continue"),
//        MenuOption("Tutorial"),
//        MenuOption("Black-and-White Nonograms"),
//        MenuOption("Colored Nonograms"),
//        MenuOption("Sent by Users"),
//        MenuOption("My Nonograms"),
//        MenuOption("Weight Categories")
//    )
//
//    MenuButtonList(options = sampleOptions)
//}

