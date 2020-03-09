package dto;

import com.opencsv.bean.CsvBindByName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarDTO {
    @CsvBindByName
    private String service_id;

    @CsvBindByName
    private Integer monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    @CsvBindByName
    private String start_date, end_date;

    private Date startDate, endDate;

    public String getService_id() {
        return service_id;
    }


    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public Integer getMonday() {
        return monday;
    }

    public void setMonday(Integer monday) {
        this.monday = monday;
    }

    public Integer getTuesday() {
        return tuesday;
    }

    public void setTuesday(Integer tuesday) {
        this.tuesday = tuesday;
    }

    public Integer getWednesday() {
        return wednesday;
    }

    public void setWednesday(Integer wednesday) {
        this.wednesday = wednesday;
    }

    public Integer getThursday() {
        return thursday;
    }

    public void setThursday(Integer thursday) {
        this.thursday = thursday;
    }

    public Integer getFriday() {
        return friday;
    }

    public void setFriday(Integer friday) {
        this.friday = friday;
    }

    public Integer getSaturday() {
        return saturday;
    }

    public void setSaturday(Integer saturday) {
        this.saturday = saturday;
    }

    public Integer getSunday() {
        return sunday;
    }

    public void setSunday(Integer sunday) {
        this.sunday = sunday;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(start_date);
            setStartDate(date);
        } catch (ParseException e) {
        }
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(end_date);
            setEndDate(date);
        } catch (ParseException e) {
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
