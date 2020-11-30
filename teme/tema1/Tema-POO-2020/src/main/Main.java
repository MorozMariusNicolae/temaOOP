package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import entertainment.Season;
import fileio.*;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation
        //System.out.println(input.getCommands());
        for (ActionInputData data : input.getCommands()) {
            if (data.getActionType().compareTo("query") == 0) {
                if (data.getObjectType().compareTo("actors") == 0) {
                    if (data.getCriteria().compareTo("average") == 0) {
                        ArrayList<String> names = new ArrayList<>();
                        ArrayList<Double> rat = new ArrayList<>();
                        for (ActorInputData actor : input.getActors()
                        ) {
                            int num = 0;
                            double total = 0;
                            for (MovieInputData movie : input.getMovies()
                            ) {
                                if (actor.getFilmography().contains(movie.getTitle()) && movie.getRating() > 0) {
                                    total += movie.getRating();
                                    num++;
                                }
                            }
                            for (SerialInputData serial : input.getSerials()
                            ) {
                                if (actor.getFilmography().contains(serial.getTitle()) && serial.totalRating() > 0) {
                                    total += serial.totalRating();
                                    num++;
                                }
                            }
                            if (total > 0) {
                                names.add(actor.getName());
                                rat.add(total / num);
                                arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "cikipop " + total + " " + num));
                            }
                        }
                        String temp = "";
                        double aux = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < names.size(); x++){
                                for (int i = 0; i < names.size() - x - 1; i++){
                                    if (rat.get(i) > rat.get(i + 1)){
                                        temp = names.get(i);
                                        aux = rat.get(i);
                                        names.set(i, names.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        names.set(i + 1, temp);
                                        rat.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < names.size(); x++){
                                for (int i = 0; i < names.size() - x - 1; i++){
                                    if (rat.get(i) < rat.get(i + 1)){
                                        temp = names.get(i);
                                        aux = rat.get(i);
                                        names.set(i, names.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        names.set(i + 1, temp);
                                        rat.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        if (names.size() > 0){
                            if (names.size() > data.getNumber()){
                                for (int i = names.size() - 1; i > data.getNumber(); i--){
                                    names.remove(i);
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + names + " " + rat));
                    }
                    if (data.getCriteria().compareTo("awards") == 0) {
                        ArrayList<String> names = new ArrayList<>();
                        ArrayList<Integer> number = new ArrayList<>();
                        List<String> awards = data.getFilters().get(3);
                        for (ActorInputData actor : input.getActors()
                        ) {
                            int ver = 0;
                            int num = 0;
                            for (int i = 0; i < awards.size(); i++) {
                                if (actor.getAwards().containsKey(awards.get(i))) {
                                    num += actor.getAwards().get(awards.get(i));
                                }
                            }
                            if (ver == awards.size()) {
                                names.add(actor.getName());
                                number.add(num);
                            }
                        }
                        String temp = "";
                        Integer aux = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < names.size(); x++) {
                                for (int i = 0; i < names.size() - x - 1; i++) {
                                    if (number.get(i) > number.get(i + 1)) {
                                        temp = names.get(i);
                                        aux = number.get(i);
                                        names.set(i, names.get(i + 1));
                                        number.set(i, number.get(i + 1));
                                        names.set(i + 1, temp);
                                        number.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < names.size(); x++) {
                                for (int i = 0; i < names.size() - x - 1; i++) {
                                    if (number.get(i) < number.get(i + 1)) {
                                        temp = names.get(i);
                                        aux = number.get(i);
                                        names.set(i, names.get(i + 1));
                                        number.set(i, number.get(i + 1));
                                        names.set(i + 1, temp);
                                        number.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + names));
                    }
                    if (data.getCriteria().compareTo("filter_description") == 0) {
                        List<String> words = data.getFilters().get(2);
                        ArrayList<String> names = new ArrayList<>();
                        for (ActorInputData actor : input.getActors()
                        ) {
                            int ver = 0;
                            for (int i = 0; i < words.size(); i++) {
                                if (actor.getCareerDescription().indexOf(words.get(i)) != -1) {
                                    ver++;
                                }
                            }
                            if (ver == words.size()) {
                                names.add(actor.getName());
                            }
                        }
                        Collections.sort(names);
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + names));
                    }
                }
                if (data.getObjectType().compareTo("users") == 0) {
                    ArrayList<Integer> nrRate = new ArrayList<>();
                    ArrayList<String> rateUsers = new ArrayList<>();
                    for (UserInputData user : input.getUsers()
                    ) {
                        if (user.getRateMovies().size() > 0) {
                            nrRate.add(user.getRateMovies().size());
                            rateUsers.add(user.getUsername());
                        }
                    }
                    int n = data.getNumber();
                    String temp = "";
                    int ntmp = 0;
                    if (rateUsers.size() > 0) {
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < rateUsers.size(); x++) {
                                for (int i = 0; i < rateUsers.size() - x - 1; i++) {
                                    if (nrRate.get(i) > nrRate.get(i + 1)) {
                                        temp = rateUsers.get(i);
                                        ntmp = nrRate.get(i);
                                        rateUsers.set(i, rateUsers.get(i + 1));
                                        nrRate.set(i, nrRate.get(i + 1));
                                        rateUsers.set(i + 1, temp);
                                        nrRate.set(i + 1, ntmp);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < rateUsers.size(); x++) {
                                for (int i = 0; i < rateUsers.size() - x - 1; i++) {
                                    if (nrRate.get(i) < nrRate.get(i + 1)) {
                                        temp = rateUsers.get(i);
                                        ntmp = nrRate.get(i);
                                        rateUsers.set(i, rateUsers.get(i + 1));
                                        nrRate.set(i, nrRate.get(i + 1));
                                        rateUsers.set(i + 1, temp);
                                        nrRate.set(i + 1, ntmp);
                                    }
                                }
                            }
                        }
                        if (n < rateUsers.size()) {
                            for (int i = rateUsers.size() - 1; i > n; i--) {
                                rateUsers.remove(i);
                            }
                        }
                    }
                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + rateUsers));
                }
                if (data.getObjectType().compareTo("shows") == 0) {
                    if (data.getCriteria().compareTo("favorite") == 0) {
                        ArrayList<String> titlu = new ArrayList<>();
                        ArrayList<Integer> nfav = new ArrayList<>();
                        for (SerialInputData serial : input.getSerials()
                        ) {
                            List<String> gen = data.getFilters().get(1);
                            List<String> year = data.getFilters().get(0);
                            if (year.get(0) != null) {
                                if (year.get(0).compareTo(String.valueOf(serial.getYear())) == 0) {
                                    for (int i = 0; i < gen.size(); i++) {
                                        for (int j = 0; j < serial.getGenres().size(); j++) {
                                            if (gen.get(i) != null) {
                                                if (gen.get(i).compareTo(serial.getGenres().get(j)) == 0) {
                                                    for (UserInputData user : input.getUsers()
                                                    ) {
                                                        if (user.getFavoriteMovies().contains(serial.getTitle())) {
                                                            int nui = 0;
                                                            if (titlu.size() > 0) {
                                                                for (int k = 0; k < titlu.size(); k++) {
                                                                    if (titlu.get(k).compareTo(serial.getTitle()) == 0) {
                                                                        nfav.set(i, nfav.get(i) + 1);
                                                                    }
                                                                }
                                                                if (nui == 1) {
                                                                    titlu.add(serial.getTitle());
                                                                    nfav.add(1);
                                                                }
                                                            } else {
                                                                titlu.add(serial.getTitle());
                                                                nfav.add(1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        String temp = "";
                        Integer aux = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < titlu.size(); x++) {
                                for (int i = 0; i < titlu.size() - x - 1; i++) {
                                    if (nfav.get(i) > nfav.get(i + 1)) {
                                        temp = titlu.get(i);
                                        aux = nfav.get(i);
                                        titlu.set(i, titlu.get(i + 1));
                                        nfav.set(i, nfav.get(i + 1));
                                        titlu.set(i + 1, temp);
                                        nfav.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < titlu.size(); x++) {
                                for (int i = 0; i < titlu.size() - x - 1; i++) {
                                    if (nfav.get(i) < nfav.get(i + 1)) {
                                        temp = titlu.get(i);
                                        aux = nfav.get(i);
                                        titlu.set(i, titlu.get(i + 1));
                                        nfav.set(i, nfav.get(i + 1));
                                        titlu.set(i + 1, temp);
                                        nfav.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + titlu));
                    }
                    if (data.getCriteria().compareTo("longest") == 0) {
                        ArrayList<String> seriale = new ArrayList<>();
                        ArrayList<Integer> durata = new ArrayList<>();
                        String temp = "";
                        Integer aux = 0;
                        int is = 0;
                        for (SerialInputData show : input.getSerials()
                        ) {
                            List<String> gen = data.getFilters().get(1);
                            List<String> year = data.getFilters().get(0);
                            if (year.get(0) != null) {
                                if (year.get(0).compareTo(String.valueOf(show.getYear())) == 0) {
                                    for (int j = 0; j < gen.size(); j++) {
                                        for (int i = 0; i < show.getGenres().size(); i++) {
                                            if (gen.get(j) != null) {
                                                if (gen.get(j).compareTo(show.getGenres().get(i)) == 0) {
                                                    seriale.add(show.getTitle());
                                                    int duratie = 0;
                                                    for (int k = 0; k < show.getSeasons().size(); k++) {
                                                        duratie += show.getSeasons().get(k).getDuration();
                                                    }
                                                    durata.add(duratie);
                                                    is = 1;
                                                    break;
                                                }
                                            }
                                        }
                                        if (is == 1) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < durata.size(); x++) {
                                for (int i = 0; i < durata.size() - x - 1; i++) {
                                    if (durata.get(i) > durata.get(i + 1)) {
                                        temp = seriale.get(i);
                                        aux = durata.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        durata.set(i, durata.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        durata.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < durata.size(); x++) {
                                for (int i = 0; i < durata.size() - x - 1; i++) {
                                    if (durata.get(i) < durata.get(i + 1)) {
                                        temp = seriale.get(i);
                                        aux = durata.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        durata.set(i, durata.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        durata.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        if (seriale.size() > data.getNumber()) {
                            for (int i = seriale.size() - 1; i > data.getNumber(); i--) {
                                seriale.remove(i);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + seriale));
                    }
                    if (data.getCriteria().compareTo("ratings") == 0) {
                        ArrayList<String> seriale = new ArrayList<>();
                        ArrayList<Double> rat = new ArrayList<>();
                        for (SerialInputData serial : input.getSerials()
                        ) {
                            if (data.getFilters().contains(serial.getYear()) && data.getFilters().contains(serial.getGenres())) {
                                seriale.add(serial.getTitle());
                                rat.add(serial.totalRating());
                            }
                        }
                        String temp = "";
                        double rtmp = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < seriale.size(); x++) {
                                for (int i = 0; i < seriale.size() - x - 1; i++) {
                                    if (rat.get(i) > rat.get(i + 1)) {
                                        temp = seriale.get(i);
                                        rtmp = rat.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        rat.set(i + 1, rtmp);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < seriale.size(); x++) {
                                for (int i = 0; i > seriale.size() - x - 1; i++) {
                                    if (rat.get(i) < rat.get(i + 1)) {
                                        temp = seriale.get(i);
                                        rtmp = rat.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        rat.set(i + 1, rtmp);
                                    }
                                }
                            }
                        }
                        if (seriale.size() > data.getNumber()) {
                            for (int i = seriale.size() - 1; i > data.getNumber(); i--) {
                                seriale.remove(i);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + seriale));
                    }
                    if (data.getCriteria().compareTo("most_viewed") == 0) {
                        ArrayList<String> seriale = new ArrayList<>();
                        ArrayList<Integer> nview = new ArrayList<>();
                        for (UserInputData user : input.getUsers()) {
                            for (SerialInputData serial : input.getSerials()) {
                                if (data.getFilters().contains(serial.getYear()) && data.getFilters().contains(serial.getGenres())) {
                                    seriale.add(serial.getTitle());
                                    nview.add(user.getHistory().get(1));
                                }
                            }
                        }
                        String temp = "";
                        Integer tenp = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < seriale.size(); x++) {
                                for (int i = 0; i < seriale.size() - x - 1; i++) {
                                    if (nview.get(i) > nview.get(i + 1)) {
                                        temp = seriale.get(i);
                                        tenp = nview.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        nview.set(i, nview.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        nview.set(i + 1, tenp);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < seriale.size(); x++) {
                                for (int i = 0; i < seriale.size() - x - 1; i++) {
                                    if (nview.get(i) < nview.get(i + 1)) {
                                        temp = seriale.get(i);
                                        tenp = nview.get(i);
                                        seriale.set(i, seriale.get(i + 1));
                                        nview.set(i, nview.get(i + 1));
                                        seriale.set(i + 1, temp);
                                        nview.set(i + 1, tenp);
                                    }
                                }
                            }
                        }
                        if (seriale.size() > 0) {
                            if (seriale.size() > data.getNumber()) {
                                for (int i = seriale.size() - 1; i < data.getNumber(); i--) {
                                    seriale.remove(i);
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + seriale));
                    }
                }
                if (data.getObjectType().compareTo("movies") == 0) {
                    if (data.getCriteria().compareTo("favorite") == 0) {
                        ArrayList<String> titlu = new ArrayList<>();
                        ArrayList<Integer> nfav = new ArrayList<>();
                        for (MovieInputData movie : input.getMovies()
                        ) {
                            List<String> gen = data.getFilters().get(1);
                            List<String> year = data.getFilters().get(0);
                            if (year.get(0) != null) {
                                if (year.get(0).compareTo(String.valueOf(movie.getYear())) == 0) {
                                    for (int i = 0; i < gen.size(); i++) {
                                        for (int j = 0; j < movie.getGenres().size(); j++) {
                                            if (gen.get(i) != null) {
                                                if (gen.get(i).compareTo(movie.getGenres().get(j)) == 0) {
                                                    for (UserInputData user : input.getUsers()
                                                    ) {
                                                        if (user.getFavoriteMovies().contains(movie.getTitle())) {
                                                            int nui = 0;
                                                            if (titlu.size() > 0) {
                                                                for (int k = 0; k < titlu.size(); k++) {
                                                                    if (titlu.get(k).compareTo(movie.getTitle()) == 0) {
                                                                        nfav.set(i, nfav.get(i) + 1);
                                                                    }
                                                                }
                                                                if (nui == 1) {
                                                                    titlu.add(movie.getTitle());
                                                                    nfav.add(1);
                                                                }
                                                            } else {
                                                                titlu.add(movie.getTitle());
                                                                nfav.add(1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        String temp = "";
                        Integer aux = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < titlu.size(); x++) {
                                for (int i = 0; i < titlu.size() - x - 1; i++) {
                                    if (nfav.get(i) > nfav.get(i + 1)) {
                                        temp = titlu.get(i);
                                        aux = nfav.get(i);
                                        titlu.set(i, titlu.get(i + 1));
                                        nfav.set(i, nfav.get(i + 1));
                                        titlu.set(i + 1, temp);
                                        nfav.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < titlu.size(); x++) {
                                for (int i = 0; i < titlu.size() - x - 1; i++) {
                                    if (nfav.get(i) < nfav.get(i + 1)) {
                                        temp = titlu.get(i);
                                        aux = nfav.get(i);
                                        titlu.set(i, titlu.get(i + 1));
                                        nfav.set(i, nfav.get(i + 1));
                                        titlu.set(i + 1, temp);
                                        nfav.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + titlu));
                    }
                    if (data.getCriteria().compareTo("longest") == 0) {
                        ArrayList<String> filme = new ArrayList<>();
                        ArrayList<Integer> durata = new ArrayList<>();
                        String temp = "";
                        Integer aux = 0;
                        int is = 0;
                        for (MovieInputData movie : input.getMovies()
                        ) {
                            List<String> gen = data.getFilters().get(1);
                            List<String> year = data.getFilters().get(0);
                            if (year.get(0) != null) {
                                if (year.get(0).compareTo(String.valueOf(movie.getYear())) == 0) {
                                    for (int j = 0; j < gen.size(); j++) {
                                        for (int i = 0; i < movie.getGenres().size(); i++) {
                                            if (gen.get(j) != null) {
                                                if (gen.get(j).compareTo(movie.getGenres().get(i)) == 0) {
                                                    filme.add(movie.getTitle());
                                                    durata.add(movie.getDuration());
                                                    is = 1;
                                                    break;
                                                }
                                            }
                                        }
                                        if (is == 1) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < durata.size(); x++) {
                                for (int i = 0; i < durata.size() - x - 1; i++) {
                                    if (durata.get(i) > durata.get(i + 1)) {
                                        temp = filme.get(i);
                                        aux = durata.get(i);
                                        filme.set(i, filme.get(i + 1));
                                        durata.set(i, durata.get(i + 1));
                                        filme.set(i + 1, temp);
                                        durata.set(i + 1, aux);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < durata.size(); x++) {
                                for (int i = 0; i < durata.size() - x - 1; i++) {
                                    if (durata.get(i) < durata.get(i + 1)) {
                                        temp = filme.get(i);
                                        aux = durata.get(i);
                                        filme.set(i, filme.get(i + 1));
                                        durata.set(i, durata.get(i + 1));
                                        filme.set(i + 1, temp);
                                        durata.set(i + 1, aux);
                                    }
                                }
                            }
                        }
                        if (filme.size() > data.getNumber()) {
                            for (int i = filme.size() - 1; i > data.getNumber(); i--) {
                                filme.remove(i);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + filme));
                    }
                    if (data.getCriteria().compareTo("ratings") == 0) {
                        ArrayList<String> movies = new ArrayList<>();
                        ArrayList<Double> rat = new ArrayList<>();
                        for (MovieInputData movie : input.getMovies()
                        ) {
                            if (data.getFilters().contains(movie.getYear()) && data.getFilters().contains(movie.getGenres())) {
                                movies.add(movie.getTitle());
                                rat.add(movie.getRating());
                            }
                        }
                        String temp = "";
                        double rtmp = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < movies.size(); x++) {
                                for (int i = 0; i < movies.size() - x - 1; i++) {
                                    if (rat.get(i) > rat.get(i + 1)) {
                                        temp = movies.get(i);
                                        rtmp = rat.get(i);
                                        movies.set(i, movies.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        movies.set(i + 1, temp);
                                        rat.set(i + 1, rtmp);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < movies.size(); x++) {
                                for (int i = 0; i > movies.size() - x - 1; i++) {
                                    if (rat.get(i) < rat.get(i + 1)) {
                                        temp = movies.get(i);
                                        rtmp = rat.get(i);
                                        movies.set(i, movies.get(i + 1));
                                        rat.set(i, rat.get(i + 1));
                                        movies.set(i + 1, temp);
                                        rat.set(i + 1, rtmp);
                                    }
                                }
                            }
                        }
                        if (movies.size() > data.getNumber()) {
                            for (int i = movies.size() - 1; i > data.getNumber(); i--) {
                                movies.remove(i);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + movies));
                    }
                    if (data.getCriteria().compareTo("most_viewed") == 0) {
                        ArrayList<String> movies = new ArrayList<>();
                        ArrayList<Integer> nview = new ArrayList<>();
                        for (UserInputData user : input.getUsers()) {
                            for (MovieInputData movie : input.getMovies()) {
                                if (user.getHistory().containsKey(movie.getTitle())) {
                                    List<String> gen = data.getFilters().get(1);
                                    List<String> year = data.getFilters().get(0);
                                    int is = 0;
                                    int este = 0;
                                    if (year.get(0) != null) {
                                        if (year.get(0).compareTo(String.valueOf(movie.getYear())) == 0) {
                                            for (int j = 0; j < gen.size(); j++) {
                                                for (int i = 0; i < movie.getGenres().size(); i++) {
                                                    if (gen.get(j) != null) {
                                                        if (gen.get(j).compareTo(movie.getGenres().get(i)) == 0) {
                                                            if (movies.size() > 0) {
                                                                for (int k = 0; k < movies.size(); k++) {
                                                                    if (movies.get(k).compareTo(movie.getTitle()) == 0) {
                                                                        nview.add(nview.get(k) + 1);
                                                                        este = 1;
                                                                    }
                                                                }
                                                                if (este == 0) {
                                                                    movies.add(movie.getTitle());
                                                                    nview.add(1);
                                                                }
                                                            } else {
                                                                movies.add(movie.getTitle());
                                                                nview.add(1);
                                                            }
                                                            is = 1;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (is == 1) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        String temp = "";
                        Integer tenp = 0;
                        if (data.getSortType().compareTo("asc") == 0) {
                            for (int x = 0; x < movies.size(); x++) {
                                for (int i = 0; i < movies.size() - x - 1; i++) {
                                    if (nview.get(i) > nview.get(i + 1)) {
                                        temp = movies.get(i);
                                        tenp = nview.get(i);
                                        movies.set(i, movies.get(i + 1));
                                        nview.set(i, nview.get(i + 1));
                                        movies.set(i + 1, temp);
                                        nview.set(i + 1, tenp);
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < movies.size(); x++) {
                                for (int i = 0; i < movies.size() - x - 1; i++) {
                                    if (nview.get(i) < nview.get(i + 1)) {
                                        temp = movies.get(i);
                                        tenp = nview.get(i);
                                        movies.set(i, movies.get(i + 1));
                                        nview.set(i, nview.get(i + 1));
                                        movies.set(i + 1, temp);
                                        nview.set(i + 1, tenp);
                                    }
                                }
                            }
                        }
                        if (movies.size() > 0) {
                            if (movies.size() > data.getNumber()) {
                                for (int i = movies.size() - 1; i < data.getNumber(); i--) {
                                    movies.remove(i);
                                }
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "Query result: " + movies));
                    }
                }
            }
            if (data.getActionType().compareTo("recommendation") == 0) {
                for (UserInputData user : input.getUsers()
                ) {
                    if (user.getUsername().compareTo(data.getUsername()) == 0) {
                        if (data.getType().compareTo("search") == 0) {
                            if (user.getSubscriptionType().compareTo("PREMIUM") == 0) {
                                ArrayList<String> nevazute = new ArrayList<>();
                                ArrayList<Double> ratinguri = new ArrayList<>();
                                for (MovieInputData movie : input.getMovies()
                                ) {
                                    if (!user.getHistory().containsKey(movie.getTitle())) {
                                        for (int i = 0; i < movie.getGenres().size(); i++) {
                                            if (movie.getGenres().get(i).compareTo(data.getGenre()) == 0) {
                                                nevazute.add(movie.getTitle());
                                                ratinguri.add(movie.getRating());
                                            }
                                        }
                                    }
                                }
                                for (SerialInputData serial : input.getSerials()
                                ) {
                                    if (!user.getHistory().containsKey(serial.getTitle())) {
                                        for (int i = 0; i < serial.getGenres().size(); i++) {
                                            if (serial.getGenres().get(i).compareTo(data.getGenre()) == 0) {
                                                nevazute.add(serial.getTitle());
                                                ratinguri.add(serial.totalRating());
                                            }
                                        }
                                    }
                                }
                                Collections.sort(nevazute);
                                String temp = "";
                                double remp = 0;
                                for (int x = 0; x < ratinguri.size(); x++) {
                                    for (int i = 0; i < ratinguri.size() - x - 1; i++) {
                                        if (ratinguri.get(i) < ratinguri.get(i + 1)) {
                                            temp = nevazute.get(i);
                                            remp = ratinguri.get(i);
                                            nevazute.set(i, nevazute.get(i + 1));
                                            ratinguri.set(i, ratinguri.get(i + 1));
                                            nevazute.set(i + 1, temp);
                                            ratinguri.set(i + 1, remp);
                                        }
                                    }
                                }

                                arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "SearchRecommendation result: " + nevazute));
                            }
                        }
                        if (data.getType().compareTo("best_unseen") == 0) {
                            ArrayList<String> nevazute = new ArrayList<>();
                            ArrayList<Double> ratinguri = new ArrayList<>();
                            for (MovieInputData movie : input.getMovies()
                            ) {
                                int is = 0;
                                if (!user.getHistory().containsKey(movie.getTitle())) {
                                    for (int i = 0; i < nevazute.size(); i++) {
                                        if (nevazute.get(i).compareTo(movie.getTitle()) == 0) {
                                            is = 1;
                                        }
                                    }
                                    if (is == 0) {
                                        nevazute.add(movie.getTitle());
                                        ratinguri.add(movie.getRating());
                                    }
                                }
                            }
                            for (SerialInputData serial : input.getSerials()
                            ) {
                                int is = 0;
                                if (!user.getHistory().containsKey(serial.getTitle())) {
                                    for (int i = 0; i < nevazute.size(); i++) {
                                        if (nevazute.get(i).compareTo(serial.getTitle()) == 0) {
                                            is = 1;
                                        }
                                    }
                                    if (is == 0) {
                                        nevazute.add(serial.getTitle());
                                        ratinguri.add(serial.totalRating());
                                    }
                                }
                            }
                            String temp = "";
                            double remp = 0;
                            for (int x = 0; x < ratinguri.size(); x++) {
                                for (int i = 0; i < ratinguri.size() - x - 1; i++) {
                                    if (ratinguri.get(i) < ratinguri.get(i + 1)) {
                                        temp = nevazute.get(i);
                                        remp = ratinguri.get(i);
                                        nevazute.set(i, nevazute.get(i + 1));
                                        ratinguri.set(i, ratinguri.get(i + 1));
                                        nevazute.set(i + 1, temp);
                                        ratinguri.set(i + 1, remp);
                                    }
                                }
                            }
                            if (nevazute.size() > 0) {
                                arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "BestRatedUnseenRecommendation result: " + nevazute.get(0)));
                            }
                        }
                        for (MovieInputData movie : input.getMovies()
                        ) {
                            if (!user.getHistory().containsKey(movie.getTitle())) {
                                if (data.getType().compareTo("standard") == 0) {
                                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "StandardRecommendation result: " + movie.getTitle()));
                                }
                            }
                        }
                    }
                }
            } else if (data.getActionType().compareTo("command") == 0) {
                if (data.getType().compareTo("favorite") == 0) {
                    for (UserInputData user : input.getUsers()
                    ) {
                        if (user.getUsername().compareTo(data.getUsername()) == 0) {
                            if (user.getHistory().containsKey(data.getTitle())) {
                                int este = 0;
                                for (int i = 0; i < user.getFavoriteMovies().size(); i++) {
                                    if (user.getFavoriteMovies().get(i).compareTo(data.getTitle()) == 0) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + data.getTitle() + " is already in favourite list"));
                                    }
                                    este = 1;
                                }
                                if (este == 0) {
                                    user.getFavoriteMovies().add(data.getTitle());
                                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "success -> " + data.getTitle() + " was added as favourite"));
                                }
                            }
                            if (!user.getHistory().containsKey(data.getTitle())) {
                                arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + data.getTitle() + " is not seen"));
                            }
                        }
                    }
                }
                for (UserInputData user : input.getUsers()
                ) {
                    if (user.getUsername().compareTo(data.getUsername()) == 0) {
                        if (data.getType().compareTo("view") == 0) {
                            if (user.getHistory().containsKey(data.getTitle())) {
                                user.getHistory().replace(data.getTitle(), user.getHistory().get(data.getTitle()) + 1);
                            } else {
                                user.getHistory().put(data.getTitle(), 1);
                            }
                            arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "success -> " + data.getTitle() + " was viewed with total views of " + user.getHistory().get(data.getTitle())));
                        }
                        for (MovieInputData movie : input.getMovies()
                        ) {
                            if (movie.getTitle().compareTo(data.getTitle()) == 0) {
                                if (user.getHistory().containsKey(movie.getTitle())) {
                                    if (data.getType().compareTo("rating") == 0) {
                                        if (movie.getRating() > 0){
                                            arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + movie.getTitle() + " has been already rated"));
                                        } else {
                                            movie.setRating(data.getGrade());
                                            user.setRateMovies(movie.getTitle());
                                            arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "success -> " + movie.getTitle() + " was rated with " + data.getGrade() + " by " + user.getUsername()));
                                        }
                                    }
                                } else {
                                    if (data.getType().compareTo("rating") == 0) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + movie.getTitle() + " is not seen"));
                                    }
                                }
                            }
                        }
                        for (SerialInputData serial : input.getSerials()
                        ) {
                            ArrayList<Double> ratings = new ArrayList<>();
                            if (serial.getTitle().compareTo(data.getTitle()) == 0) {
                                if (user.getHistory().containsKey(serial.getTitle())) {
                                    if (data.getType().compareTo("rating") == 0) {
                                        for (int i = 0; i < serial.getNumberSeason(); i++) {
                                            if (i == data.getSeasonNumber()) {
                                                ratings.add(data.getGrade());
                                            } else {
                                                ratings.add(0.0);
                                            }
                                        }
                                        user.setRateMovies(serial.getTitle());
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "success -> " + serial.getTitle() + " was rated with " + data.getGrade() + " by " + user.getUsername()));
                                    }
                                } else {
                                    if (data.getType().compareTo("rating") == 0) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + serial.getTitle() + " is not seen"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (data.getActionType().compareTo("recommendation") == 0) {
                if (data.getType().compareTo("favorite") == 0) {
                    String title = "";
                    ArrayList<String> vazute = new ArrayList<>();
                    for (UserInputData userTarget : input.getUsers()
                    ) {
                        if (userTarget.getUsername().compareTo(data.getUsername()) == 0) {
                            for (MovieInputData movie : input.getMovies()
                            ) {
                                if (userTarget.getHistory().containsKey(movie.getTitle())) {
                                    vazute.add(movie.getTitle());
                                }
                            }
                        }
                    }
                    int num = 0;
                    for (MovieInputData movie : input.getMovies()
                    ) {
                        int count = 0;
                        for (UserInputData user : input.getUsers()
                        ) {
                            if (user.getUsername().compareTo(data.getUsername()) != 0) {
                                for (int i = 0; i < user.getFavoriteMovies().size(); i++) {
                                    int nui = 0;
                                    for (int j = 0; j < vazute.size(); j++) {
                                        if (vazute.get(j).compareTo(user.getFavoriteMovies().get(i)) == 0) {
                                            nui = 1;
                                        }
                                    }
                                    if (nui == 0) {
                                        if (movie.getTitle().compareTo(user.getFavoriteMovies().get(i)) == 0) {
                                            count++;
                                        }
                                    }
                                }
                            }
                        }
                        if (count > num) {
                            title = movie.getTitle();
                            ;
                            num = count;
                        }
                    }

                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "FavoriteRecommendation result: " + title));
                }
                if (data.getType().compareTo("popular") == 0) {
                    for (UserInputData user : input.getUsers()
                    ) {
                        if (user.getSubscriptionType().compareTo("PREMIUM") == 0) {
                            ArrayList<String> genuri = new ArrayList<>();
                            for (MovieInputData movie : input.getMovies()
                            ) {
                                for (int i = 0; i < movie.getGenres().size(); i++) {
                                    int este = 0;
                                    if (genuri.size() > 0) {
                                        for (int j = 0; j < genuri.size(); j++) {
                                            if (movie.getGenres().get(i).compareTo(genuri.get(j)) == 0) {
                                                este = 1;
                                            }
                                        }
                                    }
                                    if (genuri.size() == 0 || este == 0) {
                                        genuri.add(movie.getGenres().get(i));
                                    }
                                }
                            }
                            for (SerialInputData serial : input.getSerials()
                            ) {
                                for (int i = 0; i < serial.getGenres().size(); i++) {
                                    int este = 0;
                                    if (genuri.size() > 0) {
                                        for (int j = 0; j < genuri.size(); j++) {
                                            if (serial.getGenres().get(i).compareTo(genuri.get(j)) == 0) {
                                                este = 1;
                                            }
                                        }
                                    }
                                    if (genuri.size() == 0 || este == 0) {
                                        genuri.add(serial.getGenres().get(i));
                                    }
                                }
                            }
                            ArrayList<Integer> nraparitii = new ArrayList<>();
                            for (int i = 0; i < genuri.size(); i++) {
                                int cnt = 0;
                                for (MovieInputData movie : input.getMovies()
                                ) {
                                    if (movie.getGenres().contains(genuri.get(i))) {
                                        cnt++;
                                    }
                                }
                                for (SerialInputData serial : input.getSerials()
                                ) {
                                    if (serial.getGenres().contains(genuri.get(i))) {
                                        cnt++;
                                    }
                                }
                                nraparitii.add(cnt);
                            }
                            String tmp;
                            int ntmp;
                            for (int x = 0; x < genuri.size(); x++) {
                                for (int i = 0; i < genuri.size() - x - 1; i++) {
                                    if (nraparitii.get(i) < nraparitii.get(i + 1)) {
                                        tmp = genuri.get(i);
                                        ntmp = nraparitii.get(i);
                                        genuri.set(i, genuri.get(i + 1));
                                        nraparitii.set(i, nraparitii.get(i + 1));
                                        genuri.set(i + 1, tmp);
                                        nraparitii.set(i + 1, ntmp);
                                    }
                                }
                            }
                            int is = 0;
                            int i = 0;
                            while (i < genuri.size()) {
                                for (MovieInputData movie : input.getMovies()
                                ) {
                                    if (movie.getGenres().contains(genuri.get(i)) && !user.getHistory().containsKey(movie.getTitle())) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "PopularRecommendation result: " + movie.getTitle()));
                                        is = 1;
                                        break;
                                    }
                                }
                                if (is == 1) {
                                    break;
                                }
                                for (SerialInputData serial : input.getSerials()
                                ) {
                                    if (serial.getGenres().contains(genuri.get(i)) && !user.getHistory().containsKey(serial.getTitle())) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "PopularRecommendation result: " + serial.getTitle()));
                                        is = 1;
                                        break;
                                    }
                                }
                                if (is == 1) {
                                    break;
                                }
                                genuri.remove(i);
                                if (genuri.size() == 0) {
                                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "PopularRecommendation result: All the movies was seen"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }


        fileWriter.closeJSON(arrayResult);
    }
}
