build:
	gradle clean build

report:
	gradle jacocoTestReport

.PHONY: build