package bagus2x.sosmed.presentation.feed.newfeed

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.camera.launchCamera
import bagus2x.sosmed.presentation.camera.rememberCameraLauncher
import bagus2x.sosmed.presentation.common.LocalAuthenticatedUser
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.feed.newfeed.components.*
import bagus2x.sosmed.presentation.gallery.contract.MediaType
import bagus2x.sosmed.presentation.gallery.contract.SelectMultipleMedia
import bagus2x.sosmed.presentation.home.components.Permission
import bagus2x.sosmed.presentation.imageeditor.launchImageEditor
import bagus2x.sosmed.presentation.imageeditor.rememberImageEditorLauncher

@Composable
fun NewFeedScreen(
    navController: NavController,
    viewModel: NewFeedViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gallery = rememberLauncherForActivityResult(
        contract = SelectMultipleMedia(
            max = 4,
            selected = state.selectedMedias
        ),
        onResult = { deviceMedias ->
            viewModel.setSelectedMedia(deviceMedias ?: return@rememberLauncherForActivityResult)
        }
    )
    val imageEditorLauncher = rememberImageEditorLauncher { image ->
        viewModel.replaceMedia(image ?: return@rememberImageEditorLauncher)
    }
    val cameraLauncher = rememberCameraLauncher { deviceMedia ->
        viewModel.selectMedia(deviceMedia ?: return@rememberCameraLauncher)
    }

    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
        title = stringResource(R.string.text_browse_gallery),
        permissionText = stringResource(R.string.text_gallery_require_permission),
        rationaleText = stringResource(R.string.text_gallery_rationale),
        skipp = navController::navigateUp,
        modifier = Modifier.systemBarsPadding(),
    ) {
        LaunchedEffect(Unit) {
            viewModel.loadDeviceMedias()
        }
        NewFeedScreen(
            stateProvider = { state },
            snackbarConsumed = viewModel::snackbarConsumed,
            setDescription = viewModel::setDescription,
            navigateUp = navController::navigateUp,
            selectDeviceMedia = viewModel::selectMedia,
            unselectDeviceMedia = viewModel::unselectMedia,
            navigateToEditorScreen = { media ->
                when (media) {
                    is DeviceMedia.Image -> {
                        imageEditorLauncher.launchImageEditor(context, media)
                    }
                    is DeviceMedia.Video -> {

                    }
                }
            },
            navigateToCameraScreen = {
                cameraLauncher.launchCamera(context)
            },
            navigateToGalleryScreen = {
                gallery.launch(MediaType.ImageAndVideo)
            },
            create = viewModel::create
        )
    }
}

@Composable
fun NewFeedScreen(
    stateProvider: () -> NewFeedState,
    snackbarConsumed: () -> Unit,
    setDescription: (String) -> Unit,
    navigateUp: () -> Unit,
    selectDeviceMedia: (DeviceMedia) -> Unit,
    unselectDeviceMedia: (DeviceMedia) -> Unit,
    navigateToEditorScreen: (DeviceMedia) -> Unit,
    navigateToCameraScreen: () -> Unit,
    navigateToGalleryScreen: () -> Unit,
    create: () -> Unit

) {
    val showSnackbar = LocalShowSnackbar.current
    LaunchedEffect(Unit) {
        snapshotFlow {
            stateProvider()
        }.collect { state ->
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                snackbarConsumed()
            }
            if (state.created) {
                navigateUp()
            }
        }
    }

    val (description, medias, selectedMedias, loading) = stateProvider()
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            NewFeedTopBar(
                onBackClicked = navigateUp,
                onPostClicked = create,
                buttonEnabled = !loading
            )
        },
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (profileRef, visibilityRef, descriptionRef, selectedMediasRef, simpleGalleryRef, actionBarRef) = createRefs()
            val auth = LocalAuthenticatedUser.current
            Image(
                model = auth?.photo ?: Misc.getAvatar("bagus"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .constrainAs(profileRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top, 16.dp)
                    }
            )
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .constrainAs(visibilityRef) {
                        start.linkTo(profileRef.end, 16.dp)
                        top.linkTo(profileRef.top)
                        bottom.linkTo(profileRef.bottom)
                    }
            ) {
                Text(text = stringResource(R.string.text_public))
            }
            DescriptionTextField(
                text = description,
                onChange = setDescription,
                modifier = Modifier
                    .constrainAs(descriptionRef) {
                        start.linkTo(parent.start)
                        top.linkTo(profileRef.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.matchParent
                    },
                enabled = !loading
            )
            SelectedMedias(
                modifier = Modifier
                    .constrainAs(selectedMediasRef) {
                        start.linkTo(parent.start)
                        top.linkTo(descriptionRef.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.matchParent
                    },
                medias = selectedMedias,
                onCloseClicked = unselectDeviceMedia,
                onItemClicked = navigateToEditorScreen
            )
            AnimatedVisibility(
                visible = description.isEmpty() && selectedMedias.isEmpty() && medias.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it / 2 }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.constrainAs(simpleGalleryRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(actionBarRef.top)
                    width = Dimension.matchParent
                }
            ) {
                SimpleGallery(
                    onCameraClicked = navigateToCameraScreen,
                    onGalleryClicked = navigateToGalleryScreen,
                    onItemClicked = selectDeviceMedia,
                    options = medias
                )
            }
            MediaActionBar(
                textLength = description.length,
                modifier = Modifier
                    .imePadding()
                    .constrainAs(actionBarRef) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.matchParent
                    },
                onGalleryClicked = navigateToGalleryScreen,
                onVotesClicked = { },
                onLocationClicked = { }
            )
        }
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
