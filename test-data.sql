-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Erstellungszeit: 16. Mai 2022 um 20:14
-- Server-Version: 10.4.21-MariaDB
-- PHP-Version: 8.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `gate-db`
--

-- --------------------------------------------------------

--
-- Daten für Tabelle `groups`
--

INSERT INTO `groups` (`gid`, `allowStudentsToQuit`, `allowStudentsToSignup`, `maxStudents`, `name`, `submissionGroup`, `lectureid`, `membersvisibletostudents`) VALUES
(11, b'1', b'1', 20, 'EIP Gruppe', b'1', 12, b'1'),
(222, b'1', b'1', 20, 'ProMo Gruppe', b'1', 10, b'1');


--
-- Daten für Tabelle `lectures`
--

INSERT INTO `lectures` (`id`, `gradingMethod`, `name`, `requiresAbhnahme`, `semester`, `description`, `allowselfsubscribe`) VALUES
(10, 'taskwise', 'Programmierung und Modellierung', b'1', 20201, '', b'1'),
(12, '', 'Einführung in die Programmierung', b'1', 20201, '', b'1');

--
-- Daten für Tabelle `participations`
--

INSERT INTO `participations` (`id`, `role`, `groupid`, `lectureid`, `uid`) VALUES
(4, 'NORMAL', 11, 12, 1),
(354, 'NORMAL', 222, 10, 1),
(4, 'NORMAL', 11, 12, 2),
(354, 'NORMAL', 222, 10, 2),
(4, 'NORMAL', 11, 12, 3),
(354, 'NORMAL', 222, 10, 3),
(4, 'NORMAL', 11, 12, 4),
(354, 'NORMAL', 222, 10, 4),
(4, 'NORMAL', 11, 12, 5),
(354, 'NORMAL', 222, 10, 5),
(4, 'NORMAL', 11, 12, 6),
(354, 'NORMAL', 222, 10, 6),
(4, 'NORMAL', 11, 12, 7),
(354, 'NORMAL', 222, 10, 7);


--
-- Daten für Tabelle `taskgroups`
--

INSERT INTO `taskgroups` (`taskGroupId`, `title`, `lectureid`, `task_group_id`) VALUES
(2, 'Promo Java', 10, 2),
(30, 'EIP Java', 12, 30);

--
-- Daten für Tabelle `tasks`
--

INSERT INTO `tasks` (`taskid`, `allowPrematureSubmissionClosing`, `allowSubmittersAcrossGroups`, `archiveFilenameRegexp`, `deadline`, `description`, `dynamicTask`, `featuredFiles`, `filenameRegexp`, `maxPoints`, `maxSubmitters`, `maxsize`, `minPointStep`, `showPoints`, `showTextArea`, `start`, `type`, `title`, `tutorsCanUploadFiles`, `taskgroupid`, `modelSolutionProvision`) VALUES
(3, b'0', b'0', '-', '2022-12-01 10:40:39', 'Schreiben Sie eine Java Klasse die  \"Hello World!\" ausgibt.\n\nHinweis: Laden Sie ihre Lösung als HelloWorld.java hoch', NULL, 'HelloWorld.java', 'HelloWorld\\.java', 5, 20, 10485760, 50, '2020-12-28 10:52:01', b'0', '2020-12-21 10:32:01', '', 'Hello World', b'0', 2, NULL),
(4, b'1', b'0', '-', '2022-12-28 10:53:05', 'Laden Sie eine Java Klasse hoch.\n\nHinweis: Laden Sie ihre Lösung als *.java hoch', NULL, '', '[A-Z][A-Za-z0-9_]+\\.java', 5, 20, 10485760, 50, '2120-12-28 10:53:05', b'0', '2020-12-21 10:53:05', '', 'Upload Java', b'0', 2, NULL),
(9, b'0', b'0', '', '2022-06-30 19:26:54', 'Schreiben Sie eine Java-Anwendung mit dem Namen SimpleForLoop welche in einer\nFOR-Schleife die Zahlen von 10 bis 1 herunter zählt und danach „START!“ ausgibt\n(ohne Anführungszeichen).\n\nDie hochzuladende Datei muss \"SimpleForLoop.java\" heißen.\n\nHinweis: Achten Sie für die Tests darauf, dass Sie keine Packages benutzen.\n', NULL, '', 'SimpleForLoop\\.java', 5, 20, 10485760, 10, '2022-07-05 19:26:54', b'0', '2022-04-24 19:26:54', '', 'for-Schleife', b'0', 30, NULL),
(10, b'0', b'0', '', '2022-07-31 23:16:47', 'Die (bisher unbewiesene) Collatz-Vermutung ist, dass jede Sequenz von natürlichen Zahlen, die wie folgt gebildet wird, die Teilsequenz 4, 2, 1 enthält:\r\n\r\ndie erste Zahl der Sequenz ist eine beliebige natürliche Zahl\r\nist die Zahl z der Sequenz gerade, so ist die nächste Zahl der Sequenz z/2.\r\nist die Zahl z der Sequenz ungerade, so ist die nächste Zahl der Sequenz 3z + 1.\r\nBeispiel für eine Collatz-Sequenz: 9, 28, 14, 7, 22, 11, 34, 17, . . .\r\n\r\nSchreiben Sie einen Algorithmus mithilfe einer Schleife in Java, der die sog. Collatzzahlen für ein n bis die Zahl “1” erreicht wird, simuliert. In Ihrem Programm soll eine Zahl pro Zeile ausgegeben werden, beginnend mit der eingegebenen Zahl. Zur Berechnung des Rests einer Division, können Sie den “%”-Operator verwenden.\r\n\r\nBenutzen Sie dafür die bereitgestellte Code-Vorlage.\r\n\r\nBeispiel mit n = 5\r\nAusgabe:\r\n5\r\n16\r\n8\r\n4\r\n2\r\n1\r\n\r\n\r\nDie hochzuladende Datei muss \"CollatzVermutung.java\" heißen und sich im default-Package befinden.', NULL, 'CollatzVermutung.java', 'CollatzVermutung\\.java', 5, 20, 10485760, 10, '2022-06-01 23:16:46', b'0', '2022-04-25 23:16:46', '', 'Collatz-Vermutung', b'0', 30, NULL);

