SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_hook_vaults`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_hook_vault` (
  `detailed_vault_id` INT NOT NULL AUTO_INCREMENT ,
  `player_id` INT NOT NULL ,
  `balance` FLOAT NOT NULL DEFAULT 0.0 ,
  `time` INT(11) NOT NULL,
  PRIMARY KEY (`detailed_vault_id`) ,
  INDEX `$prefix_fk_player_vault2_idx` (`player_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_vault2`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;