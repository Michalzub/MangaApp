package com.example.mangaapp.ui.screens

import MangaDetailUiState
import MangaDetailsViewModel
import android.widget.GridLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MangaDetailsScreen(
    //mangaDetailUiState: MangaDetailUiState,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    /*TODO*/
}

@Composable
fun ChapterList() {

}

@Preview
@Composable
fun MangaDetailsPreview() {
    MangaDetailsScreen(onClickBack = { /*TODO*/ })
}