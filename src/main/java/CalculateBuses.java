import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import dto.*;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculateBuses {


    private static final Logger log = Logger.getLogger(CalculateBuses.class.getName());
//    private final Date now = new Date(System.currentTimeMillis());
    private final Date now = new Date(1583326800000L);
    private final Date maxNextHours = new Date(now.getTime() + (60 * 60 * 2 * 1000));

    private String stopId;
    private Integer maxNextBusses;
    private DisplayTypeEnum displayType;

    private StopDTO stopDTO;

    public CalculateBuses(String stopId, Integer maxNextBusses, DisplayTypeEnum displayType) {
        this.stopId = stopId;
        this.maxNextBusses = maxNextBusses;
        this.displayType = displayType;
        init();
    }

    private void init() {
        StopDTO stopDTO = findStop();
        List<StopTimesDTO> stopTimesDTOS = readStopTimes();
        List<TripDTO> tripsDTOs = readTrips(stopTimesDTOS);
        List<RouteDTO> routesDTOs = readRoutes();
    }

    private StopDTO findStop() {
        StopDTO stopDTO = new StopDTO();
        try {

            Path path = Paths.get(Constants.STOPS);
            String stops = Files.readString(path);

            Stream<String> lines = stops.lines();

            Stream<String> skipedHeader = lines.skip(1);
            List<String> correctStop = skipedHeader
                    .skip(1)
                    .filter(line -> stopId.equals(line.split(Constants.DELIMITER)[0]))
                    .collect(Collectors.toList());

            List<String> splited = Arrays.asList(correctStop.get(0).split(Constants.DELIMITER));
            stopDTO.setId(splited.get(0));
            stopDTO.setCode(splited.get(1));
            stopDTO.setName(splited.get(2));

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
        return stopDTO;
    }

    private List<StopTimesDTO> readStopTimes() {
        try {
            CSVReader reader = new CSVReader(new FileReader(Constants.STOPS_TIMES));

            HeaderColumnNameMappingStrategy<StopTimesDTO> beanStrategy = new HeaderColumnNameMappingStrategy<StopTimesDTO>();
            beanStrategy.setType(StopTimesDTO.class);

            CsvToBean<StopTimesDTO> csvToBean = new CsvToBean<StopTimesDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            List<StopTimesDTO> stopTimesStream = csvToBean.stream()
                    .filter(x -> {
                        new Date(System.currentTimeMillis());
                        boolean filtered = stopId.equals(x.getStop_id());
//                                && x.getArrival_time().after(now);
//                                && x.getArrival_time().before(maxNextHours);
                        return filtered;

                    })
                    .collect(Collectors.toList());
            reader.close();
            LocalTime localTime = LocalTime.ofNanoOfDay(now.getTime());
            LocalTime localTimeMaxAllowed = LocalTime.ofSecondOfDay(maxNextHours.getTime());

            Time nowTime = new Time(now.getTime());
            Time nowTimeMax = new Time(maxNextHours.getTime());
            for(StopTimesDTO dto:  stopTimesStream) {

                if(dto.getArrival_time().after(nowTime)) {

                    System.out.println(dto.getArrival_time() + "  ,  " + dto.getTrip_id());
                }

            }
            return stopTimesStream;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
    }

    private List<TripDTO> readTrips(List<StopTimesDTO> stopTimesDTOS) {
        try {
            HashMap<String, StopTimesDTO> stringStopTimesDTOHashMap = new HashMap<String, StopTimesDTO>();
            stopTimesDTOS.forEach(dto -> stringStopTimesDTOHashMap.put(dto.getTrip_id(), dto));

            InputStreamReader fileStream = new InputStreamReader(new FileInputStream(Constants.TRIP), Constants.ENCODING);
            CSVReader reader = new CSVReader(fileStream);


            HeaderColumnNameMappingStrategy<TripDTO> beanStrategy = new HeaderColumnNameMappingStrategy<TripDTO>();
            beanStrategy.setType(TripDTO.class);

            CsvToBean<TripDTO> csvToBean = new CsvToBean<TripDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            List<TripDTO> tripDTOList = csvToBean.stream()
                    .filter(x -> stringStopTimesDTOHashMap.containsKey(x.getTrip_id()))
                    .collect(Collectors.toList());
            reader.close();
            return tripDTOList;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
    }

    private List<RouteDTO> readRoutes() {
        try {
            InputStreamReader fileStream = new InputStreamReader(new FileInputStream(Constants.ROUTE), Constants.ENCODING);
            CSVReader reader = new CSVReader(fileStream);


            HeaderColumnNameMappingStrategy<RouteDTO> beanStrategy = new HeaderColumnNameMappingStrategy<RouteDTO>();
            beanStrategy.setType(RouteDTO.class);

            CsvToBean<RouteDTO> csvToBean = new CsvToBean<RouteDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            List<RouteDTO> routeDTOList = csvToBean.stream()
                    .collect(Collectors.toList());
            reader.close();
            return routeDTOList;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
    }

    private List<CalendarDTO> readCalendar(List<TripDTO> trips) {
        try {
            HashMap<String, TripDTO> stringTripDTOHashMap = new HashMap<String, TripDTO>();
            trips.forEach(dto -> stringTripDTOHashMap.put(dto.getService_id(), dto));

            InputStreamReader fileStream = new InputStreamReader(new FileInputStream(Constants.CALENDAR), Constants.ENCODING);
            CSVReader reader = new CSVReader(fileStream);


            HeaderColumnNameMappingStrategy<CalendarDTO> beanStrategy = new HeaderColumnNameMappingStrategy<CalendarDTO>();
            beanStrategy.setType(CalendarDTO.class);

            CsvToBean<CalendarDTO> csvToBean = new CsvToBean<CalendarDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            List<CalendarDTO> calendarDTOList = csvToBean.stream()
                    .filter(x -> stringTripDTOHashMap.containsKey(x.getService_id()))
                    .collect(Collectors.toList());
            reader.close();
            return calendarDTOList;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
    }
}
