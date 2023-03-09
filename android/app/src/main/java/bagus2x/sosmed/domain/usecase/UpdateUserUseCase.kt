package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.UserRepository
import java.time.LocalDate

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        name: String? = null,
        email: String? = null,
        password: String? = null,
        photo: String? = null,
        header: String? = null,
        bio: String? = null,
        location: String? = null,
        website: String? = null,
        dateOfBirth: LocalDate? = null
    ) {
        userRepository.update(
            name,
            email,
            password,
            photo,
            header,
            bio,
            location,
            website,
            dateOfBirth
        )
    }
}

//type UpdateUserReq struct {
//    Username    null.String `json:"username"`
//            Name        null.String `json:"name"`
//            Email       null.String `json:"email"`
//            Password    null.String `json:"password"`
//            Photo       null.String `json:"photo"`
//            Bio         null.String `json:"bio"`
//            Location    null.String `json:"location"`
//            Website     null.String `json:"website"`
//            DateOfBirth null.String `json:"dateOfBirth"`
//}
