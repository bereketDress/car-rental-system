$ErrorActionPreference = "Stop"

$outputPath = Join-Path (Get-Location) "CRMS_Project_Presentation.pptx"

function RgbColor($r, $g, $b) {
    return [int]($r + ($g * 256) + ($b * 65536))
}

$Color = @{
    Navy = RgbColor 18 36 64
    Blue = RgbColor 37 99 235
    Green = RgbColor 22 163 74
    Orange = RgbColor 234 88 12
    Red = RgbColor 220 38 38
    Slate = RgbColor 71 85 105
    Light = RgbColor 248 250 252
    Line = RgbColor 203 213 225
    White = RgbColor 255 255 255
    Black = RgbColor 15 23 42
}

function Add-TextBox($slide, [string]$text, [double]$x, [double]$y, [double]$w, [double]$h, [int]$fontSize = 18, [int]$color = $Color.Black, [bool]$bold = $false) {
    $shape = $slide.Shapes.AddTextbox(1, $x, $y, $w, $h)
    $shape.TextFrame.TextRange.Text = $text
    $shape.TextFrame.MarginLeft = 8
    $shape.TextFrame.MarginRight = 8
    $shape.TextFrame.MarginTop = 5
    $shape.TextFrame.MarginBottom = 5
    $shape.TextFrame.TextRange.Font.Name = "Aptos"
    $shape.TextFrame.TextRange.Font.Size = $fontSize
    $shape.TextFrame.TextRange.Font.Color.RGB = $color
    $shape.TextFrame.TextRange.Font.Bold = if ($bold) { -1 } else { 0 }
    return $shape
}

function Add-CodeBox($slide, [string]$text, [double]$x, [double]$y, [double]$w, [double]$h, [int]$fontSize = 10) {
    $shape = $slide.Shapes.AddShape(1, $x, $y, $w, $h)
    $shape.Fill.ForeColor.RGB = RgbColor 15 23 42
    $shape.Line.ForeColor.RGB = RgbColor 30 41 59
    $shape.TextFrame.MarginLeft = 10
    $shape.TextFrame.MarginRight = 10
    $shape.TextFrame.MarginTop = 8
    $shape.TextFrame.MarginBottom = 8
    $shape.TextFrame.TextRange.Text = $text
    $shape.TextFrame.TextRange.Font.Name = "Consolas"
    $shape.TextFrame.TextRange.Font.Size = $fontSize
    $shape.TextFrame.TextRange.Font.Color.RGB = RgbColor 226 232 240
    return $shape
}

function Add-Header($slide, [string]$title, [string]$subtitle = "") {
    Add-TextBox $slide $title 34 18 890 38 24 $Color.Navy $true | Out-Null
    if ($subtitle) {
        Add-TextBox $slide $subtitle 42 54 860 24 11 $Color.Slate $false | Out-Null
    }
    $line = $slide.Shapes.AddShape(1, 40, 84, 880, 1.5)
    $line.Fill.ForeColor.RGB = $Color.Line
    $line.Line.Visible = 0
}

function Add-BulletList($slide, [string[]]$items, [double]$x, [double]$y, [double]$w, [double]$h, [int]$fontSize = 15) {
    $shape = Add-TextBox $slide ($items -join "`r") $x $y $w $h $fontSize $Color.Black $false
    $range = $shape.TextFrame.TextRange
    $range.ParagraphFormat.Bullet.Visible = -1
    $range.ParagraphFormat.Bullet.Type = 1
    return $shape
}

function Add-Card($slide, [string]$title, [string]$body, [double]$x, [double]$y, [double]$w, [double]$h, [int]$accent = $Color.Blue) {
    $shape = $slide.Shapes.AddShape(1, $x, $y, $w, $h)
    $shape.Fill.ForeColor.RGB = $Color.White
    $shape.Line.ForeColor.RGB = $Color.Line
    $bar = $slide.Shapes.AddShape(1, $x, $y, 5, $h)
    $bar.Fill.ForeColor.RGB = $accent
    $bar.Line.Visible = 0
    Add-TextBox $slide $title ($x + 14) ($y + 10) ($w - 24) 24 15 $Color.Navy $true | Out-Null
    Add-TextBox $slide $body ($x + 14) ($y + 40) ($w - 24) ($h - 45) 12 $Color.Slate $false | Out-Null
}

