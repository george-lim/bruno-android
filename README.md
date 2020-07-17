# CS 446: Group 7 Project

> Contains the codebase for Group 7's CS 446 project.

<!-- [START getstarted] -->
## Getting Started

### Environment Setup

To install, run:

```bash
bash INSTALL.sh
# or
bash INSTALL.sh <google_maps_key> <spotify_client_id> <spotify_client_secret> <spotify_redirect_uri>
```

Ensure that API 30 is installed in Android Studio by navigating to Android Studio -> Preferences -> System Settings -> Android SDK.
![Demonstration](https://i.imgur.com/3RxmVZP.png)

### API Key Setup
Follow the link [here](https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID) to create a Google Maps API key for this project.
Once you have your key (it starts with "AIza"), replace the `google_maps_key` string in `google_maps_api.xml` file.

### Spotify Key Setup
Visit [the Spotify developer dashboard](https://developer.spotify.com/dashboard/applications) and create a new application. You will receive a client id and client secret.
For the redirect URI, you will need to click "Edit settings" on your dashboard and create a URI from that interface.
You can then either edit `INSTALL.sh` and paste your keys in there, or call `INSTALL.sh` with the keys through command-line.

### Assumptions
- This app currently supports Spotify Premium users.
- When exiting a run early, Spotify will stop playing.
- All Spotify errors during a run will cause your run to exit early.
<!-- [END getstarted] -->
