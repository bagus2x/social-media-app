package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Story
import bagus2x.sosmed.presentation.common.Misc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

val loremIpsum = """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer sodales
laoreet commodo. Phasellus a purus eu risus elementum consequat. Aenean eu
elit ut nunc convallis laoreet non ut libero. Suspendisse interdum placerat
risus vel ornare. Donec vehicula, turpis sed consectetur ullamcorper, ante
nunc egestas quam, ultricies adipiscing velit enim at nunc. Aenean id diam
neque. Praesent ut lacus sed justo viverra fermentum et ut sem. Fusce
convallis gravida lacinia. Integer semper dolor ut elit sagittis lacinia.
Praesent sodales scelerisque eros at rhoncus. Duis posuere sapien vel ipsum
ornare interdum at eu quam. Vestibulum vel massa erat. Aenean quis sagittis
purus. Phasellus arcu purus, rutrum id consectetur non, bibendum at nibh.

Duis nec erat dolor. Nulla vitae consectetur ligula. Quisque nec mi est. Ut
quam ante, rutrum at pellentesque gravida, pretium in dui. Cras eget sapien
velit. Suspendisse ut sem nec tellus vehicula eleifend sit amet quis velit.
Phasellus quis suscipit nisi. Nam elementum malesuada tincidunt. Curabitur
iaculis pretium eros, malesuada faucibus leo eleifend a. Curabitur congue
orci in neque euismod a blandit libero vehicula.
""".trim().split(" ")

class GetStoriesUseCase {

    operator fun invoke(): Flow<List<Story>> {
        return flow {
            buildList {
                repeat(20) { id ->
                    val username = loremIpsum.random()
                    Story(
                        id = id.toLong(),
                        author = Story.Author(
                            userId = id.toLong(),
                            photo = Misc.getAvatar(username),
                            username = username
                        ),
                        medias = listOf(
                            Media.Image("https://images.unsplash.com/photo-1661956602116-aa6865609028?ixlib=rb-4.0.3&ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=764&q=80", true),
                            Media.Image("https://images.unsplash.com/photo-1675659999529-630a3febadfc?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80", true),
                            Media.Image("https://images.unsplash.com/photo-1645542523526-a444b4a39514?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80",true),
                            Media.Image("https://images.unsplash.com/photo-1611945440537-c552b09a89a7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80", true),
                        ).take((1..4).random()).shuffled(),
                        createdAt = LocalDateTime.now()
                            .minusDays((0..365L).random())
                            .minusHours((0..24L).random())
                            .minusMinutes((0..60L).random())
                    ).let { add(it) }
                }
            }.let {
                emit(it)
            }
        }
    }
}
