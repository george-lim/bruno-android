#!/bin/bash

# Copy hooks
cp hooks/* .git/hooks

# Install API key (if found)
google_maps_key=${1:-'<!-- Add API Key here -->'}
echo -e "<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>" > app/src/debug/res/values/google_maps_api.xml
echo -e "<resources>\n\t<string name=\"google_maps_key\" templateMergeStrategy=\"preserve\" translatable=\"false\">\n\t\t$google_maps_key\n\t</string>\n</resources>" > app/src/release/res/values/google_maps_api.xml
