
const API_URL = "http://localhost:8080/api/teams";
const TOURNAMENT_API_URL = "http://localhost:8080/api/tournaments";
const MATCH_API_URL = "http://localhost:8080/api/matches";
const STANDINGS_API_URL = "http://localhost:8080/api/standings";
const DASHBOARD_API_URL = "http://localhost:8080/api/dashboard/stats";
// Add a new team to MySQL
async function addTeam(event) {
    event.preventDefault();

    const name = document.getElementById("teamName").value.trim();
    const department = document.getElementById("teamDepartment").value.trim();
    const sport = document.getElementById("teamSport").value;
    const coach = document.getElementById("coachName").value.trim();
    const city = document.getElementById("teamCity").value.trim();

    if (!name || !department || !sport || !coach || !city) {
        alert("Please fill all team details!");
        return;
    }

    const 
    
    teamData = new URLSearchParams({
        team_name: name,
        department: department,
        sport: sport,
        coach_name: coach,
        city: city
    });

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: teamData.toString()
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Unable to add team");
        }

        alert(result.message);

        document.getElementById("teamForm").reset();

        // Reload teams from MySQL
        loadTeams();

    } catch (error) {
        console.error("Error:", error);
        alert("Team registration failed: " + error.message);
    }
}

// Get teams from MySQL and display them
async function loadTeams() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Unable to load teams");
        }

        const teams = await response.json();
        displayTeams(teams);
        loadTeamDropdown(teams);

    } catch (error) {
        console.error("Error loading teams:", error);
    }
}

