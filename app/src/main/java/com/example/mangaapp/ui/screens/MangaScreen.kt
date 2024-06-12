package com.example.mangaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R
import com.example.mangaapp.model.mangaModel.Manga
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaScreen(
    viewModel: MangaScreenViewModel,
    onMangaClick: (Manga) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black

) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            if (viewModel.mangaSearchState.isSearching) {
                MangaSearchBar(
                    scrollBehavior = scrollBehavior,
                    searchQuery = viewModel.mangaSearchState.title,
                    onSearchClicked = { viewModel.search() },
                    onSearchQueryChange = { title -> viewModel.changeTitleQuery(title) },
                    onCancelClicked = {
                        viewModel.stopSearching()
                        viewModel.changeTitleQuery("")
                        viewModel.search()
                    },
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                )
            } else {
                MangaScreenTopBar(
                    text = "Manga Search",
                    scrollBehavior = scrollBehavior,
                    secondaryColor = secondaryColor,
                    onSearchClick = { viewModel.startSearching() }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(
                        painterResource(R.drawable.filter_icon),
                        contentDescription = "",
                        tint = secondaryColor,
                    )
                },
                onClick = {
                    viewModel.openSheet()
                },
                containerColor = primaryColor
            )
        },
        modifier = Modifier.background(secondaryColor)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(secondaryColor)
        ) {
            when (val currentState = viewModel.mangaUiState) {
                is MangaUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is MangaUiState.Success -> {
                    if (currentState.manga.isNotEmpty()) {
                        MangaGridScreen(
                            currentState.manga, modifier = modifier
                                .fillMaxWidth()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                            loadMore = {
                                if (viewModel.mangaSearchState.offset < viewModel.mangaSearchState.total) {
                                    viewModel.loadMoreManga()
                                }
                            },
                            onMangaClick = onMangaClick,
                            secondaryColor = secondaryColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_manga),
                            fontSize = 25.sp,
                            color = primaryColor,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }


                is MangaUiState.Error -> ErrorScreen(
                    modifier = modifier.fillMaxSize(),
                    onReloadClick = { viewModel.reload() }
                )
            }
        }

        if (viewModel.isSheetOpen) {
            FilterBottomSheet(
                tagState = viewModel.tagChange.tagsMapState,
                sheetState = viewModel.sheetState,
                orderState = viewModel.orderState,
                onExpandIconClick = { viewModel.orderExpandedChange() },
                onOrderDismissRequest = { viewModel.closeOrderDropdown() },
                onOrderItemClick = { item -> viewModel.changeSelectedOrderItem(item) },
                onOrderGloballyPositioned = { layoutCoordinates ->
                    viewModel.setOrderTextFiledSize(
                        layoutCoordinates
                    )
                },

                onDismissRequest = {
                    viewModel.closeSheet()
                },
                onTagClick = { tag -> viewModel.cycleTagSelectionStatus(tag) },
                onFilterClick = {
                    viewModel.search()
                    viewModel.closeSheet()
                },
                onResetClick = {
                    viewModel.resetSearchState()
                    viewModel.resetOrderState()
                },
            )
        }

    }
}

@Composable
fun MangaGridScreen(
    mangaList: List<Manga>,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    secondaryColor: Color = Color.Black,
    loadMore: () -> Unit,
    onMangaClick: (Manga) -> Unit
) {
    val reachedBottom: Boolean by remember { derivedStateOf { lazyGridState.reachedBottom() } }

    if (reachedBottom) {
        loadMore()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .padding(horizontal = 4.dp)
            .background(secondaryColor),
        state = lazyGridState,
        contentPadding = contentPadding,
    ) {
        items(items = mangaList, key = { manga -> manga.id }
        ) { manga ->
            MangaCard(
                manga = manga,
                onMangaClick = onMangaClick,
                secondaryColor = secondaryColor,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxHeight()
                    .aspectRatio(0.7f)
            )
        }
    }
}

