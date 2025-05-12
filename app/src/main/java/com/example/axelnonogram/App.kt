@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.axelnonogram



import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.axelnonogram.game.CreateNonogram
import com.example.axelnonogram.game.InitiateCreateNonogram
import com.example.axelnonogram.game.NonogramGame
import com.example.axelnonogram.game.decompress
import androidx.compose.foundation.lazy.items

@Composable
fun App(viewModel: NonogramViewModel) {
    val navController = rememberNavController()

    val standardNonograms by viewModel.defaultNonograms.collectAsState()
    val importedNonograms by viewModel.importedNonograms.collectAsState()
    val userNonograms by viewModel.userNonograms.collectAsState()

//    val abc by navController.currentBackStack.collectAsState()
//    for (i in abc){
//        Log.e("ADC","${i}")
//    }
//    Log.e("ADC","\n\n\n\n")

    Scaffold(
        topBar = {
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
//                actions = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.Settings,
//                            contentDescription = "Back to menu"
//                        )
//                    }
//                },
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

                MainMenu(onMenuButton = {menu ->
                    navController.navigate("menu/${menu}")
                })
            }
            composable("menu/Default nonograms") {

                SubMenuButton(standardNonograms,"d", onButton = {menu,type,id,nonogram ->
                    navController.navigate("${menu}/${type}/${id}/${nonogram}")
                })
            }
            composable("menu/Imported nonograms") {
                SubMenuButton(importedNonograms,"i", onButton = {menu,type,id,nonogram ->
                    if (menu=="addNonogram"){
                        navController.navigate(menu)
                    }
                    else {
                        navController.navigate("${menu}/${type}/${id}/${nonogram}")
                    }
                })
            }
            composable("menu/User nonograms") {
                SubMenuButton(userNonograms,"u", onButton = {menu,type,id,nonogram ->
                    if (menu=="createNonogram"){
                        navController.navigate(menu)
                    }
                    else {
                        navController.navigate("${menu}/${type}/${id}/${nonogram}")
                    }
                })
            }
            composable("addNonogram") {

                AddNonogramMenu(viewModel,whenSuccesful = { if(!navController.popBackStack()){
                    navController.navigate("main_menu")

                } } )
            }
            composable("createNonogram"){
                InitiateCreateNonogram { nonogram ->
                    val nonogramData = NonogramData(type = "u", nonogram = nonogram)
                    viewModel.createNonogram(nonogramData)
                    navController.popBackStack()
                }
            }
            composable("edit/{type}/{id}/{nonogram}") { backStackEntry ->

                val id = backStackEntry.arguments?.getString("id")?.toInt()

                for (nonogram in userNonograms){
                    if (nonogram.id == id){
                        CreateNonogram(viewModel, nonogram)
                    }
                }
            }
            composable("delete/{type}/{id}/{nonogram}"){ backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt()
                val nonogram = backStackEntry.arguments?.getString("nonogram").toString()

                DeleteNonogramMenu(viewModel,id!!, nonogram,onButton = {if(!navController.popBackStack()){
                    navController.navigate("main_menu")

                }})

            }
            composable("play/{type}/{id}/{nonogram}") { backStackEntry ->

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
                        for (nonogram in userNonograms){
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
    val buttons = remember { listOf("Default nonograms", "Imported nonograms", "User nonograms")}

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
            .fillMaxSize()
    )
    {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nonogramList) { nonogram ->
                val buttonText = if (nonogram.nonogram.length > 15) {
                    "${nonogram.nonogram.take(8)}...${nonogram.nonogram.takeLast(4)}"
                } else {
                    nonogram.nonogram
                }

                Button(
                    onClick = {
                        onButton("play", nonogram.type, nonogram.id, nonogram.nonogram)
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
                    if (nonogram.type == "i" || nonogram.type == "u") {
                        IconButton(onClick = {
                            onButton("delete", nonogram.type, nonogram.id, nonogram.nonogram)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = {
                            val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                            val clip = ClipData.newPlainText("Nonogram: ", nonogram.nonogram)
                            clipboardManager.setPrimaryClip(clip)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy to clipboard"
                            )
                        }
                    }
                    if (nonogram.type == "u") {
                        IconButton(onClick = {
                            onButton("edit", nonogram.type, nonogram.id, nonogram.nonogram)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit"
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
        if (type == "u") {
            FloatingActionButton(
                onClick = {
                    onButton("createNonogram","",0,"")
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
                    viewModel.createNonogram(nonogram)
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
fun DeleteNonogramMenu(viewModel: NonogramViewModel, id: Int, nonogram: String, onButton: () -> Unit){
    Column(
        verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Do you want to delete ${nonogram}?")
        Row { Button(onClick = {
            viewModel.deleteNonogram(id)
            onButton()
        }){Text(text = "Yes")}
            Button(onClick = {onButton()}){Text(text = "No")}}
    }
}

