package Util;

import android.app.Application;

public class CurrentUser extends Application {
    private String userName;
    private String userSurname;
    private String userId;
    private String userEmail;
    private String userRole;
    private String userClub;

    private static CurrentUser instance;

    public static CurrentUser getInstance(){
        if(instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    public CurrentUser(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserClub() {
        return userClub;
    }

    public void setUserClub(String userClub) {
        this.userClub = userClub;
    }

    public static void setInstance(CurrentUser instance) {
        CurrentUser.instance = instance;
    }
}
