package com.example.inzynierka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ClubFragment extends Fragment {

    private BottomNavigationView club_nav;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_club, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        club_nav = requireView().findViewById(R.id.club_navigation);

        club_nav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.memberFragment) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new MemberFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.exerciseFragment) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new ExerciseFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.calendarFragment) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new CalendarFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.squadFragment) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new SquadFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.profileFragment) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new ProfileFragment())
                        .commit();
                return true;
            } else {
                return false;
            }
        });
    }

}