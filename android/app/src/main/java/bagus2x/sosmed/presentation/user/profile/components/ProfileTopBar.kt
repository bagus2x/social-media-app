package bagus2x.sosmed.presentation.user.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.noRippleClickable
import bagus2x.sosmed.presentation.user.profile.SwipingStates

@OptIn(
    ExperimentalMotionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ProfileTopBar(
    user: User,
    own: Boolean,
    progressProvider: () -> Float,
    onBackClicked: () -> Unit,
    onSettingClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onMessageClicked: () -> Unit,
    onFollowClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onFollowingClicked: () -> Unit,
    onFollowersClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    MotionLayout(
        motionScene = rememberProfileScene(ownProfile = own),
        modifier = modifier,
        progress = progressProvider()
    ) {
        if (own) {
            Button(
                onClick = onEditClicked,
                modifier = Modifier
                    .layoutId(LayoutItem.FollowEditButton)
                    .wrapContentSize(),
            ) {
                Text(text = stringResource(R.string.text_edit))
            }
        } else {
            Button(
                onClick = onFollowClicked,
                modifier = Modifier
                    .layoutId(LayoutItem.FollowEditButton)
                    .wrapContentSize(),
            ) {
                Text(
                    text =
                    if (user.following)
                        stringResource(R.string.text_unfollow)
                    else
                        stringResource(R.string.text_follow)
                )
            }
            IconButton(
                onClick = onMessageClicked,
                modifier = Modifier.layoutId(LayoutItem.MessageButton)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sms_outlined),
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier.layoutId(LayoutItem.ProfileInfo)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.caption
            )
            if (!user.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                TextFormatter(
                    text = user.bio,
                    style = MaterialTheme.typography.body2
                )
            }
            val infoWithIcon = @Composable { resId: Int, text: String, color: Color ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(resId),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body2,
                        color = color
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!user.location.isNullOrBlank()) {
                    infoWithIcon(
                        R.drawable.ic_location_outlined,
                        user.location,
                        MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                    )
                }
                if (!user.website.isNullOrBlank()) {
                    infoWithIcon(
                        R.drawable.ic_link_outlined,
                        user.website,
                        MaterialTheme.colors.primary
                    )
                }
                infoWithIcon(
                    R.drawable.ic_calendar_outlined,
                    stringResource(R.string.text_joined_at, Misc.formatDate(user.createdAt)),
                    MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.noRippleClickable(onClick = onFollowingClicked)
                ) {
                    Text(
                        text = "${user.totalFollowing}",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.text_following),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.noRippleClickable(onClick = onFollowersClicked)
                ) {
                    Text(
                        text = "${user.totalFollowers}",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.text_followers),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                    )
                }
            }
        }
        Image(
            model = user.header,
            contentDescription = null,
            modifier = Modifier
                .layoutId(LayoutItem.Header)
                .blur(4.dp * progressProvider()),
            contentScale = ContentScale.FillWidth
        )
        if (!own) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.layoutId(LayoutItem.BackButton)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left_outlined),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
        IconButton(
            onClick = onSearchClicked,
            modifier = Modifier.layoutId(LayoutItem.SearchButton)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_outlined),
                contentDescription = null,
                tint = Color.White,
            )
        }
        IconButton(
            onClick = onSettingClicked,
            modifier = Modifier.layoutId(LayoutItem.MenuButton)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_gear_outlined),
                contentDescription = null,
                tint = Color.White,
            )
        }
        Image(
            model = user.photo ?: Misc.getAvatar(user.username),
            contentDescription = null,
            modifier = Modifier
                .layoutId(LayoutItem.ProfilePhoto)
                .clip(CircleShape)
                .drawWithContent {
                    drawContent()
                    drawCircle(
                        radius = size.height / 2,
                        color = Color.White,
                        style = Stroke(width = 4.dp.toPx())
                    )
                },
            contentScale = ContentScale.Crop
        )
    }
}

