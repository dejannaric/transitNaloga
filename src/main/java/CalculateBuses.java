import dto.*;

import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

public class CalculateBuses {


    private static final Logger log = Logger.getLogger(CalculateBuses.class.getName());
//    private final Date now = new Date(System.currentTimeMillis());
    private final Date now = new Date(1583326800000L);
    private final Date maxNextHours = new Date(now.getTime() + (Constants.TWO_HOURS));

    private String stopId;
    private Integer maxNextBusses;
    private DisplayTypeEnum displayType;

    private StopDTO stopDTO;
    private List<StopTimesDTO> stopTimesDTOS;
    private Map<String, TripDTO> tripsMap;
    private Map<String, RouteDTO> routesMap;
    private Map<String, CalendarDTO> calendarMap;



    public CalculateBuses(String stopId, Integer maxNextBusses, DisplayTypeEnum displayType) {
        this.stopId = stopId;
        this.maxNextBusses = maxNextBusses;
        this.displayType = displayType;
        init();
    }

    private void init() {
        GtfsReader reader = new GtfsReader(stopId, now, maxNextHours);

        stopDTO = reader.findStop();
        stopTimesDTOS = reader.readStopTimes();

        List<TripDTO> tripsDTOS = reader.readTrips(stopTimesDTOS);
        tripsMap = tripsDTOS.stream().collect(Collectors.toMap(TripDTO::getTrip_id, item -> item));

        List<RouteDTO> routesDTOS = reader.readRoutes();
        routesMap = routesDTOS.stream().collect(Collectors.toMap(RouteDTO::getRoute_id, item -> item));

        List<CalendarDTO> calendarDTOS = reader.readCalendar(tripsDTOS);
        calendarMap = calendarDTOS.stream().collect(Collectors.toMap(CalendarDTO::getService_id, item -> item));

        Map<String, ArrayList<StopTimesDTO>> routesWithStops = getRoutesWithStops();
        print(routesWithStops);
    }

    private Map<String, ArrayList<StopTimesDTO>> getRoutesWithStops() {


        Map<String, ArrayList<StopTimesDTO>> stopTimesPerRoute = new HashMap<String, ArrayList<StopTimesDTO>>();
        for(StopTimesDTO stopTimesDTO : stopTimesDTOS) {
            TripDTO tripDTO = tripsMap.get(stopTimesDTO.getTrip_id());
            RouteDTO routeDTO = routesMap.get(tripDTO.getRoute_id());
            CalendarDTO calendarDTO = calendarMap.get(tripDTO.getService_id());


            Calendar c = Calendar.getInstance();
            c.setTime(stopTimesDTO.getArrival_time());
            int dayOfWeekNow = c.get(Calendar.DAY_OF_WEEK);
            int day = 0;
            switch (dayOfWeekNow) {
                case 1:
                    day = calendarDTO.getSunday();
                    break;
                case 2:
                    day = calendarDTO.getMonday();
                    break;
                case 3:
                    day = calendarDTO.getTuesday();
                    break;
                case 4:
                    day = calendarDTO.getWednesday();
                    break;
                case 5:
                    day = calendarDTO.getThursday();
                    break;
                case 6:
                    day = calendarDTO.getFriday();
                    break;
                case 7:
                    day = calendarDTO.getSaturday();
                    break;
            }
            boolean afterStart = now.after(calendarDTO.getStartDate());
            boolean beforeEnd = now.before(calendarDTO.getEndDate());
            boolean correctCalendar = afterStart
                    && beforeEnd
                    && day == 1;

            if (correctCalendar) {
                ArrayList<StopTimesDTO> arrayStopTimes;
                if(stopTimesPerRoute.containsKey(routeDTO.getRoute_id())) {
                    arrayStopTimes = stopTimesPerRoute.get(routeDTO.getRoute_id());
                    arrayStopTimes.add(stopTimesDTO);
                } else {
                    arrayStopTimes = new ArrayList<>();
                    arrayStopTimes.add(stopTimesDTO);
                }
                stopTimesPerRoute.put(routeDTO.getRoute_id(), arrayStopTimes);
            }

        }
        // sort stops by arrival Time and limit max showed by maxNextBusses
        stopTimesPerRoute.forEach((key, value) -> {
            value.sort(new Comparator<StopTimesDTO>() {
                @Override
                public int compare(StopTimesDTO o1, StopTimesDTO o2) {
                    return o1.getArrival_time().compareTo(o2.getArrival_time());
                }
            });
            if(value.size() > maxNextBusses) {
                List<StopTimesDTO> sublist = value.subList(0, maxNextBusses);
                stopTimesPerRoute.replace(key, new ArrayList<StopTimesDTO>(sublist));
            }
        });

        return stopTimesPerRoute;
    }


    private void print(Map<String, ArrayList<StopTimesDTO>> routesWithStops) {

        System.out.println("Postajališče " + stopDTO.getName());
        routesWithStops.forEach((key, value) -> {
            RouteDTO routeDTO = routesMap.get(key);

            StringBuilder timesOutPrint = new StringBuilder();
            timesOutPrint.append(routeDTO.getRoute_short_name());
            timesOutPrint.append(": ");

            for (int i = 0; i < value.size(); i++) {
                StopTimesDTO dto = value.get(i);
                if(displayType == DisplayTypeEnum.ABSOLUTE) {
                    timesOutPrint.append(dto.getArrival_time().toString());
                    if(i + 1 < value.size()) {
                        timesOutPrint.append(", ");
                    }

                } else {
                    LocalTime arrivalTime = LocalTime.parse(dto.getArrival_time().toString());
                    LocalTime nowTime = LocalTime.parse(now.toString());
                    long between = MINUTES.between(arrivalTime, nowTime);
                    timesOutPrint.append(between);
                    if(i + 1 < value.size()) {
                        timesOutPrint.append(", ");
                    }
                }
            }
            System.out.println(timesOutPrint.toString());


        });
    }
}