function Add-FlowBox($slide, [string]$text, [double]$x, [double]$y, [double]$w, [double]$h, [int]$fill) {
    $shape = $slide.Shapes.AddShape(5, $x, $y, $w, $h)
    $shape.Fill.ForeColor.RGB = $fill
    $shape.Line.ForeColor.RGB = $fill
    $shape.TextFrame.TextRange.Text = $text
    $shape.TextFrame.TextRange.Font.Name = "Aptos"
    $shape.TextFrame.TextRange.Font.Size = 13
    $shape.TextFrame.TextRange.Font.Bold = -1
    $shape.TextFrame.TextRange.Font.Color.RGB = $Color.White
    $shape.TextFrame.VerticalAnchor = 3
    $shape.TextFrame.TextRange.ParagraphFormat.Alignment = 2
    return $shape
}

function Add-Arrow($slide, [double]$x1, [double]$y1, [double]$x2, [double]$y2) {
    $line = $slide.Shapes.AddLine($x1, $y1, $x2, $y2)
    $line.Line.ForeColor.RGB = $Color.Slate
    $line.Line.Weight = 2
    $line.Line.EndArrowheadStyle = 3
}

function Add-TableSlide($presentation, [string]$title, [array]$rows) {
    $slide = $presentation.Slides.Add($presentation.Slides.Count + 1, 12)
    Add-Header $slide $title "Line-number walkthrough for presentation talking points."
    $tableShape = $slide.Shapes.AddTable($rows.Count + 1, 2, 38, 104, 884, 420)
    $table = $tableShape.Table
    $table.Cell(1,1).Shape.TextFrame.TextRange.Text = "Line(s)"
    $table.Cell(1,2).Shape.TextFrame.TextRange.Text = "What to say"
    for ($c = 1; $c -le 2; $c++) {
        $table.Cell(1,$c).Shape.Fill.ForeColor.RGB = $Color.Navy
        $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Color.RGB = $Color.White
        $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Bold = -1
        $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Size = 11
    }
    for ($i = 0; $i -lt $rows.Count; $i++) {
        $r = $i + 2
        $table.Cell($r,1).Shape.TextFrame.TextRange.Text = $rows[$i][0]
        $table.Cell($r,2).Shape.TextFrame.TextRange.Text = $rows[$i][1]
        $table.Cell($r,1).Shape.TextFrame.TextRange.Font.Size = 9
        $table.Cell($r,2).Shape.TextFrame.TextRange.Font.Size = 9
        $table.Cell($r,1).Shape.TextFrame.TextRange.Font.Name = "Aptos"
        $table.Cell($r,2).Shape.TextFrame.TextRange.Font.Name = "Aptos"
        $table.Cell($r,1).Shape.Fill.ForeColor.RGB = if ($i % 2 -eq 0) { $Color.Light } else { $Color.White }
        $table.Cell($r,2).Shape.Fill.ForeColor.RGB = if ($i % 2 -eq 0) { $Color.Light } else { $Color.White }
    }
    $table.Columns.Item(1).Width = 85
    $table.Columns.Item(2).Width = 799
    return $slide
}

$app = New-Object -ComObject PowerPoint.Application
$app.Visible = -1
$app.WindowState = 2
$presentation = $app.Presentations.Add()
$presentation.PageSetup.SlideWidth = 960
$presentation.PageSetup.SlideHeight = 540

# Slide 1
$slide = $presentation.Slides.Add(1, 12)
$bg = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
$bg.Fill.ForeColor.RGB = $Color.Navy
$bg.Line.Visible = 0
Add-TextBox $slide "CRMS Project Presentation" 54 128 840 58 34 $Color.White $true | Out-Null
Add-TextBox $slide "Frontend/Backend Communication, Operations Flow, and seed.sql Walkthrough" 60 192 820 40 20 (RgbColor 219 234 254) $false | Out-Null
Add-TextBox $slide "Car Rental Management System | React + Spring Boot + MySQL + JWT + Stripe" 60 275 790 34 15 (RgbColor 226 232 240) $false | Out-Null
Add-TextBox $slide "Prepared from source files in C:\Users\Bereket\SWEProject" 60 470 790 24 11 (RgbColor 203 213 225) $false | Out-Null

