package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversDetailsUIState
import com.joshai.nasajoshaichallenge.dataClasses.PhotoLinks
import com.joshai.nasajoshaichallenge.dataClasses.Rover

@Composable
fun RoverDetailScreen(navController: NavController, viewModel: NASARoverDetailViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RoverDetailScreenContent(onNavigationIconClick = { navController.popBackStack() }, uiState = uiState, viewModel::updateDateGetPhotos)
}

@Composable
fun RoverDetailScreenContent(onNavigationIconClick: () -> Unit, uiState: NASARoversDetailsUIState, dateChanged: (Long) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, true, onNavigationIconClick) }) { innerPadding ->

        when {
            uiState.isLoading && uiState.roverDetails == null -> {
                LoadingIndicator(innerPadding)
            }
            uiState.roverErrorMessage != null -> {
                ErrorMessage(innerPadding, stringResource(id = uiState.roverErrorMessage))
            }
            uiState.roverDetails != null -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    RoverDetailsHeader(uiState.roverDetails)
                    if (uiState.photoErrorMessage != null) {
                        ErrorMessage(errorMessage = stringResource(uiState.photoErrorMessage))
                    } else {
                        RoverPhotosList(uiState, uiState.roverPhotos, dateChanged)
                    }
                }
            }
        }
    }
}

@Composable
fun RoverDetailsHeader(rover: Rover) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(id = R.dimen.standard_padding),
                top = dimensionResource(id = R.dimen.header_padding),
                end = dimensionResource(id = R.dimen.standard_padding)),
            style = MaterialTheme.typography.headlineLarge,
            text = rover.attributes.name)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.launch_title, rover.attributes.launchDate))
            Text(
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.standard_padding),
                        top = dimensionResource(id = R.dimen.small_padding),
                        end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.photos_title, rover.attributes.totalPhotos))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.landing_title, rover.attributes.landingDate))
            Text(modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.standard_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.standard_padding),),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.cameras_title, rover.relationships.cameras.size))
        }
    }
}

@Composable
fun RoverPhotosList(uiState: NASARoversDetailsUIState, photos: List<PhotoLinks>, dateChanged: (Long) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {

        var showDatePickerModal by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDateMillis,
            selectableDates = object : SelectableDates {

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis in uiState.minEpoch..uiState.maxEpoch
                }
            }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(R.dimen.small_padding),
                        top = dimensionResource(id = R.dimen.header_padding),
                        end = dimensionResource(id = R.dimen.small_padding),
                    ),
                readOnly = true,
                value = uiState.startDate,
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.date)) },
                trailingIcon = {
                    Icon(
                        painterResource(R.drawable.outline_calendar_today_24),
                        contentDescription = stringResource(id = R.string.date))
                }
            )
            Box(modifier = Modifier
                .matchParentSize()
                .clickable { showDatePickerModal = true })
        }

        if (showDatePickerModal) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerModal = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dateChanged(it)
                        }
                        showDatePickerModal = false
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerModal = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }) {
                    DatePicker(state = datePickerState)
                }
        }

        LazyVerticalGrid(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            columns = GridCells.Fixed(2)) {
            items(photos.size) { index ->
                Box(modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding))) {
                    SubcomposeAsyncImage(modifier = Modifier.fillMaxSize(),
                        model = photos[index].full,
                        contentDescription = null,
                        loading = {
                            LoadingIndicator()
                        })
                }
            }
        }
    }
}

@Preview
@Composable
fun RoverDetailScreenPreview() {
    RoverDetailScreenContent(
        onNavigationIconClick = {},
        uiState = NASARoversDetailsUIState(),
        {}
    )
}