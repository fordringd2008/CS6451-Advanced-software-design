package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the correct strategy for a payment type.
 * <p>
 * Spring injects every {@link PaymentStrategy} bean; the factory indexes them
 * by supported type. Adding a new payment type = add one bean, nothing else
 * changes here (open-closed principle).
 *
 * @author Fordring
 * @since 1.0
 */
@Component
public class PaymentStrategyFactory {

    private final Map<PaymentTypeEnum, PaymentStrategy> strategyMap = new EnumMap<>(PaymentTypeEnum.class);

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        for (PaymentStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedType(), strategy);
        }
    }

    /**
     * @param type payment type (never null)
     * @return the strategy registered for this type
     * @throws IllegalArgumentException if no strategy is registered for the type
     */
    public PaymentStrategy getStrategy(PaymentTypeEnum type) {
        PaymentStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("no payment strategy registered for type: " + type);
        }
        return strategy;
    }

    /** All registered strategies — unmodifiable view (used to list available payment methods). */
    public Map<PaymentTypeEnum, PaymentStrategy> getAllStrategies() {
        return Collections.unmodifiableMap(strategyMap);
    }
}