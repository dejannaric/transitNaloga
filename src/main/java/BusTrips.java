import java.util.logging.Level;
import java.util.logging.Logger;

public class BusTrips {
    private static final Logger log = Logger.getLogger(BusTrips.class.getName());

    private String stopId;
    private Integer maxNextBusses;
    private String displayType;

    public static void main(String[] args) {
        if(args.length != 3) {
            log.log(Level.SEVERE, "Invalid parameters");
            stop();
        }
        String stopId = args[0];
        Integer maxNextBusses = Integer.valueOf(args[1]);
        String displayType = args[2];


        DisplayTypeEnum displayTypeEnum = DisplayTypeEnum.valueOfType(displayType);

        CalculateBuses calculateBuses = new CalculateBuses(stopId, maxNextBusses, displayTypeEnum);


    }

    private static void stop() {
        log.log(Level.INFO, "Stopping application BusTrips...");
        System.exit(1);
    }

}
