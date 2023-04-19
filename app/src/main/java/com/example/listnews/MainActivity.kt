package com.example.listnews

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.listnews.ui.theme.ListNewsTheme
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            ListNewsTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = "home"
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier,
    navController: NavController,
    startDestination: String = "home"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        /* creating route "home" */
        composable(route = "home") {
            /* Using composable function */
            MainList(navController)
        }
        /* creating route "post" */
        composable(route = "post/{id}") { backStackEntry ->
            /* Using composable function */
            PostDetail(
                navController = navController,
                id = backStackEntry.arguments?.getInt("id") ?: 0
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainList(navController: NavController) {
    val context = LocalContext.current
    val response = remember { mutableStateOf<JSONArray?>(null) }

    fetchPosts(context, response)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "List News")
                }
            )

        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding
        ) {
            items(response.value?.length() ?: 0) {index ->
                val post = response.value?.getJSONObject(index)
                ListItem(
                    leadingContent = {
                        Text((index + 1).toString())
                    },
                    headlineText = { Text(post?.getString("title") ?:"") },
                    trailingContent = {
                        Icon(
                            Icons.Filled.ArrowRight,
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate("post/${post?.getInt("id")}")
                    }
                )
                Divider()
            }
        }
    }
}


fun fetchPosts(context: Context, response: MutableState<JSONArray?>) {
    val url = "https://jsonplaceholder.typicode.com/posts"
    val queue = Volley.newRequestQueue(context)

    val jsonArrayRequest = JsonArrayRequest(
        Request.Method.GET, url, null, {resp ->
            response.value = resp

        }, {
            Log.e("Volley", "Error: ${it.message}")
        }
    )
    queue.add(jsonArrayRequest)
}

@Composable
fun PostDetail(navController: NavController, id: Int) {
    Text(text = "Post Detail: $id")
}

}