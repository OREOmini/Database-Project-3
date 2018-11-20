set @status = 'done';
DROP TRIGGER IF EXISTS `project3-nudb`.`uosoffering_AFTER_UPDATE`;

DELIMITER $$
USE `project3-nudb`$$
CREATE DEFINER=`root`@`localhost` TRIGGER `uosoffering_AFTER_UPDATE` AFTER UPDATE ON `uosoffering` FOR EACH ROW BEGIN
	SET @status = 'done';
	IF (NEW.Enrollment<(0.5*NEW.MAXEnrollment)) THEN
	SET @status = 'below';
    END IF;
END$$
DELIMITER ;
