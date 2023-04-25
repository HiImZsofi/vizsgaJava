package hu.petrik.konyvtarasztali;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if(Arrays.asList(args).contains("--stat")){
            Statisztika.run();
        } else {
            HelloApplication.main(args);
        }
    }
}
