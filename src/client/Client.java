package client;

import entity.Staff;

import java.io.IOException;
public class Client {

    private static boolean programEnd = false;

    public static void main(String[] args) throws IOException {

        run();

    }
    public static void run() throws IOException{

        Staff currentStaff;

        do{
            System.out.println(PrintingUtil.getLogo());
            System.out.printf("%38s Welcome to 3PM Yamcha Catering System\n", "");
            currentStaff = StaffModule.login();

            if(currentStaff == null)
                System.out.printf("%38s Invalid credentials!\n", "");

            if(StaffModule.loginTries > 5){
                System.out.printf("%38s You have exceeded maximum login count !\n", "");
                programEnd = true;
                return;
            }

        }while(currentStaff == null);

        if(currentStaff.isLogin()) {
            PrintingUtil.clearConsole();
            System.out.printf("%38s Login Successful!", "");
            OrderModule module = new OrderModule(currentStaff);
            module.run();
        }

    }
}
