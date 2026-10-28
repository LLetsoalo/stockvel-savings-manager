
# Stockvel Savings Manager
# A web application for managing members, monthly contributions,
# loans, repayments, and downloadable contribution reports.
#
# Requirements: Java 25, PostgreSQL, and GNU Make.
# Configure the Spring datasource environment variables before running.
#
# Run `make help` to see the available commands.

.PHONY: help run test package clean

ifeq ($(OS),Windows_NT)
MVNW := .\mvnw.cmd
else
MVNW := ./mvnw
endif

help:
	@echo "Stockvel Savings Manager"
	@echo "  make run      Start the application"
	@echo "  Windows      .\mvnw.cmd spring-boot:run"
	@echo "  make test     Run the test suite"
	@echo "  make package  Build the executable JAR"
	@echo "  make clean    Remove generated build files"

run:
	$(MVNW) spring-boot:run

test:
	$(MVNW) test

package:
	$(MVNW) clean package

clean:
	$(MVNW) clean
