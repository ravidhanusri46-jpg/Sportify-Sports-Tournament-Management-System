CREATE DATABASE IF NOT EXISTS sports_management;
USE sports_management;

CREATE TABLE IF NOT EXISTS teams (
    team_id INT PRIMARY KEY,
    team_name VARCHAR(80) NOT NULL,
    department VARCHAR(60) NOT NULL,
    sport VARCHAR(40) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS players (
    player_id INT PRIMARY KEY,
    player_name VARCHAR(80) NOT NULL,
    team_id INT NOT NULL,
    participation VARCHAR(50) NOT NULL,
    CONSTRAINT fk_players_team FOREIGN KEY (team_id)
        REFERENCES teams(team_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tournaments (
    tournament_id INT PRIMARY KEY,
    tournament_name VARCHAR(100) NOT NULL,
    sport VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    CONSTRAINT chk_tournament_dates CHECK (end_date >= start_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team_id INT NOT NULL,
    CONSTRAINT fk_reg_tournament FOREIGN KEY (tournament_id)
        REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_team FOREIGN KEY (team_id)
        REFERENCES teams(team_id) ON DELETE CASCADE,
    CONSTRAINT uq_tournament_team UNIQUE (tournament_id, team_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS matches (
    match_id INT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team1_id INT NOT NULL,
    team2_id INT NOT NULL,
    match_date DATE NOT NULL,
    score1 INT NULL,
    score2 INT NULL,
    winner_id INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    CONSTRAINT fk_match_tournament FOREIGN KEY (tournament_id)
        REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    CONSTRAINT fk_match_team1 FOREIGN KEY (team1_id) REFERENCES teams(team_id),
    CONSTRAINT fk_match_team2 FOREIGN KEY (team2_id) REFERENCES teams(team_id),
    CONSTRAINT fk_match_winner FOREIGN KEY (winner_id) REFERENCES teams(team_id),
    CONSTRAINT chk_different_teams CHECK (team1_id <> team2_id),
    CONSTRAINT chk_match_scores CHECK ((score1 IS NULL AND score2 IS NULL) OR (score1 >= 0 AND score2 >= 0)),
    CONSTRAINT chk_match_status CHECK (status IN ('Scheduled', 'Completed', 'Cancelled'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS standings (
    tournament_id INT NOT NULL,
    team_id INT NOT NULL,
    played INT NOT NULL DEFAULT 0,
    wins INT NOT NULL DEFAULT 0,
    draws INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0,
    points INT NOT NULL DEFAULT 0,
    goals_for INT NOT NULL DEFAULT 0,
    goals_against INT NOT NULL DEFAULT 0,
    PRIMARY KEY (tournament_id, team_id),
    CONSTRAINT fk_standings_tournament FOREIGN KEY (tournament_id)
        REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    CONSTRAINT fk_standings_team FOREIGN KEY (team_id)
        REFERENCES teams(team_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Sample teams
INSERT INTO teams(team_id, team_name, department, sport) VALUES
(1, 'IT Warriors', 'IT', 'Football'),
(2, 'CSE Kings', 'CSE', 'Football'),
(3, 'ECE Eagles', 'ECE', 'Football'),
(4, 'EEE Stars', 'EEE', 'Football')
ON DUPLICATE KEY UPDATE team_id = VALUES(team_id);

-- Sample players
INSERT INTO players(player_id, player_name, team_id, participation) VALUES
(101, 'Arun', 1, 'Forward'),
(102, 'Kavin', 1, 'Defender'),
(103, 'Bala', 2, 'Forward'),
(104, 'Ravi', 2, 'Goalkeeper'),
(105, 'Suresh', 3, 'Defender'),
(106, 'Manoj', 4, 'Forward')
ON DUPLICATE KEY UPDATE player_id = VALUES(player_id);

-- Sample tournament
INSERT INTO tournaments(tournament_id, tournament_name, sport, start_date, end_date) VALUES
(1, 'College Football Cup', 'Football', '2026-11-15', '2026-11-25')
ON DUPLICATE KEY UPDATE tournament_id = VALUES(tournament_id);

-- Register all four teams
INSERT INTO registrations(tournament_id, team_id) VALUES
(1,1), (1,2), (1,3), (1,4)
ON DUPLICATE KEY UPDATE registration_id = registration_id;

-- Sample match results
INSERT INTO matches(match_id, tournament_id, team1_id, team2_id, match_date, score1, score2, winner_id, status) VALUES
(1, 1, 1, 2, '2026-11-16', 3, 1, 1, 'Completed'),
(2, 1, 3, 4, '2026-11-17', 2, 2, NULL, 'Completed'),
(3, 1, 1, 3, '2026-11-19', 1, 0, 1, 'Completed'),
(4, 1, 2, 4, '2026-11-20', 0, 2, 4, 'Completed')
ON DUPLICATE KEY UPDATE match_id = VALUES(match_id);