@Composable
fun MangaCard(
    manga: Manga,
    secondaryColor: Color = Color.Black,
    onMangaClick: (Manga) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onMangaClick(manga) }
            .clip(RoundedCornerShape(16.dp))
    ) {

        val coverName = manga.relationships.find { it.type == "cover_art" }?.attributes?.fileName
        if (coverName != null) {
            val coverLink = "https://uploads.mangadex.org/covers/${manga.id}/${coverName}.256.jpg"

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(coverLink)
                    .crossfade(true)
                    .build(),
                error = if(secondaryColor == Color.Black) {
                    painterResource(R.drawable.white_error_outline)
                } else {
                    painterResource(R.drawable.error_outline)
                },
                placeholder = if(secondaryColor == Color.Black) {
                    painterResource(R.drawable.white_cloud_queue)
                } else {
                    painterResource(R.drawable.cloud_queue)
                },
                contentScale = ContentScale.Crop,
                contentDescription = "cover",
                modifier = Modifier.fillMaxHeight()
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 500f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        Text(
            text = manga.attributes.title["en"] ?: "",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            maxLines = 2,
        )
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black,
    trackColor: Color = Color.DarkGray,
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(secondaryColor)) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center),
            color = primaryColor,
            trackColor = trackColor,
        )
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.error),
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black,
    onReloadClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column {
            Text(text = text,
                fontSize = 25.sp,
                color = primaryColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = onReloadClick,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.reload), color = secondaryColor, )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaScreenTopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black,
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = text,
                color = primaryColor,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    tint = primaryColor,
                    contentDescription = stringResource(R.string.search),
                    modifier = Modifier
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = secondaryColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSearchBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black,
) {
    val controller = LocalSoftwareKeyboardController.current
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = primaryColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onSearchClicked()
                    controller?.hide()
                })
            )
        },
        actions = {
            Icon(imageVector = Icons.Filled.Close,
                tint = primaryColor,
                contentDescription = stringResource(R.string.cancel),
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onCancelClicked() }
            )
        },
        navigationIcon = {
            Icon(imageVector = Icons.Filled.Search,
                tint = primaryColor,
                contentDescription = stringResource(R.string.search),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onSearchClicked()
                        controller?.hide()
                    }
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = secondaryColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    onFilterClick: () -> Unit,
    onResetClick: () -> Unit,
    onDismissRequest: () -> Unit,

    tagState: Map<String, TagState>,
    onTagClick: (String?) -> Unit,
    lazyColumnState: LazyListState = rememberLazyListState(),

    orderState: OrderState,
    onExpandIconClick: () -> Unit,
    onOrderDismissRequest: () -> Unit,
    onOrderItemClick: (String) -> Unit,
    onOrderGloballyPositioned: (LayoutCoordinates) -> Unit,

    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            FilterDragHandle(
                onFilterClick = onFilterClick,
                onResetClick = onResetClick,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        },
        containerColor = secondaryColor,
        contentColor = primaryColor,
        modifier = Modifier
    ) {
        LazyColumn(
            state = lazyColumnState
        ) {

            item {
                OrderDropDownMenu(
                    expanded = orderState.expanded,
                    list = orderState.list,
                    selectedItem = orderState.selectedItem,
                    textFiledSize = orderState.textFiledSize,
                    onExpandIconClick = onExpandIconClick,
                    onOrderDismissRequest = onOrderDismissRequest,
                    onOrderItemClick = onOrderItemClick,
                    onOrderGloballyPositioned = onOrderGloballyPositioned,
                    primaryColor = primaryColor
                )
            }
            items(tagState.toList()) { tag ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { onTagClick(tag.second.mangaTag.attributes.name["en"]) },
                    horizontalArrangement = Arrangement.Start
                ) {
                    TagRow(
                        tagSelectionStatus = tagState[tag.first]?.tagSelectionStatus,
                        tagName = tag.first,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDragHandle(
    primaryColor: Color,
    secondaryColor: Color,
    onResetClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { onResetClick() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(35.dp),
            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
        ) {
            Text(text = "Reset", color = primaryColor)
        }

        Button(
            onClick = { onFilterClick() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(35.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text(text = "Filter", color = secondaryColor)
        }
    }
}

@Composable
fun OrderDropDownMenu(
    expanded: Boolean,
    primaryColor: Color,
    list: List<String>,
    selectedItem: String,
    textFiledSize: Size,
    onOrderGloballyPositioned: (LayoutCoordinates) -> Unit,
    onExpandIconClick: () -> Unit,
    onOrderDismissRequest: () -> Unit,
    onOrderItemClick: (String) -> Unit
) {

    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Order", fontSize = 20.sp)
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        onOrderGloballyPositioned(coordinates)
                    },
                trailingIcon = {
                    Icon(
                        icon,
                        contentDescription = "",
                        modifier = Modifier.clickable { onExpandIconClick() },
                        tint = primaryColor
                    )
                },
                readOnly = true,
                textStyle = TextStyle(color = primaryColor)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onOrderDismissRequest() },
                modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
            ) {
                list.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            onOrderItemClick(label)
                            onOrderDismissRequest()
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun TagRow(
    tagSelectionStatus: TagSelectionStatus?,
    tagName: String,
    modifier: Modifier = Modifier
) {
    when (tagSelectionStatus) {
        TagSelectionStatus.Included -> Icon(
            painter = painterResource(id = R.drawable.checked_box),
            contentDescription = "",
            modifier = modifier
        )

        TagSelectionStatus.Excluded -> Icon(
            painter = painterResource(id = R.drawable.excluded_box),
            contentDescription = "",
            modifier = modifier
        )

        TagSelectionStatus.Unselected -> Icon(
            painter = painterResource(id = R.drawable.blank_box),
            contentDescription = "",
            modifier = modifier
        )

        else -> {}
    }
    Text(
        text = tagName,
        fontSize = 15.sp,
        modifier = modifier
            .padding(start = 5.dp)
    )
}

internal fun LazyGridState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

@Preview
@Composable
fun errorPrev() {
    ErrorScreen {

    }
}