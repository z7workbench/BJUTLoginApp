package top.z7workbench.bjutloginapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.appcompattheme.AppCompatTheme

class ComposableUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserFramework()
        }
    }

    @Composable
    fun UserFramework() {
        AppCompatTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text("主页")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                onBackPressed()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null)
                            }
                        }
                    )
                }) {
                UserList(usernames = listOf(), currentId = 0)
            }
        }
    }
}

@Composable
fun UserCard(user: String, isSelected: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(all = 8.dp)) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                modifier = Modifier.padding(all = 8.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = user,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun UserList(usernames: List<String>, currentId: Int) {
    LazyColumn {
        items(usernames) {
            UserCard(user = it, isSelected = currentId == usernames.indexOf(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UserList(usernames = listOf("123", "456", "789"), currentId = 2)
}