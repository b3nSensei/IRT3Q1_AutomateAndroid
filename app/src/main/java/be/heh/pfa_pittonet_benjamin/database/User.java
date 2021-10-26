package be.heh.pfa_pittonet_benjamin.database;

import java.io.Serializable;

public class User implements Serializable {

    private Integer id;
    private String name;
    private String surname;
    private String mail;
    private String password;
    private int isAdmin;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public User() {
    }

    public User(Integer id, String name, String surname, String mail, String password, int isAdmin) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}
