package dto;

import com.opencsv.bean.CsvBindByName;

public class RouteDTO {

    @CsvBindByName
    private String route_id, agency_id, route_short_name;

    @CsvBindByName
    private Integer route_sort_order;

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getRoute_short_name() {
        return route_short_name;
    }

    public void setRoute_short_name(String route_short_name) {
        this.route_short_name = route_short_name;
    }

    public Integer getRoute_sort_order() {
        return route_sort_order;
    }

    public void setRoute_sort_order(Integer route_sort_order) {
        this.route_sort_order = route_sort_order;
    }
}
