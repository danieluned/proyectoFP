SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `proyectofp` ;
CREATE SCHEMA IF NOT EXISTS `proyectofp` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `proyectofp` ;

-- -----------------------------------------------------
-- Table `proyectofp`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyectofp`.`Usuario` (
  `usuario` VARCHAR(45) NOT NULL,
  `password` VARCHAR(32) NULL,
  `jsonConfi` VARCHAR(1000) NULL,
  PRIMARY KEY (`usuario`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyectofp`.`Evento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyectofp`.`Evento` (
  `fecha` DATE NOT NULL,
  `contenido` VARCHAR(200) NULL,
  `Usuario_usuario` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`fecha`, `Usuario_usuario`),
  INDEX `fk_Evento_Usuario_idx` (`Usuario_usuario` ASC),
  CONSTRAINT `fk_Evento_Usuario`
    FOREIGN KEY (`Usuario_usuario`)
    REFERENCES `mydb`.`Usuario` (`usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