enum class LayoutItem {
    Header,
    BackButton,
    SearchButton,
    MenuButton,
    ProfilePhoto,
    FollowEditButton,
    MessageButton,
    ProfileInfo
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun rememberProfileScene(ownProfile: Boolean): MotionScene {
    val configuration = LocalConfiguration.current
    val expandedHeight = configuration.screenWidthDp.dp * 1f / 3
    val profilePhotoDiameter = 80.dp
    val collapsedHeight = 56.dp
    val density = LocalDensity.current
    return remember {
        MotionScene {
            val headerRef = createRefFor(LayoutItem.Header)
            val backButtonRef = createRefFor(LayoutItem.BackButton)
            val searchButtonRef = createRefFor(LayoutItem.SearchButton)
            val menuButtonRef = createRefFor(LayoutItem.MenuButton)
            val profilePhotoRef = createRefFor(LayoutItem.ProfilePhoto)
            val followEditButtonRef = createRefFor(LayoutItem.FollowEditButton)
            val messageButtonRef = createRefFor(LayoutItem.MessageButton)
            val profileInfoRef = createRefFor(LayoutItem.ProfileInfo)
            defaultTransition(
                from = constraintSet {
                    constrain(headerRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        height = Dimension.preferredValue(expandedHeight)
                    }
                    constrain(backButtonRef) {
                        start.linkTo(parent.start, 4.dp)
                        top.linkTo(parent.top, 4.dp)
                    }
                    constrain(searchButtonRef) {
                        top.linkTo(parent.top, 4.dp)
                        end.linkTo(menuButtonRef.start)
                    }
                    constrain(menuButtonRef) {
                        top.linkTo(parent.top, 4.dp)
                        end.linkTo(parent.end, 4.dp)
                    }
                    constrain(profilePhotoRef) {
                        start.linkTo(headerRef.start, 16.dp)
                        bottom.linkTo(headerRef.bottom, (-profilePhotoDiameter / 2))
                        width = Dimension.preferredValue(profilePhotoDiameter)
                        height = Dimension.ratio("H,1:1")
                    }
                    constrain(followEditButtonRef) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(headerRef.bottom, 4.dp)
                    }
                    constrain(messageButtonRef) {
                        end.linkTo(followEditButtonRef.start, 12.dp)
                        top.linkTo(followEditButtonRef.top)
                        bottom.linkTo(followEditButtonRef.bottom)
                    }
                    constrain(profileInfoRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(followEditButtonRef.bottom, 4.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                },
                to = constraintSet {
                    constrain(headerRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.preferredValue(collapsedHeight)
                    }
                    constrain(backButtonRef) {
                        start.linkTo(parent.start, 4.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    constrain(searchButtonRef) {
                        top.linkTo(parent.top)
                        end.linkTo(menuButtonRef.start)
                        bottom.linkTo(parent.bottom)
                    }
                    constrain(menuButtonRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end, 4.dp)
                        bottom.linkTo(parent.bottom)
                    }
                    if (ownProfile) {
                        constrain(profilePhotoRef) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(parent.top, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)
                            width = Dimension.ratio("W,1:1")
                            height = Dimension.preferredValue(profilePhotoDiameter)
                        }
                    } else {
                        constrain(profilePhotoRef) {
                            start.linkTo(backButtonRef.end)
                            top.linkTo(backButtonRef.top)
                            bottom.linkTo(backButtonRef.bottom)
                            width = Dimension.ratio("W,1:1")
                            height = Dimension.preferredValue(profilePhotoDiameter)
                        }
                    }
                    constrain(followEditButtonRef) {
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.top, 16.dp)
                    }
                    constrain(messageButtonRef) {
                        end.linkTo(followEditButtonRef.start, 12.dp)
                        top.linkTo(followEditButtonRef.top)
                        bottom.linkTo(followEditButtonRef.bottom)
                    }
                    constrain(profileInfoRef) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(headerRef.bottom)
                        width = Dimension.fillToConstraints
                    }
                },
                transitionContent = {
                    keyAttributes(profilePhotoRef) {
                        frame(50) {
                            this.translationY = with(density) { -(16.dp).toPx() }
                        }
                        frame(100) {
                            this.translationY = 0f
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberNestedScrollConnection(
    swipingState: SwipeableState<SwipingStates>
): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return if (delta < 0) {
                    swipingState.performDrag(delta).toOffset()
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return swipingState.performDrag(delta).toOffset()
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                swipingState.performFling(velocity = available.y)
                return super.onPostFling(consumed, available)
            }

            private fun Float.toOffset() = Offset(0f, this)
        }
    }
}
