include app.env
export

dev:
	go run -x main.go

build:
	go build -x main.go

run-build:
	./main

build-w:
	GOOS=linux GOARCH=amd64 go build main.go

create_migration:
	migrate create -ext sql -dir migration -seq $(name)

migrate:
	migrate -source file://migration -database ${POSTGRES_URI} $(t)

migrateN:
	migrate -source file://migration -database ${POSTGRES_URI} $(t) $(n)

force:
	migrate -path migration -database ${POSTGRES_URI} force $(v)

drop:
	migrate -path migration -database ${POSTGRES_URI} drop