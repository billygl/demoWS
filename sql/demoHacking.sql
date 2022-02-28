CREATE DATABASE demoHacking;
USE demoHacking;

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL COMMENT 'no hashing',
  `role` varchar(128) NOT NULL COMMENT 'admin,user',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `description` TEXT NOT NULL,
  `price` float NOT NULL,
  `image_url` varchar(255) NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_id_UNIQUE` (`product_id`),
  UNIQUE KEY `title_UNIQUE` (`title`)
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

INSERT INTO users (user, password, role) VALUES ('admin@mail.com', '12345678', 'admin');
INSERT INTO users (user, password, role) VALUES ('user@mail.com', '12345678', 'user');
INSERT INTO accounts (balance, name, number, user_id) VALUES 
(100, 'Ahorro Soles', '19345654321012', 1),
(1000, 'Ahorro Soles', '19345654321013', 2),
(2000, 'Ahorro Soles', '19345654321014', 2)
;
INSERT INTO movements (amount, account_id, created_at) VALUES 
(40, 1, '2022-02-24 09:00:00'),
(60, 1, '2022-02-25 09:00:00'),
(300, 2, '2022-02-26 09:00:00'),
(700, 2, '2022-02-27 09:00:00'),
(3000, 2, '2022-02-26 09:00:00'),
(7000, 2, '2022-02-27 09:00:00')
;
INSERT INTO products (title, description, price, image_url) VALUES 
('Echo Dot', 'Parlante Inteligente con Alexa', 140, NULL),
('Kindle Paperwhite', 'Pantalla de 6.8 pulgadas', 420, NULL),
('Apple iPad', 'iPad de 10.2 pulgadas', 1150, NULL)
;