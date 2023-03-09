package bagus2x.sosmed.data.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

fun <Local, Remote> networkBoundResource(
    local: () -> Flow<Local>,
    remote: suspend () -> Remote,
    shouldUpdate: (cache: Local) -> Boolean = { true },
    update: suspend (latest: Remote) -> Unit
): Flow<Local> {
    return flow {
        val cache = local().first()
        emit(cache)
        if (shouldUpdate(cache)) {
            val latest = remote()
            update(latest)
        }
        emitAll(local())
    }
}

fun <Local, Remote> networkBoundResource(
    local: () -> Flow<Local>,
    remote: suspend () -> Remote,
    shouldUpdate: (cache: Local) -> Boolean = { true },
    update: suspend (cache: Local?, latest: Remote) -> Unit
): Flow<Local> {
    return flow {
        val cache = local().first()
        emit(cache)
        if (shouldUpdate(cache)) {
            val latest = remote()
            update(cache, latest)
        }
        emitAll(local())
    }
}