--
-- Daten für Tabelle `tests`
--

INSERT INTO `tests` (`DTYPE`, `id`, `forTutors`, `giveDetailsToStudents`, `needsToRun`, `testDescription`, `testTitle`, `timeout`, `timesRunnableByStudents`, `mainClass`, `commandLineParameter`, `regularExpression`, `preparationshellcode`, `excludedFiles`, `minProzent`, `taskid`) VALUES
('CompileTest', 1, b'1', b'1', b'0', '', 'Syntax-Test', 5, 2, NULL, NULL, NULL, NULL, NULL, NULL, 3),
('JavaAdvancedIOTest', 2, b'1', b'1', b'0', NULL, 'Java IO Test', 15, 2, 'HelloWorld', NULL, 'Hello World', NULL, NULL, NULL, 3),
('CommentsMetricTest', 4, b'0', b'1', b'1', '', 'Kommentar-Metrik', 5, 2, NULL, NULL, NULL, NULL, '', 5, 3),
('CompileTest', 5, b'0', b'1', b'0', '', 'Syntax-Test', 5, 2, NULL, NULL, NULL, NULL, NULL, NULL, 9);

--
-- Daten für Tabelle `users`
--

INSERT INTO `users` (`uid`, `username`, `email`, `firstName`, `lastName`, `superUser`, `matrikelno`, `studiengang`, `lastLoggedIn`) VALUES
(1, `gateTester1`, `gatetester1@gmail.com`, `Tester1`, `Gate`, b'0', 11860611, 'Informatik', '2022-05-16 20:08:49'),
(2, `gateTester2`, `gatetester2@gmail.com`, `Tester2`, `Gate`, b'0', 11860612, 'Biologie', '2022-05-16 20:08:49'),
(3, `gateTester3`, `gatetester3@gmail.com`, `Tester3`, `Gate`, b'0', 11860613, 'Informatik plus Computerlinguistik', '2022-05-16 20:08:49'),
(4, `gateTester4`, `gatetester4@gmail.com`, `Tester4`, `Gate`, b'0', 11860614, 'Mathematik', '2022-05-16 20:08:49'),
(5, `gateTester5`, `gatetester5@gmail.com`, `Tester5`, `Gate`, b'0', 11860615, 'Sport', '2022-05-16 20:08:49'),
(6, `gateTester6`, `gatetester6@gmail.com`, `Tester6`, `Gate`, b'0', 11860616, 'Physik', '2022-05-16 20:08:49'),
(7, `gateTester7`, `gatetester7@gmail.com`, `Tester7`, `Gate`, b'0', 11860617, 'Informatik plus Mathematik', '2022-05-16 20:08:49');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;