# Slide 2
$slide = $presentation.Slides.Add(2, 12)
Add-Header $slide "Project Overview" "What the system does and where each part lives."
Add-Card $slide "Frontend" "React/Vite UI in frontend/src. Components call services/api.js instead of calling fetch directly from every page." 48 118 260 145 $Color.Blue
Add-Card $slide "Backend" "Spring Boot 3.4.5 API in backend/src/main/java/com/crms. Controllers receive HTTP, services run business rules, repositories persist data." 350 118 260 145 $Color.Green
Add-Card $slide "Database" "MySQL database sweDB. Hibernate updates schema; seed.sql loads demo branches, users, cars, reservations, rentals, payments, and damages." 652 118 260 145 $Color.Orange
Add-BulletList $slide @(
    "Backend port: 8081 from application.properties.",
    "Frontend origin allowed by CORS: http://localhost:5173 and http://127.0.0.1:5173.",
    "Authentication: /api/auth/login returns JWT plus userId, name, and role.",
    "Authorization: frontend sends Authorization: Bearer <token>; JwtFilter builds ROLE_CUSTOMER, ROLE_STAFF, or ROLE_MANAGER."
) 70 314 820 136 16 | Out-Null

# Slide 3
$slide = $presentation.Slides.Add(3, 12)
Add-Header $slide "Runtime Architecture" "The normal request path from browser to database and back."
Add-FlowBox $slide "React UI`nPages/Components" 42 145 128 64 $Color.Blue | Out-Null
Add-FlowBox $slide "api.js`nrequest()" 214 145 128 64 (RgbColor 59 130 246) | Out-Null
Add-FlowBox $slide "Spring Security`nCORS + JWT" 386 145 128 64 $Color.Navy | Out-Null
Add-FlowBox $slide "Controller`n/api/..." 558 145 128 64 $Color.Green | Out-Null
Add-FlowBox $slide "Service`nBusiness Rules" 730 145 128 64 (RgbColor 21 128 61) | Out-Null
Add-FlowBox $slide "Repository`nJPA" 558 310 128 64 $Color.Orange | Out-Null
Add-FlowBox $slide "MySQL`nsweDB" 386 310 128 64 (RgbColor 217 119 6) | Out-Null
Add-FlowBox $slide "Stripe API`nCard Payments" 730 310 128 64 $Color.Red | Out-Null
Add-Arrow $slide 170 177 214 177
Add-Arrow $slide 342 177 386 177
Add-Arrow $slide 514 177 558 177
Add-Arrow $slide 686 177 730 177
Add-Arrow $slide 794 209 794 310
Add-Arrow $slide 730 342 686 342
Add-Arrow $slide 558 342 514 342
Add-Arrow $slide 622 209 622 310
Add-TextBox $slide "Response returns in reverse: entity/data -> DTO/summary JSON -> fetch response -> React state -> UI render." 76 442 820 42 15 $Color.Slate $false | Out-Null

# Slide 4
$slide = $presentation.Slides.Add(4, 12)
Add-Header $slide "Frontend API Wrapper" "frontend/src/services/api.js is the communication center."
Add-CodeBox $slide "Lines 6-8: BASE_URL points to http://localhost:8081 and currentAuth starts from browser storage.`rLines 10-17: readStoredAuth() safely loads JSON auth data from sessionStorage/localStorage.`rLines 23-31: storeAuth() writes the token/user object for this browser session.`rLines 33-42: clearStoredAuth() removes auth and broadcasts crms:auth-cleared." 42 112 420 160 12 | Out-Null
Add-CodeBox $slide "Lines 45-56: request(url, options) builds JSON headers, adds Authorization: Bearer token, then calls fetch(BASE_URL + url).`rLines 58-64: handles empty 204 responses and parses JSON only when the response is JSON.`rLines 65-76: non-OK responses become JavaScript Error messages; 401 also clears stored auth.`rLines 78-79: successful calls return { data } for pages/components." 498 112 420 160 12 | Out-Null
Add-BulletList $slide @(
    "Every service method below line 81 calls request(), so token/error behavior is consistent.",
    "The frontend never talks to repositories or the database directly.",
    "The backend controls permissions; the frontend only sends the user action and token."
) 82 320 800 90 15 | Out-Null
Add-CodeBox $slide "Example: reservationService.create() -> POST /api/reservations with JSON body { carId, pickupDate }." 82 430 800 54 14 | Out-Null

