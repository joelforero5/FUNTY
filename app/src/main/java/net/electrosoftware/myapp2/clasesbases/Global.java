package net.electrosoftware.myapp2.clasesbases;

import java.util.ArrayList;

public class Global {

    static ArrayList<User> listaUsers = new ArrayList<User>();

    public static ArrayList<User> getListaUsers() {
        return listaUsers;
    }

    public static void setListaUsers(ArrayList<User> listaUsers) {
        Global.listaUsers = listaUsers;
    }
}
