-- Schema for Car Rental Management System (CRMS)
DROP TABLE IF EXISTS damage CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS rental CASCADE;
DROP TABLE IF EXISTS car_reservation CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS manager CASCADE;
DROP TABLE IF EXISTS car CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS branch CASCADE;

CREATE TABLE branch (
    branch_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address_id BIGINT,
    city TEXT,
    street TEXT,
    zipcode TEXT
);

CREATE TABLE manager (
    manager_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT,
    email TEXT UNIQUE,
    password TEXT,
    branch_id BIGINT UNIQUE REFERENCES branch(branch_id)
);

CREATE TABLE staff (
    staff_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    role TEXT NOT NULL,
    email TEXT UNIQUE,
    phone TEXT,
    password TEXT,
    branch_id BIGINT REFERENCES branch(branch_id),
    manager_id BIGINT REFERENCES manager(manager_id)
);

CREATE TABLE customer (
    customer_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    password TEXT,
    phone TEXT,
    license_number TEXT,
    outstanding_balance DOUBLE PRECISION,
    address_id BIGINT,
    city TEXT,
    street TEXT,
    zipcode TEXT
);

CREATE TABLE car (
    car_id BIGSERIAL PRIMARY KEY,
    vin_number VARCHAR(50) UNIQUE NOT NULL,
    plate_number TEXT,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    year INTEGER NOT NULL,
    mileage INTEGER NOT NULL,
    availability TEXT NOT NULL,
    daily_rate DOUBLE PRECISION NOT NULL,
    car_type TEXT,
    branch_id BIGINT REFERENCES branch(branch_id)
);

CREATE TABLE reservation (
    reservation_id BIGSERIAL PRIMARY KEY,
    reservation_date DATE,
    pickup_date DATE,
    status TEXT,
    customer_id BIGINT REFERENCES customer(customer_id),
    staff_id BIGINT REFERENCES staff(staff_id)
);

CREATE TABLE car_reservation (
    reservation_id BIGINT NOT NULL REFERENCES reservation(reservation_id) ON DELETE CASCADE,
    car_id BIGINT NOT NULL REFERENCES car(car_id) ON DELETE CASCADE,
    PRIMARY KEY (reservation_id, car_id)
);

CREATE TABLE rental (
    rental_id BIGSERIAL PRIMARY KEY,
    checkout_date DATE,
    return_date DATE,
    start_mileage INTEGER,
    end_mileage INTEGER,
    status TEXT,
    reservation_id BIGINT REFERENCES reservation(reservation_id),
    customer_id BIGINT REFERENCES customer(customer_id),
    car_id BIGINT REFERENCES car(car_id)
);

CREATE TABLE payment (
    payment_id BIGSERIAL PRIMARY KEY,
    payment_date DATE,
    amount DOUBLE PRECISION,
    payment_method TEXT,
    staff_id BIGINT REFERENCES staff(staff_id),
    rental_id BIGINT UNIQUE REFERENCES rental(rental_id),
    stripe_payment_intent_id TEXT,
    status TEXT
);

CREATE TABLE damage (
    damage_id BIGSERIAL PRIMARY KEY,
    report_date DATE,
    repair_cost DOUBLE PRECISION,
    status TEXT,
    description TEXT,
    rental_id BIGINT REFERENCES rental(rental_id)
);