# Slide 5
$slide = $presentation.Slides.Add(5, 12)
Add-Header $slide "Login Communication" "How a user becomes authenticated."
Add-FlowBox $slide "LoginPage`nsubmits form" 54 126 136 60 $Color.Blue | Out-Null
Add-FlowBox $slide "AuthContext`nlogin()" 244 126 136 60 (RgbColor 59 130 246) | Out-Null
Add-FlowBox $slide "api.js`nPOST /api/auth/login" 434 126 154 60 $Color.Navy | Out-Null
Add-FlowBox $slide "AuthController`nlogin()" 642 126 136 60 $Color.Green | Out-Null
Add-Arrow $slide 190 156 244 156
Add-Arrow $slide 380 156 434 156
Add-Arrow $slide 588 156 642 156
Add-BulletList $slide @(
    "AuthContext lines 27-37 call authService.login(), normalize the response, store the token, and update React state.",
    "api.js lines 83-87 send POST /api/auth/login with the JSON login body.",
    "AuthController lines 21-24 return 200 OK on success or 401 with message on failure.",
    "AuthService lines 20-39 choose customer/staff/manager repository based on role.",
    "AuthService lines 50-61 compare BCrypt password and return token, userId, name, role."
) 78 230 820 150 15 | Out-Null
Add-CodeBox $slide "After login: api.js lines 52-54 attach Authorization: Bearer <JWT> to protected calls. JwtFilter lines 21-34 validates it and places ROLE_<role> in Spring Security." 82 420 800 54 13 | Out-Null

# Slide 6
$slide = $presentation.Slides.Add(6, 12)
Add-Header $slide "Security and CORS" "How the backend decides what the frontend may do."
Add-BulletList $slide @(
    "SecurityConfig lines 41-45 enable CORS, disable CSRF/httpBasic, and set stateless sessions.",
    "Lines 49-54 allow OPTIONS, login/register, Stripe config/webhook, card confirmation, and public car browsing.",
    "Lines 56-59 restrict car management, manager reports, branches, and staff endpoints to ROLE_MANAGER.",
    "Lines 61-64 allow reservation/rental/payment work to CUSTOMER, STAFF, and MANAGER according to route.",
    "Lines 66-68 reserve checkout/checkin/damage operations for STAFF or MANAGER.",
    "Lines 80-89 allow Vite origins and JSON/Auth headers."
) 58 118 838 168 15 | Out-Null
Add-Card $slide "401" "No valid login token, expired token, or missing Authorization header." 86 330 230 96 $Color.Red
Add-Card $slide "403" "Logged in, but the role does not match the route rule." 365 330 230 96 $Color.Orange
Add-Card $slide "200/204" "Request passed security and controller/service finished successfully." 644 330 230 96 $Color.Green

# Slide 7
$slide = $presentation.Slides.Add(7, 12)
Add-Header $slide "Search and Reserve Cars" "Customer-facing operation from UI to database."
Add-BulletList $slide @(
    "CarsPage lines 19-31 load cars and call carService.searchAvailable(type).",
    "api.js lines 99-107 builds URLSearchParams and calls GET /api/cars/search?type=...",
    "CarController lines 18-22 receives type/carType and returns carService.searchAvailableResponses(...).",
    "CarsPage lines 37-45 blocks reservation if not logged in or not CUSTOMER.",
    "CarsPage lines 47-50 calls reservationService.create({ carId, pickupDate }).",
    "ReservationController lines 35-47 extracts customerId from JWT details and calls service.",
    "ReservationService lines 45-69 checks outstanding balance, car availability, creates PENDING reservation, and marks the car unavailable."
) 46 112 870 188 14 | Out-Null
Add-FlowBox $slide "Search" 84 350 110 48 $Color.Blue | Out-Null
Add-Arrow $slide 194 374 258 374
Add-FlowBox $slide "GET /api/cars/search" 258 350 165 48 $Color.Navy | Out-Null
Add-Arrow $slide 423 374 488 374
Add-FlowBox $slide "List available cars" 488 350 145 48 $Color.Green | Out-Null
Add-Arrow $slide 633 374 696 374
Add-FlowBox $slide "Render CarGrid" 696 350 145 48 $Color.Blue | Out-Null
Add-CodeBox $slide "Reservation status starts as PENDING; the selected car is removed from availability immediately." 82 438 800 46 13 | Out-Null

