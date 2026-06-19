

-- BRANCH
INSERT INTO branch (branch_id, name, phone, street, city, zipcode) VALUES
(1, 'Main Branch', '0111234567', 'Bole Road', 'Addis Ababa', '1000'),
(2, 'Airport Branch', '0117654321', 'Airport Road', 'Addis Ababa', '2000'),
(3, 'Downtown Branch', '0119876543', 'Piassa', 'Addis Ababa', '3000');

-- MANAGER
INSERT INTO manager (manager_id, name, phone, email, password) VALUES
(1, 'Abebe Bikila', '0911000001', 'abebe@crms.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu'),
(2, 'Dawit Lema', '0911000002', 'dawit@crms.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu');

-- STAFF
INSERT INTO staff (staff_id, name, role, email, phone, password, branch_id) VALUES
(1, 'Kassa Tessema', 'STAFF', 'kassa@crms.com', '0911111111', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 1),
(2, 'Sara Bekele', 'STAFF', 'sara@crms.com', '0911222222', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 2),
(3, 'Marta Alemu', 'STAFF', 'marta@crms.com', '0911333333', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu', 3);

-- CUSTOMER
INSERT INTO customer
(customer_id, name, email, password, phone, license_no, outstanding_balance,
 street, city, zipcode)
VALUES
(1, 'John Doe', 'john@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu',
'0911444444', 'ETH123456', 0.00, 'Churchill Avenue', 'Addis Ababa', '1000'),

(2, 'Jane Smith', 'jane@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu',
'0911555555', 'ETH654321', 150.00, 'Bole Road', 'Addis Ababa', '2000'),

(3, 'Michael Brown', 'michael@example.com', '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu',
'0911666666', 'ETH789012', 0.00, 'Kazanchis', 'Addis Ababa', '3000');

-- CAR
INSERT INTO car(car_id, plate_number, brand, model, year, mileage, availability, daily_rate, car_type, branch_id)VALUES
    (1, 'AA-10001', 'Toyota', 'Corolla', 2022, 15000, 'AVAILABLE', 50.00, 'SEDAN', 1),
    (2, 'AA-10002', 'Hyundai', 'Tucson', 2023, 7000, 'AVAILABLE', 80.00, 'SUV', 1),
    (3, 'AA-10003', 'Ford', 'Ranger', 2021, 28000, 'AVAILABLE', 100.00, 'PICKUP', 2),
    (4, 'AA-10004', 'Mercedes', 'C-Class', 2024, 1500, 'UNAVAILABLE', 150.00, 'LUXURY', 3),
    (5, 'AA-10005', 'Kia', 'Sportage', 2023, 9000, 'AVAILABLE', 75.00, 'SUV', 3);

-- RESERVATION
INSERT INTO reservation(reservation_id, reservation_date, pickup_date, status, customer_id) VALUES
    (1, '2026-06-01', '2026-06-10', 'CONVERTED', 1),
    (2, '2026-06-05', '2026-06-15', 'PENDING', 2),
    (3, '2026-06-08', '2026-06-12', 'CONVERTED', 3);

-- CAR_RESERVATION
INSERT IGNORE INTO car_reservation (car_id, reservation_id) VALUES
(1, 1),
(2, 2),
(4, 3);

-- RENTAL
INSERT INTO rental
(rental_id, checkout_date, return_date, start_mileage, end_mileage, status, base_charge, customer_id, car_id) VALUES
    (1, '2026-06-10', '2026-06-17', 15000, NULL, 'ACTIVE', 350.00, 1, 1),
    (2, '2026-06-12', '2026-06-19', 1500, NULL, 'ACTIVE', 1050.00, 3, 4);

-- PAYMENT
INSERT INTO payment
(payment_id, payment_date, amount, payment_method, stripe_payment_intent_id, status) VALUES
(1, '2026-06-10', 350.00, 'CREDIT_CARD', 'pi_test_123456', 'COMPLETED'),
(2, '2026-06-12', 450.00, 'CASH', NULL, 'COMPLETED');

-- DAMAGE
INSERT INTO damage (damage_id, report_date, repair_cost, status, description, rental_id)VALUES
    (1, '2026-06-18', 0.00, 'CLOSED', 'Vehicle returned without damage', 1),
    (2, '2026-06-20', 500.00, 'OPEN', 'Front bumper scratched', 2);

UPDATE manager SET password = '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu'
WHERE email IN ('abebe@crms.com', 'dawit@crms.com');

UPDATE staff SET password = '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu'
WHERE email IN ('kassa@crms.com', 'sara@crms.com', 'marta@crms.com');

UPDATE customer SET password = '$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu'
WHERE email IN ('john@example.com', 'jane@example.com', 'michael@example.com');
