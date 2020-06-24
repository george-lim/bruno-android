#!/bin/bash

# cd to project root
cd `dirname $0`

# Copy hooks
cp hooks/* .git/hooks

# Install API key (if found)
google_maps_key=${1:-'<!-- Add API Key here -->'}
mkdir -p app/src/debug/res/values
echo -e "<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>" > app/src/debug/res/values/google_maps_api.xml
mkdir -p app/src/release/res/values
echo -e "<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>" > app/src/release/res/values/google_maps_api.xml
