package models

type Auth struct {
	AccessToken  string `json:"accessToken"`
	RefreshToken string `json:"refreshToken"`
}

type AuthResponse struct {
	AccessToken  string  `json:"accessToken"`
	RefreshToken string  `json:"refreshToken"`
	Profile      Profile `json:"profile"`
}

type SignUpReq struct {
	Email    string `json:"email"`
	Username string `json:"username"`
	Password string `json:"password"`
}

type SignInReq struct {
	UsernameOrEmail string `json:"username"`
	Password        string `json:"password"`
}
