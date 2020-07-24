package com.cs446.group7.bruno.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.preferencesstorage.PreferencesStorage;

import java.util.HashMap;
import java.util.Map;

public class OnboardingAvatarFragment extends Fragment {

    private static int NUM_AVATARS = 8;

    private OnboardingFragment onboardingFragment;
    private Map<Integer, Integer> ImageViewToAvatarMapping;
    private ImageView[] avatars;
    private int curSelectedAvatar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();

        ImageViewToAvatarMapping = new HashMap<>();
        ImageViewToAvatarMapping.put(R.id.pick_avatar_1, R.drawable.ic_avatar_1);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_2, R.drawable.ic_avatar_2);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_3, R.drawable.ic_avatar_3);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_4, R.drawable.ic_avatar_4);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_5, R.drawable.ic_avatar_5);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_6, R.drawable.ic_avatar_6);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_7, R.drawable.ic_avatar_7);
        ImageViewToAvatarMapping.put(R.id.pick_avatar_8, R.drawable.ic_avatar_8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_avatar, container, false);
        setupAvatarPicker(view);
        Button btnNext = view.findViewById(R.id.btn_tab2_next);
        btnNext.setOnClickListener(this::handleNext);
        return view;
    }

    private void setupAvatarPicker(final View view) {
        avatars = new ImageView[NUM_AVATARS];
        int pos = 0;
        for (int id : ImageViewToAvatarMapping.keySet()) {
            avatars[pos] = view.findViewById(id);
            avatars[pos].setOnClickListener(this::selectAvatar);
            pos++;
        }
        // set avatar 1 as default, note that avatars array may not be in order because of hashmap
        curSelectedAvatar = R.drawable.ic_avatar_1;
        ImageView avatar1 = view.findViewById(R.id.pick_avatar_1);
        avatar1.setSelected(true);
        saveUserAvatarSetting(curSelectedAvatar);
    }

    private void selectAvatar(final View view) {
        int id = view.getId();
        curSelectedAvatar = ImageViewToAvatarMapping.get(id);
        for (ImageView imageView : avatars) {
            imageView.setSelected(false);
        }
        view.setSelected(true);
        saveUserAvatarSetting(curSelectedAvatar);
    }

    private void handleNext(final View view) {
        onboardingFragment.moveToNextTab();
    }

    private void saveUserAvatarSetting(@DrawableRes int drawable) {
        PreferencesStorage storage = MainActivity.getPreferencesStorage();
        storage.putInt(PreferencesStorage.USER_AVATAR, drawable);
    }
}