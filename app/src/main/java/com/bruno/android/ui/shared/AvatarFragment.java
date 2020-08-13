package com.bruno.android.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.storage.PreferencesStorage;

import java.util.HashMap;
import java.util.Map;

public class AvatarFragment extends Fragment {

    private static final int NUM_AVATARS = 8;

    private Map<Integer, Integer> imageViewToAvatarMapping;
    private ImageView[] avatars;
    private int curSelectedAvatar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageViewToAvatarMapping = new HashMap<>();
        imageViewToAvatarMapping.put(R.id.pick_avatar_1, R.drawable.ic_avatar_1);
        imageViewToAvatarMapping.put(R.id.pick_avatar_2, R.drawable.ic_avatar_2);
        imageViewToAvatarMapping.put(R.id.pick_avatar_3, R.drawable.ic_avatar_3);
        imageViewToAvatarMapping.put(R.id.pick_avatar_4, R.drawable.ic_avatar_4);
        imageViewToAvatarMapping.put(R.id.pick_avatar_5, R.drawable.ic_avatar_5);
        imageViewToAvatarMapping.put(R.id.pick_avatar_6, R.drawable.ic_avatar_6);
        imageViewToAvatarMapping.put(R.id.pick_avatar_7, R.drawable.ic_avatar_7);
        imageViewToAvatarMapping.put(R.id.pick_avatar_8, R.drawable.ic_avatar_8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        setupAvatarPicker(view);
        return view;
    }

    private void setupAvatarPicker(final View view) {
        avatars = new ImageView[NUM_AVATARS];
        int pos = 0;
        for (int id : imageViewToAvatarMapping.keySet()) {
            avatars[pos] = view.findViewById(id);
            avatars[pos].setOnClickListener(this::selectAvatar);
            pos++;
        }
        // set avatar 1 as default, note that avatars array may not be in order because of hashmap
        curSelectedAvatar = MainActivity.getPreferencesStorage()
                .getInt(PreferencesStorage.KEYS.USER_AVATAR, PreferencesStorage.DEFAULT_AVATAR);
        int imageViewId = R.id.pick_avatar_1;
        for (Map.Entry<Integer, Integer> mapping : imageViewToAvatarMapping.entrySet()) {
            if (curSelectedAvatar == mapping.getValue()) {
                imageViewId = mapping.getKey();
                break;
            }
        }
        ImageView avatar1 = view.findViewById(imageViewId);
        avatar1.setSelected(true);
        saveUserAvatarSetting(curSelectedAvatar);
    }

    private void selectAvatar(final View view) {
        int id = view.getId();
        //noinspection ConstantConditions
        curSelectedAvatar = imageViewToAvatarMapping.get(id);
        for (ImageView imageView : avatars) {
            imageView.setSelected(false);
        }
        view.setSelected(true);
        saveUserAvatarSetting(curSelectedAvatar);
    }

    private void saveUserAvatarSetting(@DrawableRes int drawable) {
        PreferencesStorage storage = MainActivity.getPreferencesStorage();
        storage.putInt(PreferencesStorage.KEYS.USER_AVATAR, drawable);
    }
}