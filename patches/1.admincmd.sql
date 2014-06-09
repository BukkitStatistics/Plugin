SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_hook_admincmd`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_hook_admincmd` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_hook_admincmd` (
  `player_id` INT NOT NULL ,
  `afk` TINYINT(1) NOT NULL DEFAULT 0 ,
  `vanished` TINYINT(1) NOT NULL DEFAULT 0 ,
  `ban_reason` TEXT NULL ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_admcmd1`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
