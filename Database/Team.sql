UPDATE teams SET coach_name = 'Mounica', city = 'Mettur'
WHERE team_name = 'IT Warriors';

UPDATE teams SET coach_name = 'Jeevitha', city = 'Erode'
WHERE team_name = 'CSE Kings';

UPDATE teams SET coach_name = 'Dhanusri', city = 'Tiruppur'
WHERE team_name = 'ECE Eagles';

UPDATE teams SET coach_name = 'Adithya', city = 'Salem'
WHERE team_name = 'EEE Stars';

UPDATE teams SET coach_name = 'Jeffrin', city = 'Bhavani'
WHERE team_name = 'warriours';

UPDATE teams SET coach_name = 'Abishek', city = 'Namakkal'
WHERE team_name = 'fire';

SELECT team_id, team_name, coach_name, city
FROM teams;