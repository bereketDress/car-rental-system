# 2. User Stories

## User Story 1: Vehicle Reservation

**Story:**
As a customer, I want to reserve a vehicle so that I can rent it for a specific time.

**Acceptance Criteria:**
- Given the customer is logged in
- When they select a vehicle and pickup date and click Confirm
- Then the system saves the reservation with status "Pending" and shows a confirmation message

---

## User Story 2: Vehicle Check-out

**Story:**
As a staff member, I want to check out a vehicle so that I can hand it over to the customer.

**Acceptance Criteria:**
- Given a valid reservation exists
- When the staff member enters start mileage and fuel level out and confirms
- Then the system creates a rental record with checkout date and due date, and changes vehicle status to "Rented"

---

## User Story 3: Vehicle Check-in

**Story:**
As a staff member, I want to check in a vehicle so that I can complete the rental process.

**Acceptance Criteria:**
- Given the vehicle status is "Rented"
- When the staff member records end mileage, fuel level in, and damage notes
- Then the system calculates the total payment amount based on daily rate and rental duration, updates the vehicle status to "Available", updates the rental status to "Completed", and saves the payment record
