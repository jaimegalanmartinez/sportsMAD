package com.mdp.sportsmad.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mdp.sportsmad.ProfileDataActivity;
import com.mdp.sportsmad.databinding.FragmentProfileBinding;
import com.mdp.sportsmad.model.UserProfile;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private UserProfile userProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                //Update the UI
                if(userProfile != null){
                    binding.profileUserNameValue.setText(userProfile.getName());
                    binding.profileUserAgeValue.setText(String.valueOf(userProfile.getAge()));
                    binding.profileUserHeightValue.setText(String.valueOf(userProfile.getHeight()));
                    binding.profileUserWeightValue.setText(String.valueOf(userProfile.getWeight()));
                    binding.profileUserGenderValue.setText(userProfile.getGender().name());
                    binding.profileUserBMIValue.setText(String.format("%.2f",userProfile.getBMI()));
                    binding.profileUserStepsValue.setText(String.valueOf(userProfile.getSteps()));
                }


            }
        });
        //UI references

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Retrieve user profile data
                            Intent dataRetrieved = result.getData();
                            userProfile = (UserProfile) dataRetrieved.getSerializableExtra("userProfileObj");
                            if(userProfile != null){
                                profileViewModel.setmUserProfile(userProfile);
                                Log.d("USERINFO",userProfile.getName());
                                Log.d("USERINFO",String.valueOf(userProfile.getAge()));
                                Log.d("USERINFO",String.valueOf(userProfile.getHeight()));
                                Log.d("USERINFO",String.valueOf(userProfile.getWeight()));
                                Log.d("USERINFO",userProfile.getGender().name());
                                Log.d("USERINFO",String.valueOf(userProfile.getBMI()));
                            }

                            //doSomeOperations();
                        }else if (result.getResultCode() == Activity.RESULT_CANCELED){

                        }
                    }
                });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnFillPersonalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch(new Intent(getActivity(), ProfileDataActivity.class));
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}