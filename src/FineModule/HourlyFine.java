package FineModule;

// hourly fine strategy which increases based on the number of hours overstayed
public class HourlyFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        // charges 20 per hour overstayed
        return hoursOverstayed * 20;
    }
}