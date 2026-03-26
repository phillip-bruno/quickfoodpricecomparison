# Quick Food Price Comparison

An Android app that instantly compares food prices per unit across 40+ metric and imperial measurements, using real density data for 500+ food items.

## Features

- Convert between metric and imperial units for both mass and volume
- Get price-per-volume estimates using density data for 500+ food items across 20 categories
- Save a history of conversions for later reference
- Export and import conversion history as JSON
- Customizable currency symbol
- Works completely offline

## Building

### Prerequisites

- JDK 21
- Android SDK with compileSdk 35

### Debug Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Release Build (Local)

Set the signing environment variables and build:

```bash
export KEYSTORE_FILE="../keystore.jks"
export KEYSTORE_PASSWORD="<your-keystore-password>"
export KEY_ALIAS="quickfood-release"
export KEY_PASSWORD="<your-key-password>"

./gradlew bundleRelease
```

The signed AAB will be at `app/build/outputs/bundle/release/app-release.aab`.

### Release Build (CI/CD)

The GitHub Actions workflow at `.github/workflows/release.yml` builds a signed release AAB automatically.

It triggers on:
- Pushing a version tag (e.g. `v2.0.0`)
- Manual dispatch from the Actions tab

The workflow requires these repository secrets (Settings > Secrets and variables > Actions):

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded keystore file (see below) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Signing key alias |
| `KEY_PASSWORD` | Signing key password |

To generate the base64 keystore value:

```bash
base64 -w 0 keystore.jks
```

The built AAB is uploaded as a downloadable artifact on the workflow run.

## Releasing a New Version

1. Bump `versionCode` (integer, must increase every release) and `versionName` in `app/build.gradle`:

    ```gradle
    defaultConfig {
        versionCode 12
        versionName "2.1.0"
    }
    ```

2. Commit the version bump:

    ```bash
    git add app/build.gradle
    git commit -m "Bump version to 2.1.0 (versionCode 12)"
    ```

3. Tag the release and push:

    ```bash
    git tag v2.1.0
    git push origin main --tags
    ```

4. The GitHub Actions workflow will build the signed AAB. Download it from the workflow run's artifacts.

5. Upload the AAB to [Google Play Console](https://play.google.com/console):
    - Go to Release > Production (or Testing > Closed testing for staged rollout)
    - Create new release
    - Upload the AAB
    - Add release notes
    - Review and roll out

## Running Tests

```bash
./gradlew test
```
