package com.alex.wordsreminder.models;

import java.util.Date;


public class UserModel{
    private String id;
    private String user_name;
    private String email;
    private String interfaceLanguage;
    private String main_language;
    private String learning_language;
    private int daily_load;
    private String dateCreated;
    private String is_current;
    private static UserModel mInstance = null;


    public void setNotificationsTime(String notificationsTime) {
        this.notificationsTime = notificationsTime;
    }

    private String notificationsTime;

    public UserModel(String id,
                     String user_name,
                     String email,
                     String interfaceLanguage,
                     String main_language,
                     String learning_language,
                     int daily_load,
                     String dateCreated,
                     String is_current,
                     String notificationsTime) {
        this.id = id;
        this.user_name = user_name;
        this.email = email;
        this.interfaceLanguage = interfaceLanguage;
        this.main_language = main_language;
        this.learning_language = learning_language;
        this.daily_load = daily_load;
        this.dateCreated = dateCreated;
        this.is_current = is_current;
        this.notificationsTime = notificationsTime;
    }


    public static UserModel newInstance(){
        if (mInstance == null) {
            mInstance = new UserModel("1",
                    "",
                    "",
                    "en",
                    "uk",
                    "en",
                    3,
                    String.valueOf(new Date()),
                    "1",
                    "OFF");
        }
        return mInstance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMain_language() {
        return main_language;
    }

    public void setMain_language(String main_language) {
        this.main_language = main_language;
    }

    public String getLearning_language() {
        return learning_language;
    }

    public void setLearning_language(String learning_language) {
        this.learning_language = learning_language;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getDaily_load() {
        return daily_load;
    }

    public void setDaily_load(int daily_load) {
        this.daily_load = daily_load;
    }

    public String getIs_current() {
        return is_current;
    }

    public void setIs_current(String is_current) {
        this.is_current = is_current;
    }


    public int getIntId() {
        return Integer.parseInt(id);
    }

    public String getNotificationsTime() {
        return notificationsTime;
    }


}
