package bagus2x.sosmed.presentation.user.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Button
import bagus2x.sosmed.presentation.common.components.DatePickerDialog
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.components.TextField
import bagus2x.sosmed.presentation.common.components.rememberDatePickerDialogState
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.noRippleClickable
import bagus2x.sosmed.presentation.gallery.contract.MediaType
import bagus2x.sosmed.presentation.gallery.contract.SelectSingleMedia
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val galleryPhoto = rememberLauncherForActivityResult(
        contract = SelectSingleMedia(),
        onResult = { media ->
            viewModel.setPhoto(
                (media as? DeviceMedia.Image) ?: return@rememberLauncherForActivityResult
            )
        }
    )
    val galleryHeader = rememberLauncherForActivityResult(
        contract = SelectSingleMedia(),
        onResult = { media ->
            viewModel.setHeader(
                (media as? DeviceMedia.Image) ?: return@rememberLauncherForActivityResult
            )
        }
    )
    EditProfileScreen(
        stateProvider = { state },
        setPhoto = viewModel::setPhoto,
        setName = viewModel::setName,
        setHeader = viewModel::setHeader,
        setBio = viewModel::setBio,
        setLocation = viewModel::setLocation,
        setWebsite = viewModel::setWebsite,
        setDateOfBirth = viewModel::setDateOfBirth,
        consumeSnackbar = viewModel::consumeSnackbar,
        selectPhoto = {
            galleryPhoto.launch(MediaType.Image)
        },
        selectHeader = {
            galleryHeader.launch(MediaType.Image)
        },
        navigateUp = navController::navigateUp,
        saveAndUpdate = viewModel::saveAndUpdate
    )
}

@Composable
fun EditProfileScreen(
    stateProvider: () -> EditProfileState,
    setPhoto: (DeviceMedia.Image?) -> Unit,
    setHeader: (DeviceMedia.Image?) -> Unit,
    setName: (String) -> Unit,
    setBio: (String) -> Unit,
    setLocation: (String) -> Unit,
    setWebsite: (String) -> Unit,
    setDateOfBirth: (LocalDate?) -> Unit,
    consumeSnackbar: () -> Unit,
    selectPhoto: () -> Unit,
    selectHeader: () -> Unit,
    navigateUp: () -> Unit,
    saveAndUpdate: () -> Unit,
) {
    val state = stateProvider()
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
            if (state.updated) {
                navigateUp()
            }
            if (state.snackbar.isNotBlank()) {
                consumeSnackbar()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left_outlined),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(text = "Edit profile")
                },
                actions = {
                    Button(
                        onClick = saveAndUpdate,
                        modifier = Modifier.padding(end = 12.dp),
                        enabled = state.isFulfilled && !state.loading
                    ) {
                        Text(text = "Save")
                    }
                }, backgroundColor = MaterialTheme.colors.background
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Box {
                    Box {
                        AsyncImage(
                            model = state.header?.file ?: state.defaultHeader,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(3 / 1f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        if (state.header == null) {
                                            drawRect(color = Color.Black.copy(alpha = .5f))
                                        }
                                    }
                                }
                                .clickable(onClick = selectHeader),
                            contentScale = ContentScale.FillWidth
                        )
                        if (state.header == null) {
                            Icon(
                                painter = painterResource(R.drawable.ic_gallery_filled),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp)
                            .offset(y = 40.dp)
                            .clip(CircleShape)
                            .border(width = 2.dp, color = Color.White, shape = CircleShape)
                    ) {
                        AsyncImage(
                            model = state.photo?.file ?: state.defaultPhoto
                            ?: Misc.getAvatar("bagus"),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        if (state.photo == null) {
                                            drawCircle(color = Color.Black.copy(alpha = .5f))
                                        }
                                    }
                                }
                                .size(80.dp)
                                .clickable(onClick = selectPhoto)
                        )
                        if (state.photo == null) {
                            Icon(
                                painter = painterResource(R.drawable.ic_gallery_filled),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
            TextField(
                value = state.name,
                onValueChange = setName,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = {
                    Text(text = "Name")
                },
                enabled = !state.loading,
                contentPadding = PaddingValues(horizontal = 0.dp)
            )
            TextField(
                value = state.bio,
                onValueChange = setBio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = {
                    Text(text = "Bio")
                },
                enabled = !state.loading,
                contentPadding = PaddingValues(horizontal = 0.dp)
            )
            TextField(
                value = state.location,
                onValueChange = setLocation,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = {
                    Text(text = "Location")
                },
                enabled = !state.loading,
                contentPadding = PaddingValues(horizontal = 0.dp)
            )
            TextField(
                value = state.website,
                onValueChange = setWebsite,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = {
                    Text(text = "Website")
                },
                enabled = !state.loading,
                contentPadding = PaddingValues(horizontal = 0.dp)
            )
            val datePickerDialogState = rememberDatePickerDialogState(
                currentDate = LocalDate.now().minusYears(10),
                endDate = LocalDate.now(),
            )
            DatePickerDialog(
                state = datePickerDialogState,
                onResult = setDateOfBirth
            )
            TextField(
                value = state.dateOfBirth?.toString() ?: "",
                onValueChange = setName,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .noRippleClickable { datePickerDialogState.show() },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = {
                    Text(text = "Date of birth")
                },
                enabled = false,
                contentPadding = PaddingValues(horizontal = 0.dp),
                readOnly = true
            )
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
