import java.util.ArrayList;


public class PlayingDay {
    private String name;

    private String onMorning;
    private String onAfternoon;
    private String onEvening;

    ArrayList<Long> listOfUsersOnMorning;
    ArrayList<Long> listOfUsersOnAfternoon;
    ArrayList<Long> listOfUsersOnEvening;


    public PlayingDay(String name, String onMorning, String onAfternoon, String onEvening) {

        this.name = name;
        this.onMorning = onMorning;
        this.onAfternoon = onAfternoon;
        this.onEvening = onEvening;
        this.listOfUsersOnMorning = new ArrayList<Long>();
        this.listOfUsersOnAfternoon = new ArrayList<Long>();
        this.listOfUsersOnEvening = new ArrayList<Long>();
    }


    public String getOnMorning() {
        return onMorning;
    }

    public void setOnMorning(String onMorning) {
        this.onMorning = onMorning;
    }

    public String getOnAfternoon() {
        return onAfternoon;
    }

    public void setOnAfternoon(String onAfternoon) {
        this.onAfternoon = onAfternoon;
    }

    public String getOnEvening() {
        return onEvening;
    }

    public void setOnEvening(String onEvening) {
        this.onEvening = onEvening;
    }

    public ArrayList<Long> getListOfUsersOnMorning() {
        return listOfUsersOnMorning;
    }

    public void setListOfUsersOnMorning(ArrayList<Long> listOfUsersOnMorning) {
        this.listOfUsersOnMorning = listOfUsersOnMorning;
    }

    public ArrayList<Long> getListOfUsersOnAfternoon() {
        return listOfUsersOnAfternoon;
    }

    public void setListOfUsersOnAfternoon(ArrayList<Long> listOfUsersonAfternoon) {
        this.listOfUsersOnAfternoon = listOfUsersonAfternoon;
    }

    public ArrayList<Long> getListOfUsersOnEvening() {
        return listOfUsersOnEvening;
    }

    public void setListOfUsersOnEvening(ArrayList<Long> listOfUsersonEvening) {
        this.listOfUsersOnEvening = listOfUsersonEvening;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUser(boolean onDay, boolean onMorning, boolean onAfternoon, boolean onEvening) {
        //////////////////////////////////////////////
    }

    static public ArrayList<PlayingDay> getPlayingDays(String... days) {

        ArrayList<PlayingDay> neededDays = new ArrayList<PlayingDay>();

        for (String day : days) {
            if (day.equals("default"))
                return getPlayingDays("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");


            neededDays.add(new PlayingDay(day, "m", "a", "e"));

        }

        return neededDays;
    }


}