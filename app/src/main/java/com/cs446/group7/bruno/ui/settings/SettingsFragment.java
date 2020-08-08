package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) requireActivity(),
                view,
                R.id.appbar_settings,
                getResources().getString(R.string.title_settings),
                false);
        setupUserPreferences(view);
        setupAboutBruno(view);
        return view;
    }

    private void setupUserPreferences(final View rootView) {
        LinearLayout avatarItem = rootView.findViewById(R.id.settings_avatar);
        avatarItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplevel_to_fragmentsettingavatar));
        LinearLayout fallbackPlaylistItem = rootView.findViewById(R.id.settings_fallback_playlist);
        fallbackPlaylistItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplevel_to_fragmentfallbackplaylist));
    }

    private void setupAboutBruno(final View rootView) {
        LinearLayout termsAndConditionsItem = rootView.findViewById(R.id.settings_terms_and_conditions);
        termsAndConditionsItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmenttermsandconditions));
        LinearLayout privacyPolicyItem = rootView.findViewById(R.id.settings_privacy_policy);
        privacyPolicyItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmentprivacypolicy));
        LinearLayout creditsItem = rootView.findViewById(R.id.settings_credits);
        creditsItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmentcredits));
    }

    private void navigateUsingAction(@IdRes final int action) {
        if (getActivity() != null) {
            NavController navController = Navigation.findNavController(
                    getActivity(),
                    R.id.nav_host_fragment
            );
            navController.navigate(action);
        }
    }
}
