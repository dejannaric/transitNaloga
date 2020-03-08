package dto;

import com.opencsv.bean.CsvBindByName;

import java.sql.Time;

public class StopTimesDTO {

    @CsvBindByName(column = "trip_id")
    private String trip_id;

    @CsvBindByName
    private Time arrival_time;

    @CsvBindByName
    private Time departure_time;

    @CsvBindByName
    private String stop_id;

    @CsvBindByName
    private String stop_sequence;

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public Time getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(Time arrival_time) {
        this.arrival_time = arrival_time;
    }

    public Time getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(Time departure_time) {
        this.departure_time = departure_time;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_sequence() {
        return stop_sequence;
    }

    public void setStop_sequence(String stop_sequence) {
        this.stop_sequence = stop_sequence;
    }
}
