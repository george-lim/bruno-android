# Bruno Android

> The codebase for Bruno's Android client

<!-- [START getstarted] -->
## Getting Started

### Configuration

All third-party API keys are stored in `config.yml`.

**Google Maps Setup**
1. Create a [Google Maps API key](https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID) for this project
1. Add the key as `google_maps_key` in `config.yml`

**Spotify Setup**
1. Create a new application from the [Spotify developer dashboard](https://developer.spotify.com/dashboard/applications)
1. Find the client ID and add it as `spotify_client_id` in `config.yml`
1. Find the client secret and add it as `spotify_client_secret` in `config.yml`
1. On the dashboard, navigate to edit settings and create a redirect URI
1. Add the URI as `spotify_redirect_uri` in `config.yml`

### Installation

Run `./INSTALL` to complete project setup.
<!-- [END getstarted] -->
