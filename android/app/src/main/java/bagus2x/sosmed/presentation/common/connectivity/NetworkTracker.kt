package bagus2x.sosmed.presentation.common.connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkTracker {

    val flow: Flow<State>

    sealed class State

    object Init : State()

    object Available : State()

    object Unavailable : State()
}
