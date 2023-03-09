package bagus2x.sosmed.data

import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.local.entity.asDomainModel
import bagus2x.sosmed.data.remote.AuthRemoteDataSource
import bagus2x.sosmed.domain.model.Auth
import bagus2x.sosmed.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val authLocalDataSource: AuthLocalDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val database: SosmedDatabase
) : AuthRepository {

    override suspend fun signUp(email: String, username: String, password: String) =
        withContext(dispatcher) {
            val auth = authRemoteDataSource.signUp(email, username, password)
            authLocalDataSource.save(auth.asEntity())
        }

    override suspend fun signIn(email: String, password: String) =
        withContext(dispatcher) {
            val auth = authRemoteDataSource.signIn(email, password)
            authLocalDataSource.save(auth.asEntity())
        }

    override suspend fun signOut() = withContext(dispatcher) {
        database.clearAllTables()
        authLocalDataSource.clear()
    }

    override fun getAuth(): Flow<Auth?> {
        return authLocalDataSource
            .getAuth()
            .map { it?.asDomainModel() }
    }
}
