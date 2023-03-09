package collections

func Filter[T any](data []T, f func(T) bool) []T {

	res := make([]T, 0, len(data))

	for _, e := range data {
		if f(e) {
			res = append(res, e)
		}
	}

	return res
}

func Map[T, U any](data []T, f func(T) U) []U {

	res := make([]U, 0, len(data))

	for _, e := range data {
		res = append(res, f(e))
	}

	return res
}
