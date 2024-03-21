package com.example.vknewsclient.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.vknewsclient.R
import com.example.vknewsclient.domain.entity.User
import com.example.vknewsclient.presentation.core.getApplicationComponent

@Composable
fun ProfileScreen(
paddingValues: PaddingValues) {
    val component = getApplicationComponent()
    val viewModel: ProfileViewModel = viewModel(factory = component.getViewModelFactory())
    val state = viewModel.user.collectAsState()
    when (val currentState = state.value) {
        is ProfileScreenState.Content -> {
            ProfileScreenContent(
                paddingValues = paddingValues,
                user = currentState.user,
                onLogout = { viewModel.logout() }
            )
        }
        ProfileScreenState.Initial -> {}
    }
}

@Composable
private fun ProfileScreenContent(
    paddingValues: PaddingValues,
    user: User,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
                .clickable { onLogout() },
            imageVector = Icons.Default.ExitToApp,
            contentDescription = null
        )
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(top = 50.dp)
                .size(140.dp)
                .clip(shape = CircleShape)
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, text = "${user.firstName} ${user.lastName}"
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = user.city)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.Phone,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = user.phone)
        }
        About(user)
    }
}

@Composable
private fun ColumnScope.About(
    user: User
) {
    Text(
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, bottom = 16.dp)
            .align(Alignment.Start),
        text = stringResource(R.string.about),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp)
            .align(Alignment.Start),
        text = "@ ${user.id}"
    )
    AboutTextItem(stringResource(R.string.friends), user.friends.toString())
    AboutTextItem(stringResource(R.string.followers), user.followers.toString())
    AboutTextItem(stringResource(R.string.groups), user.groups.toString())
    AboutTextItem(stringResource(R.string.photos), user.photos.toString())
    AboutTextItem(stringResource(R.string.videos), user.videos.toString())
    AboutTextItem(stringResource(R.string.gifts), user.gifts.toString())
}

@Composable
private fun AboutTextItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}

