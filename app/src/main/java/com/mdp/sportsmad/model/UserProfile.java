package com.mdp.sportsmad.model;

import java.io.Serializable;

/**
 * Class that describes a User with his personal information and steps done.
 */
public class UserProfile implements Serializable {

    public enum Gender {male, female, undefined}

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

    /**
     * Calculates the user's BMI
     */
    private void calculateBMI(){
        this.BMI = weight / ((this.height/100f)*(this.height/100f));
    }

    /**
     * Get the user's name
     * @return name
     */
    public String getName() { return name; }

    /**
     * Get the user's age
     * @return age
     */
    public int getAge() { return age; }

    /**
     * Get the user's height
     * @return height in cm
     */
    public int getHeight() { return height; }

    /**
     * Get the user's weight
     * @return weight in kg
     */
    public float getWeight() { return weight; }

    /**
     * Get the user's gender
     * @return gender (male, female)
     */
    public Gender getGender() { return gender; }

    /**
     * Get the user's BMI (Body Mass Index)
     * @return BMI
     */
    public float getBMI() { return BMI; }

    /**
     * Get the user's steps
     * @return steps number
     */
    public int getSteps() { return steps; }

    private void calculateCaloriesBurned(float speedFactor){
        //https://calculator.academy/steps-to-calories-calculator/
        this.caloriesBurned = this.steps * 0.04f * this.BMI * this.age * speedFactor;
    }

    /**
     * Set the user's steps
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }
}
