package mtakeshi1.playground;

public class InterestRateCalculator {
    
    public static double calculateTotalAmount(double initialValue, double interest, int numberOfParcels) {
        double sum = initialValue;
        for(int i = 0; i < numberOfParcels; i++) {
            sum += interest;
        }
        return sum;
    }
    
}
