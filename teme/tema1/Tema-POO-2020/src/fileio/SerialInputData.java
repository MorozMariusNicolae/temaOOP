package fileio;

import entertainment.Season;

import java.util.ArrayList;

/**
 * Information about a tv show, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class  SerialInputData extends ShowInput {
    /**
     * Number of seasons
     */
    private final int numberOfSeasons;
    /**
     * Season list
     */
    private final ArrayList<Season> seasons;

    public SerialInputData(final String title, final ArrayList<String> cast,
                           final ArrayList<String> genres,
                           final int numberOfSeasons, final ArrayList<Season> seasons,
                           final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }

    public double totalRating(){
        double totalseason = 0.0;
        double total = 0.0;
        int nr = 0;
        if (seasons.size() > 0){
            for (int i = 0; i < seasons.size(); i++) {
                total = 0;
                nr = 0;
                for (int j = 0; j < seasons.get(i).getRatings().size(); j++) {
                        total += seasons.get(i).getRatings().get(j);
                        nr++;
                }
                if (nr > 0) {
                    totalseason += total / seasons.get(i).getRatings().size();
                }
            }
            return totalseason / seasons.size();
        }
        return totalseason;
    }
}
