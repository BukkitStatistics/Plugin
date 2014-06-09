SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_players` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_players` (
  `player_id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(16) NOT NULL ,
  `online` TINYINT(1) NOT NULL DEFAULT 0 ,
  `first_login` INT(11) NULL DEFAULT NULL ,
  `logins` INT(11) NULL DEFAULT 0 ,
  `login_time` INT(11) NULL DEFAULT 0 ,
  `playtime` BIGINT(11) NULL DEFAULT 0 ,
  `longest_session` BIGINT(11) NULL DEFAULT 0 ,
  PRIMARY KEY (`player_id`),
  UNIQUE KEY `name` (`name`) );


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_log_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_log_players` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_log_players` (
  `detailed_log_players_id` INT NOT NULL AUTO_INCREMENT ,
  `player_id` INT NOT NULL ,
  `time` INT(11) NOT NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `is_login` TINYINT(1) NULL DEFAULT 1 ,
  PRIMARY KEY (`detailed_log_players_id`) ,
  INDEX `$prefix_fk_player_id4_idx` (`player_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id4`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_distance_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_distances` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_distances` (
  `player_id` INT NOT NULL ,
  `foot` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  `boat` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  `minecart` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  `pig` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  `swim` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  `flight` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id1`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_entities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_entities` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_entities` (
  `entity_id` INT NOT NULL ,
  `tp_name` VARCHAR(100) NULL DEFAULT NULL ,
  PRIMARY KEY (`entity_id`) );


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_materials`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_materials` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_materials` (
  `material_id` VARCHAR(16) NOT NULL ,
  `tp_name` VARCHAR(45) NULL DEFAULT 'none' ,
  PRIMARY KEY (`material_id`) );


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_pvp_kills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_pvp_kills` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_pvp_kills` (
  `detailed_pvp_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `victim_id` INT NOT NULL ,
  `cause` VARCHAR(45) NULL ,
  `world` VARCHAR(255) NULL ,
  `x` INT NULL ,
  `y` INT NULL ,
  `z` INT NULL ,
  `time` INT(11) NULL ,
  PRIMARY KEY (`detailed_pvp_id`) ,
  INDEX `fk_player_id17_idx` (`player_id` ASC) ,
  INDEX `fk_player_id18_idx` (`victim_id` ASC) ,
  INDEX `fk_material_id7_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id17`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_player_id18`
    FOREIGN KEY (`victim_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id7`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_death_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_death_players` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_death_players` (
  `detailed_death_players_id` INT NOT NULL AUTO_INCREMENT ,
  `player_id` INT NOT NULL ,
  `cause` VARCHAR(45) NOT NULL ,
  `world` VARCHAR(255) NULL ,
  `x` INT NULL ,
  `y` INT NULL ,
  `z` INT NULL ,
  `time` INT NULL ,
  PRIMARY KEY (`detailed_death_players_id`) ,
  INDEX `$prefix_fk_player_id2_idx` (`player_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id2`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_placed_blocks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_placed_blocks` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_placed_blocks` (
  `detailed_placed_blocks_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `time` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`detailed_placed_blocks_id`) ,
  INDEX `$prefix_fk_player_id7_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id3_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id7`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id3`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_destroyed_blocks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_destroyed_blocks` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_destroyed_blocks` (
  `detailed_destroyed_blocks_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL ,
  `x` INT NULL ,
  `y` INT NULL ,
  `z` INT NULL ,
  `time` INT(11) NULL ,
  PRIMARY KEY (`detailed_destroyed_blocks_id`) ,
  INDEX `$prefix_fk_player_id6_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id8_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id6`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id8`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_dropped_items`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_dropped_items` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_dropped_items` (
  `detailed_dropped_items_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `time` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`detailed_dropped_items_id`) ,
  INDEX `$prefix_fk_player_id11_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id4_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id11`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id4`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_pickedup_items`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_pickedup_items` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_pickedup_items` (
  `detailed_pickedup_items_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `time` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`detailed_pickedup_items_id`) ,
  INDEX `$prefix_fk_player_id12_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id10_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id12`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id10`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_pve_kills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_pve_kills` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_pve_kills` (
  `detailed_pve_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `entity_id` INT NOT NULL ,
  `player_id` INT NOT NULL ,
  `cause` VARCHAR(45) NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `time` INT(11) NULL DEFAULT NULL ,
  `player_killed` TINYINT(1) NULL DEFAULT 0 ,
  PRIMARY KEY (`detailed_pve_id`) ,
  INDEX `$prefix_fk_player_id16_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_entity_id2_idx` (`entity_id` ASC) ,
  INDEX `$prefix_fk_material_id11_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id16`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_entity_id2`
    FOREIGN KEY (`entity_id` )
    REFERENCES `$dbname`.`$prefix_entities` (`entity_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id11`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_total_blocks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_total_blocks` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_total_blocks` (
  `total_blocks_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `destroyed` INT UNSIGNED NULL DEFAULT 0 ,
  `placed` INT UNSIGNED NULL DEFAULT 0 ,
  PRIMARY KEY (`total_blocks_id`) ,
  INDEX `$prefix_fk_player_id5_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id1_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id5`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id1`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_total_items`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_total_items` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_total_items` (
  `total_items_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `dropped` INT UNSIGNED NULL DEFAULT 0 ,
  `picked_up` INT UNSIGNED NULL DEFAULT 0 ,
  `used` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `crafted` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `smelted` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `broken` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `enchanted` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `repaired` INT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`total_items_id`) ,
  INDEX `$prefix_fk_player_id10_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id6_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id10`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id6`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_total_death_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_total_deaths` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_total_deaths` (
  `total_death_players_id` INT NOT NULL AUTO_INCREMENT ,
  `player_id` INT NOT NULL ,
  `cause` VARCHAR(45) NOT NULL ,
  `times` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`total_death_players_id`) ,
  INDEX `$prefix_fk_player_id3_idx` (`player_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id3`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_total_pvp_kills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_total_pvp_kills` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_total_pvp_kills` (
  `total_pvp_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `victim_id` INT NOT NULL ,
  `times` INT(10) NULL DEFAULT NULL ,
  PRIMARY KEY (`total_pvp_id`) ,
  INDEX `$prefix_fk_player_id14_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_player_id15_idx` (`victim_id` ASC) ,
  INDEX `$prefix_fk_material_id2_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id14`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_player_id15`
    FOREIGN KEY (`victim_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id2`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_total_pve_kills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_total_pve_kills` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_total_pve_kills` (
  `total_pve_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `entity_id` INT NOT NULL ,
  `player_id` INT NOT NULL ,
  `player_killed` INT(10) NULL DEFAULT 0 ,
  `creature_killed` INT(10) NULL DEFAULT 0 ,
  PRIMARY KEY (`total_pve_id`) ,
  INDEX `$prefix_fk_player_id13_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_entity_id1_idx` (`entity_id` ASC) ,
  INDEX `$prefix_fk_material_id9_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id13`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_entity_id1`
    FOREIGN KEY (`entity_id` )
    REFERENCES `$dbname`.`$prefix_entities` (`entity_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id9`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_detailed_used_items`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_detailed_used_items` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_detailed_used_items` (
  `detailed_used_items_id` INT NOT NULL AUTO_INCREMENT ,
  `material_id` VARCHAR(16) NOT NULL ,
  `player_id` INT NOT NULL ,
  `world` VARCHAR(255) NULL DEFAULT NULL ,
  `x` INT NULL DEFAULT NULL ,
  `y` INT NULL DEFAULT NULL ,
  `z` INT NULL DEFAULT NULL ,
  `time` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`detailed_used_items_id`) ,
  INDEX `$prefix_fk_player_id8_idx` (`player_id` ASC) ,
  INDEX `$prefix_fk_material_id5_idx` (`material_id` ASC) ,
  CONSTRAINT `$prefix_fk_player_id8`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `$prefix_fk_material_id5`
    FOREIGN KEY (`material_id` )
    REFERENCES `$dbname`.`$prefix_materials` (`material_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_server_statistics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_server_statistics` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_server_statistics` (
  `key` VARCHAR(64) NOT NULL ,
  `value` TEXT NOT NULL ,
  PRIMARY KEY (`key`) );


-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_misc_info_players`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_misc_info_players` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_misc_info_players` (
  `player_id` INT NOT NULL ,
  `is_op` TINYINT(1) NOT NULL DEFAULT 0 ,
  `is_banned` TINYINT(1) NOT NULL DEFAULT 0 ,
  `player_ip` VARCHAR(32) NOT NULL ,
  `gamemode` TINYINT(2) NOT NULL DEFAULT 0 ,
  `exp_level` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `exp_perc` FLOAT(3,2) UNSIGNED NOT NULL DEFAULT '0.00',
  `exp_total` INT(8) UNSIGNED NOT NULL DEFAULT 0 ,
  `food_level` TINYINT UNSIGNED NOT NULL DEFAULT 0 ,
  `health` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `armor_rating` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `fish_caught` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `times_kicked` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `eggs_thrown` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `food_eaten` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `arrows_shot` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `damage_taken` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `times_jumped` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `words_said` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `commands_sent` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `beds_entered` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `portals_entered` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `kill_streak` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `max_kill_streak` INT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id9`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_player_inventories`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_player_inventories` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_player_inventories` (
  `player_id` INT NOT NULL ,
  `hotbar` TEXT NULL ,
  `row_one` TEXT NULL ,
  `row_two` TEXT NULL ,
  `row_three` TEXT NULL ,
  `armor` TEXT NULL ,
  `potion_effects` TEXT NULL ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `$prefix_fk_player_id19`
    FOREIGN KEY (`player_id` )
    REFERENCES `$dbname`.`$prefix_players` (`player_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

USE `$dbname` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
