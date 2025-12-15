-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Anamakine: localhost:8889
-- Üretim Zamanı: 11 Ara 2025, 11:11:22
-- Sunucu sürümü: 8.0.40
-- PHP Sürümü: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `hotel_db`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `reservations`
--

CREATE TABLE `reservations` (
  `id` int NOT NULL,
  `user_id` int DEFAULT NULL,
  `room_number` varchar(10) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tablo döküm verisi `reservations`
--

INSERT INTO `reservations` (`id`, `user_id`, `room_number`, `start_date`, `end_date`, `total_price`, `status`) VALUES
(11, 9, '1', '2025-12-20', '2025-12-25', 5000, 'İPTAL'),
(12, 11, '2', '2025-12-20', '2025-12-25', 5000, 'İPTAL'),
(13, 13, '3', '2025-12-20', '2025-12-25', 5000, 'Beklemede');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `rooms`
--

CREATE TABLE `rooms` (
  `room_number` varchar(10) NOT NULL,
  `room_type` varchar(50) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tablo döküm verisi `rooms`
--

INSERT INTO `rooms` (`room_number`, `room_type`, `price`, `capacity`, `status`) VALUES
('1', 'Standart', 1000, 1, 'MÜSAİT'),
('10', 'Aile', 2000, 4, 'MÜSAİT'),
('2', 'Standart', 1000, 2, 'MÜSAİT'),
('3', 'Standart', 1000, 3, 'REZERVE'),
('4', 'Suit', 2000, 1, 'MÜSAİT'),
('5', 'Suit', 2000, 2, 'MÜSAİT'),
('6', 'Suit', 2000, 3, 'MÜSAİT'),
('7', 'Aile', 2000, 1, 'MÜSAİT'),
('8', 'Aile', 2000, 2, 'MÜSAİT'),
('9', 'Aile', 2000, 3, 'MÜSAİT');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `settings`
--

CREATE TABLE `settings` (
  `id` int NOT NULL DEFAULT '1',
  `max_reservation_days` int DEFAULT '30',
  `default_daily_fine` decimal(10,2) DEFAULT '5.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tablo döküm verisi `settings`
--

INSERT INTO `settings` (`id`, `max_reservation_days`, `default_daily_fine`) VALUES
(1, 30, 5.00);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `users`
--

CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `tc_no` varchar(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('CUSTOMER','STAFF') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tablo döküm verisi `users`
--

INSERT INTO `users` (`user_id`, `tc_no`, `username`, `email`, `password_hash`, `first_name`, `last_name`, `phone`, `role`, `created_at`) VALUES
(2, '22233344455', 'staff1', 'staff@hotel.com', '23', 'Admin', 'User', '05559876542', 'STAFF', '2025-12-10 13:58:58'),
(9, '11111111111', 'Irmak1', 'irmakKizil@.com', '123', 'Irmak', 'Kızıl', '05555555555', 'CUSTOMER', '2025-12-10 19:04:52'),
(11, '11111111112', 'Ayça1', 'aycaeren@.com', '123', 'Ayça', 'Eren', '05444444444', 'CUSTOMER', '2025-12-10 19:25:32'),
(13, '11111111113', 'Rabia1', 'rabiacirik@.com', '123', 'Rabia', 'Cırık', '05333333333', 'CUSTOMER', '2025-12-10 19:29:05'),
(15, '11111111114', 'Veli1', 'velicin@.com', '123', 'Veli', 'Cin', '05222222222', 'CUSTOMER', '2025-12-10 19:30:33');

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `room_number` (`room_number`);

--
-- Tablo için indeksler `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_number`);

--
-- Tablo için indeksler `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`id`);

--
-- Tablo için indeksler `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `tc_no` (`tc_no`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- Tablo için AUTO_INCREMENT değeri `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Dökümü yapılmış tablolar için kısıtlamalar
--

--
-- Tablo kısıtlamaları `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`room_number`) REFERENCES `rooms` (`room_number`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
