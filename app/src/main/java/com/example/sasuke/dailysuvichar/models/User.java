package com.example.sasuke.dailysuvichar.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rishabhshukla on 22/04/17.
 */

public class User {
    private String name;
    private String email;
    private String bio;
    private String preferredLang;
    private ArrayList<String> interests;
    private String photoUrl;
    private String coverUrl;
    private HashMap<String, Boolean> motivation, religion, astrology, yoga, ayurveda, health, diet;
    private HashMap<String, HashMap<String, Boolean>> allInterests;
    private String DOB;
    private String gender;
    private String phone;
    private int age;
    ArrayList<Post> posts;

    public User(){
        //Empty Constructor
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPreferredLang(String preferredLang) {
        this.preferredLang = preferredLang;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setMotivation(HashMap<String, Boolean> motivation) {
        this.motivation = motivation;
    }

    public void setReligion(HashMap<String, Boolean> religion) {
        this.religion = religion;
    }

    public void setAstrology(HashMap<String, Boolean> astrology) {
        this.astrology = astrology;
    }

    public User(String name, String email, String bio, String preferredLang, ArrayList<String> interests, String photoUrl, String coverUrl, String DOB, String gender, String phone, int age) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.preferredLang = preferredLang;
        this.interests = interests;
        this.photoUrl = photoUrl;
        this.coverUrl = coverUrl;
        this.DOB = DOB;
        this.gender = gender;
        this.phone = phone;
        this.age = age;
    }

    public void setYoga(HashMap<String, Boolean> yoga) {
        this.yoga = yoga;
    }

    public void setAyurveda(HashMap<String, Boolean> ayurveda) {
        this.ayurveda = ayurveda;
    }

    public void setHealth(HashMap<String, Boolean> health) {
        this.health = health;
    }

    public void setDiet(HashMap<String, Boolean> diet) {
        this.diet = diet;
    }


    public void setAllInterests(HashMap<String, HashMap<String, Boolean>> allInterests) {
        this.allInterests = allInterests;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public ArrayList<String> getInterests() {

        return interests;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getPreferredLang() {
        return preferredLang;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public HashMap<String, Boolean> getMotivation() {
        return motivation;
    }

    public HashMap<String, Boolean> getReligion() {
        return religion;
    }

    public HashMap<String, Boolean> getAstrology() {
        return astrology;
    }

    public HashMap<String, Boolean> getYoga() {
        return yoga;
    }

    public HashMap<String, Boolean> getAyurveda() {
        return ayurveda;
    }

    public HashMap<String, Boolean> getHealth() {
        return health;
    }

    public HashMap<String, Boolean> getDiet() {
        return diet;
    }

    public HashMap<String, HashMap<String, Boolean>> getAllInterests() {
        return allInterests;
    }

    public String getDOB() {
        return DOB;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }
}
