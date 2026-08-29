
-- tmp migration script for testing, will be removed completely after implementation

-- test data -> all assetto corsa ultimate edition tracks (will be potentially differently structured when the simulator dependency is implemented)
insert into track (country, name, length_km, version) values
('Spain', 'Barcelona - GP', 4.655, 1),
('Spain', 'Barcelona - Moto', 4.727, 1),
('USA', 'Black Cat County', 6.478, 1),
('USA', 'Black Cat County - Long', 11.244, 1),
('USA', 'Black Cat County - Short', 6.542, 1),
('Great Britain', 'Brands Hatch - GP', 3.908, 1),
('Great Britain', 'Brands Hatch - Indy', 1.929, 1),
('Scotland', 'Highlands', 8.152, 1),
('Scotland', 'Highlands Drift', 5.167, 1),
('Scotland', 'Highlands Long', 12.191, 1),
('Scotland', 'Highlands Short', 1.714, 1),
('Italy', 'Imola', 4.909, 1),
('USA', 'Laguna Seca', 3.602, 1),
('Italy', 'Magione', 2.507, 1),
('Italy', 'Monza', 5.793, 1),
('Italy', 'Monza 1966 - Full Course', 10.000, 1),
('Italy', 'Monza 1966 - Junior Course', 2.405, 1),
('Italy', 'Monza 1966 - Road Course', 5.793, 1),
('Italy', 'Mugello', 5.245, 1),
('Germany', 'Nordschleife', 20.832, 1),
('Germany', 'Nordschleife - Endurance', 25.378, 1),
('Germany', 'Nordschleife - Endurance Cup', 24.433, 1),
('Germany', 'Nurburgring - GP', 5.148, 1),
('Germany', 'Nurburgring - GP (GT)', 5.137, 1),
('Germany', 'Nurburgring - Sprint', 3.629, 1),
('Germany', 'Nurburgring - Sprint (GT)', 3.618, 1),
('Austria', 'Red Bull Ring GP', 4.326, 1),
('Austria', 'Red Bull Ring National', 2.336, 1),
('Great Britain', 'Silverstone - International', 3.619, 1),
('Great Britain', 'Silverstone - National', 2.638, 1),
('Great Britain', 'Silverstone 1967', 4.710, 1),
('Great Britain', 'Silverstone GP', 5.901, 1),
('Belgium', 'Spa', 7.004, 1),
('Italy', 'Vallelunga', 4.085, 1),
('Italy', 'Vallelunga - Classic', 3.222, 1),
('Italy', 'Vallelunga - Club', 1.746, 1),
('Netherlands', 'Zandvoort', 4.307, 1);

insert into car (brand, name, horse_power, torque, wheel_drive, version)
	values ('Ferrari','LaFerrari', 963, 900, 'REAR', 1);

-- test users
insert into users (username, password, enabled)
	values ('admin', '$2a$10$kBC31uAS0uX4YjS9qgaO.eQliPKjzRidDWJbfdHnnaZn5ZmhsR1ye', true),
	       ('participant', '$2a$10$/4TayRdR7me4A8VAH2rrlOah7BpdGkG5dfCTELNsQ2ipLQk34EaTe', true),
       	       ('thor', '$2a$10$dcoUcEhmRg/jRZjRuIpx/.gSn3OVn9mZ0/Yf0a0/7smuWzhwc/KwC', true);

insert into authorities (username, authority)
	values ('participant', 'PARTICIPANT'),
               ('thor', 'PARTICIPANT'),
               ('thor', 'ADMIN'),
               ('admin', 'ADMIN');

