package com.example.mangaapp.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaScreen(
    viewModel: MangaScreenViewModel,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    loadMore: () -> Unit,
    onMangaClick: (Manga) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            if (viewModel.mangaSearchState.isSearching) {
                MangaSearchBar(
                    scrollBehavior = scrollBehavior,
                    searchQuery = viewModel.mangaSearchState.title,
                    backgroundColor = backgroundColor,
                    onSearchClicked = { viewModel.search() },
                    onSearchQueryChange = { title -> viewModel.changeTitleQuery(title) },
                    onCancelClicked = {
                        viewModel.stopSearching()
                        viewModel.changeTitleQuery("")
                        viewModel.search()
                    }
                )
            } else {
                MangaScreenTopBar(
                    text = "Manga Search",
                    modifier = modifier,
                    scrollBehavior = scrollBehavior,
                    backgroundColor = backgroundColor,
                    onSearchClick = { viewModel.startSearching() }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(
                        painterResource(id = R.drawable.filter_icon),
                        contentDescription = "",
                        tint = Color.Black,
                    )
                },
                onClick = {
                    viewModel.openSheet()
                },
                containerColor = Color.Cyan
            )
        },
        modifier = Modifier.background(backgroundColor)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            when (val currentState = viewModel.mangaUiState) {
                is MangaUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is MangaUiState.Success -> MangaGridScreen(
                    currentState.manga, modifier = modifier
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    loadMore = loadMore,
                    onMangaClick = onMangaClick,
                    backgroundColor = backgroundColor
                )

                is MangaUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
            }
        }

        if (viewModel.isSheetOpen) {
            FilterBottomSheet(
                tagState = viewModel.tagsMapState,
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
    backgroundColor: Color,
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
            .background(backgroundColor),
        state = lazyGridState,
        contentPadding = contentPadding,
    ) {
        items(items = mangaList, key = { manga -> manga.id }
        ) { mangaCover ->
            MangaCard(
                manga = mangaCover,
                onMangaClick = onMangaClick,
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
                error = painterResource(R.drawable.ic_launcher_background),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
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
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = "Error", fontSize = 30.sp, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaScreenTopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    backgroundColor: Color,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    tint = Color.White,
                    contentDescription = "Search",
                    modifier = Modifier
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSearchBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchQuery: String,
    backgroundColor: Color,
    onSearchQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val controller = LocalSoftwareKeyboardController.current
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onSearchClicked()
                    controller?.hide()
                })
            )
        },
        actions = {
            Icon(imageVector = Icons.Filled.Close,
                tint = Color.White,
                contentDescription = "Cancel",
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onCancelClicked() }
            )
        },
        navigationIcon = {
            Icon(imageVector = Icons.Filled.Search,
                tint = Color.White,
                contentDescription = "Search",
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    tagState: Map<String, TagState>,
    lazyColumnState: LazyListState = rememberLazyListState(),

    orderState: OrderState,
    onExpandIconClick: () -> Unit,
    onOrderDismissRequest: () -> Unit,
    onOrderItemClick: (String) -> Unit,
    onOrderGloballyPositioned: (LayoutCoordinates) -> Unit,

    onFilterClick: () -> Unit,
    onResetClick: () -> Unit,
    onTagClick: (String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            FilterDragHandle(
                onFilterClick = onFilterClick,
                onResetClick = onResetClick
            )
        },
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
                    onOrderGloballyPositioned = onOrderGloballyPositioned
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
                    when (tagState[tag.first]?.tagSelectionStatus) {
                        TagSelectionStatus.Included -> Icon(
                            painter = painterResource(id = R.drawable.checked_box),
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        TagSelectionStatus.Excluded -> Icon(
                            painter = painterResource(id = R.drawable.excluded_box),
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        TagSelectionStatus.Unselected -> Icon(
                            painter = painterResource(id = R.drawable.blank_box),
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        else -> {}
                    }
                    Text(
                        text = tag.first,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDragHandle(
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
            modifier = Modifier.height(35.dp)
        ) {
            Text(text = "Reset")
        }

        Button(
            onClick = { onFilterClick() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(35.dp)
        ) {
            Text(text = "Filter")
        }
    }
}

@Composable
fun OrderDropDownMenu(
    expanded: Boolean,
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
                        modifier = Modifier.clickable { onExpandIconClick() }
                    )
                },
                readOnly = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onOrderDismissRequest() /*expanded = false*/ },
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

internal fun LazyGridState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}