// Display teams in the website table
function displayTeams(teams) {
    const table = document.getElementById("teamTable");
    table.innerHTML = "";

    teams.forEach(function(team) {
        const row = document.createElement("tr");

        [
            team.team_name,
            team.department,
            team.sport,
            team.coach_name,
            team.city
        ].forEach(function(value) {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}

// Sidebar navigation
function showSection(sectionId) {
    const sections = document.querySelectorAll(".section");

    sections.forEach(function(section) {
        section.classList.remove("active");
    });

    document.getElementById(sectionId).classList.add("active");
}

// Load existing teams when website opens
document.addEventListener("DOMContentLoaded", function() {
    loadTeams();
    loadPlayers();
    loadTournaments();
});
const PLAYER_API_URL = "http://localhost:8080/api/players";

async function addPlayer(event) {
    event.preventDefault();

    const playerName = document.getElementById("playerName").value.trim();
    const teamId = document.getElementById("playerTeam").value;
    const participation = document.getElementById("participation").value;

    const playerData = new URLSearchParams({
        player_name: playerName,
        team_id: teamId,
        participation: participation
    });

    try {
        const response = await fetch(PLAYER_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: playerData.toString()
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Unable to register player");
        }

        alert(result.message);
        document.getElementById("playerForm").reset();
        loadPlayers();

    } catch (error) {
        alert("Player registration failed: " + error.message);
    }
}

async function loadPlayers() {
    try {
        const response = await fetch(PLAYER_API_URL);
        const players = await response.json();
        displayPlayers(players);
    } catch (error) {
        console.error("Error loading players:", error);
    }
}

function displayPlayers(players) {
    const table = document.getElementById("playerTable");
    table.innerHTML = "";

    players.forEach(function(player) {
        const row = document.createElement("tr");

        [
            player.player_id,
            player.player_name,
            player.team_name,
            player.participation
        ].forEach(function(value) {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}
function loadTeamDropdown(teams) {
    const dropdown = document.getElementById("playerTeam");

    dropdown.innerHTML = '<option value="">Select Team</option>';

    teams.forEach(function(team) {
        const option = document.createElement("option");
        option.value = team.team_id;
        option.textContent = team.team_name;
        dropdown.appendChild(option);
    });
}
async function loadTournaments() {
    try {
        const response = await fetch(TOURNAMENT_API_URL);

        if (!response.ok) {
            throw new Error("Unable to load tournaments");
        }

        const tournaments = await response.json();

        displayTournaments(tournaments);

    } catch (error) {
        console.error("Error loading tournaments:", error);
    }
}

function displayTournaments(tournaments) {
    const table = document.getElementById("tournamentTable");

    table.innerHTML = "";

    tournaments.forEach(function(tournament) {
        const row = document.createElement("tr");

        [
            tournament.tournament_id,
            tournament.tournament_name,
            tournament.sport,
            tournament.start_date,
            tournament.end_date
        ].forEach(function(value) {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}
async function addTournament(event) {
    event.preventDefault();

    const name = document.getElementById("tournamentName").value.trim();
    const sport = document.getElementById("tournamentSport").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    if (!name || !sport || !startDate || !endDate) {
        alert("Please fill all tournament details!");
        return;
    }

    if (endDate < startDate) {
        alert("End date cannot be before start date!");
        return;
    }

    const tournamentData = new URLSearchParams({
        tournament_name: name,
        sport: sport,
        start_date: startDate,
        end_date: endDate
    });

    try {
        const response = await fetch(TOURNAMENT_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: tournamentData.toString()
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Unable to create tournament");
        }

        alert(result.message);
        document.getElementById("tournamentForm").reset();

        loadTournaments();

    } catch (error) {
        alert("Tournament creation failed: " + error.message);
    }
}
async function loadMatchDropdowns() {
    try {
        const tournamentResponse = await fetch(TOURNAMENT_API_URL);
        const tournaments = await tournamentResponse.json();

        const teamResponse = await fetch(API_URL);
        const teams = await teamResponse.json();

        const tournamentSelect = document.getElementById("matchTournament");
        const team1Select = document.getElementById("matchTeam1");
        const team2Select = document.getElementById("matchTeam2");

        tournaments.forEach(function(tournament) {
            const option = document.createElement("option");
            option.value = tournament.tournament_id;
            option.textContent = tournament.tournament_name;
            tournamentSelect.appendChild(option);
        });

        teams.forEach(function(team) {
            const option1 = document.createElement("option");
            option1.value = team.team_id;
            option1.textContent = team.team_name;
            team1Select.appendChild(option1);

            const option2 = document.createElement("option");
            option2.value = team.team_id;
            option2.textContent = team.team_name;
            team2Select.appendChild(option2);
        });

    } catch (error) {
        console.error("Error loading match dropdowns:", error);
    }
}
async function addMatch(event) {
    event.preventDefault();

    const tournamentId = document.getElementById("matchTournament").value;
    const team1Id = document.getElementById("matchTeam1").value;
    const team2Id = document.getElementById("matchTeam2").value;
    const matchDate = document.getElementById("matchDate").value;

    if (team1Id === team2Id) {
        alert("Please select two different teams!");
        return;
    }

    const matchData = new URLSearchParams();

    matchData.append("tournament_id", tournamentId);
    matchData.append("team1_id", team1Id);
    matchData.append("team2_id", team2Id);
    matchData.append("match_date", matchDate);

    try {
        const response = await fetch(MATCH_API_URL, {
            method: "POST",
            body: matchData
        });

        const result = await response.json();

        alert(result.message);

        if (response.ok) {
            document.getElementById("matchForm").reset();
            await loadMatches();
        }

    } catch (error) {
        console.error("Error scheduling match:", error);
        alert("Unable to schedule match!");
    }
}
async function loadMatches() {
    try {
        const response = await fetch(MATCH_API_URL);

        if (!response.ok) {
            throw new Error("Unable to load matches");
        }

        const matches = await response.json();

        const table = document.getElementById("matchTable");
        table.innerHTML = "";

        matches.forEach(function(match) {
            const row = document.createElement("tr");

            [
                match.match_id,
                match.tournament_name,
                match.team1_name,
                match.team2_name,
                match.match_date,
                match.score1 + " - " + match.score2,
                match.winner_name || "Draw",
                match.status
            ].forEach(function(value) {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            table.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading matches:", error);
    }
}

async function loadScoreMatchDropdown() {

    const response = await fetch(MATCH_API_URL);
    const matches = await response.json();

    const dropdown = document.getElementById("scoreMatch");

    dropdown.innerHTML = '<option value="">Select Match</option>';

    matches.forEach(match => {

        const option = document.createElement("option");

        option.value = match.match_id;

        option.textContent =
            `Match ${match.match_id}: ${match.team1_name} vs ${match.team2_name}`;

        dropdown.appendChild(option);
    });
}

async function loadStandingsDropdown() {

    const response = await fetch(TOURNAMENT_API_URL);
    const tournaments = await response.json();

    const dropdown = document.getElementById("standingsTournament");

    dropdown.innerHTML = '<option value="">Select Tournament</option>';

    tournaments.forEach(tournament => {

        const option = document.createElement("option");

        option.value = tournament.tournament_id;
        option.textContent = tournament.tournament_name;

        dropdown.appendChild(option);
    });
}

async function loadStandings() {

    const tournamentId = document.getElementById("standingsTournament").value;

    const table = document.getElementById("standingsTable");

    if (!tournamentId) {
        table.innerHTML = `
            <tr>
                <td colspan="7">Please select a tournament.</td>
            </tr>
        `;
        return;
    }

    try {

        const response = await fetch(
            `${STANDINGS_API_URL}?tournament_id=${tournamentId}`
        );

        const standings = await response.json();

        if (!response.ok) {
            throw new Error(standings.message || "Unable to load standings");
        }

        if (standings.length === 0) {
            table.innerHTML = `
                <tr>
                    <td colspan="7">No completed matches available.</td>
                </tr>
            `;
            return;
        }

        table.innerHTML = "";

        standings.forEach(team => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${team.rank}</td>
                <td>${team.team_name}</td>
                <td>${team.played}</td>
                <td>${team.won}</td>
                <td>${team.draws}</td>
                <td>${team.lost}</td>
                <td><strong>${team.points}</strong></td>
            `;

            table.appendChild(row);
        });

    } catch (error) {
        console.error("Standings error:", error);
        table.innerHTML = `
            <tr>
                <td colspan="7">Unable to load standings.</td>
            </tr>
        `;
    }
}


async function updateMatchScore(event) {

    event.preventDefault();

    const matchId = document.getElementById("scoreMatch").value;
    const score1 = document.getElementById("score1").value;
    const score2 = document.getElementById("score2").value;

    const data = new URLSearchParams();

    data.append("match_id", matchId);
    data.append("score1", score1);
    data.append("score2", score2);

    try {

        const response = await fetch(MATCH_API_URL, {
            method: "PUT",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data
        });

        const result = await response.json();

        alert(result.message);

        if (response.ok) {
            document.getElementById("scoreForm").reset();
            await loadMatches();
        }

    } catch (error) {
        console.error("Error updating score:", error);
        alert("Unable to update match result");
    }
}


async function loadDashboardStats() {
    try {
        const response = await fetch(DASHBOARD_API_URL);
        const data = await response.json();

        document.getElementById("dashboardTeamCount").textContent =
            data.total_teams;

        document.getElementById("dashboardPlayerCount").textContent =
            data.total_players;

        document.getElementById("dashboardTournamentCount").textContent =
            data.total_tournaments;

        document.getElementById("dashboardMatchCount").textContent =
            data.total_matches;

    } catch (error) {
        console.error("Dashboard stats error:", error);
    }
}
loadMatches();
loadMatchDropdowns();
loadScoreMatchDropdown();
loadStandingsDropdown();
loadDashboardStats();

async function refreshAnalytics() {

    try {
        const response = await fetch(
            "http://localhost:8080/api/analytics/refresh",
            {
                method: "POST"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Refresh failed");
        }

        alert("Analytics charts refreshed successfully!");

        const images = document.querySelectorAll(
            "#analytics img"
        );

        images.forEach(image => {
            image.src = image.src.split("?")[0] + "?t=" + Date.now();
        });

    } catch (error) {
        console.error("Analytics refresh error:", error);
        alert("Analytics refresh failed: " + error.message);
    }
}
