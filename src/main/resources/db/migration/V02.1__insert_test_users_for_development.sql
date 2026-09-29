
-- test users (will be disabled/deleted for production running application)
insert into users (username, password, enabled)
	values ('admin', '$2a$10$kBC31uAS0uX4YjS9qgaO.eQliPKjzRidDWJbfdHnnaZn5ZmhsR1ye', true),
	       ('participant', '$2a$10$/4TayRdR7me4A8VAH2rrlOah7BpdGkG5dfCTELNsQ2ipLQk34EaTe', true),
       	       ('thor', '$2a$10$dcoUcEhmRg/jRZjRuIpx/.gSn3OVn9mZ0/Yf0a0/7smuWzhwc/KwC', true);

insert into authorities (username, authority)
	values ('participant', 'PARTICIPANT'),
               ('thor', 'PARTICIPANT'),
               ('thor', 'ADMIN'),
               ('admin', 'ADMIN');

