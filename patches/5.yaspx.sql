SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- drop foreign keys
ALTER TABLE `$prefix_detailed_pve_kills` DROP FOREIGN KEY $prefix_fk_entity_id2;
ALTER TABLE `$prefix_total_pve_kills` DROP FOREIGN KEY $prefix_fk_entity_id1;

-- alter entity table 
ALTER TABLE `$prefix_entities` MODIFY entity_id VARCHAR(16);

-- alter child tables
ALTER TABLE `$prefix_detailed_pve_kills` MODIFY entity_id VARCHAR(16);
ALTER TABLE `$prefix_total_pve_kills` MODIFY entity_id VARCHAR(16);

-- add foreign keys
ALTER TABLE `$prefix_detailed_pve_kills` ADD CONSTRAINT $prefix_fk_entity_id2 FOREIGN KEY (entity_id) REFERENCES $prefix_entities(entity_id);
ALTER TABLE `$prefix_total_pve_kills` ADD CONSTRAINT $prefix_fk_entity_id1 FOREIGN KEY (entity_id) REFERENCES $prefix_entities(entity_id);

-- alter health and food
ALTER TABLE `$prefix_misc_info_players` MODIFY `food_level` DOUBLE UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE `$prefix_misc_info_players` MODIFY `health` DOUBLE UNSIGNED NOT NULL DEFAULT '0';

-- rename distances.pig to distances.ride
ALTER TABLE `$prefix_distances` CHANGE `pig` `ride` BIGINT( 20 ) UNSIGNED NOT NULL DEFAULT '0';

-- add horses
INSERT INTO `$prefix_entities` (entity_id, tp_name) VALUES('100:0', 'horse');
INSERT INTO `$prefix_entities` (entity_id, tp_name) VALUES('100:1', 'donkey');
INSERT INTO `$prefix_entities` (entity_id, tp_name) VALUES('100:2', 'mule');
INSERT INTO `$prefix_entities` (entity_id, tp_name) VALUES('100:3', 'horse_zombie');
INSERT INTO `$prefix_entities` (entity_id, tp_name) VALUES('100:4', 'horse_skelton');

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Materials for 1.6
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("43:8", "smooth_stone_slab_double");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("43:9", "smooth_sandstone_slab_double");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("145:1", "anvil_slightly_damaged");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("145:2", "anvil_very_damaged");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:0", "stained_clay_white");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:1", "stained_clay_orange");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:2", "stained_clay_magenta");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:3", "stained_clay_light_blue");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:4", "stained_clay_yellow");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:5", "stained_clay_lime");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:6", "stained_clay_pink");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:7", "stained_clay_gray");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:8", "stained_clay_light_gray");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:9", "stained_clay_cyan");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:10", "stained_clay_purple");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:11", "stained_clay_blue");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:12", "stained_clay_brown");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:13", "stained_clay_green");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:14", "stained_clay_red");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("159:15", "stained_clay_black");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("170:0", "hay_bale");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:0", "carpet_white");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:1", "carpet_orange");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:2", "carpet_magenta");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:3", "carpet_light_blue");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:4", "carpet_yellow");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:5", "carpet_lime");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:6", "carpet_pink");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:7", "carpet_grey");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:8", "carpet_light_gray");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:9", "carpet_cyan");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:10", "carpet_purple");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:11", "carpet_blue");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:12", "carpet_brown");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:13", "carpet_green");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:14", "carpet_red");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("171:15", "carpet_black");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("172:0", "hardened_clay");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("173:0", "block_of_coal");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:8289", "regeneration_potion_ii_1");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:8290", "swiftness_potion_ii_4");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:8292", "poison_potion_ii_1");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:8297", "strength_potion_ii_4");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:16481", "regeneration_splash_ii_45");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:16482", "swiftness_splash_ii_3");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:16484", "poison_splash_ii_45");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("373:16489", "strength_splash_ii_3");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("383:100", "spawn_egg_horse");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("417:0", "iron_horse_armor");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("418:0", "gold_horse_armor");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("419:0", "diamond_horse_armor");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("420:0", "lead");
INSERT INTO `$prefix_materials` (`material_id`, `tp_name`) VALUES ("421:0", "name_tag");