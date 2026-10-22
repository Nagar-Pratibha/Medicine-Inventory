-- RUN THIS SCRIPT
-- CHANGE THE DB NAME ACCORDINGLY
-- Default Creds for
-- ADMIN: admin@meds.com / AdminPass123!
-- USER1: papa.jones@meds.com / UserPass123!
-- USER2: jane.smith@meds.com / UserPass123!
-- For rest of the users, try UserPass123!

create database meds2;
-- drop database meds2;
use meds2;

CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME,
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255),
  password VARCHAR(255),
  role ENUM('ADMIN','USER') NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses (
  address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  city VARCHAR(255) NOT NULL,
  country VARCHAR(255) NOT NULL,
  is_default BIT(1) NOT NULL,
  line1 VARCHAR(255) NOT NULL,
  line2 VARCHAR(255),
  postal_code VARCHAR(255) NOT NULL,
  state VARCHAR(255) NOT NULL,
  user_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS carts (
  cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  user_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS medicines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  composition_text TEXT,
  description TEXT,
  manufacture_name VARCHAR(255),
  name VARCHAR(255) NOT NULL,
  pack_size VARCHAR(255),
  price DECIMAL(38,2) NOT NULL,
  stock INT
);

CREATE TABLE IF NOT EXISTS cart_items (
  cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  quantity INT NOT NULL,
  cart_id BIGINT NOT NULL,
  medicine_id BIGINT NOT NULL,
  FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
  FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  status ENUM('CANCELLED','DELIVERED','PENDING','SHIPPED') NOT NULL,
  total_amount DECIMAL(38,2) NOT NULL,
  updated_at DATETIME NOT NULL,
  address_id BIGINT NOT NULL,
  user_id INT NOT NULL,
  FOREIGN KEY (address_id) REFERENCES addresses(address_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  quantity INT NOT NULL,
  medicine_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE, -- if med is deleted, delete that row from order-items also
  FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Insert data into users
INSERT INTO users (id, created_at, email, name, password, role) VALUES
(1,'2025-09-26 16:30:14.870981','admin@meds.com','Admin User','$2a$10$.mZTWx/eIdQNr1HgLGCE2.DO1rxnINSvLmTi.WaQ06rE5aUEsQWfK','ADMIN'),
(2,'2025-09-26 16:31:03.546119','jane.smith@meds.com','Jane Smith','$2a$10$mVpd9vydXnCvr77Spt65Hern7lLiq7hyrT5mKxn4YJ7AYrkHjSZ92','USER'),
(3,'2025-09-26 16:31:13.469503','sam.brown@meds.com','Sam Brown','$2a$10$ts5yhMJy648WkAfC55K/5e.rCeJ9eS/KXecoTKW9OLSRO4fV7LOmq','USER'),
(4,'2025-09-26 16:31:22.401222','alice.ray@meds.com','Alice Ray','$2a$10$BlkggeF1iJ5.PzwkYB0yiOcK04zulSMsAeHQXPI.46UwFsaBQemfe','USER'),
(5,'2025-09-26 16:31:27.486377','bob.lee@meds.com','Bob Lee','$2a$10$DnxR1menJtJgm/jsHI2zLexkhRqJGdGSj2UBeyl4VOavj/2tcR.uS','USER'),
(6,'2025-09-26 16:31:13.469503','papa.jones@meds.com','Papa Jones','$2a$10$ts5yhMJy648WkAfC55K/5e.rCeJ9eS/KXecoTKW9OLSRO4fV7LOmq','USER');

-- Insert data into addresses
INSERT INTO addresses (address_id, city, country, is_default, line1, line2, postal_code, state, user_id) VALUES
(1,'Anytown','USA',1,'123 Main St','Apt 1A','90210','CA',2),
(2,'Big City','USA',0,'456 Oak Ave',NULL,'10001','NY',3),
(3,'Metropolis','USA',0,'789 Pine Ln','Suite 500','07001','NJ',3),
(4,'Smallville','USA',1,'101 Test Blvd',NULL,'73301','TX',5),
(5,'Hill Valley','USA',1,'202 Rural Rd',NULL,'98004','WA',6),
(6,'Jammu','India',1,'Jammu','','180001','Jammu and Kashmir',3),
(7,'SOmewhere','India',0,'Somewhere','only I know','182013','Somewhere',3);

-- Insert data into carts
INSERT INTO carts (cart_id, created_at, updated_at, user_id) VALUES
(1,'2025-09-01 10:00:00','2025-09-26 14:00:00',2),
(2,'2025-09-25 09:00:00','2025-09-25 09:00:00',4),
(3,'2025-09-26 14:30:00','2025-09-26 14:30:00',5),
(4,'2025-09-27 15:36:39.830388','2025-09-27 15:36:39.830388',3),
(5,'2025-09-28 00:14:55.506489','2025-09-28 00:14:55.506489',6);


-- Insert data into medicines
INSERT INTO medicines (id, composition_text, description, manufacture_name, name, pack_size, price, stock) VALUES
(101, 'Paracetamol 500mg', 'Common pain relief and fever reducer.', 'MedCorp Pharmaceuticals', 'Paracetamol 500mg Tab', '10 Tablets', 5.50, 500),
(102,'Amoxicillin 250mg','Antibiotic for bacterial infections.','GenMed Labs','Amoxicillin 250mg Cap','15 Capsules',12.00,25),
(103,'Atorvastatin 20mg','Used to lower cholesterol.','HeartCare Inc.','Atorvastatin 20mg','30 Tablets',95.00,150),
(104,'Ibuprofen 400mg','Anti-inflammatory.','ReliefFast Co.','Ibuprofen 400mg Gel','20 Capsules',8.75,0),
(105,'Cholecalciferol 1000 IU','Dietary supplement.','VitaLife','Vitamin D3 1000 IU','60 Softgels',20.00,300),
(106,'Cetirizine 5mg/ml','Childrens antihistamine.','KiddieMeds','Allergy-Go Syrup','100 ml Bottle',45.00,100),
(107,'Paracetamol, Phenylephrine, Caffeine','Multi-symptom cold relief.','WinterHealth','Cold & Flu Max Relief','12 Tablets',15.00,250),
(108,'Dextromethorphan HBr','General cough relief.','LowCost Meds','Generic Cough Suppressant','100 ml Bottle',7.50,400),
(109,'Insulin Lispro','Fast-acting insulin.','Diabetes Solutions','Insulin Lispro Pen','3ml Pen',550.00,50),
(110,'Essential Vitamins & Minerals','General health maintenance.','Wellness Brands','Multi-Vitamin Daily','90 Tablets',30.00,600),
(111,'Zinc Oxide 20%','Topical cream for skin protection.','SkinSoothing','Zinc Oxide Cream','50g Tube',18.00,120),
(112,'Loperamide Hydrochloride 2mg','Anti-diarrheal.','TummyCare','Loperamide 2mg','6 Tablets',4.99,350),
(113,'Warfarin Sodium 5mg','Blood thinner.','ClotStop Pharma','Warfarin 5mg','28 Tablets',120.00,80),
(114,'Prednisone 10mg','Corticosteroid.','SteroidPro','Prednisone 10mg','10 Tablets',85.00,90),
(115,'Diclofenac Diethylamine','Topical pain relief patches.','PatchTec','PainRelief Ultra Patches','5 Patches',35.00,150),
(116,'Omeprazole 20mg','Acid reflux treatment.','AcidGone','Omeprazole 20mg','14 Capsules',22.50,280),
(118,'Cetirizine Hydrochloride 10mg','Non-drowsy allergy relief.','ClearBreathe','Cetirizine 10mg','7 Tablets',6.50,450),
(119,'Magnesium Glycinate','Sleep and muscle support.','CalmMind','Magnesium Glycinate','100 Capsules',28.00,200),
(120,'Penicillin V 500mg','Common antibiotic.','OldSchool Meds','Penicillin V 500mg','14 Tablets',10.00,15);


-- Insert data into cart_items
INSERT INTO cart_items (cart_item_id, quantity, cart_id, medicine_id) VALUES
(1,12,1,101),
(2,1,1,103),
(3,2,1,104),
(4,10,3,110),
(5,3,1,102),
(6,1,1,119);

-- Insert data into orders
INSERT INTO orders (order_id, created_at, status, total_amount, updated_at, address_id, user_id) VALUES
(1001,'2025-09-05 12:00:00','DELIVERED',185.00,'2025-09-08 10:00:00',1,2),
(1002,'2025-09-10 15:00:00','SHIPPED',500.00,'2025-09-11 09:00:00',2,3),
(1003,'2025-09-10 15:00:00','DELIVERED',512.00,'2025-09-11 09:00:00',2,3),
(1004,'2025-08-20 18:00:00','CANCELLED',1500.00,'2025-09-27 11:04:43.596293',4,5),
(1005,'2025-09-20 09:00:00','CANCELLED',60.00,'2025-09-20 09:15:00',3,3),
(1006,'2025-09-15 14:00:00','DELIVERED',120.00,'2025-09-17 16:00:00',5,6),
(1007,'2025-09-27 16:17:58.474763','SHIPPED',30.00,'2025-09-27 17:09:11.060334',2,3),
(1008,'2025-09-27 16:22:25.396797','SHIPPED',9.45,'2025-09-27 17:09:12.479693',6,3),
(1009,'2025-09-27 18:23:25.366105','CANCELLED',27.56,'2025-09-27 18:23:45.163821',7,3),
(1010,'2025-09-27 23:55:44.208744','PENDING',89.25,'2025-09-27 23:55:44.208744',3,3);

-- Insert data into order_items
INSERT INTO order_items (order_item_id, quantity, medicine_id, order_id) VALUES
(5001,3,107,1001),
(5002,1,118,1001),
(5003,1,109,1002),
(5004,1,112,1003),
(5005,10,101,1004),
(5006,5,103,1004),
(5007,20,105,1004),
(5008,2,116,1005),
(5009,1,113,1006),
(5010,2,101,1007),
(5011,1,102,1007),
(5012,1,101,1008),
(5013,3,104,1009),
(5014,2,105,1010),
(5015,1,106,1010);


