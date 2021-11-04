package com.mdp.sportsmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.mdp.sportsmad.databinding.ActivityProfileDataBinding;
import com.mdp.sportsmad.model.UserProfile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProfileDataActivity extends AppCompatActivity {

    private ActivityProfileDataBinding binding;

    private String name;
    private int age;
    private int height;
    private float weight;
    private UserProfile.Gender gender;

    private EditText editName;
    private EditText editAge;
    private EditText editHeight;
    private EditText editWeight;
    private RadioGroup radioGroup;
    private Button submitBtn;
    private TextWatcherProfileData textWatcherProfileData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button cancelBtn = binding.formCancelBtn;
        submitBtn = binding.formSubmitBtn;
        editName = binding.formInputName.getEditText();
        editAge = binding.formInputAge.getEditText();
        editHeight = binding.formInputHeight.getEditText();
        editWeight = binding.formInputWeight.getEditText();
        radioGroup = binding.formRadiogroup;

        if (savedInstanceState != null){
            ArrayList<String> editFieldsRetrieved = savedInstanceState.getStringArrayList("list_editTexts");
            if(editFieldsRetrieved != null){
                editName.setText(editFieldsRetrieved.get(0));
                editAge.setText(editFieldsRetrieved.get(1));
                editHeight.setText(editFieldsRetrieved.get(2));
                editWeight.setText(editFieldsRetrieved.get(3));
            }
        }
        textWatcherProfileData = new TextWatcherProfileData();
        //EditTexts listeners
        editName.addTextChangedListener(textWatcherProfileData);
        editAge.addTextChangedListener(textWatcherProfileData);
        editHeight.addTextChangedListener(textWatcherProfileData);
        editWeight.addTextChangedListener(textWatcherProfileData);

        submitBtn.setEnabled(false);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = radioGroup.findViewById(checkedId);
                String selectedOption = radioButton.getText().toString();
                switch (selectedOption) {
                    case "Male":
                        gender = UserProfile.Gender.male;
                        Log.d("FORMINFO",String.valueOf(gender));
                        if(isValidUserInfo()){
                            submitBtn.setEnabled(true);
                        }else{
                            submitBtn.setEnabled(false);
                        }
                        break;

                    case "Female":
                        gender = UserProfile.Gender.female;
                        if(isValidUserInfo()){
                            submitBtn.setEnabled(true);
                        }else{
                            submitBtn.setEnabled(false);
                        }
                        Log.d("FORMINFO",getLifecycle().getCurrentState().name());

                        break;
                }
            }
        });


        //Buttons
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish(); //Finish activity and go back to Profile Fragment
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfile userProfile = new UserProfile(name, age, height, weight, gender);
                Log.d("USERINFO",userProfile.getName());
                Log.d("USERINFO",String.valueOf(userProfile.getAge()));
                Log.d("USERINFO",String.valueOf(userProfile.getHeight()));
                Log.d("USERINFO",String.valueOf(userProfile.getWeight()));
                Log.d("USERINFO",userProfile.getGender().name());
                Log.d("USERINFO",String.valueOf(userProfile.getBMI()));
                Intent userData = new Intent();
                userData.putExtra("userProfileObj", userProfile);
                setResult(RESULT_OK, userData);
                finish(); //Finish activity and go back to Profile Fragment
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> editFields = new ArrayList<>();
        editFields.add(editName.getText().toString());
        editFields.add(editAge.getText().toString());
        editFields.add(editHeight.getText().toString());
        editFields.add(editWeight.getText().toString());
        //Save editTexts value to survive orientation changes
        outState.putStringArrayList("list_editTexts",editFields);
    }

    public boolean isValidUserInfo(){
        boolean isValid = false;
        //If edit text fields are not empty
        if(!(TextUtils.isEmpty(editName.getText().toString())) &&
                !(TextUtils.isEmpty(editAge.getText().toString())) &&
                !(TextUtils.isEmpty(editHeight.getText().toString())) &&
                !(TextUtils.isEmpty(editWeight.getText().toString()))) {
                if(radioGroup.getCheckedRadioButtonId() != -1)
                    isValid = true;

        }
        return isValid;
    }

    private class TextWatcherProfileData implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable == editName.getEditableText()) {
                if (TextUtils.isEmpty(editName.getText().toString())) {
                    editName.setError("Enter your name");
                } else {
                    //Trim to remove leading and trailing spaces
                    name = editName.getText().toString().trim();
                    Log.d("FORMINFO", name);
                }

            } else if (editable == editAge.getEditableText()) {
                if (TextUtils.isEmpty(editAge.getText().toString())) {
                    editAge.setError("Enter your age");
                } else {
                    age = Integer.parseInt(editAge.getText().toString().trim());
                    Log.d("FORMINFO", String.valueOf(age));
                }

            } else if (editable == editHeight.getEditableText()) {
                if (TextUtils.isEmpty(editHeight.getText().toString())) {
                    editHeight.setError("Enter your height");
                } else {
                    height = Integer.parseInt(editHeight.getText().toString().trim());
                    Log.d("FORMINFO", String.valueOf(height));
                }

            } else if (editable == editWeight.getEditableText()) {
                if (TextUtils.isEmpty(editWeight.getText().toString())) {
                    editWeight.setError("Enter your weight");
                } else {
                    weight = Float.parseFloat(editWeight.getText().toString().trim());
                    Log.d("FORMINFO", String.valueOf(weight));
                }

            }
            if(isValidUserInfo()){
                submitBtn.setEnabled(true);
            }else{
                submitBtn.setEnabled(false);
            }

        }
    }
}