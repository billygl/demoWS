-- DROP DATABASE demoHacking;

CREATE DATABASE demoHacking;
USE demoHacking;

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL COMMENT 'no hashing',
  `document_id` varchar(50) NOT NULL,
  `role` varchar(128) NOT NULL COMMENT 'admin,user,other',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `document_id_UNIQUE` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `description` TEXT NOT NULL,
  `price` float NOT NULL,
  `image_url` varchar(255) NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_id_UNIQUE` (`product_id`),
  UNIQUE KEY `title_UNIQUE` (`title`),
  FOREIGN KEY (`user_id`) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `accounts` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `balance` float NOT NULL,
  `name` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`account_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(user_id),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `movements` (
  `movement_id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` float NOT NULL COMMENT 'positive and negative values',
  `account_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`movement_id`),
  FOREIGN KEY (`account_id`) REFERENCES accounts(account_id),
  UNIQUE KEY `movement_id_UNIQUE` (`movement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tokens` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,  
  `token` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`token_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(user_id),
  UNIQUE KEY `token_id_UNIQUE` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO users (user, password, document_id, role) VALUES 
('admin@mail.com', '12345678', '91234567', 'admin'),
('user@mail.com', '12345678', '81234567', 'user'),
('other@mail.com', '12345678', '71234567', 'other'),
('other1@mail.com', '12345678', '61234567', 'other'),
('other2@mail.com', '12345678', '51234567', 'other'),
('other3@mail.com', '12345678', '41234567', 'other'),
('other4@mail.com', '12345678', '31234567', 'other'),
('other5@mail.com', '12345678', '21234567', 'other'),
('other6@mail.com', '12345678', '11234567', 'other'),
('other7@mail.com', '12345678', '01234567', 'other'),
('other8@mail.com', '12345678', '12234567', 'other')
;
INSERT INTO accounts (balance, name, number, user_id) VALUES 
(100, 'Ahorro Soles', '19345654321012', 1),
(1000, 'Ahorro Soles', '19345654321013', 2),
(2000, 'Ahorro Soles', '19345654321014', 3),
(2000, 'Ahorro Soles', '19345654321015', 4),
(2000, 'Ahorro Soles', '19345654321016', 5),
(2000, 'Ahorro Soles', '19345654321017', 6),
(2000, 'Ahorro Soles', '19345654321018', 7),
(2000, 'Ahorro Soles', '19345654321019', 8),
(2000, 'Ahorro Soles', '19345654321020', 9),
(2000, 'Ahorro Soles', '19345654321021', 10),
(2000, 'Ahorro Soles', '19345654321022', 11)
;
INSERT INTO movements (amount, account_id, created_at) VALUES 
(40, 1, '2022-02-24 09:00:00'),
(60, 1, '2022-02-25 09:00:00'),
(300, 2, '2022-02-26 09:00:00'),
(700, 2, '2022-02-27 09:00:00'),
(3000, 2, '2022-02-26 09:00:00'),
(7000, 2, '2022-02-27 09:00:00')
;
INSERT INTO products (title, description, price, image_url, user_id) VALUES 
('Echo Dot', 'Parlante Inteligente con Alexa', 141, NULL, 1),
('Echo Dot 2', 'Parlante Inteligente con Alexa', 142, NULL, 2),
('Echo Dot 3', 'Parlante Inteligente con Alexa', 143, NULL, 3),
('Kindle Paperwhite', 'Pantalla de 6.8 pulgadas', 421, NULL, 1),
('Kindle Paperwhite 2', 'Pantalla de 6.8 pulgadas', 422, NULL, 2),
('Kindle Paperwhite 3', 'Pantalla de 6.8 pulgadas', 423, NULL, 3),
('Apple iPad', 'iPad de 10.2 pulgadas', 1151, NULL, 1),
('Apple iPad 2', 'iPad de 10.2 pulgadas', 1152, NULL, 2),
('Apple iPad 3', 'iPad de 10.2 pulgadas', 1153, NULL, 3)
;