# Slide 8
$slide = $presentation.Slides.Add(8, 12)
Add-Header $slide "Reservation Staff Operations" "Confirm, cancel, and checkout actions."
Add-BulletList $slide @(
    "ReservationTable lines 38-46: Confirm button calls reservationService.confirm(id).",
    "api.js lines 202-205: PUT /api/reservations/{id}/confirm.",
    "ReservationService lines 77-84: only PENDING reservations become CONFIRMED.",
    "ReservationTable lines 48-56: Cancel calls DELETE /api/reservations/{id}.",
    "ReservationController lines 69-80: customers may cancel only their own reservation.",
    "ReservationTable lines 62-78: Checkout form sends reservationId, startMileage, and returnDate.",
    "api.js lines 215-219: POST /api/rentals/checkout."
) 52 110 835 180 14 | Out-Null
Add-CodeBox $slide "For staff/manager, checkout is the moment a reservation turns into an active rental. If the reservation is still PENDING, RentalService confirms it first." 80 338 800 56 13 | Out-Null
Add-FlowBox $slide "PENDING" 164 430 110 46 $Color.Orange | Out-Null
Add-Arrow $slide 274 453 348 453
Add-FlowBox $slide "CONFIRMED" 348 430 130 46 $Color.Blue | Out-Null
Add-Arrow $slide 478 453 552 453
Add-FlowBox $slide "CONVERTED" 552 430 130 46 $Color.Green | Out-Null
Add-Arrow $slide 682 453 756 453
Add-FlowBox $slide "ACTIVE Rental" 756 430 130 46 $Color.Navy | Out-Null

# Slide 9
$slide = $presentation.Slides.Add(9, 12)
Add-Header $slide "Rental Checkout and Check-In" "Backend rules that update cars, mileage, damage, and charges."
Add-BulletList $slide @(
    "RentalController lines 57-65 parses checkout JSON and forwards reservationId, mileage, returnDate, and optional customer ownership.",
    "RentalService lines 40-58 prevents duplicate rentals, checks ownership, confirms pending reservations, and rejects invalid statuses.",
    "RentalService lines 61-78 marks the car UNAVAILABLE, converts reservation, creates ACTIVE rental, and saves it.",
    "RentalController lines 67-93 handles check-in with mileage, damage, repair cost, and CASH/CARD choice.",
    "RentalService lines 85-113 validates ownership/status, sets end mileage, calculates overdue balance, and marks RETURNED.",
    "RentalService lines 114-132 updates car mileage and creates a Damage record when a description is present.",
    "RentalService lines 154-209 computes baseCharge + damageRepairCost = totalCharge."
) 48 112 860 196 14 | Out-Null
Add-CodeBox $slide "State path: Reservation CONFIRMED/ PENDING -> Rental ACTIVE -> Rental RETURNED -> Payment COMPLETED -> Car AVAILABLE again." 76 350 812 46 13 | Out-Null
Add-Card $slide "Mileage" "startMileage at checkout, endMileage at check-in; car mileage is updated on check-in." 76 424 240 74 $Color.Blue
Add-Card $slide "Damage" "Optional damageDescription creates a Damage row and adds repairCost to total charge." 360 424 240 74 $Color.Red
Add-Card $slide "Charges" "Daily rate * rental days + damage costs; late return can add outstanding balance." 644 424 240 74 $Color.Orange

# Slide 10
$slide = $presentation.Slides.Add(10, 12)
Add-Header $slide "Payment Communication" "Cash path and Stripe card path."
Add-BulletList $slide @(
    "api.js lines 228-250 defines payment calls: list, record, process, createIntent, confirmCard, stripeConfig.",
    "PaymentController lines 29-36 lists all payments for staff/manager or only customer-owned payments for customers.",
    "Cash: PaymentController lines 38-54 records payment through PaymentService.recordPayment(...).",
    "Card: lines 56-72 create Stripe PaymentIntent and save a PENDING payment row.",
    "Card confirmation: lines 78-91 retrieve Stripe intent; only succeeded intents mark local payment COMPLETED.",
    "Webhook: lines 94-108 can also mark succeeded/failed intents from Stripe events.",
    "PaymentService lines 38-47 and 69-80 complete payment and make the car AVAILABLE again."
) 52 108 850 190 14 | Out-Null
Add-FlowBox $slide "Returned Rental" 92 352 136 52 $Color.Navy | Out-Null
Add-Arrow $slide 228 378 292 378
Add-FlowBox $slide "Cash`nrecord/process" 292 352 136 52 $Color.Green | Out-Null
Add-Arrow $slide 428 378 492 378
Add-FlowBox $slide "Payment`nCOMPLETED" 492 352 136 52 $Color.Green | Out-Null
Add-Arrow $slide 628 378 692 378
Add-FlowBox $slide "Car`nAVAILABLE" 692 352 136 52 $Color.Blue | Out-Null
Add-CodeBox $slide "Card branch: create-intent -> Stripe Elements confirms card -> confirm-card/webhook -> markPaid() -> car available." 94 438 734 48 13 | Out-Null

