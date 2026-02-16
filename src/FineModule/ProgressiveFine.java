package FineModule;

// progressive fine strategy which increases in tiers based on how long the vehicle overstayed 
public class ProgressiveFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        // apply tier based fine amounts depending on duration range
        if (hoursOverstayed <= 24) return 50;
        if (hoursOverstayed <= 48) return 150;
        if (hoursOverstayed <= 72) return 300;
        return 500; // maximum tier
    }
}