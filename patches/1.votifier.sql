SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_votifier_totals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_votifier_totals` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_votifier_totals` (
  `player_id` INT NOT NULL ,
  `service_name` VARCHAR(64) NOT NULL ,
  `votes` INT(11) NULL DEFAULT 0 ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id_vot1`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_votifier_detailed`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_votifier_detailed` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_votifier_detailed` (
  `player_id` INT NOT NULL ,
  `service_name` VARCHAR(64) NOT NULL ,
  `time` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id_vot2`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
