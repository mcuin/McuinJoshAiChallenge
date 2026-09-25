package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NASARoverListScreen() {
    Scaffold(topBar = { NASARoverTopBar(Modifier, true, {}) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NASARoverListHeader()
            NASARoverList()
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
fun NASARoverList() {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(100) {
            Text(text = "Item $it")
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
    NASARoverListScreen()
}