# Bruno Android

> The codebase for Bruno's Android client.

<!-- [START getstarted] -->
## Getting Started

All configuration files are stored in the `$projectDir/team` folder which is ignored by git.
Contact [@george-lim](http://github.com/george-lim) for access to the team's existing configuration files. Alternatively, you may wish to create new configuration files for this project.

### Creating new configuration files
**NOTE: If you already have the configuration files, you may skip to the [installation](#installation) step**

1. Create a `team` folder in the project root directory and `cd` into it
2. Create `debug.keystore` by executing:
```bash
store_name='debug.keystore'
store_password='android'
key_alias='androiddebugkey'
key_password=$store_password

keytool -genkey -v -keystore $store_name -storepass $store_password -alias $key_alias -keypass $key_password -keyalg RSA -keysize 2048 -validity 100000 -dname "C=US, O=Android, CN=Android Debug"
```
3. Create `release.keystore` by executing:
```bash
store_name='release.keystore'
store_password='********'
key_alias='androidreleasekey'
key_password=$store_password

keytool -genkey -v -keystore $store_name -storepass $store_password -alias $key_alias -keypass $key_password -keyalg RSA -keysize 2048 -validity 100000
```
4. Using variable values from step 2, create `debug-config.gradle` using the following template:
```gradle
ext.debugConfig = [
  // Signing configuration keys
  store_file: '../team/{store_name}',
  store_password: {store_password},
  key_alias: {key_alias},
  key_password: {key_password},
  // Third-party secret keys
  google_api_key: '',
  spotify_client_id: '',
  spotify_client_secret: '',
  spotify_redirect_uri: ''
]
```
5. Using variable values from step 3, create `release-config.gradle` using the template from step 4

The following guides show how to create and add third-party secret keys to `debug-config.gradle`. You may want to consider repeating certain steps to have different secret keys for `release-config.gradle`.

**Google Setup**
1. Create a new project on [Google Cloud](https://console.developers.google.com/projectcreate)
2. Enable [Maps SDK for Android](https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID)
3. Enable [Directions API](https://console.developers.google.com/apis/library/directions-backend.googleapis.com?q=Directions)
4. Create and [link a billing account](https://console.developers.google.com/billing/linkedaccount)
5. Create an [API key](https://console.developers.google.com/apis/credentials?authuser=1&project=bruno-286305&supportedpurview=project)
6. Restrict the API key to the two enabled APIs
7. Add the key to `google_api_key` in `debug-config.gradle`

**Spotify Setup**
1. Create a new application from the [Spotify developer dashboard](https://developer.spotify.com/dashboard/applications)
2. Find the client ID and add it to `spotify_client_id` in `debug-config.gradle`
3. Find the client secret and add it to `spotify_client_secret` in `debug-config.gradle`
4. From the dashboard, add the package name `com.bruno.android.dev` and the `debug.keystore` SHA1 fingerprint through the `edit settings` menu. Repeat this step for the package name `com.bruno.android` and the `release.keystore` SHA1 fingerprint. You can get the SHA1 fingerprint of a Keystore by executing:
```
keytool -list -v -keystore {store_name} -storepass {store_password} -alias {key_alias} -keypass {key_password}
```
5. From the dashboard, create a redirect URI through the `edit settings` menu
6. Add the URI to `spotify_redirect_uri` in `debug-config.gradle`

### Installation

Run `./INSTALL` to complete project setup.
<!-- [END getstarted] -->
