#!/bin/bash

# cd to project root
cd `dirname $0`

# Copy hooks
cp hooks/* .git/hooks

# Install API key (if found)
google_maps_key=${1:-'<!-- Add Google Maps API Key here -->'}
google_maps_api_xml="<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>"

spotify_client_id=${2:-'<!-- Add Spotify Client ID here -->'}
spotify_client_id_xml="<resources>\n\t<string name=\"spotify_client_id\">\n\t\t
$spotify_client_id\n\t</string>\n</resources>"

spotify_client_secret=${3:-'<!-- Add Spotify Client Secret here -->'}
spotify_client_secret_xml="<resources>\n\t<string name=\"spotify_client_secret\">\n\t\t
$spotify_client_secret\n\t</string>\n</resources>"

spotify_redirect_uri=${4:-'<!-- Add Spotify Redirect URI here -->'}
spotify_redirect_uri_xml="<resources>\n\t<string name=\"spotify_redirect_uri\">\n\t\t
$spotify_redirect_uri\n\t</string>\n</resources>"


for config in debug release; do
  api_key_dir=app/src/$config/res/values
  mkdir -p $api_key_dir
  echo -e $google_maps_api_xml > $api_key_dir/google_maps_api.xml
  echo -e $spotify_client_id_xml > $api_key_dir/spotify_client_id.xml
  echo -e $spotify_client_secret_xml > $api_key_dir/spotify_client_secret.xml
  echo -e $spotify_redirect_uri_xml > $api_key_dir/spotify_redirect_uri.xml


done
