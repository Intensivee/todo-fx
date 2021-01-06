package org.example.model;

public class Item {
    public static final String MONDAY = "Poniedziałek";
    public static final String TUESDAY = "Wtorek";
    public static final String WEDNESDAY = "Środa";
    public static final String THURSDAY = "Czwartek";
    public static final String FRIDAY = "Piątek";
    public static final String SATURDAY = "Sobota";
    public static final String SUNDAY = "Niedziela";

    private String heading;
    private String note;
    private Day day;

    public enum Day {
        MONDAY(Item.MONDAY),
        TUESDAY(Item.TUESDAY),
        WEDNESDAY(Item.WEDNESDAY),
        THURSDAY(Item.THURSDAY),
        FRIDAY(Item.FRIDAY),
        SATURDAY(Item.SATURDAY),
         SUNDAY(Item.SUNDAY);

        public final String label;
        Day(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public Item() {
    }

    public Item(String heading, String note, Day day) {
        this.heading = heading;
        this.note = note;
        this.day = day;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return  heading;
    }
}
