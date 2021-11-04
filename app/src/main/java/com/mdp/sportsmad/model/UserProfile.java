package com.mdp.sportsmad.model;

import java.io.Serializable;

public class UserProfile implements Serializable {

    public enum Gender {male, female}

    private String name;
    private int age;
    private int height; //cm
    private float weight; //kg
    private final Gender gender;

    private float BMI;
    private int steps;
    private float caloriesBurned;

    public UserProfile(String name, int age, int height, float weight, Gender gender ){
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        calculateBMI();
        this.steps = 0;
        this.caloriesBurned = 0;

    }

    private void calculateBMI(){
        this.BMI = weight / ((this.height/100f)*(this.height/100f));
    }

    public String getName() { return name; }

    public int getAge() { return age; }

    public int getHeight() { return height; }

    public float getWeight() { return weight; }

    public Gender getGender() { return gender; }

    public float getBMI() { return BMI; }

    public int getSteps() { return steps; }

    private void calculateCaloriesBurned(float speedFactor){
        //https://calculator.academy/steps-to-calories-calculator/
        this.caloriesBurned = this.steps * 0.04f * this.BMI * this.age * speedFactor;
    }
}
