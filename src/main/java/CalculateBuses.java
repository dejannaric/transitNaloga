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

    public CalculateBuses(String stopId, Integer maxNextBusses, DisplayTypeEnum displayType) {
        this.stopId = stopId;
        this.maxNextBusses = maxNextBusses;
        this.displayType = displayType;
        init();
    }

    private void init() {
        GtfsReader reader = new GtfsReader(stopId, now, maxNextHours);
        StopDTO stopDTO = reader.findStop();
        List<StopTimesDTO> stopTimesDTOS = reader.readStopTimes();
        List<TripDTO> tripsDTOS = reader.readTrips(stopTimesDTOS);
        List<RouteDTO> routesDTOS = reader.readRoutes();
        List<CalendarDTO> calendarDTOS = reader.readCalendar(tripsDTOS);
    }

}
