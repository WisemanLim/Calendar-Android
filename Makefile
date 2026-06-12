# WiseCalA — android-compose
# Android Studio JBR 21 사용 (시스템 JDK 25/26은 Kotlin 컴파일러 미지원)
JAVA_HOME ?= /Applications/Android Studio.app/Contents/jbr/Contents/Home
export JAVA_HOME

.PHONY: dev test build deploy
dev:    ; ./gradlew installDevDebug
test:   ; ./gradlew testDevDebugUnitTest
build:  ; ./gradlew bundleProdRelease
deploy: ; cd fastlane && fastlane android beta   # Play internal
