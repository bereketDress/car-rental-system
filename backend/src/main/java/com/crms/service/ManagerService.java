package com.crms.service;
import com.crms.model.*;
import lombok.RequiredArgsConstructor;
import com.crms.repository.*;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;

    public List<Manager> getAll() {
        return managerRepository.findAll();
    }

    public Manager getById(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + id));
    }

    public Manager create(Manager manager) {
        return managerRepository.save(manager);
    }

    public void delete(Long id) {
        managerRepository.deleteById(id);
    }
    public Map<String, Object> viewReports(Long branchId) {
        Map<String, Object> report = new HashMap<>();

        List<Rental> allRentals = rentalRepository.findAll().stream()
                .filter(rental -> branchId == null || rentalBelongsToBranch(rental, branchId))
                .toList();
        List<Payment> allPayments = paymentRepository.findAll().stream()
                .filter(payment -> branchId == null || paymentBelongsToBranch(payment, branchId))
                .toList();
        List<Car> allCars = carRepository.findAll().stream()
                .filter(car -> branchId == null || carBelongsToBranch(car, branchId))
                .toList();
        List<Customer> allCustomers = customerRepository.findAll();

        long totalRentals = allRentals.size();
        long activeRentals = allRentals.stream()
                .filter(r -> RENTAL_ACTIVE.equalsIgnoreCase(string(r, "status")))
                .count();
        long completedRentals = allRentals.stream()
                .filter(r -> RENTAL_RETURNED.equalsIgnoreCase(string(r, "status")))
                .count();

        double totalRevenue = allPayments.stream()
                .filter(payment -> PAYMENT_COMPLETED.equalsIgnoreCase(string(payment, "status")))
                .mapToDouble(payment -> {
                    Double amount = doubleValue(payment, "amount");
                    return amount == null ? 0.0 : amount;
                })
                .sum();

        long availableCars = allCars.stream()
                .filter(car -> available(car))
                .count();
        long rentedCars = allCars.stream()
                .filter(c -> !available(c))
                .count();

        report.put("branchId", branchId);
        report.put("totalRentals", totalRentals);
        report.put("activeRentals", activeRentals);
        report.put("completedRentals", completedRentals);
        report.put("totalRevenue", totalRevenue);
        report.put("totalCustomers", allCustomers.size());
        report.put("totalFleetSize", allCars.size());
        report.put("availableCars", availableCars);
        report.put("rentedCars", rentedCars);

        return report;
    }

    public List<Car> getInventory(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));
        return branch.getCars() == null ? Collections.emptyList() : branch.getCars();
    }
    public Car addCarToInventory(Long branchId, Car car) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));
        set(car, "branch", branch);
        if (string(car, "availability") == null || string(car, "availability").isBlank()) {
            setAvailable(car, true);
        }
        return carRepository.save(car);
    }

    public Car updateCarInInventory(String vinNumber, Car updated) {
        Car existing = carRepository.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Car not found: " + vinNumber));
        set(existing, "plateNumber", string(updated, "plateNumber"));
        set(existing, "brand", string(updated, "brand"));
        set(existing, "model", string(updated, "model"));
        set(existing, "year", integer(updated, "year"));
        set(existing, "mileage", integer(updated, "mileage"));
        set(existing, "availability", string(updated, "availability"));
        set(existing, "dailyRate", doubleValue(updated, "dailyRate"));
        set(existing, "carType", string(updated, "carType"));
        return carRepository.save(existing);
    }
    public void removeCarFromInventory(Long branchId, String vinNumber) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));
        Car car = carRepository.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Car not found: " + vinNumber));
        if (branch.equals(get(car, "branch"))) {
            set(car, "branch", null);
            carRepository.save(car);
        }
    }

    private boolean rentalBelongsToBranch(Rental rental, Long branchId) {
        Car car = get(rental, "car", Car.class);
        return carBelongsToBranch(car, branchId);
    }

    private boolean paymentBelongsToBranch(Payment payment, Long branchId) {
        Rental rental = get(payment, "rental", Rental.class);
        return rental != null && rentalBelongsToBranch(rental, branchId);
    }

    private boolean carBelongsToBranch(Car car, Long branchId) {
        if (car == null) {
            return false;
        }
        Branch branch = get(car, "branch", Branch.class);
        return branch != null && branchId.equals(branch.getBranchId());
    }
}
