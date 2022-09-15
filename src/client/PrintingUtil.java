package client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PrintingUtil {

    public static void printLine(int n){
        for(int i = 0; i < n; i++){
            System.out.print("-");
        }
        System.out.println();
    }

    public final static void clearConsole(){
        for(int i = 0; i < 50; i++)
            System.out.println();
    }

    public final static String getLogo() throws IOException {
        Path path = Paths.get("src/txt/logo.txt");

        Stream<String> lines = Files.lines(path);

        return lines.reduce("", (a,v) -> a + v + "\n");
    }

}
