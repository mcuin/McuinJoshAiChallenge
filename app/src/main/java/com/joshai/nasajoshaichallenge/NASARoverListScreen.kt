package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.joshai.nasajoshaichallenge.dataClasses.FullRoverData
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversListUIState
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute

@Composable
fun NASARoverListScreen(
    navController: NavController,
    nasaRoversListViewModel: NASARoversListViewModel = hiltViewModel()
) {
    val roversUiState by nasaRoversListViewModel.roversUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        nasaRoversListViewModel.getRovers()
    }

    NASARoverScreenContent(
        roversUiState = roversUiState,
        onRoverClick = { rover ->
            navController.navigate(RoverDetailRoute(rover))
        }
    )
}

@Composable
fun NASARoverScreenContent(
    roversUiState: NASARoversListUIState,
    onRoverClick: (roverId: String) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, false) {} }) { innerPadding ->

        when {
            roversUiState.isLoading && roversUiState.rovers.isEmpty() -> {
                LoadingIndicator(innerPadding)
            }

            roversUiState.errorMessage != null -> {
                ErrorMessage(innerPadding, roversUiState.errorMessage)
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
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
        Text(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.header_padding),
                    end = dimensionResource(id = R.dimen.standard_padding)),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            text = stringResource(id = R.string.nasa_rovers_list_header_title)
        )
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(id = R.dimen.standard_padding),
                top = dimensionResource(R.dimen.small_padding),
                end = dimensionResource(id = R.dimen.standard_padding)),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(id = R.string.nasa_rovers_list_header_subtitle))
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
        .padding(
            start = dimensionResource(id = R.dimen.standard_padding),
            end = dimensionResource(id = R.dimen.standard_padding),
            top = dimensionResource(id = R.dimen.small_padding),
            bottom = dimensionResource(id = R.dimen.small_padding)
        ),
        onClick = { onRoverClick(rover.attributes.name.lowercase()) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.appbar_elevation)
        )) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.standard_padding))) {
            SubcomposeAsyncImage(modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.rover_card_image_size)),
                model = rover.photoData?.full,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = {
                    LoadingIndicator()
                })
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = dimensionResource(R.dimen.standard_padding),
                            top = dimensionResource(id = R.dimen.small_padding),
                            end = dimensionResource(id = R.dimen.small_padding)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    text = rover.attributes.name
                )
                Text(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.small_padding),
                        top = dimensionResource(id = R.dimen.standard_padding),
                        end = dimensionResource(id = R.dimen.standard_padding)),
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(id = R.string.view_images)
                )
            }
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.launch_title, rover.attributes.launchDate))
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.landing_title, rover.attributes.landingDate))
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.photos_title, rover.attributes.totalPhotos))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(R.dimen.standard_padding),
                        top = dimensionResource(id = R.dimen.small_padding),
                        end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.cameras_title, rover.relationships.cameras.size))
        }
    }
}



@Preview
@Composable
fun NASARoverListScreenPreview() {
    NASARoverScreenContent(
        roversUiState = NASARoversListUIState(),
        onRoverClick = {}
    )
}