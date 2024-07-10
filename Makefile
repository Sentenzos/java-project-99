build:
	./gradlew clean build

report:
	./gradlew jacocoTestReport

run:
	./gradlew bootRun

.PHONY: build