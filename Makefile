.PHONY: db-up app-up db-down

db-up:
	@docker compose ps postgres | grep "Up" > /dev/null || docker compose up -d postgres

db-down:
	@docker compose down