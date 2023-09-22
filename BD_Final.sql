-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: mydb
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `apoderado`
--

DROP TABLE IF EXISTS `apoderado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apoderado` (
  `rut_apd` varchar(9) NOT NULL,
  `nombre_ap` varchar(50) NOT NULL,
  `apellido_ap` varchar(50) NOT NULL,
  `direccion_ap` varchar(50) NOT NULL,
  `celular_ap` int DEFAULT NULL,
  `parentesco_ap` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`rut_apd`),
  KEY `fk_Apoderado_Usuario1_idx` (`rut_apd`),
  CONSTRAINT `fk_Apoderado_Usuario1` FOREIGN KEY (`rut_apd`) REFERENCES `usuario` (`rut_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apoderado`
--

LOCK TABLES `apoderado` WRITE;
/*!40000 ALTER TABLE `apoderado` DISABLE KEYS */;
INSERT INTO `apoderado` VALUES ('10567890','Juan','Perez','Quelentaro 2536',985632145,'Padre'),('11234567','Maria','Gonzalez','Avenida Central 789',965432189,'Madre'),('11567890','Pedro','Lopez','Calle Principal 789',975432198,'Padre'),('12234567','Ana','Fernandez','Calle de los Pinos 654',965431234,'Madre'),('12245678','Pedro','Lopez','Calle Principal 456',954321876,'Padre'),('12445678','Juan','Perez','Quelentaro 2536',985632145,'Padre'),('12789543','Luis','Martinez','Avenida de la Luna 789',987654321,'Padre'),('13123456','Ana','Fernandez','Calle de los Pinos 123',945678321,'Madre'),('13234567','Sofia','Hernandez','Calle de la Montaña 456',965431234,'Madre'),('13245678','Luis','Martinez','Avenida de la Luna 987',987654321,'Padre'),('13765432','Carla','Rodriguez','Calle del Sol 654',965432198,'Madre'),('13876543','Maria','Gonzalez','Avenida Central 789',965432189,'Madre'),('14123456','Carla','Rodriguez','Calle del Sol 123',945678321,'Madre'),('14567890','Carlos','Sanchez','Avenida de las Estrellas 987',975432189,'Padre');
/*!40000 ALTER TABLE `apoderado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asisente`
--

DROP TABLE IF EXISTS `asisente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asisente` (
  `rut_asit` varchar(9) NOT NULL,
  `anios_exp` int DEFAULT NULL,
  `ideon_psico` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`rut_asit`),
  KEY `fk_Asisente_Funcionario1_idx` (`rut_asit`),
  CONSTRAINT `fk_Asisente_Funcionario1` FOREIGN KEY (`rut_asit`) REFERENCES `funcionario` (`rut_fun`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asisente`
--

LOCK TABLES `asisente` WRITE;
/*!40000 ALTER TABLE `asisente` DISABLE KEYS */;
/*!40000 ALTER TABLE `asisente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `curso`
--

DROP TABLE IF EXISTS `curso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `curso` (
  `cod_curso` int NOT NULL,
  `desc_curso` varchar(20) DEFAULT NULL,
  `rut_doc` varchar(9) NOT NULL,
  PRIMARY KEY (`cod_curso`),
  KEY `fk_curso_docente1_idx` (`rut_doc`),
  CONSTRAINT `fk_curso_docente1` FOREIGN KEY (`rut_doc`) REFERENCES `docente` (`rut_doc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `curso`
--

LOCK TABLES `curso` WRITE;
/*!40000 ALTER TABLE `curso` DISABLE KEYS */;
INSERT INTO `curso` VALUES (1,'Primero','123456789'),(2,'Segundo','987654321'),(3,'Tercero','567890123'),(4,'Cuarto','345678901'),(5,'Quinto','678901234'),(6,'Sexto','184872404'),(7,'Septimo','100056380'),(8,'Octavo','105951469');
/*!40000 ALTER TABLE `curso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `det_falta`
--

DROP TABLE IF EXISTS `det_falta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `det_falta` (
  `id_det_falta` int NOT NULL AUTO_INCREMENT,
  `cod_curso` int NOT NULL,
  `rut_est` varchar(9) NOT NULL,
  `id_falta` int NOT NULL,
  `rut_fun` varchar(9) NOT NULL,
  `com_det_falta` varchar(500) DEFAULT NULL,
  `fecha_det_falta` date NOT NULL,
  `hora_det_falta` time NOT NULL,
  PRIMARY KEY (`id_det_falta`),
  KEY `cod_curso_idx` (`cod_curso`),
  KEY `rut_fun_idx` (`rut_fun`),
  KEY `id_falta_idx` (`id_falta`),
  KEY `rut_est_idx` (`rut_est`),
  CONSTRAINT `cod_curso` FOREIGN KEY (`cod_curso`) REFERENCES `curso` (`cod_curso`),
  CONSTRAINT `id_falta` FOREIGN KEY (`id_falta`) REFERENCES `falta` (`id_falta`),
  CONSTRAINT `rut_est` FOREIGN KEY (`rut_est`) REFERENCES `estudiante` (`rut_est`),
  CONSTRAINT `rut_fun` FOREIGN KEY (`rut_fun`) REFERENCES `funcionario` (`rut_fun`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `det_falta`
--

LOCK TABLES `det_falta` WRITE;
/*!40000 ALTER TABLE `det_falta` DISABLE KEYS */;
INSERT INTO `det_falta` VALUES (39,2,'20000001',50,'987654321','Este alumno es sorprendido lanzando papeles dentro de la sala de clases','2023-09-06','22:25:13'),(40,2,'16000001',64,'987654321','Hdhdjjf','2023-09-06','22:54:44'),(41,6,'18500005',78,'184872404','Este  Alumno agrede fisicamente a inspector de patio','2023-09-10','14:58:36'),(42,4,'20000004',67,'123456789','Este alumno amenaza con un cortacarton a copanero para que este entregara su postre en el comedor','2023-09-10','15:02:35');
/*!40000 ALTER TABLE `det_falta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docente`
--

DROP TABLE IF EXISTS `docente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `docente` (
  `rut_doc` varchar(9) NOT NULL,
  `especializacion_doc` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`rut_doc`),
  KEY `fk_docente_Funcionario1_idx` (`rut_doc`),
  CONSTRAINT `fk_docente_Funcionario1` FOREIGN KEY (`rut_doc`) REFERENCES `funcionario` (`rut_fun`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docente`
--

LOCK TABLES `docente` WRITE;
/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
INSERT INTO `docente` VALUES ('100056380','2'),('105951469','1'),('123456789','1'),('184872404','1'),('345678901','1'),('567890123','1'),('678901234','1'),('987654321','1');
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estudiante`
--

DROP TABLE IF EXISTS `estudiante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estudiante` (
  `rut_est` varchar(9) NOT NULL,
  `rut_apod` varchar(9) NOT NULL,
  `cod_curso` int NOT NULL,
  `nombre_est` varchar(50) NOT NULL,
  `apellido_est` varchar(50) NOT NULL,
  `direccion_est` varchar(45) NOT NULL,
  `fec_nac_est` date NOT NULL,
  `sexo_est` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`rut_est`),
  KEY `fk_Estudiante_Apoderado1_idx` (`rut_apod`),
  KEY `fk_Estudiante_curso1_idx` (`cod_curso`),
  CONSTRAINT `fk_Estudiante_Apoderado1` FOREIGN KEY (`rut_apod`) REFERENCES `apoderado` (`rut_apd`),
  CONSTRAINT `fk_Estudiante_curso1` FOREIGN KEY (`cod_curso`) REFERENCES `curso` (`cod_curso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estudiante`
--

LOCK TABLES `estudiante` WRITE;
/*!40000 ALTER TABLE `estudiante` DISABLE KEYS */;
INSERT INTO `estudiante` VALUES ('16000001','12234567',2,'Javier','Catalan','Calle de los Pinos 654','2019-05-20','M'),('17000001','11567890',2,'Diego','Lopez','Calle Principal 789','2019-04-12','M'),('17000002','12234567',1,'Isabella','Catalan','Calle de los Pinos 654','2020-08-10','F'),('17500003','12234567',3,'Lucas','Catalan','Calle de los Pinos 654','2018-12-15','M'),('18000002','11567890',1,'Sara','Lopez','Calle Principal 789','2020-08-18','F'),('18000004','12234567',5,'Valeria','Catalan','Calle de los Pinos 654','2017-10-15','F'),('18500003','11567890',3,'Luis','Lopez','Calle Principal 789','2018-12-05','M'),('18500005','12234567',6,'Diego','Catalan','Calle de los Pinos 654','2016-09-05','M'),('19000002','14123456',2,'Mateo','Rodriguez','Calle del Sol 123','2020-07-25','M'),('19000004','11567890',4,'Valentina','Lopez','Calle Principal 789','2017-10-20','F'),('19500003','14123456',3,'Ana','Rodriguez','Calle del Sol 123','2018-12-15','F'),('195678901','10567890',1,'Camila','Perez','Quelentaro 2536','2019-05-12','F'),('197654321','10567890',4,'Valentina','Perez','Quelentaro 2536','2016-11-15','F'),('199000000','11234567',1,'Luis','Gonzalez','Avenida Central 789','2019-04-25','M'),('199999999','10567890',2,'Lucas','Perez','Quelentaro 2536','2018-08-20','M'),('20000000','13123456',2,'Laura','Cerda','Calle de los Pinos 123','2019-03-15','F'),('20000001','12789543',2,'Hans','Martinez','Avenida de la Luna 789','2019-05-20','M'),('20000004','14123456',4,'Emilio','Rodriguez','Calle del Sol 123','2017-09-05','M'),('20000005','11567890',5,'Pedro','Lopez','Calle Principal 789','2016-07-15','M'),('201500000','11234567',1,'Ana','Gonzalez','Avenida Central 789','2020-10-12','F'),('208900000','11234567',2,'Diego','Gonzalez','Avenida Central 789','2018-08-07','M'),('21000000','13123456',4,'Manuel','Cerda','Calle de los Pinos 123','2020-06-28','M'),('21000002','12789543',1,'Valeria','Martinez','Avenida de la Luna 789','2020-08-10','F'),('21500000','13123456',2,'Isabella','Cerda','Calle de los Pinos 123','2018-11-10','F'),('21500003','12789543',3,'Raul','Martinez','Avenida de la Luna 789','2018-12-05','M'),('22000000','13123456',3,'Javier','Cerda','Calle de los Pinos 123','2017-09-03','M'),('22000004','12789543',4,'Luisa','Martinez','Avenida de la Luna 789','2017-10-15','F');
/*!40000 ALTER TABLE `estudiante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `falta`
--

DROP TABLE IF EXISTS `falta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `falta` (
  `id_falta` int NOT NULL AUTO_INCREMENT,
  `des_falta` varchar(500) NOT NULL,
  `cat_falta` varchar(45) NOT NULL,
  PRIMARY KEY (`id_falta`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `falta`
--

LOCK TABLES `falta` WRITE;
/*!40000 ALTER TABLE `falta` DISABLE KEYS */;
INSERT INTO `falta` VALUES (46,'Olvidos reiterados de materiales solicitados','1'),(47,'No entrar a tiempo a clases después del recreo en reiteradas ocaciones','1'),(48,'Interrumpir el normal desarrollo de clase, no dejando trabajar a sus compañeros y profesor','1'),(49,'Comer durante el desarrollo de la clase a excepción que estén en alguna actividad extraescolar y o en una clase de preparación de alimentos','1'),(50,'Lanzar papeles u otros elementos dentro de la sala como una forma de molestar a sus compañeros o al profesor','1'),(51,'Encerrarse en los baños para hacer desorden botar agua y o romper algún artefacto','1'),(52,'Usar inadecuadamente o sin autorización artefactos eléctricos dentro de la escuela, principalmente si no hay vigilancia de un mayor','1'),(53,'No usar el uniforme de la escuela','1'),(54,'Levantarse del puesto y caminar por la sala sin permiso','1'),(55,'Manifestar conductas afectivas de pareja que impliquen contacto físico','1'),(56,'Desordenar el mobiliario de sala de clases u otras dependencias','1'),(58,'Salir o permanecer fuera de la sala de clases sin autorización','1'),(59,'Rayar el inmueble y o bienes del colegio','1'),(60,'Dañar infraestructura o materiales de la escuela o compañeros con excepción de situaciones accidentales','2'),(61,'Desafiar denigrar u ofender a algún miembro de la comunidad educativa','2'),(62,'Falsear o alterar calificaciones y comunicaciones','2'),(63,'Tener conducta inadecuada y o inmoral dentro del establecimiento o durante las salidas pedagógicas giras de estudio y o paseos','2'),(64,'Burlas y discriminación hacia compañeros','2'),(65,'Utilizar un lenguaje ofensivo o seoz en contra de algún compañero u otra persona dentro de la escuela','2'),(66,'Agresión verbal a un compañero','2'),(67,'Amenaza a compañeros','2'),(68,'Tener conductas que atentan en contra de la integridad física y o psicológica de cualquier miembro de la comunidad educativa','2'),(69,'No respetar la autoridad','2'),(70,'Salir de la sala o del establecimiento sin autorización','2'),(71,'No asistir pruebas fijadas y no presentar justificación en inspectoría en retiradas veces','2'),(72,'No acatar las instrucciones de profesor durante la clase','2'),(73,'Mentir deliberadamente faltar a la verdad a la honradez y la providad','2'),(74,'Usar de manera incorrecta la tecnología existente en la escuela','2'),(75,'Intentar fugarse del establecimiento','2'),(76,'Hacer mal uso del sistema de alimentación escolar entregado por la junaeb','2'),(77,'Agresión física y o psicológica a compañeros','3'),(78,'Agresión física y o psicológica a profesores u otro miembro de la comunidad escolar','3'),(79,'Robo a algún miembro de la comunidad escolar','3'),(80,'Abuso sexual a algún estudiante','3'),(81,'Agresiones sexuales y hechos de connotación que atenten contra la integridad física y o psicológica de los y las estudiantes','3'),(82,'Traer difundir intercambiar material pornográfico en la escuela','3'),(83,'Acosar o agredir mediante medios tecnológicos','3'),(84,'Realizar bullying a compañeros u otro miembro de la comunidad escolar','3'),(85,'Portar cualquier arma dentro del establecimiento','3'),(86,'Traer portar y o consumir drogas o alcohol al interior de la escuela','3'),(87,'Fumar dentro de la escuela','3'),(88,'Hacer la cimarra','3'),(89,'Agredir física o psicológicamente algún funcionario del establecimiento','2');
/*!40000 ALTER TABLE `falta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `funcionario`
--

DROP TABLE IF EXISTS `funcionario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `funcionario` (
  `rut_fun` varchar(9) NOT NULL,
  `nom_fun` varchar(50) NOT NULL,
  `apellido_fun` varchar(50) NOT NULL,
  `direccion_fun` varchar(45) NOT NULL,
  `correo_fun` varchar(45) DEFAULT NULL,
  `cel_fun` int NOT NULL,
  `horas_cont_fun` int NOT NULL,
  `especialidad_fun` int DEFAULT NULL,
  PRIMARY KEY (`rut_fun`),
  KEY `fk_Funcionario_Usuario1_idx` (`rut_fun`),
  CONSTRAINT `fk_Funcionario_Usuario1` FOREIGN KEY (`rut_fun`) REFERENCES `usuario` (`rut_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `funcionario`
--

LOCK TABLES `funcionario` WRITE;
/*!40000 ALTER TABLE `funcionario` DISABLE KEYS */;
INSERT INTO `funcionario` VALUES ('100056380','Emiliano','Palominos','Pueblo Inventado 965','emiliano@example.com',958585858,44,1),('105951469','Dominga','Devia','Pueblo Inventado 3745','dominga@example.com',956845874,44,1),('123456789','Ana','López','Calle Ficticia 123','ana@example.com',98765432,40,1),('184872404','Brian','Cerda','Pueblo Inventado 256','brian@example.com',987505007,36,1),('345678901','Carlos','Sánchez','Calle Ficticia 456','carlos@example.com',54321098,35,1),('567890123','María','Rodríguez','Pueblo Inventado 789','maria@example.com',65432109,42,1),('678901234','Laura','Gómez','Avenida Imaginaria 789','laura@example.com',43210987,37,1),('987654321','Pedro','Martínez','Avenida Imaginaria 456','pedro@example.com',78901234,38,1);
/*!40000 ALTER TABLE `funcionario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patio`
--

DROP TABLE IF EXISTS `patio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patio` (
  `cod_patio` int NOT NULL,
  `rut_asit` varchar(9) NOT NULL,
  `desc_patio` varchar(45) NOT NULL,
  PRIMARY KEY (`cod_patio`),
  KEY `fk_Patio_Asisente1_idx` (`rut_asit`),
  CONSTRAINT `fk_Patio_Asisente1` FOREIGN KEY (`rut_asit`) REFERENCES `asisente` (`rut_asit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patio`
--

LOCK TABLES `patio` WRITE;
/*!40000 ALTER TABLE `patio` DISABLE KEYS */;
/*!40000 ALTER TABLE `patio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `rut_usuario` varchar(9) NOT NULL,
  `clave_usuario` varchar(45) NOT NULL,
  `perfil_usuario` varchar(10) NOT NULL,
  PRIMARY KEY (`rut_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES ('100056380','1419','1'),('10567890','password1','2'),('105951469','0519','1'),('11234567','password2','2'),('11567890','password11','2'),('12234567','password12','2'),('12245678','password3','2'),('123456789','password1','1'),('12445678','password19','2'),('12789543','password5','2'),('13123456','password4','2'),('13234567','password8','2'),('13245678','password13','2'),('13765432','password6','2'),('13876543','password10','2'),('14123456','password14','2'),('14567890','password7','2'),('184872404','1904','1'),('234567890','password6','2'),('333333333','password3','3'),('345678901','password4','1'),('456789012','password8','2'),('567890123','password3','1'),('567890321','password10','2'),('678901234','password5','1'),('789012345','password7','2'),('890123456','password9','2'),('987654321','password2','1');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-11  9:43:11
