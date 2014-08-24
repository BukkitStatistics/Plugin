SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

SET storage_engine=InnoDB;

CREATE SCHEMA IF NOT EXISTS `$dbname` DEFAULT CHARACTER SET utf8 ;
USE `$dbname` ;


-- -----------------------------------------------------
-- Create new server_statistics table
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_server_statistics` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_server_statistics` (
  `server_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `server_name` TEXT NOT NULL,
  `server_ip` TEXT NOT NULL,
  `server_motd` TEXT NOT NULL ,
  `server_port` INT UNSIGNED NOT NULL,
  `bukkit_version` TEXT NOT NULL,
  `current_uptime` INT(11) UNSIGNED NOT NULL DEFAULT 0,
  `first_startup` INT(11) UNSIGNED NOT NULL,
  `free_memory` INT(11) UNSIGNED NOT NULL,
  `java.vendor` TEXT NOT NULL,
  `java.vendor.url` TEXT NOT NULL,
  `java.version` TEXT NOT NULL,
  `java.vm.name` TEXT NOT NULL,
  `java.vm.vendor` TEXT NOT NULL,
  `java.vm.version` TEXT NOT NULL,
  `last_shutdown` INT(11) UNSIGNED NOT NULL DEFAULT 0,
  `last_startup` INT(11) UNSIGNED NOT NULL,
  `max_players_online` INT UNSIGNED NOT NULL DEFAULT 0,
  `max_players_online_time` INT(11) UNSIGNED NOT NULL DEFAULT 0,
  `os.arch` TEXT NOT NULL,
  `os.name` TEXT NOT NULL,
  `os.version` TEXT NOT NULL,
  `players_allowed` INT UNSIGNED NOT NULL,
  `plugins` INT UNSIGNED NOT NULL,
  `server_time` INT(11) UNSIGNED NOT NULL,
  `ticks_per_second` INT UNSIGNED NOT NULL,
  `total_memory` INT(11) UNSIGNED NOT NULL,
  `total_uptime` INT(11) UNSIGNED NOT NULL DEFAULT 0,
  `weather` TEXT NOT NULL,
  `weather_duration` INT(11) UNSIGNED NOT NULL);

-- -----------------------------------------------------
-- Table `$dbname`.`$prefix_modules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `$dbname`.`$prefix_modules` ;

CREATE  TABLE IF NOT EXISTS `$dbname`.`$prefix_modules` (
  `module_id` INT NOT NULL AUTO_INCREMENT ,
  `server_id` INT NOT NULL,
  `module_name` VARCHAR(16) NOT NULL ,
  `module_type` VARCHAR(16) NOT NULL ,
  `is_enabled` TINYINT(1) NOT NULL DEFAULT 0 ,
  `version` INT(11) NULL DEFAULT 0 ,
  PRIMARY KEY (`module_id`),
  UNIQUE KEY `module_id` (`module_id`),
  CONSTRAINT `$prefix_fk_server_id1`
    FOREIGN KEY (`server_id` )
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Alter existing tables
-- -----------------------------------------------------

ALTER TABLE `$prefix_players` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `online`;
ALTER TABLE `$prefix_players` ADD CONSTRAINT `$prefix_fk_server_id2`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_death_players` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `cause`;
ALTER TABLE `$prefix_detailed_death_players` ADD CONSTRAINT `$prefix_fk_server_id3`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_destroyed_blocks` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `player_id`;
ALTER TABLE `$prefix_detailed_destroyed_blocks` ADD CONSTRAINT `$prefix_fk_server_id4`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_dropped_items` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `player_id`;
ALTER TABLE `$prefix_detailed_dropped_items` ADD CONSTRAINT `$prefix_fk_server_id5`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_log_players` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `player_id`;
ALTER TABLE `$prefix_detailed_log_players` ADD CONSTRAINT `$prefix_fk_server_id6`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_pickedup_items` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id`;
ALTER TABLE `$prefix_detailed_pickedup_items` ADD CONSTRAINT `$prefix_fk_server_id7`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_placed_blocks` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `player_id`;
ALTER TABLE `$prefix_detailed_placed_blocks` ADD CONSTRAINT `$prefix_fk_server_id8`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_pve_kills` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `cause` ;
ALTER TABLE `$prefix_detailed_pve_kills` ADD CONSTRAINT `$prefix_fk_server_id9`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_pvp_kills` ADD `server_id` INT NOT NULL DEFAULT 1 AFTER `cause`;
ALTER TABLE `$prefix_detailed_pvp_kills` ADD CONSTRAINT `$prefix_fk_server_id10`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_detailed_used_items` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_detailed_used_items` ADD CONSTRAINT `$prefix_fk_server_id11`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `$prefix_distances` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_distances` ADD CONSTRAINT `$prefix_fk_server_id12`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_distances` DROP PRIMARY KEY, ADD PRIMARY KEY (`player_id`, `server_id`);

ALTER TABLE `$prefix_misc_info_players` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_misc_info_players` ADD CONSTRAINT `$prefix_fk_server_id13`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_misc_info_players` DROP PRIMARY KEY, ADD PRIMARY KEY (`player_id`, `server_id`);

ALTER TABLE `$prefix_player_locations` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_player_locations` ADD CONSTRAINT `$prefix_fk_server_id14`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_player_locations` DROP PRIMARY KEY, ADD PRIMARY KEY (`player_id`, `server_id`);

ALTER TABLE `$prefix_total_blocks` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_total_blocks` ADD CONSTRAINT `$prefix_fk_server_id15`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_total_blocks` ADD UNIQUE `total_blocks_id` (`player_id`, `server_id`, `material_id`);

ALTER TABLE `$prefix_total_deaths` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_total_deaths` ADD CONSTRAINT `$prefix_fk_server_id16`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_total_deaths` ADD UNIQUE `total_deaths_id` (`player_id`, `server_id`, `cause`);

ALTER TABLE `$prefix_total_items` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_total_items` ADD CONSTRAINT `$prefix_fk_server_id17`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_total_items` ADD UNIQUE `total_items_id` (`player_id`, `server_id`, `material_id`);

ALTER TABLE `$prefix_total_pve_kills` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_total_pve_kills` ADD CONSTRAINT `$prefix_fk_server_id18`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_total_pve_kills` ADD UNIQUE `total_pve_kills_id` (`material_id`, `entity_id`, `player_id`, `server_id`);

ALTER TABLE `$prefix_total_pvp_kills` ADD `server_id` INT NOT NULL DEFAULT 1  AFTER `player_id` ;
ALTER TABLE `$prefix_total_pvp_kills` ADD CONSTRAINT `$prefix_fk_server_id19`
    FOREIGN KEY (`server_id`)
    REFERENCES `$dbname`.`$prefix_server_statistics` (`server_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;
ALTER TABLE `$prefix_total_pvp_kills` ADD UNIQUE `total_pvp_kills_id` (`material_id`, `player_id`, `server_id`, `victim_id`);

-- -----------------------------------------------------
-- Clean up settings table
-- -----------------------------------------------------
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.admincmd';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.banhammer';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.commandbook';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.factions';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.jail';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.jobs';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.mcbans';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.mcmmo';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.mobarena';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.pvparena';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.towny';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.vanish.no_tracking';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.vanishnopacket';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.vault';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.votifier';
DELETE FROM `$prefix_settings` WHERE `key` = 'hook.worldguard';

DELETE FROM `$prefix_settings` WHERE `key` = 'version.admincmd';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.banhammer';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.commandbook';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.factions';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.jail';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.jobs';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.mcbans';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.mcmmo';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.mobarena';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.pvparena';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.towny';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.vanish.no_tracking';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.vanishnopacket';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.vault';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.votifier';
DELETE FROM `$prefix_settings` WHERE `key` = 'version.worldguard';

DELETE FROM `$prefix_settings` WHERE `key` = 'merged_data_tracking';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.blocks';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.deaths';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.inventory';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.items';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.server';
DELETE FROM `$prefix_settings` WHERE `key` = 'module.unknown';



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;