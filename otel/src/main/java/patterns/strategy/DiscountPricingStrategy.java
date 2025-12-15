package patterns.strategy;

public class DiscountPricingStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(double pricePerNight, long days) {
        double rawTotal = pricePerNight * days;
        // %20 İndirim uygula
        return rawTotal * 0.80;
    }
}