package com.crms.util;

import com.crms.model.Car;
import com.crms.model.Damage;
import com.crms.model.Reservation;
import com.crms.model.Rental;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class EntityFields {

    public static final String RESERVATION_PENDING = "PENDING";
    public static final String RESERVATION_CONFIRMED = "CONFIRMED";
    public static final String RESERVATION_CONVERTED = "CONVERTED";
    public static final String RESERVATION_CANCELLED = "CANCELLED";
    public static final String RENTAL_ACTIVE = "ACTIVE";
    public static final String RENTAL_RETURNED = "RETURNED";
    public static final String DAMAGE_REPORTED = "REPORTED";
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_FAILED = "FAILED";

    private EntityFields() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object target, String fieldName, Class<T> type) {
        Object value = get(target, fieldName);
        return value == null ? null : (T) value;
    }

    public static Object get(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to read field " + fieldName + " from " + target.getClass(), e);
        }
    }

    public static void set(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to write field " + fieldName + " on " + target.getClass(), e);
        }
    }

    public static String string(Object target, String fieldName) {
        return get(target, fieldName, String.class);
    }

    public static Long longValue(Object target, String fieldName) {
        return get(target, fieldName, Long.class);
    }

    public static Integer integer(Object target, String fieldName) {
        return get(target, fieldName, Integer.class);
    }

    public static Double doubleValue(Object target, String fieldName) {
        return get(target, fieldName, Double.class);
    }

    public static LocalDate date(Object target, String fieldName) {
        return get(target, fieldName, LocalDate.class);
    }

    public static boolean available(Car car) {
        return "AVAILABLE".equalsIgnoreCase(string(car, "availability"))
                || "TRUE".equalsIgnoreCase(string(car, "availability"));
    }

    public static void setAvailable(Car car, boolean available) {
        set(car, "availability", available ? "AVAILABLE" : "UNAVAILABLE");
    }

    public static Car reservationCar(Reservation reservation) {
        List<Car> cars = get(reservation, "reservationCars", List.class);
        return cars == null || cars.isEmpty() ? null : cars.get(0);
    }

    public static void setReservationCar(Reservation reservation, Car car) {
        set(reservation, "reservationCars", car == null ? List.of() : List.of(car));
    }

    public static double rentalCharge(Rental rental) {
        return baseRentalCharge(rental) + damageRepairCost(rental);
    }

    public static double baseRentalCharge(Rental rental) {
        Car car = get(rental, "car", Car.class);
        LocalDate checkoutDate = date(rental, "checkoutDate");
        LocalDate returnDate = date(rental, "returnDate");
        Double dailyRate = car == null ? null : doubleValue(car, "dailyRate");

        if (checkoutDate == null || returnDate == null || dailyRate == null) {
            return 0.0;
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(checkoutDate, returnDate));
        return days * dailyRate;
    }

    public static double damageRepairCost(Rental rental) {
        List<Damage> damages = get(rental, "damages", List.class);
        if (damages == null) {
            return 0.0;
        }

        return damages.stream()
                .map(damage -> doubleValue(damage, "repairCost"))
                .filter(cost -> cost != null && cost > 0)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
