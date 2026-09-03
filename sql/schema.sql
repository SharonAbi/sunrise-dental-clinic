-- Sunrise Dental Clinic - schema and seed data
-- Run this once against a fresh MySQL server:
--   mysql -u root -p < sql/schema.sql

CREATE DATABASE IF NOT EXISTS sunrise_dental;
USE sunrise_dental;

CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'STAFF'
);

CREATE TABLE IF NOT EXISTS dentists (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS treatments (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)  NOT NULL,
    fee  DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS patients (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    address        VARCHAR(255),
    contact_number VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number  VARCHAR(30) NOT NULL UNIQUE,
    patient_id          INT NOT NULL,
    dentist_id          INT NOT NULL,
    treatment_id        INT NOT NULL,
    appointment_date    DATE NOT NULL,
    appointment_time    TIME NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    FOREIGN KEY (patient_id)   REFERENCES patients(id),
    FOREIGN KEY (dentist_id)   REFERENCES dentists(id),
    FOREIGN KEY (treatment_id) REFERENCES treatments(id)
);

CREATE TABLE IF NOT EXISTS bills (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id     INT NOT NULL,
    consultation_fee   DECIMAL(10,2) NOT NULL,
    treatment_fee      DECIMAL(10,2) NOT NULL,
    discount_amount    DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount       DECIMAL(10,2) NOT NULL,
    insurance_applied  BOOLEAN NOT NULL DEFAULT FALSE,
    generated_at       DATETIME NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- Seed data -------------------------------------------------------------

-- Default staff login: admin / admin123
-- SHA2(x, 256) here matches PasswordUtil.hash() in the Java code.
INSERT INTO users (username, password_hash, full_name, role)
VALUES ('admin', SHA2('admin123', 256), 'System Administrator', 'ADMIN');

INSERT INTO dentists (name, specialization) VALUES
    ('Dr. Nimal Perera', 'General Dentistry'),
    ('Dr. Amaya Silva', 'Orthodontics'),
    ('Dr. Ruwan Fernando', 'Oral Surgery');

INSERT INTO treatments (name, fee) VALUES
    ('Consultation Only', 0.00),
    ('Scaling & Polishing', 3500.00),
    ('Tooth Extraction', 5000.00),
    ('Root Canal Treatment', 15000.00),
    ('Filling', 4000.00),
    ('Braces Fitting', 45000.00);
