@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.axelnonogram



import android.content.ClipData
import android.content.ClipboardManager
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.axelnonogram.game.NonogramGame
import com.example.axelnonogram.game.decompress

@Composable
fun App(viewModel: NonogramViewModel) {
    val navController = rememberNavController()

    val standardNonograms by viewModel.defaultNonograms.collectAsState()
    val importedNonograms by viewModel.importedNonograms.collectAsState()

    val abc by navController.currentBackStack.collectAsState()
    for (i in abc){
        Log.e("ADC","${i}")
    }
    Log.e("ADC","\n\n\n\n")

    Scaffold(
        topBar = {
//            PuzzleTopBar(navController)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        if(!navController.popBackStack()){
                            navController.navigate("main_menu")

                    } }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Back to menu"
                        )
                    }
                },
            )
        }

    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main_menu",
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            composable("main_menu") {
                Log.e("XYZ123", "mm")

                MainMenu(onMenuButton = {menu ->
                    navController.navigate("menu/${menu}")
                })
            }
            composable("menu/Default") {
                Log.e("XYZ123", "md")

                SubMenuButton(standardNonograms,"d", onButton = {menu,type,id,nonogram ->
                    navController.navigate("${menu}/${type}/${id}/${nonogram}")
                })
            }
            composable("menu/Import") {
                Log.e("XYZ123", "mi")
                SubMenuButton(importedNonograms,"i", onButton = {menu,type,id,nonogram ->
                    if (menu=="addNonogram"){
                        navController.navigate("${menu}")

                    }
                    else {
                        navController.navigate("${menu}/${type}/${id}/${nonogram}")
                    }
                })
//                MainMenuButtons(navController,importedNonograms,true)
            }
            composable("menu/User") {
                Log.e("XYZ123", "mu")
                SubMenuButton(importedNonograms,"i", onButton = {menu,type,id,nonogram ->
                    navController.navigate("${menu}/${type}/${id}/${nonogram}")
                })
//                MainMenuButtons(navController,standardNonograms)
            }
            composable("addNonogram") {
                Log.e("XYZ123", "an")

                AddNonogramMenu(viewModel,whenSuccesful = { if(!navController.popBackStack()){
                    navController.navigate("main_menu")

                } } )
            }
            composable("delete/{type}/{id}/{nonogram}"){ backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt()
                val nonogram = backStackEntry.arguments?.getString("nonogram").toString()
                Log.e("XYZ123", "dn")

                deleteNonogramMenu(viewModel,id!!, nonogram,onButton = {if(!navController.popBackStack()){
                    navController.navigate("main_menu")

                }})

            }
            composable("play/{type}/{id}/{nonogram}") { backStackEntry ->
                Log.e("XYZ123", "pn")

                val id = backStackEntry.arguments?.getString("id")?.toInt()
                val type = backStackEntry.arguments?.getString("type")

                when(type){
                    "d" -> {
                        for (nonogram in standardNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                    "i" -> {
                        for (nonogram in importedNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                    "u" -> {
                        for (nonogram in standardNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenu(onMenuButton: (menu:String)-> Unit){
    val buttons = remember { listOf("Default", "Import", "User")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        for (button in buttons) {
            Button(
                onClick = { onMenuButton(button) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = button,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }


}

@Composable
fun SubMenuButton(nonogramList: List<NonogramData>, type:String = "",onButton: (menu:String,type:String,id:Int,nonogram:String)-> Unit){
    val context = LocalContext.current

        Box(
        modifier = Modifier
            .fillMaxSize() // Fills the available screen size
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (nonogram in nonogramList){
                var buttonText = if (nonogram.nonogram.length>23) "${nonogram.nonogram.take(15)}...${nonogram.nonogram.takeLast(5)}" else nonogram.nonogram
                val navigatePath = "nonogram/${nonogram.type}/${nonogram.id}"

                Button(
                    onClick = {
                        onButton("play",nonogram.type,nonogram.id,nonogram.nonogram)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                            if (nonogram.isComplete) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Green
                            )
                        }
                            if (nonogram.type=="i"){
                                IconButton(onClick = {
                                    onButton("delete",nonogram.type,nonogram.id,nonogram.nonogram)

                                }
                                )
                                { Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                )

                                }
                                IconButton(onClick = {
                                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                                    val clip = ClipData.newPlainText("Nonogram: ",nonogram.nonogram)
                                    clipboardManager.setPrimaryClip(clip)
                                }){
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy to clipboard"
                                    )
                                }
                        }
                }
            }
        }
        if (type == "i") {
            FloatingActionButton(
                onClick = {
                    onButton("addNonogram","",0,"")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    }
}
@Composable
fun AddNonogramMenu(viewModel: NonogramViewModel,whenSuccesful:() -> Unit){
    var text = remember { mutableStateOf("") }
    var badInput = remember { mutableStateOf(false) }
    Column() {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text("Enter your text") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (decompress(text.value)[0]!="false"){

            val nonogram = NonogramData(
                type = "i",
                nonogram = text.value
            )
            Log.e("XYZ123","${nonogram.id}")
            viewModel.createNonogram(text.value,"i")
                whenSuccesful()
            }
            badInput.value=true
        }) { Text(text = "Save Nonogram")}
        if (badInput.value){
            Text(text = "Not a correct nonogram")
        }
    }
}

@Composable
fun deleteNonogramMenu(viewModel: NonogramViewModel, id: Int, nonogram: String, onButton: () -> Unit){
    Column {
        Log.e("ADC",nonogram)
        Text(text = "Do you want to delete ${nonogram}?")
        Row { Button(onClick = {
            viewModel.deleteNonogram(id)
            onButton()
        }){Text(text = "Yes")}
            Button(onClick = {onButton()}){Text(text = "No")}}
    }
}

