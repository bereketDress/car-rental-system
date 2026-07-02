package com.crms.service;

import com.crms.dto.car.CarRequest;
import com.crms.dto.car.CarResponse;
import com.crms.dto.manager.ManagerRequest;
import com.crms.dto.manager.ManagerResponse;
import com.crms.model.Branch;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.model.Manager;
import com.crms.model.Payment;
import com.crms.model.Rental;
import com.crms.repository.BranchRepository;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.repository.ManagerRepository;
import com.crms.repository.PaymentRepository;
import com.crms.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    public List<ManagerResponse> getAll() {
        return managerRepository.findAll()
                .stream()
                .map(this::toManagerResponse)
                .toList();
    }

    public ManagerResponse getById(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + id));
        return toManagerResponse(manager);
    }

    public ManagerResponse create(ManagerRequest request) {
        Manager manager = new Manager();
        manager.setName(request.name());
        manager.setEmail(request.email());
        manager.setPhone(request.phone());
        manager.setPassword(request.password());
        return toManagerResponse(managerRepository.save(manager));
    }

    public boolean delete(Long id) {
        managerRepository.deleteById(id);
        return true;
    }

    // -----------------------------
    // BASIC INVENTORY MANAGEMENT
    // -----------------------------

    public List<CarResponse> getInventory(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));

        return (branch.getCars() == null ? List.<Car>of() : branch.getCars())
                .stream()
                .map(this::toCarResponse)
                .toList();
    }

    public CarResponse addCarToInventory(Long branchId, CarRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));

        Car car = new Car();
        car.setPlateNumber(request.plateNumber());
        car.setBrand(request.brand());
        car.setModel(request.model());
        car.setYear(request.year());
        car.setMileage(request.mileage());
        car.setDailyRate(request.dailyRate().doubleValue());
        car.setCarType(request.carType());
        car.setAvailability("AVAILABLE");

        Car saved = carRepository.save(car);

        if (branch.getCars() == null) branch.setCars(new ArrayList<>());
        branch.getCars().add(saved);
        branchRepository.save(branch);

        return toCarResponse(saved);
    }

    public CarResponse updateCarInInventory(Long carId, CarRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));

        car.setPlateNumber(request.plateNumber());
        car.setBrand(request.brand());
        car.setModel(request.model());
        car.setYear(request.year());
        car.setMileage(request.mileage());
        car.setDailyRate(request.dailyRate().doubleValue());
        car.setCarType(request.carType());
        car.setAvailability(request.availability());

        return toCarResponse(carRepository.save(car));
    }

    @Transactional
    public boolean removeCarFromInventory(Long branchId, Long carId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));

        if (branch.getCars() != null) {
            branch.getCars().removeIf(c -> carId.equals(c.getCarId()));
            branchRepository.save(branch);
        }

        carRepository.findById(carId).ifPresent(car -> {
            if ((car.getRentals() != null && !car.getRentals().isEmpty())
                    || (car.getReservations() != null && !car.getReservations().isEmpty())) {
                car.setAvailability("UNAVAILABLE");
                carRepository.save(car);
            } else {
                carRepository.delete(car);
            }
        });

        return true;
    }

    public Map<String, Object> viewReports(Long branchId) {
        Map<String, Object> report = new HashMap<>();
        List<Car> cars = branchId == null
                ? carRepository.findAll()
                : branchRepository.findById(branchId)
                .map(Branch::getCars)
                .orElse(List.of());

        long availableCars = cars.stream()
                .filter(car -> "AVAILABLE".equalsIgnoreCase(car.getAvailability()))
                .count();

        List<Rental> rentals = rentalsForCars(branchId, cars);
        long activeRentals = rentals.stream()
                .filter(rental -> "ACTIVE".equalsIgnoreCase(rental.getStatus()))
                .count();
        long completedRentals = rentals.stream()
                .filter(rental -> "RETURNED".equalsIgnoreCase(rental.getStatus()))
                .count();

        report.put("branchId", branchId);
        report.put("totalFleetSize", cars.size());
        report.put("availableCars", availableCars);
        report.put("rentedCars", cars.size() - availableCars);
        report.put("totalRentals", rentals.size());
        report.put("activeRentals", activeRentals);
        report.put("completedRentals", completedRentals);
        report.put("totalCustomers", totalCustomers(branchId, rentals));
        report.put("totalRevenue", totalRevenue(branchId, rentals));
        return report;
    }

    private List<Rental> rentalsForCars(Long branchId, List<Car> cars) {
        if (branchId == null) {
            return rentalRepository.findAll();
        }

        Set<Long> rentalIds = new HashSet<>();
        List<Rental> rentals = new ArrayList<>();

        for (Car car : cars) {
            if (car.getRentals() == null) {
                continue;
            }

            for (Rental rental : car.getRentals()) {
                if (rental.getRentalId() != null && rentalIds.add(rental.getRentalId())) {
                    rentals.add(rental);
                }
            }
        }

        return rentals;
    }

    private long totalCustomers(Long branchId, List<Rental> rentals) {
        if (branchId == null) {
            return customerRepository.count();
        }

        Set<Long> rentalIds = new HashSet<>();
        rentals.stream()
                .map(Rental::getRentalId)
                .forEach(rentalIds::add);

        return customerRepository.findAll().stream()
                .filter(customer -> customer.getRentals() != null)
                .filter(customer -> customer.getRentals().stream()
                        .map(Rental::getRentalId)
                        .anyMatch(rentalIds::contains))
                .count();
    }

    private double totalRevenue(Long branchId, List<Rental> rentals) {
        if (branchId == null) {
            return paymentRepository.findAll().stream()
                    .filter(this::completedPayment)
                    .map(Payment::getAmount)
                    .filter(amount -> amount != null)
                    .mapToDouble(Float::doubleValue)
                    .sum();
        }

        return rentals.stream()
                .map(Rental::getPayment)
                .filter(this::completedPayment)
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    private boolean completedPayment(Payment payment) {
        return payment != null && "COMPLETED".equalsIgnoreCase(payment.getStatus());
    }

    // -----------------------------
    // BASIC RESPONSE MAPPERS
    // -----------------------------

    private ManagerResponse toManagerResponse(Manager manager) {
        return new ManagerResponse(
                manager.getManagerId(),
                manager.getName(),
                manager.getEmail(),
                manager.getPhone()
        );
    }

    private CarResponse toCarResponse(Car car) {
        return new CarResponse(
                car.getCarId(),
                car.getPlateNumber(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getMileage(),
                "AVAILABLE".equalsIgnoreCase(car.getAvailability()),
                car.getAvailability(),
                car.getDailyRate(),
                car.getCarType(),
                null,
                null
        );
    }
}
