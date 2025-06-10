
CREATE TABLE IF NOT EXISTS `hotels` (
   `hotel_id` BIGINT NOT NULL AUTO_INCREMENT,
   `name` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`hotel_id`))
    ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `rooms` (
    `room_id` BIGINT NOT NULL AUTO_INCREMENT,
    `hotel_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `capacity` TINYINT NOT NULL,
    `floor` TINYINT NOT NULL,
    `bathtub_flag` TINYINT(1) NOT NULL DEFAULT 1,
    `view_type` TINYINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`room_id`),
    INDEX `fk_Rooms_hotels_idx` (`hotel_id` ASC),
    CONSTRAINT `fk_Rooms_hotels`
    FOREIGN KEY (`hotel_id`)
    REFERENCES `hotels` (`hotel_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


insert into hotels values (100, 'Hilton');

insert into rooms (hotel_id, name, capacity, floor, bathtub_flag, view_type, created_at) values ( 100, 'WestWing 1102', 2, 11, 1, 0, now());
insert into rooms (hotel_id, name, capacity, floor, bathtub_flag, view_type, created_at) values ( 100, 'WestWing 1103', 2, 11, 1, 1, now());
insert into rooms (hotel_id, name, capacity, floor, bathtub_flag, view_type, created_at) values ( 100, 'WestWing 1104', 2, 11, 1, 2, now());


