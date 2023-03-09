package bagus2x.sosmed.domain.repository

import bagus2x.sosmed.domain.model.Auth
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUp(email: String, username: String, password: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signOut()

    fun getAuth(): Flow<Auth?>
}
