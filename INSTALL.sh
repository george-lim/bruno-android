#!/bin/bash

# cd to project root
cd `dirname $0`

# Copy hooks
cp hooks/* .git/hooks

# Install API key (if found)
google_maps_key=${1:-'<!-- Add Google Maps API Key here -->'}
google_maps_api_xml="<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>"

spotify_client_id=${2:-'<!-- Add Spotify Client ID here -->'}
spotify_client_id_xml="\t<string name=\"spotify_client_id\">\n\t\t
$spotify_client_id\n\t</string>"

spotify_client_secret=${3:-'<!-- Add Spotify Client Secret here -->'}
spotify_client_secret_xml="\t<string name=\"spotify_client_secret\">\n\t\t
$spotify_client_secret\n\t</string>"

spotify_redirect_uri=${4:-'<!-- Add Spotify Redirect URI here -->'}
spotify_redirect_uri_xml="\t<string name=\"spotify_redirect_uri\">\n\t\t
$spotify_redirect_uri\n\t</string>"


for config in debug release; do
  api_key_dir=app/src/$config/res/values
  mkdir -p $api_key_dir
  echo -e $google_maps_api_xml > $api_key_dir/google_maps_api.xml

  # Combines Spotify keys into a single resource file
  if [ -f "$api_key_dir/spotify.xml" ]; then
    rm $api_key_dir/spotify.xml
  fi

  echo -e "<resources>" >> $api_key_dir/spotify.xml
  echo -e $spotify_client_id_xml >> $api_key_dir/spotify.xml
  echo -e $spotify_client_secret_xml >> $api_key_dir/spotify.xml
  echo -e $spotify_redirect_uri_xml >> $api_key_dir/spotify.xml
  echo -e "</resources>" >> $api_key_dir/spotify.xml

done

# Install spotify-app-remote library if it does not exist
spotify_app_remote_libary_location=https://github.com/spotify/android-sdk/releases/download/v7.0.0-appremote_v1.2.3-auth/spotify-app-remote-release-0.7.0.aar 
spotify_app_remote_release_dir=dependencies/spotify-app-remote
spotify_app_remote_release_file=spotify-app-remote-release-0.7.0.aar

if [ ! -f "$spotify_app_remote_release_dir/$spotify_app_remote_release_file" ]; then
  echo "Spotify app remote library missing, downloading from GitHub"
  curl -L "$spotify_app_remote_libary_location" --output "$spotify_app_remote_release_dir/$spotify_app_remote_release_file"
fi
