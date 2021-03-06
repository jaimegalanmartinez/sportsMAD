package com.mdp.sportsmad.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mdp.sportsmad.activities.ProfileDataActivity;
import com.mdp.sportsmad.R;
import com.mdp.sportsmad.databinding.FragmentProfileBinding;
import com.mdp.sportsmad.model.UserProfile;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.jetbrains.annotations.NotNull;
/**
 * Corresponds to the Profile tab
 */
public class ProfileFragment extends Fragment implements SensorEventListener {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String fileNameDefaultSharedPreferences;
    private UserProfile userProfile;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount;
    private int maxSteps;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        maxSteps = 300;
        //Circular progress bar
        //Set max steps and progress bar direction
        binding.profileUserStepsMax.setText(getString(R.string.steps_count_max,maxSteps));
        binding.profileCircularProgressBarSteps.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
        binding.profileCircularProgressBarSteps.setProgressMax((float)maxSteps);
        View root = binding.getRoot();
        //Detect if the device supports the step sensor
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            Log.d("Hardware supported:", "Step detector sensor supported");
        }

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

        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted-> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        Toast.makeText(getActivity(), "Step sensor not supported. Need the activity permission.", Toast.LENGTH_LONG).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            //Ask for permission
            //If sdk device version >= android 10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionLauncher.launch((Manifest.permission.ACTIVITY_RECOGNITION));
            }
        }
        // Get the reference to the sensor manager and the sensors:
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        //UI references
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCount = 0;

        //Listener to check if switch Step sensor state changes
        binding.switchStepSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //Called when the checked state of a compound button has changed.
                if (isChecked) {
                    //If the switch button changed from off to on
                    sensorManager.registerListener(ProfileFragment.this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    //If the switch button changed from on to off
                    sensorManager.unregisterListener(ProfileFragment.this, stepSensor);
                }
            }
        });

        //Set long click listener to reset steps count when making long click in steps number count
        binding.profileUserStepsValue.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                stepCount = 0;
                if(userProfile != null) {
                    userProfile.setSteps(stepCount);
                    if(binding != null) {
                        binding.profileUserStepsValue.setText(String.valueOf(userProfile.getSteps()));
                        binding.profileCircularProgressBarSteps.setProgressWithAnimation((float) userProfile.getSteps());
                        Toast.makeText(getContext(),"Steps reseted", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        //To receive the userProfile data object when the user submits his information in the ProfileDataActivity
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
                                userProfile.setSteps(stepCount);
                                profileViewModel.setmUserProfile(userProfile);
                                Log.d("USERINFO",userProfile.getName());
                                Log.d("USERINFO",String.valueOf(userProfile.getAge()));
                                Log.d("USERINFO",String.valueOf(userProfile.getHeight()));
                                Log.d("USERINFO",String.valueOf(userProfile.getWeight()));
                                Log.d("USERINFO",userProfile.getGender().name());
                                Log.d("USERINFO",String.valueOf(userProfile.getBMI()));
                            }

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
    public void onStart() {
        super.onStart();
        fileNameDefaultSharedPreferences = getActivity().getPackageName() + "_preferences";
        SharedPreferences sharedPref = getActivity().getSharedPreferences(fileNameDefaultSharedPreferences, Context.MODE_PRIVATE);
        //Retrieve user profile data from shared preferences
        userProfile = new UserProfile(sharedPref.getString("nameUser", "Name"),
                sharedPref.getInt("ageUser", 0), sharedPref.getInt("heightUser", 0),
                sharedPref.getFloat("weightUser", 0f),
                UserProfile.Gender.valueOf(sharedPref.getString("genderUser", "undefined")));

        //Retrieve step count from shared preferences
        stepCount = sharedPref.getInt("stepCount", 0);
        //Set step count to the circular progress bar
        binding.profileCircularProgressBarSteps.setProgress(stepCount);
        //Set steps to userProfile
        userProfile.setSteps(stepCount);
        profileViewModel.setmUserProfile(userProfile);

        //Retrieve switch Step sensor status
        binding.switchStepSensor.setChecked(sharedPref.getBoolean("switchStepSensor", false));

        //Set user profile data
        binding.profileUserNameValue.setText(userProfile.getName());
        binding.profileUserAgeValue.setText(String.valueOf(userProfile.getAge()));
        binding.profileUserHeightValue.setText(String.valueOf(userProfile.getHeight()));
        binding.profileUserWeightValue.setText(String.valueOf(userProfile.getWeight()));
        binding.profileUserGenderValue.setText(userProfile.getGender().name());
        binding.profileUserBMIValue.setText(String.format("%.2f",userProfile.getBMI()));
        binding.profileUserStepsValue.setText(String.valueOf(userProfile.getSteps()));

    }

    @Override
    public void onStop() {
        super.onStop();
        fileNameDefaultSharedPreferences = getActivity().getPackageName() + "_preferences";
        SharedPreferences sharedPref = getActivity().getSharedPreferences(fileNameDefaultSharedPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Store userProfile data on shared preferences
        if(userProfile != null){
            editor.putString("nameUser", userProfile.getName());
            editor.putInt("ageUser", userProfile.getAge());
            editor.putInt("heightUser", userProfile.getHeight());
            editor.putFloat("weightUser", userProfile.getWeight());
            editor.putString("genderUser", userProfile.getGender().name());
            editor.putInt("stepCount",stepCount);
        }
        //Store switch stepSensor status on shared preferences
        if(binding.switchStepSensor.isChecked()){
            editor.putBoolean("switchStepSensor", true);
        }else {
            editor.putBoolean("switchStepSensor", false);
            sensorManager.unregisterListener(ProfileFragment.this, stepSensor);
        }

        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //sensorEvent.values[0] = 1 if step detected
            stepCount = (int) (stepCount + sensorEvent.values[0]);
            if(userProfile != null){
                userProfile.setSteps(stepCount);
                if(binding!=null) {
                    //Set steps count in UI (TextView and Progress Bar)
                    binding.profileUserStepsValue.setText(String.valueOf(userProfile.getSteps()));
                    binding.profileCircularProgressBarSteps.setProgressWithAnimation((float) userProfile.getSteps());
                }
                Log.d("STEPSCOUNT:",stepCount + " "+userProfile.getSteps());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}