# AnimalRental
Backend for magic animals rental - Used with REST API - 03/10

Back-end d'un site de location d'animaux magiques. Communique avec le front-end via une REST API (JSON sur HTTP).

DB <------> Java <-------> Front-end (HTML/CSS/JS)

## Objectif
Utiliser les différentes notions de Java OO apprises au cours (création d'exceptions, streams)

## Techno utilisées
* Architecture : 3-tiers (servlets, domain, services)
* Framework : Tomcat (fourni par le formateur)
* Front-End : aucun (utilisation d'Insomnia pour les tests). Possibilité d'utiliser https://github.com/lutin2706/Animagic (de Gael) ou https://github.com/lutin2706/Magimal (de Michel)
* Persistance des données : JDBC avec MySQL
* Autres : Insomnia (tests), JSON, BCrypt, JWT (gestion des tokens)
Mapping par un web.xml

## Structure des packages
* Domain : contient les différentes entités stockées en DB
* Exception : les exceptions personnalisées
* Main : fourni par le formateur pour lancer le server
* Services : DBService pour l'accès à la DB (JDBC)
* Servlets : les servlets pour les différentes fonctionnalités
* Util : BCrypt (pour hasher les mots de passe) + une classe utilitaire

## Librairies utilisées
* gson-javatime-serializers-1.1.1.jar
* mysql-connector-java-5.1.44-bin.jar
* gson-2.8.1.jar
* java-jwt-3.2.0.jar

## Procédure pour refaire le même projet
Reprendre le projet et le customiser (en gardant le main, et toutes les libs)

## Comment l'installer/l'utiliser
* Lancer le main pour faire tourner le server
* Dans le navigateur, entrer localhost:8080/animals
* Pour envoyer le JSON, utiliser Insomnia, avec le fichier fourni.
