package client;

import entity.Staff;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class StaffModule {

    final static Staff[] staffs = {new Staff("S001","98765","Mr Ridzuan")};
    public static int loginTries = 0;
    private static boolean loginSuccess = false;
    private static Scanner input = new Scanner(System.in);
    public static Staff login(){

        System.out.printf("%38s Enter your staff id : ","");
        String id = input.next();
        System.out.printf("%38s Enter your password : ","");
        String password = input.next();

        loginTries++;

        Optional<Staff> optional = Arrays.stream(staffs)
                .filter(e -> e.authenticate(id,password))
                .findFirst();

        if(optional.isPresent()) {
            optional.get().setLogin(true);
            return optional.get();
        }
        else
            return null;


    }
}
