package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
                (AppCompatActivity) getActivity(),
                view,
                R.id.appbar_settings,
                getResources().getString(R.string.title_settings),
                false);

        LinearLayout termsAndConditionsItem = view.findViewById(R.id.settings_terms_and_conditions);
        termsAndConditionsItem.setOnClickListener(this::handleNavigateToTermsAndConditions);
        LinearLayout privacyPolicyItem = view.findViewById(R.id.settings_privacy_policy);
        privacyPolicyItem.setOnClickListener(this::handleNavigateToPrivacyPolicy);
        return view;
    }

    private void handleNavigateToTermsAndConditions(final View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplvl_to_fragmenttermsandconditions);
    }


    private void handleNavigateToPrivacyPolicy(final View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplvl_to_fragmentprivacypolicy);
    }
}
