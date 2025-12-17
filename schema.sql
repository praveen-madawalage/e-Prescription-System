DROP DATABASE IF EXISTS hospital_sys;
CREATE DATABASE hospital_sys CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_sys;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(40) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('DOCTOR', 'PHARMACIST', 'PATIENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    stock INT NOT NULL DEFAULT 0,
    unit VARCHAR(20) NOT NULL DEFAULT 'tablet',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prescriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    secure_token CHAR(6) NOT NULL UNIQUE,
    status ENUM('PENDING', 'DISPENSED') NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (patient_id) REFERENCES users(id)
);

CREATE TABLE prescription_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    dosage_instructions VARCHAR(255),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);

INSERT INTO medicines (name, description, stock, unit) VALUES
('Amoxicillin 500mg', 'Antibiotic capsules', 40, 'capsule'),
('Ibuprofen 200mg', 'Pain reliever tablets', 120, 'tablet'),
('Metformin 500mg', 'Blood sugar control', 75, 'tablet'),
('Lisinopril 10mg', 'Blood pressure management', 60, 'tablet'),
('Albuterol Inhaler', 'Bronchodilator inhaler', 15, 'inhaler');


