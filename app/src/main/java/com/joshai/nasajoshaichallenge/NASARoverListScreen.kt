package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversListUIState
import com.joshai.nasajoshaichallenge.dataClasses.FullRoverData
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute

@Composable
fun NASARoverListScreen(navController: NavController) {
    NASARoverScreenContent(onRoverClick = { rover ->
        navController.navigate(RoverDetailRoute(rover))
    })
}

@Composable
fun NASARoverScreenContent(nasaRoversListViewModel: NASARoversListViewModel = hiltViewModel(), onRoverClick: (roverId: String) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, false) {} }) { innerPadding ->

        LaunchedEffect(Unit) {
            nasaRoversListViewModel.getRovers()
        }

        val roversUiState by nasaRoversListViewModel.roversUIState.collectAsStateWithLifecycle()

        when {
            roversUiState.isLoading && roversUiState.rovers.isEmpty() -> {
                LoadingIndicator(innerPadding)
            }
            roversUiState.errorMessage != null -> {
                ErrorMessage(innerPadding, roversUiState.errorMessage!!)
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    NASARoverListHeader()
                    NASARoverList(roversUiState, onRoverClick)
                }
            }
        }
    }
}

@Composable
fun NASARoverListHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.nasa_rovers_list_header_title))
        Text(text = stringResource(id = R.string.nasa_rovers_list_header_subtitle))
    }
}

@Composable
fun NASARoverList(roversUiState: NASARoversListUIState, onRoverClick: (roverId: String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(roversUiState.rovers.size) { index ->
            NASARoverListCard(roversUiState.rovers[index], onRoverClick = onRoverClick)
        }
    }
}

@Composable
fun NASARoverListCard(rover: FullRoverData, onRoverClick: (roverId: String) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        onClick = { onRoverClick(rover.attributes.name) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(model = rover.photoData?.full, contentDescription = null)
            Text(text = rover.attributes.name)
            Text(text = stringResource(R.string.launch_title, rover.attributes.landingDate))
            Text(text = stringResource(R.string.landing_title, rover.attributes.launchDate))
            Text(text = stringResource(R.string.photos_title, rover.attributes.totalPhotos))
            Text(text = stringResource(R.string.cameras_title, rover.relationships.cameras.size))
        }
    }
}



@Preview
@Composable
fun NASARoverListScreenPreview() {
    NASARoverScreenContent(onRoverClick = {})
}