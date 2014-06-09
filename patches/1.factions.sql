SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_hook_factions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_hook_factions` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_hook_factions` (
  `player_id` INT NOT NULL ,
  `faction` VARCHAR(255) NOT NULL DEFAULT 'none' ,
  `current_position` VARCHAR(255) NULL ,
  `current_power` FLOAT NOT NULL DEFAULT 0.0 ,
  `max_power` FLOAT NOT NULL DEFAULT 0.0 ,
  `role` VARCHAR(255) NULL ,
  `title` VARCHAR(25) NULL ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_factns1`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
