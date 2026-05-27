## User Story 1: Vehicle reservation

**Story:**
As a customer, I want to reserve a vehicle 
so that I can rent it for a specific time.

**Acceptance Criteria:**

- Given the customer is logged in
- When they select a vehicle and dates and click Confirm
- Then the system saves the booking and shows a confirmation message

## User Story 2: Vehicle check-out

**Story:**
As a staff member, I want to check out a vehicle 
so that I can hand it over to the customer.

**Acceptance Criteria:**

- Given a valid reservation exists
- When the staff member enters mileage and fuel level and confirms
- Then the system saves the check-out details and changes vehicle status to "Rented"


## User Story 3: Vehicle check-in

**Story:**
As a staff member, I want to check in a vehicle,
so that I can complete the rental process.

**Acceptance Criteria:**

- Given the vehicle is currently rented
- When the staff member records return mileage, fuel, and any damage
- Then the system calculates charges, updates the vehicle status to "Available",
  and closes the rental
