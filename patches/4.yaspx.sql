-- New values for the server statistics

INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("os.name", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("os.version", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("os.arch", "");

INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.version", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.vendor", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.vendor.url", "");

INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.vm.version", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.vm.vendor", "");
INSERT INTO `$prefix_server_statistics` (`key` , `value`) VALUES ("java.vm.name", "");

-- Option to stop tracking player stats when vanish is active

INSERT INTO `$prefix_settings` (`key` , `value`) VALUES ("hook.vanish.no_tracking", "0");
INSERT INTO `$prefix_settings` (`key` , `value`) VALUES ("merged_data_tracking", "0");

-- Alter existing tables

ALTER TABLE `$prefix_player_inventories` ADD `selected_item` SMALLINT UNSIGNED NOT NULL DEFAULT 0;

-- Create new tables

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_player_locations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_player_locations` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_player_locations` (
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `biome` VARCHAR(32) NULL DEFAULT NULL ,
  `humidity` FLOAT NOT NULL DEFAULT 0.0 ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id20`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
