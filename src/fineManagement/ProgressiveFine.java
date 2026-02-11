package fineManagement;

public class ProgressiveFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        if (hoursOverstayed <= 24) return 50;
        if (hoursOverstayed <= 48) return 150;
        if (hoursOverstayed <= 72) return 300;
        return 500;
    }
}