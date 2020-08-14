# Bruno Android

> The codebase for Bruno's Android client.

<!-- [START getstarted] -->
## Getting Started

### Import configuration

1. Contact [@george-lim](http://github.com/george-lim/cs-446-group-7-project/pulls) for access to the team's `config.yml`
1. Copy `config.yml` to the root directory

### Create new configuration

Create a file in the root directory called `config.yml` with the following template:
```yaml
debug:
  google_api_key: ''
  spotify_client_id: ''
  spotify_client_secret: ''
  spotify_redirect_uri: ''
release:
  google_api_key: ''
  spotify_client_id: ''
  spotify_client_secret: ''
  spotify_redirect_uri: ''
```

**Google Setup**
1. Create a new project on [Google Cloud](https://console.developers.google.com/projectcreate)
1. Enable [Maps SDK for Android](https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID)
1. Enable [Directions API](https://console.developers.google.com/apis/library/directions-backend.googleapis.com?q=Directions)
1. Create and [link a billing account](https://console.developers.google.com/billing/linkedaccount)
1. Create an [API key](https://console.developers.google.com/apis/credentials?authuser=1&project=bruno-286305&supportedpurview=project)
1. Restrict the API key application to `Android apps` and APIs to the ones listed above
1. Add the key to `google_api_key` in `config.yml`

**Spotify Setup**
1. Create a new application from the [Spotify developer dashboard](https://developer.spotify.com/dashboard/applications)
1. Find the client ID and add it to `spotify_client_id` in `config.yml`
1. Find the client secret and add it to `spotify_client_secret` in `config.yml`
1. From the dashboard, create a redirect URI through the `edit settings` menu
1. Add the URI to `spotify_redirect_uri` in `config.yml`

### Installation

Run `./INSTALL` to complete project setup.
<!-- [END getstarted] -->
