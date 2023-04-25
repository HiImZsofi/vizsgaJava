package hu.petrik.konyvtarasztali;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Statisztika {
    private static List<Konyv> konyvek;

    public static void run(){
        try{
            readBooksFromDatabase();
            System.out.printf("500 oldalnál hosszabb könyvek száma: %d\n", CountLongerThan500());
            System.out.printf("%s 1950-nél régebbi könyv", olderThan1950()? "Van" : "Nincs");
            printLongest();
            printAuthorWithMostBooks();
            String title = readTitleFromConsole();
            printAuthor(title);
        }catch (SQLException e){
            System.out.println("Hiba történt az adatbázis kapcsolódása során");
            System.out.println(e.getMessage());
        }

    }

    private static void printAuthor(String title) {
        Optional<Konyv> optionalKonyv = getBook(title);
        if(optionalKonyv.isPresent()){
            System.out.printf("A megadott könyv szerzője: %s", optionalKonyv.get().getAuthor());
        } else{
            System.out.printf("Nincs ilyen könyv");
        }
        System.out.printf("A megadott könyv szerzője: %s", getBook(title));
    }

    private static Optional<Konyv> getBook(String title) {
        return konyvek.stream().filter(konyv -> konyv.getTitle().equals(title)).findFirst();
    }

    private static String readTitleFromConsole() {
        Scanner sc = new Scanner(System.in);
        System.out.printf("Adjon meg egy könyv címet: ");
        return sc.nextLine();
    }

    private static void printAuthorWithMostBooks() {
        String authorWithMostBooks = getAuthorWithMostBooks();
        System.out.printf("A legtöbb könyvvel rendelkező szerző: %s\n", authorWithMostBooks);
    }

    private static String getAuthorWithMostBooks() {
        return konyvek.stream().collect(Collectors.groupingBy(Konyv::getAuthor, Collectors.counting()))
                .entrySet().stream().max(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
    }

    private static void printLongest() {
        Konyv longestBook = getLongestBook();
        System.out.printf("A leghosszabb könyv: \n" +
                "\n Szerző: %s\n" +
                "\n Cím: Libero %s\n"+
                "\n Kiadás éve: %d\n"+
                "\n Oldalszám: %d\n", longestBook.getAuthor(), longestBook.getTitle(), longestBook.getPublish_year(),
                longestBook.getPage_count());
    }

    private static Konyv getLongestBook() {
        return konyvek.stream().max(Comparator.comparing(Konyv::getPage_count)).get();
    }

    private static boolean olderThan1950() {
        return konyvek.stream().anyMatch(konyv -> konyv.getPublish_year() < 1950);
    }

    private static long CountLongerThan500() {
        return konyvek.stream().filter(konyv -> konyv.getPage_count() > 500).count();
    }

    private static void readBooksFromDatabase() throws SQLException {
        DatabaseConn db = new DatabaseConn();
        konyvek = db.readBooks();
    }
}
