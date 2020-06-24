#!/bin/bash

# cd to project root
cd `dirname $0`

# Copy hooks
cp hooks/* .git/hooks

# Install API key (if found)
google_maps_key=${1:-'<!-- Add API Key here -->'}
google_maps_api_xml="<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>"

for config in debug release; do
  api_key_dir=app/src/$config/res/values
  mkdir -p api_key_dir
  echo -e $google_maps_api_xml > $api_key_dir/google_maps_api.xml
done
