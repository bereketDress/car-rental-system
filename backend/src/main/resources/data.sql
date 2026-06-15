-- Seed data for CRMS

INSERT INTO branch (branch_id, name, phone, address_id, street, city, zipcode) VALUES
(1, 'Main Branch', '011-1234567', 1, 'Bole Road', 'Addis Ababa', '1000'),
(2, 'Airport Branch', '011-7654321', 2, 'Airport Road', 'Addis Ababa', '2000'),
(3, 'Downtown Branch', '011-9876543', 3, 'Piassa', 'Addis Ababa', '3000');

INSERT INTO manager (manager_id, name, phone, email, password, branch_id) VALUES
(1, 'Abebe Bikila', '0911000001', 'abebe@crms.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 1),
(2, 'Dawit Lema', '0911000004', 'dawit@crms.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 3);

INSERT INTO staff (staff_id, name, role, email, phone, password, branch_id, manager_id) VALUES
(1, 'Kassa Tessema', 'STAFF', 'kassa@crms.com', '0911000002', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 1, 1),
(2, 'Sara Bekele', 'STAFF', 'sara@crms.com', '0911000003', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 2, 1),
(3, 'Marta Alemu', 'STAFF', 'marta@crms.com', '0911000005', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 3, 2);

INSERT INTO customer (customer_id, name, email, password, phone, license_number, outstanding_balance, address_id, street, city, zipcode) VALUES
(1, 'John Doe', 'john@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', '0911112233', 'ETH123456', 0.0, 1, 'Churchill Ave', 'Addis Ababa', '1000'),
(2, 'Jane Smith', 'jane@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', '0911445566', 'ETH654321', 120.5, 2, 'Bole Rd', 'Addis Ababa', '2000'),
(3, 'Michael Brown', 'michael@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', '0911223344', 'ETH789012', 0.0, 3, 'Kazanchis', 'Addis Ababa', '3000');

INSERT INTO car (car_id, vin_number, plate_number, brand, model, year, mileage, availability, daily_rate, car_type, branch_id) VALUES
(1, 'VIN1234567890', 'AA-1-23456', 'Toyota', 'Corolla', 2022, 15000, 'UNAVAILABLE', 50.0, 'SEDAN', 1),
(2, 'VIN0987654321', 'AA-2-65432', 'Hyundai', 'Tucson', 2023, 5000, 'UNAVAILABLE', 80.0, 'SUV', 1),
(3, 'VIN1122334455', 'AA-3-11223', 'Ford', 'Ranger', 2021, 30000, 'AVAILABLE', 100.0, 'PICKUP', 2),
(4, 'VIN5544332211', 'AA-4-44332', 'Mercedes', 'C-Class', 2024, 1200, 'UNAVAILABLE', 150.0, 'LUXURY', 3),
(5, 'VIN6677889900', 'AA-5-55667', 'Kia', 'Sportage', 2023, 8000, 'AVAILABLE', 75.0, 'SUV', 3);

INSERT INTO reservation (reservation_id, reservation_date, pickup_date, status, customer_id, staff_id) VALUES
(1, '2026-06-01', '2026-06-10', 'CONVERTED', 1, 1),
(2, '2026-06-05', '2026-06-15', 'PENDING', 2, 1),
(3, '2026-06-08', '2026-06-12', 'CONVERTED', 3, 3);

INSERT INTO car_reservation (reservation_id, car_id) VALUES
(1, 1),
(2, 2),
(3, 4);

INSERT INTO rental (rental_id, checkout_date, return_date, start_mileage, end_mileage, status, reservation_id, customer_id, car_id) VALUES
(1, '2026-06-10', '2026-06-17', 15000, NULL, 'ACTIVE', 1, 1, 1),
(2, '2026-06-12', '2026-06-19', 1200, NULL, 'ACTIVE', 3, 3, 4);

INSERT INTO payment (payment_id, payment_date, amount, payment_method, rental_id, stripe_payment_intent_id, status) VALUES
(1, '2026-06-10', 50.0, 'CREDIT_CARD', 1, 'pi_123456789', 'COMPLETED'),
(2, '2026-06-12', 150.0, 'CASH', 2, NULL, 'COMPLETED');

INSERT INTO damage (damage_id, report_date, repair_cost, status, description, rental_id) VALUES
(1, '2026-06-11', 0.0, 'CLOSED', 'No damage reported yet', 1),
(2, '2026-06-13', 0.0, 'CLOSED', 'Initial inspection clear', 2);

SELECT setval('branch_branch_id_seq', (SELECT MAX(branch_id) FROM branch));
SELECT setval('manager_manager_id_seq', (SELECT MAX(manager_id) FROM manager));
SELECT setval('staff_staff_id_seq', (SELECT MAX(staff_id) FROM staff));
SELECT setval('customer_customer_id_seq', (SELECT MAX(customer_id) FROM customer));
SELECT setval('car_car_id_seq', (SELECT MAX(car_id) FROM car));
SELECT setval('reservation_reservation_id_seq', (SELECT MAX(reservation_id) FROM reservation));
SELECT setval('rental_rental_id_seq', (SELECT MAX(rental_id) FROM rental));
SELECT setval('payment_payment_id_seq', (SELECT MAX(payment_id) FROM payment));
SELECT setval('damage_damage_id_seq', (SELECT MAX(damage_id) FROM damage));
