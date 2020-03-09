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
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GtfsReader {

    private static final Logger log = Logger.getLogger(GtfsReader.class.getName());

    private Date now;
    private Date maxNextHours;
    private String stopId;

    public GtfsReader(String stopId, Date now, Date maxNextHours) {
        this.stopId = stopId;
        this.now = now;
        this.maxNextHours = maxNextHours;
    }


    public StopDTO findStop() {
        try {
            InputStreamReader fileStream = new InputStreamReader(new FileInputStream(Constants.STOPS), Constants.ENCODING);
            CSVReader reader = new CSVReader(fileStream);

            HeaderColumnNameMappingStrategy<StopDTO> beanStrategy = new HeaderColumnNameMappingStrategy<StopDTO>();
            beanStrategy.setType(StopDTO.class);

            CsvToBean<StopDTO> csvToBean = new CsvToBean<StopDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            Map<String, StopDTO> mapedStops = csvToBean.stream()
                    .filter(line -> stopId.equals(line.getId()))
                    .collect(Collectors.toMap(StopDTO::getId, v -> v));
            reader.close();
            return mapedStops.get(stopId);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }

    }

    public List<StopTimesDTO> readStopTimes() {
        try {
            CSVReader reader = new CSVReader(new FileReader(Constants.STOPS_TIMES));

            HeaderColumnNameMappingStrategy<StopTimesDTO> beanStrategy = new HeaderColumnNameMappingStrategy<StopTimesDTO>();
            beanStrategy.setType(StopTimesDTO.class);

            CsvToBean<StopTimesDTO> csvToBean = new CsvToBean<StopTimesDTO>();
            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(beanStrategy);

            Time nowTime = new Time(now.getTime());
            Time nowTimeMax = new Time(maxNextHours.getTime());

            LocalTime localNowTime = LocalTime.parse(nowTime.toString());
            LocalTime localNowTimeMax = LocalTime.parse(nowTimeMax.toString());

            List<StopTimesDTO> stopTimesStream = csvToBean.stream()
                    .filter(x -> {
                        LocalTime arrivalTime = LocalTime.parse(x.getArrival_time().toString());

                        boolean filtered = stopId.equals(x.getStop_id())
                                && arrivalTime.isAfter(localNowTime)
                                && arrivalTime.isBefore(localNowTimeMax);
                        return filtered;

                    })
                    .collect(Collectors.toList());
            reader.close();

            return stopTimesStream;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException();
        }
    }

    public List<TripDTO> readTrips(List<StopTimesDTO> stopTimesDTOS) {
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

    public List<RouteDTO> readRoutes() {
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

    public List<CalendarDTO> readCalendar(List<TripDTO> trips) {
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
