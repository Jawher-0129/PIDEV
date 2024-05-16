package test;

import Entity.User;
import cnx.MyConnections;
import service.Usercrud;
import java.util.ArrayList;
import java.util.List;



public class Main {

    public static void main(String[] args) {
        MyConnections cnx= new MyConnections();
        System.out.println(cnx);
        User u1 =new User(23,"firas","zighni","firas@gmail.com","firas123","tunis","4534333","Admin");
        //User updatedUser = new User( 19, "Name", "LastName", "fggfgd@example.com", "firas1234", "tunis","567567466","donateur");
        Usercrud serv =new Usercrud();
        serv.addUser(u1);
        //serv.updateUser(updatedUser);
        //serv.deleteUser(updatedUser);

        List<User> userList = serv.getAllData();

        // Display the list of users
        System.out.println("List of users:");
        for (User user : userList) {
            System.out.println(user); // Assuming User class has overridden toString() method
        }

    }
}
