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
                                        movie.setRating(data.getGrade());
                                        user.setRateMovies(movie.getTitle());
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "success -> " + movie.getTitle() + " was rated with " + data.getGrade() + " by " + user.getUsername()));
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
