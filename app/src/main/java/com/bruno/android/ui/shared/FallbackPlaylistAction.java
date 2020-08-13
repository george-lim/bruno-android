package com.bruno.android.ui.shared;

import android.view.View;

public interface FallbackPlaylistAction {
    enum ActionType {
        SELECT_PLAYLIST,
        NO_PLAYLIST,
        QUIT
    }

    void updatePrimaryAction(ActionType action, final View.OnClickListener clickListener);
    void onSelectPlaylistPressed();
    void onNoPlaylistPressed();
}
