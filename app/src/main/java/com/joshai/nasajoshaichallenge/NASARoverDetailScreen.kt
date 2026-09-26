package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.joshai.nasajoshaichallenge.dataClasses.Attributes
import com.joshai.nasajoshaichallenge.dataClasses.FullRoverData
import com.joshai.nasajoshaichallenge.dataClasses.PhotoLinks
import com.joshai.nasajoshaichallenge.dataClasses.Relationships
import java.sql.Date

@Composable
fun RoverDetailScreen(navController: NavController, roverId: String) {
    RoverDetailScreenContent(roverId = roverId, onNavigationIconClick = { navController.popBackStack() })
}

@Composable
fun RoverDetailScreenContent(roverId: String, onNavigationIconClick: () -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { NASARoverTopBar(Modifier, true, onNavigationIconClick) }) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {
            RoverDetailsHeader(rover)
            RoverPhotosList()
        }
    }
}

@Composable
fun RoverDetailsHeader(rover: FullRoverData) {
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
fun RoverPhotosList() {
    Column(modifier = Modifier.fillMaxWidth()) {

        var showDatePickerModal by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        val selectedDate = Date(datePickerState.selectedDateMillis!!)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedDate.toString(),
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.date)) },
            trailingIcon = { painterResource(R.drawable.outline_calendar_today_24) }
        )
    }

    LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {

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