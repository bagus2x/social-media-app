package models

type Error struct {
	Message string `json:"message"`
	Code    int    `json:"code"`
}

func (e *Error) Error() string {
	return e.Message
}

func NewError(message string, code int) error {
	return &Error{message, code}
}

var (
	ErrBadRequest   = NewError("Bad request", 400)
	ErrUnauthorized = NewError("Unauthorized", 401)
	ErrForbidden    = NewError("Forbidden", 403)
	ErrNotFound     = NewError("Not found", 404)
	ErrConflict     = NewError("Conflict", 409)
	ErrServerError  = NewError("Server error", 500)
)