# Slide 11
$slide = $presentation.Slides.Add(11, 12)
Add-Header $slide "Endpoint Map" "Frontend service methods and backend controllers."
$rows = @(
    @("Auth", "POST /api/auth/login", "authService.login", "Public"),
    @("Cars", "GET /api/cars, GET /api/cars/search", "carService.listAll/searchAvailable", "Public"),
    @("Cars Admin", "POST/PUT/DELETE /api/cars", "carService.create/update/delete", "MANAGER"),
    @("Reservations", "GET/POST/PUT/DELETE /api/reservations", "reservationService", "CUSTOMER/STAFF/MANAGER by route"),
    @("Rentals", "GET /api/rentals, POST /checkout, POST /{id}/checkin", "rentalService", "CUSTOMER/STAFF/MANAGER; checkout/checkin staff/manager"),
    @("Payments", "GET/POST /api/payments/...", "paymentService", "CUSTOMER/STAFF/MANAGER"),
    @("Manager", "GET /reports, branch vehicle CRUD", "managerService", "MANAGER"),
    @("Branches/Staff/Damages", "/api/branches, /api/staff, /api/damages", "branch/staff/damage services", "MANAGER or STAFF/MANAGER for damages")
)
$tableShape = $slide.Shapes.AddTable($rows.Count + 1, 4, 38, 108, 884, 320)
$table = $tableShape.Table
@("Area","Endpoint","Frontend caller","Access") | ForEach-Object -Begin {$c=1} -Process {
    $table.Cell(1,$c).Shape.TextFrame.TextRange.Text = $_
    $table.Cell(1,$c).Shape.Fill.ForeColor.RGB = $Color.Navy
    $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Color.RGB = $Color.White
    $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Size = 10
    $table.Cell(1,$c).Shape.TextFrame.TextRange.Font.Bold = -1
    $c++
}
for ($i=0; $i -lt $rows.Count; $i++) {
    for ($c=1; $c -le 4; $c++) {
        $table.Cell($i+2,$c).Shape.TextFrame.TextRange.Text = $rows[$i][$c-1]
        $table.Cell($i+2,$c).Shape.TextFrame.TextRange.Font.Size = 8.5
        $table.Cell($i+2,$c).Shape.Fill.ForeColor.RGB = if ($i % 2 -eq 0) { $Color.Light } else { $Color.White }
    }
}
$table.Columns.Item(1).Width = 110
$table.Columns.Item(2).Width = 300
$table.Columns.Item(3).Width = 230
$table.Columns.Item(4).Width = 244
Add-CodeBox $slide "Use this slide as the quick reference when explaining how a UI button turns into a backend route." 74 456 812 46 13 | Out-Null

# Slide 12
$slide = $presentation.Slides.Add(12, 12)
Add-Header $slide "seed.sql Purpose" "The seed file creates a complete demo scenario."
Add-BulletList $slide @(
    "application.properties sets spring.sql.init.mode=always and spring.sql.init.data-locations=classpath:seed.sql.",
    "On startup, Spring initializes the database from src/main/resources/seed.sql after Hibernate prepares/updates tables.",
    "The seed data provides login accounts, branch structure, fleet inventory, reservations, rentals, payments, and damage examples.",
    "Because continue-on-error=true is set, repeated startup may continue even if some insert rows already exist."
) 56 114 840 108 15 | Out-Null
Add-CodeBox $slide "Sample login accounts from seed.sql lines 1-7:`rManager:  abebe@crms.com / Test1234`rStaff:    kassa@crms.com / Test1234`rCustomer: john@example.com / Test1234`rStored values are BCrypt hashes, not plain passwords." 92 268 776 118 14 | Out-Null
Add-Card $slide "Presentation angle" "Explain seed.sql as the prepared dataset that makes all frontend screens show realistic data immediately." 176 422 610 70 $Color.Green

