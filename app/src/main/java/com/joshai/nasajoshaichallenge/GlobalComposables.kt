package com.joshai.nasajoshaichallenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun LoadingIndicator(paddingValues: PaddingValues = PaddingValues()) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ErrorMessage(paddingValues: PaddingValues = PaddingValues(), errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center) {
        Text(text = errorMessage)
    }
}

@Composable
fun NASARoverTopBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigationIconClick: () -> Unit
) {
    Surface(modifier = modifier
        .fillMaxWidth(),
        color = Color.White,
        shadowElevation = dimensionResource(id = R.dimen.appbar_elevation)) {

        Box(modifier = modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.appbar_height))) {

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
                modifier = Modifier.size(dimensionResource(id = R.dimen.appbar_image_size)).align(Alignment.Center)
            )
        }
    }
}