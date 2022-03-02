This is complete system of software used to control weather station assembled from later mentioned devices


Repository is divided into separate directories, each one of them are destined to be run on different devices
Server - Raspberry Pi 4 - Raspbian os serves as main host for database, managing other devices, user interface
Arduino - Nodemcu V3 with esp8266 and BME 280 sensor connected

Keep in mind, that folder names in main directory are subject to change, DO NOT make any references between directories,
as ultimately those directories will be another physical devices
