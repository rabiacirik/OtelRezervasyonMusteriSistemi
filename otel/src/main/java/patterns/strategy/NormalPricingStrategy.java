package patterns.strategy;

public class NormalPricingStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(double pricePerNight, long days) {
        // Normal fiyat: Gün sayısı * Gecelik ücret
        return pricePerNight * days;
    }
}