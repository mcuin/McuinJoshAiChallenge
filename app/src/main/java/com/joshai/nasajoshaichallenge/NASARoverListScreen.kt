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

@Composable
fun NASARoverListScreen(navController: NavController) {
    NASARoverScreenContent(onRoverClick = { rover ->
        navController.navigate(rover)
    })
}

@Composable
fun NASARoverScreenContent(nasaRoversListViewModel: NASARoversListViewModel = hiltViewModel(), onRoverClick: (FullRoverData) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, false) {} }) { innerPadding ->

        LaunchedEffect(Unit) {
            nasaRoversListViewModel.getRovers()
        }

        val roversUiState by nasaRoversListViewModel.roversUIState.collectAsStateWithLifecycle()

        when {
            roversUiState.isLoading && roversUiState.rovers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            roversUiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center) {
                    Text(text = roversUiState.errorMessage!!)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    NASARoverListHeader()
                    NASARoverList(roversUiState)
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
fun NASARoverList(roversUiState: NASARoversListUIState) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(roversUiState.rovers.size) { index ->
            NASARoverListCard(roversUiState.rovers[index])
        }
    }
}

@Composable
fun NASARoverListCard(rover: FullRoverData) {
    Card(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
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

@Composable
fun NASARoverTopBar(
    modifier: Modifier,
    canNavigateBack: Boolean,
    onNavigationIconClick: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(56.dp)
        .background(Color.White)) {

        if (canNavigateBack) {
            IconButton(
                onClick = onNavigationIconClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_arrow_back_24),
                    contentDescription = stringResource(id = R.string.back_button)
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.nasared),
            contentDescription = stringResource(id = R.string.app_name),
            tint = Color.Red,
            modifier = Modifier.size(60.dp).align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun NASARoverListScreenPreview() {
    NASARoverScreenContent(onRoverClick = {})
}