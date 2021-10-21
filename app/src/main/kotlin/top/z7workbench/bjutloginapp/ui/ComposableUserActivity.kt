package top.z7workbench.bjutloginapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.appcompattheme.AppCompatTheme
import top.z7workbench.bjutloginapp.LoginApp
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.model.BundledUser
import top.z7workbench.bjutloginapp.model.UserViewModel

class ComposableUserActivity : ComponentActivity() {
    val viewModel by viewModels<UserViewModel>()
    val dao by lazy { (application as LoginApp).appDatabase.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val users = dao.allUsers()
        setContent {
            UserFramework(users.value ?: listOf(), -1)
        }
    }

    @Composable
    fun UserFramework(users: List<BundledUser>, currentId: Int) {
        AppCompatTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(id = R.string.user))
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
                if (users.isNotEmpty()) UserList(users, currentId)
                else UserPlaceholder()
            }
        }
    }
}

@Composable
fun UserPlaceholder() {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_group),
            contentDescription = null,
            modifier = Modifier
                .padding(all = 2.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.placeholder),
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun UserCard(user: BundledUser, isSelected: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(all = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.Start)
                )
                Text(
                    text = user.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
//                    .fillMaxWidth()
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically),
                    fontSize = 16.sp
                )
            }
//            Divider(
//                modifier = Modifier
//                    .padding(all = 1.dp)
//                    .fillMaxHeight()
//            )
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_edit
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_delete
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.End)
                        .align(Alignment.CenterVertically)
                        .size(24.dp),
                )
            }
        }
    }
}

@Composable
fun UserList(usernames: List<BundledUser>, currentId: Int) {
    LazyColumn {
        items(usernames) {
            UserCard(user = it, isSelected = currentId == usernames.indexOf(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UserList(
        usernames = listOf(
            BundledUser(0, "123"),
            BundledUser(1, "456"),
            BundledUser(2, "789")
        ), currentId = 2
    )
//    UserPlaceholder()
}