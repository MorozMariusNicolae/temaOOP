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
import java.util.ArrayList;
import java.util.Objects;

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
        for (ActionInputData data: input.getCommands()) {
            if (data.getActionType().compareTo("recommendation") == 0){
                for (UserInputData user: input.getUsers()
                     ) {
                    if (user.getUsername().compareTo(data.getUsername()) == 0){
                        if (data.getType().compareTo("search") == 0){
                            if (user.getSubscriptionType().compareTo("PREMIUM") == 0) {
                                ArrayList<String> nevazute = new ArrayList<>();
                                for (MovieInputData movie : input.getMovies()
                                ) {
                                    if (!user.getHistory().containsKey(movie.getTitle())){
                                        for (int i =0; i < movie.getGenres().size(); i++){
                                            if (movie.getGenres().get(i).compareTo(data.getGenre()) == 0){
                                                nevazute.add(movie.getTitle());
                                            }
                                        }
                                    }
                                }
                                for (SerialInputData serial: input.getSerials()
                                     ) {
                                    if (!user.getHistory().containsKey(serial.getTitle())){
                                        for (int i =0; i < serial.getGenres().size(); i++){
                                            if (serial.getGenres().get(i).compareTo(data.getGenre()) == 0){
                                                nevazute.add(serial.getTitle());
                                            }
                                        }
                                    }
                                }
                                arrayResult.add(fileWriter.writeFile(data.getActionId(), "message","StandardRecommendation result: " + nevazute));
                            }
                        }
                        for (MovieInputData movie: input.getMovies()
                             ) {
                            if (!user.getHistory().containsKey(movie.getTitle())){
                                if (data.getType().compareTo("standard") == 0){
                                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "message","StandardRecommendation result: " + movie.getTitle()));
                                }
                            }
                        }
                    }
                }
            } else if(data.getActionType().compareTo("command") == 0){
                for (UserInputData user: input.getUsers()
                     ) {
                    if (user.getUsername().compareTo(data.getUsername()) == 0){
                        for (MovieInputData movie: input.getMovies()
                             ) {
                            if (movie.getTitle().compareTo(data.getTitle()) == 0){
                                if (user.getHistory().containsKey(movie.getTitle())){
                                    if (data.getType().compareTo("rating") == 0) {
                                        movie.setRating(data.getGrade());
                                        user.setRateMovies(data.getGrade());
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "succes -> " + movie.getTitle() + " was rated with " + data.getGrade() + " by " + user.getUsername()));
                                    }
                                }
                                else {
                                    if (data.getType().compareTo("rating") == 0) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + movie.getTitle() + " is not seen"));
                                    }
                                }
                            }
                        }
                        for (SerialInputData serial: input.getSerials()
                             ) {
                            ArrayList<Double> ratings = new ArrayList<>();
                            if (serial.getTitle().compareTo(data.getTitle()) == 0){
                                if (user.getHistory().containsKey(serial.getTitle())){
                                    if (data.getType().compareTo("rating") == 0){
                                        for (int i = 0; i < serial.getNumberSeason(); i++){

                                        }
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "succes -> " + serial.getTitle() + " was rated with " + data.getGrade() + " by " + user.getUsername()));
                                    }
                                }
                                else {
                                    if (data.getType().compareTo("rating") == 0) {
                                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "message", "error -> " + serial.getTitle() + " is not seen"));
                                    }
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
