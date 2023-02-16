-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 16 Lut 2023, 06:55
-- Wersja serwera: 10.4.27-MariaDB
-- Wersja PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `LibraryDB`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `t_books`
--

CREATE TABLE `t_books` (
  `id` int(11) NOT NULL,
  `author` varchar(50) NOT NULL,
  `title` varchar(50) NOT NULL,
  `isbn` varchar(13) NOT NULL,
  `loanable` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `t_books`
--

INSERT INTO `t_books` (`id`, `author`, `title`, `isbn`, `loanable`) VALUES
(1, 'B. A. Paris', 'Uwięziona', '100', '1'),
(2, 'Annie Ernaux', 'Bliscy', '101', '1'),
(3, 'Jaume Cabre', 'Spaleni w ogniu', '102', '1'),
(4, 'Rene Goscinny', 'Mikolajek', '103', '1'),
(5, 'Remigiusz Mróz', 'Kobalista', '104', '1'),
(6, 'Szczepan Twardoch', 'Chołod', '105', '1'),
(7, 'Delia Owens', 'Gdzie śpiewają raki', '106', '1'),
(8, 'Jo Nesbo', 'Krwawy księżyc', '107', '1'),
(9, 'Andrzej Sapkowski', 'Krew elfów', '108', '1'),
(10, 'Jacek Dukaj', 'Katedra', '109', '1'),
(11, 'Adam Wajrak', 'Na północ. Jak pokochałem Arktykę', '110', '1'),
(12, 'Isaac Asimov', 'Koniec Wieczności', '111', '1'),
(13, 'Carl Sagan', 'Kontakt', '112', '1'),
(14, 'Stanisław Lem', 'Solaris', '113', '1'),
(15, 'Dan Simmons', 'Hyperion. Tom 1. Hyperion', '114', '1'),
(16, 'C. L. Werner', 'Przeklęte miasto', '115', '1'),
(17, 'J.R.R. Tolkien', 'Hobbit, czyli tam i z powrotem', '116', '1'),
(18, 'Stephen King', 'Baśniowa opowieść', '117', '1'),
(19, 'Andrzej Sapkowski', 'Wiedźmin. Sezon burz', '118', '1'),
(20, 'Dan Simmons', 'Olimp', '119', '1'),
(21, 'J.R.R. Tolkien', 'Natura Śródziemia', '120', '0'),
(22, 'Witold Vargas', 'Bestiariusz japoński', '121', '1'),
(23, 'Jennifer L. Armentrout', 'Krew i popiół. Tom 1', '122', '1'),
(24, 'Brandon Sanderson', 'Zaginiony metal', '123', '1'),
(25, 'Craig Walls', 'Spring w akcji, wydanie 5', '124', '1'),
(26, 'Cay S. Horstamann', 'Java. Techniki zaawansowane. Wydanie XI', '125', '1'),
(27, 'Meyers Scott', 'Skuteczny nowoczesny C++', '126', '1'),
(28, 'Poe Edgar Allan', 'Poezje', '127', '1'),
(29, 'Tanya Talaga', 'Siedem opadłych piór', '128', '1'),
(30, 'Fiodor Dostojewski', 'Zbrodnia i kara', '129', '1'),
(31, 'H.P. Lovecraft', 'Zew Cthulhu', '130', '1'),
(32, 'Ian Rankin', 'W domu kłamstw', '131', '1');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `t_loans`
--

CREATE TABLE `t_loans` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `t_loans`
--

INSERT INTO `t_loans` (`id`, `user_id`, `book_id`, `start_date`, `end_date`) VALUES
(1, 2, 21, '2023-02-01', '2023-02-15');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `t_users`
--

CREATE TABLE `t_users` (
  `id` int(11) NOT NULL,
  `login` varchar(15) NOT NULL,
  `first_name` varchar(25) NOT NULL,
  `last_name` varchar(25) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `t_users`
--

INSERT INTO `t_users` (`id`, `login`, `first_name`, `last_name`, `password`, `role`) VALUES
(1, 'Admin', 'admin', 'admin', '106b84851d4e3c50e6c37cdcb625de42', 'ADMIN'),
(2, 'Testowy12', 'test', 'test', 'be255200ef01df876b24109ae22c3688', 'USER');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `t_books`
--
ALTER TABLE `t_books`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `t_loans`
--
ALTER TABLE `t_loans`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `t_users`
--
ALTER TABLE `t_users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `t_books`
--
ALTER TABLE `t_books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT dla tabeli `t_loans`
--
ALTER TABLE `t_loans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT dla tabeli `t_users`
--
ALTER TABLE `t_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