Add-TableSlide $presentation "seed.sql Lines 1-24" @(
    @("1", "Comment heading: sample login data starts here."),
    @("2", "Manager demo credential: abebe@crms.com with password Test1234."),
    @("3", "Staff demo credential: kassa@crms.com with password Test1234."),
    @("4", "Customer demo credential: john@example.com with password Test1234."),
    @("5", "Blank comment separator for readability."),
    @("6", "Clarifies the plain test password is only for testing."),
    @("7", "Clarifies database rows store BCrypt hashes."),
    @("9", "Section label for branch data."),
    @("10", "INSERT statement defines branch columns: ids, name, phone, address fields."),
    @("11-13", "Creates Main, Airport, and Downtown branches in Addis Ababa."),
    @("15", "Section label for manager data."),
    @("16", "INSERT statement defines manager columns and branch relationship."),
    @("17-18", "Creates two managers and links them to branch 1 and branch 3."),
    @("20", "Section label for staff data."),
    @("21", "INSERT statement defines staff identity, role, hashed password, branch, manager."),
    @("22-24", "Creates three staff records; Sara has no manager_id, Kassa/Marta report to managers.")
) | Out-Null

Add-TableSlide $presentation "seed.sql Lines 26-46" @(
    @("26", "Section label for customer data."),
    @("27-30", "Customer INSERT statement spans multiple lines because it includes address and balance fields."),
    @("31-32", "John Doe customer; zero outstanding balance, so he can reserve a car."),
    @("34-35", "Jane Smith customer; outstanding_balance is 150.00, so reservation eligibility can fail."),
    @("37-38", "Michael Brown customer; zero outstanding balance."),
    @("40", "Section label for fleet data."),
    @("41", "Car INSERT statement defines plate, make/model/year, mileage, availability, daily rate, type, and branch."),
    @("42", "Car 1 Toyota Corolla is AVAILABLE at Main Branch for 50/day."),
    @("43", "Car 2 Hyundai Tucson is AVAILABLE at Main Branch for 80/day."),
    @("44", "Car 3 Ford Ranger is AVAILABLE at Airport Branch for 100/day."),
    @("45", "Car 4 Mercedes C-Class is UNAVAILABLE at Downtown Branch for 150/day."),
    @("46", "Car 5 Kia Sportage is AVAILABLE at Downtown Branch for 75/day.")
) | Out-Null

Add-TableSlide $presentation "seed.sql Lines 48-70" @(
    @("48", "Section label for reservations."),
    @("49", "Reservation INSERT defines reservation date, pickup date, status, customer, and staff."),
    @("50", "Reservation 1 is CONVERTED for customer 1 and staff 1."),
    @("51", "Reservation 2 is PENDING for customer 2 and staff 2."),
    @("52", "Reservation 3 is CONVERTED for customer 3 and staff 3."),
    @("54", "Section label for reservation-to-car join table."),
    @("55", "car_reservation INSERT defines many-to-many link columns."),
    @("56-58", "Links reservations 1, 2, 3 to cars 1, 2, 4."),
    @("60", "Section label for rentals."),
    @("61-62", "Rental INSERT defines checkout/return, mileage, status, reservation, customer, car."),
    @("63", "Rental 1 is ACTIVE for reservation 1, customer 1, car 1."),
    @("64", "Rental 2 is ACTIVE for reservation 3, customer 3, car 4."),
    @("66", "Section label for payments."),
    @("67-68", "Payment INSERT defines date, amount, method, rental, Stripe intent, and status."),
    @("69", "Payment 1 is completed credit card payment for rental 1."),
    @("70", "Payment 2 is completed cash payment for rental 2.")
) | Out-Null

Add-TableSlide $presentation "seed.sql Lines 72-84" @(
    @("72", "Section label for damage data."),
    @("73", "Damage INSERT defines report date, repair cost, status, description, and rental."),
    @("74", "Damage 1 says rental 1 was returned without damage; repair cost is 0.00."),
    @("75", "Damage 2 records front bumper scratch for rental 2 with 500.00 repair cost."),
    @("77-78", "Updates manager rows so both managers have the BCrypt password hash for Test1234."),
    @("80-81", "Updates staff rows so all demo staff accounts use the same BCrypt hash."),
    @("83-84", "Updates customer rows so all demo customer accounts use the same BCrypt hash.")
) | Out-Null

