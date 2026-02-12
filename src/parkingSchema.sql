CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    status TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicles (
    plate_num TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    is_vip BOOLEAN DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tickets (
    ticket_id TEXT PRIMARY KEY,
    plate_num TEXT NOT NULL,
    spot_id TEXT NOT NULL,
    entry_time TEXT NOT NULL,
    FOREIGN KEY (plate_num) REFERENCES vehicles(plate_num),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)
);

CREATE TABLE IF NOT EXISTS fines (
    fine_id INTEGER PRIMARY KEY AUTOINCREMENT,
    plate_num TEXT NOT NULL,
    amount REAL NOT NULL,
    reason TEXT NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (plate_num) REFERENCES vehicles(plate_num)
);

CREATE TABLE IF NOT EXISTS receipts (
    receipt_id TEXT PRIMARY KEY,
    ticket_id TEXT NOT NULL,
    plate_num TEXT NOT NULL,
    spot_id TEXT NOT NULL,
    entry_time TEXT NOT NULL,
    exit_time TEXT NOT NULL,
    hours_parked REAL NOT NULL,
    parking_fee REAL NOT NULL,
    fine_amount REAL NOT NULL,
    total_paid REAL NOT NULL,
    payment_method TEXT NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)
);

CREATE TABLE IF NOT EXISTS admin_settings (
    setting_key TEXT PRIMARY KEY,
    setting_value TEXT NOT NULL
);

INSERT OR IGNORE INTO admin_settings (setting_key, setting_value) VALUES ('fine_strategy', 'FIXED');