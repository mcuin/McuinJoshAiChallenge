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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.joshai.nasajoshaichallenge.dataClasses.PhotoLinks
import com.joshai.nasajoshaichallenge.dataClasses.Rover
import java.sql.Date
import java.text.SimpleDateFormat
import androidx.compose.ui.platform.LocalLocale
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@Composable
fun RoverDetailScreen(navController: NavController, roverId: String) {
    RoverDetailScreenContent(roverId = roverId, onNavigationIconClick = { navController.popBackStack() })
}

@Composable
fun RoverDetailScreenContent(roverId: String, onNavigationIconClick: () -> Unit, viewModel: NASARoverDetailViewModel = hiltViewModel()) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, true, onNavigationIconClick) }) { innerPadding ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        when {
            uiState.isLoading && uiState.roverDetails == null -> {
                LoadingIndicator(innerPadding)
            }
            uiState.roverErrorMessage != null -> {
                ErrorMessage(innerPadding, uiState.roverErrorMessage!!)
            }
            uiState.roverDetails != null -> {
                Column(modifier = Modifier.padding(innerPadding)) {
                    RoverDetailsHeader(uiState.roverDetails!!)
                    if (uiState.photoErrorMessage != null) {
                        ErrorMessage(errorMessage = uiState.photoErrorMessage!!)
                    } else {
                        RoverPhotosList(uiState.roverDetails!!, uiState.roverPhotos, viewModel::updateDateGetPhotos)
                    }
                }
            }
        }
    }
}

@Composable
fun RoverDetailsHeader(rover: Rover) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = rover.attributes.name)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.launch_title, rover.attributes.landingDate))
            Text(text = stringResource(R.string.photos_title, rover.attributes.totalPhotos))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.landing_title, rover.attributes.launchDate))
            Text(text = stringResource(R.string.cameras_title, rover.relationships.cameras.size))
        }
    }
}

@Composable
fun RoverPhotosList(rover: Rover, photos: List<PhotoLinks>, dateChanged: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {

        val selectedFormatter = SimpleDateFormat("MM/dd/yyyy", LocalLocale.current.platformLocale).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val apiFormatter = SimpleDateFormat("yyyy-MM-dd", LocalLocale.current.platformLocale).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val epochFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", LocalLocale.current.platformLocale)
        val minEpoch = LocalDate.parse(rover.attributes.landingDate, epochFormatter)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        val maxEpoch = LocalDate.parse(rover.attributes.maxDate, epochFormatter)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        var showDatePickerModal by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = maxEpoch,
            selectableDates = object : SelectableDates {

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis in minEpoch..maxEpoch
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in LocalDate.parse(rover.attributes.landingDate, epochFormatter).year..
                            LocalDate.parse(rover.attributes.maxDate).year
                }
            }
        )
        val selectedDateText = datePickerState.selectedDateMillis?.let {
            selectedFormatter.format(Date(it))
        } ?: ""

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = selectedDateText,
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.date)) },
                trailingIcon = { painterResource(R.drawable.outline_calendar_today_24) }
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
                            dateChanged(apiFormatter.format(Date(it)))
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
    }

    LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
        items(photos.size) { index ->
            Box(modifier = Modifier.padding(8.dp)) {
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

@Preview
@Composable
fun RoverDetailScreenPreview() {
    RoverDetailScreenContent(
        roverId = "",
        onNavigationIconClick = {}
    )
}