.PHONY: db-up app-up db-down

db-up:
	@docker compose ps postgres | grep "Up" > /dev/null || docker compose up -d postgres

app-up: db-up
	@mvn spring-boot:run

db-down:
	@docker compose down