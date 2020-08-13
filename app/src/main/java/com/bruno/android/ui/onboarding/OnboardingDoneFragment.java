package com.bruno.android.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.storage.PreferencesStorage;

public class OnboardingDoneFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_done, container, false);
        Button btnGetStarted = view.findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(this::finishOnboarding);
        return view;
    }

    private void finishOnboarding(final View view) {
        PreferencesStorage storage = MainActivity.getPreferencesStorage();
        storage.putBoolean(PreferencesStorage.KEYS.COMPLETED_ONBOARDING, true);

        if (getActivity() != null) {
            NavController navController = Navigation.findNavController(
                    getActivity(),
                    R.id.nav_host_fragment
            );
            navController.navigate(R.id.action_fragmentonboarding_to_fragmenttoplevel);
        }
    }
}