# Slide 17
$slide = $presentation.Slides.Add($presentation.Slides.Count + 1, 12)
Add-Header $slide "Database Relationships from seed.sql" "How seeded rows connect to application operations."
Add-FlowBox $slide "Branch" 68 135 110 46 $Color.Navy | Out-Null
Add-Arrow $slide 178 158 238 158
Add-FlowBox $slide "Manager" 238 135 110 46 $Color.Green | Out-Null
Add-Arrow $slide 348 158 408 158
Add-FlowBox $slide "Staff" 408 135 110 46 $Color.Blue | Out-Null
Add-FlowBox $slide "Car" 68 270 110 46 $Color.Orange | Out-Null
Add-Arrow $slide 178 293 238 293
Add-FlowBox $slide "Reservation" 238 270 130 46 $Color.Green | Out-Null
Add-Arrow $slide 368 293 428 293
Add-FlowBox $slide "Rental" 428 270 110 46 $Color.Navy | Out-Null
Add-Arrow $slide 538 293 598 293
Add-FlowBox $slide "Payment" 598 270 110 46 $Color.Green | Out-Null
Add-Arrow $slide 538 322 598 390
Add-FlowBox $slide "Damage" 598 382 110 46 $Color.Red | Out-Null
Add-FlowBox $slide "Customer" 68 390 120 46 $Color.Blue | Out-Null
Add-Arrow $slide 188 413 238 310
Add-BulletList $slide @(
    "Branch owns cars and staff; managers are linked to branches.",
    "Customer creates reservation; reservation is linked to car through car_reservation.",
    "Checkout converts reservation to rental and uses the same customer/car.",
    "Check-in may create damage; payment completes the returned rental and releases the car."
) 650 120 245 210 13 | Out-Null

# Slide 18
$slide = $presentation.Slides.Add($presentation.Slides.Count + 1, 12)
Add-Header $slide "Suggested Demo Script" "A short order for presenting the project live."
Add-BulletList $slide @(
    "Start backend on port 8081 and frontend on port 5173.",
    "Open Available Cars: explain GET /api/cars/search and public access.",
    "Login as customer john@example.com / Test1234: explain POST /api/auth/login and JWT storage.",
    "Reserve an available car: explain POST /api/reservations and car availability update.",
    "Login as staff kassa@crms.com / Test1234: confirm/checkout reservation from Reservations page.",
    "Check in rental with mileage and optional damage; show total charge.",
    "Record cash payment or card payment; explain completed payment releases the car.",
    "Use seed.sql slides to explain why demo data already appears in each screen."
) 78 122 820 230 15 | Out-Null
Add-CodeBox $slide "Core sentence: The frontend sends JSON to Spring controllers; Spring Security validates JWT and role; services enforce business rules; repositories update MySQL; JSON responses update React state." 74 404 812 58 13 | Out-Null

# Slide 19
$slide = $presentation.Slides.Add($presentation.Slides.Count + 1, 12)
Add-Header $slide "Key Takeaways" "What to emphasize in the presentation."
Add-Card $slide "Communication" "A single request wrapper adds JSON headers, JWT auth, response parsing, and error handling for all frontend calls." 64 130 250 132 $Color.Blue
Add-Card $slide "Operations" "Reservation, rental, check-in, payment, and damage are state transitions guarded by service-layer rules." 354 130 250 132 $Color.Green
Add-Card $slide "Data" "seed.sql creates realistic demo rows and relationships so the project can be presented without manual setup." 644 130 250 132 $Color.Orange
Add-BulletList $slide @(
    "The backend, not the UI, decides permissions and data ownership.",
    "JWT contains role and userId; Spring Security maps it into request authentication.",
    "Payment completion is the point where a returned car becomes available again.",
    "The seed data demonstrates the whole lifecycle: branches -> cars -> reservations -> rentals -> payments/damages."
) 102 322 760 130 16 | Out-Null

$presentation.SaveAs($outputPath)
$presentation.Close()
$app.Quit()

Write-Output "Created $outputPath"
