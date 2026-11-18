import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== SPORTS TOURNAMENT MANAGEMENT SYSTEM ===");
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            System.out.println("Check MySQL, database name, username and password.");
            return;
        }

        int choice;
        do {
            System.out.println("\n1.Team Management  2.Player Management");
            System.out.println("3.Tournament Management  4.Register Team");
            System.out.println("5.Schedule Match  6.Record Match Result");
            System.out.println("7.View Match Results  8.Tournament Standings");
            System.out.println("9.View Player Participation  0.Exit");
            choice = number("Enter choice: ");

            switch (choice) {
                case 1 -> teamMenu();
                case 2 -> playerMenu();
                case 3 -> tournamentMenu();
                case 4 -> registerTeam();
                case 5 -> scheduleMatch();
                case 6 -> recordResult();
                case 7 -> viewMatches();
                case 8 -> showStandings();
                case 9 -> viewPlayers();
                case 0 -> System.out.println("Thank you!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private static int number(String message) {
        while (true) {
            System.out.print(message);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a valid number."); }
        }
    }

    private static String input(String message) {
        while (true) {
            System.out.print(message);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty.");
        }
    }

    private static LocalDate date(String message) {
        while (true) {
            System.out.print(message + " (YYYY-MM-DD): ");
            try { return LocalDate.parse(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Invalid date format."); }
        }
    }

    private static void teamMenu() {
        System.out.println("1.Add  2.View  3.Update  4.Delete");
        int c = number("Choose: ");
        if (c == 1) addTeam();
        else if (c == 2) viewTeams();
        else if (c == 3) updateTeam();
        else if (c == 4) deleteTeam();
        else System.out.println("Invalid choice.");
    }

    private static void addTeam() {
        String sql = "INSERT INTO teams VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, number("Team ID: "));
            ps.setString(2, input("Team name: "));
            ps.setString(3, input("Department: "));
            ps.setString(4, input("Sport: "));
            ps.executeUpdate();
            System.out.println("Team added successfully.");
        } catch (SQLException e) { error(e); }
    }

    private static void viewTeams() {
        String sql = "SELECT * FROM teams ORDER BY team_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nID\tTEAM\t\tDEPARTMENT\tSPORT");
            while (rs.next())
                System.out.printf("%d\t%-18s\t%-12s\t%s%n", rs.getInt(1),
                    rs.getString(2), rs.getString(3), rs.getString(4));
        } catch (SQLException e) { error(e); }
    }

    private static void updateTeam() {
        String sql = "UPDATE teams SET team_name=?, department=?, sport=? WHERE team_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, input("New team name: "));
            ps.setString(2, input("New department: "));
            ps.setString(3, input("New sport: "));
            ps.setInt(4, number("Team ID: "));
            System.out.println(ps.executeUpdate() == 1 ? "Team updated." : "Team ID not found.");
        } catch (SQLException e) { error(e); }
    }

    private static void deleteTeam() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM teams WHERE team_id=?")) {
            ps.setInt(1, number("Team ID: "));
            System.out.println(ps.executeUpdate() == 1 ? "Team deleted." : "Team ID not found.");
        } catch (SQLException e) { error(e); }
    }

    private static void playerMenu() {
        System.out.println("1.Add  2.View  3.Update  4.Delete");
        int c = number("Choose: ");
        if (c == 1) addPlayer();
        else if (c == 2) viewPlayers();
        else if (c == 3) updatePlayer();
        else if (c == 4) deletePlayer();
        else System.out.println("Invalid choice.");
    }

    private static void addPlayer() {
        String sql = "INSERT INTO players VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, number("Player ID: "));
            ps.setString(2, input("Player name: "));
            ps.setInt(3, number("Team ID: "));
            ps.setString(4, input("Participation / position: "));
            ps.executeUpdate();
            System.out.println("Player registered successfully.");
        } catch (SQLException e) { error(e); }
    }

    private static void viewPlayers() {
        String sql = "SELECT p.player_id,p.player_name,t.team_name,p.participation " +
                     "FROM players p JOIN teams t ON p.team_id=t.team_id ORDER BY p.player_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nID\tPLAYER\t\tTEAM\t\tPOSITION");
            while (rs.next())
                System.out.printf("%d\t%-16s\t%-16s\t%s%n", rs.getInt(1),
                    rs.getString(2), rs.getString(3), rs.getString(4));
        } catch (SQLException e) { error(e); }
    }

    private static void updatePlayer() {
        String sql = "UPDATE players SET player_name=?,team_id=?,participation=? WHERE player_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, input("New player name: "));
            ps.setInt(2, number("New team ID: "));
            ps.setString(3, input("New participation / position: "));
            ps.setInt(4, number("Player ID: "));
            System.out.println(ps.executeUpdate() == 1 ? "Player updated." : "Player ID not found.");
        } catch (SQLException e) { error(e); }
    }

    private static void deletePlayer() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM players WHERE player_id=?")) {
            ps.setInt(1, number("Player ID: "));
            System.out.println(ps.executeUpdate() == 1 ? "Player deleted." : "Player ID not found.");
        } catch (SQLException e) { error(e); }
    }

    private static void tournamentMenu() {
        System.out.println("1.Create Tournament  2.View Tournaments");
        int c = number("Choose: ");
        if (c == 1) {
            int id = number("Tournament ID: ");
            String name = input("Tournament name: ");
            String sport = input("Sport: ");
            LocalDate start = date("Start date");
            LocalDate end = date("End date");
            if (end.isBefore(start)) {
                System.out.println("End date must not be before start date.");
                return;
            }
            String sql = "INSERT INTO tournaments VALUES(?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id); ps.setString(2, name); ps.setString(3, sport);
                ps.setDate(4, Date.valueOf(start)); ps.setDate(5, Date.valueOf(end));
                ps.executeUpdate();
                System.out.println("Tournament created.");
            } catch (SQLException e) { error(e); }
        } else if (c == 2) {
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM tournaments ORDER BY tournament_id")) {
                while (rs.next())
                    System.out.printf("%d | %s | %s | %s to %s%n", rs.getInt(1),
                        rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5));
            } catch (SQLException e) { error(e); }
        } else System.out.println("Invalid choice.");
    }

    private static void registerTeam() {
        String sql = "INSERT INTO registrations(tournament_id,team_id) VALUES(?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, number("Tournament ID: "));
            ps.setInt(2, number("Team ID: "));
            ps.executeUpdate();
            System.out.println("Team registered for tournament.");
        } catch (SQLException e) { error(e); }
    }

    private static boolean registered(Connection con, int tournament, int team) throws SQLException {
        String sql = "SELECT 1 FROM registrations WHERE tournament_id=? AND team_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tournament); ps.setInt(2, team);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void scheduleMatch() {
        int id = number("Match ID: ");
        int tournament = number("Tournament ID: ");
        int team1 = number("First Team ID: ");
        int team2 = number("Second Team ID: ");
        if (team1 == team2) {
            System.out.println("A team cannot play against itself.");
            return;
        }
        LocalDate matchDate = date("Match date");
        String sql = "INSERT INTO matches(match_id,tournament_id,team1_id,team2_id,match_date,status) " +
                     "VALUES(?,?,?,?,?,'Scheduled')";
        try (Connection con = DBConnection.getConnection()) {
            if (!registered(con, tournament, team1) || !registered(con, tournament, team2)) {
                System.out.println("Register both teams in this tournament before scheduling.");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id); ps.setInt(2, tournament); ps.setInt(3, team1);
                ps.setInt(4, team2); ps.setDate(5, Date.valueOf(matchDate));
                ps.executeUpdate();
                System.out.println("Match scheduled successfully.");
            }
        } catch (SQLException e) { error(e); }
    }

    private static void recordResult() {
        int id = number("Match ID: ");
        int s1 = number("First team score: ");
        int s2 = number("Second team score: ");
        if (s1 < 0 || s2 < 0) {
            System.out.println("Scores cannot be negative.");
            return;
        }
        String sql = "UPDATE matches SET score1=?,score2=?," +
            "winner_id=CASE WHEN ?>? THEN team1_id WHEN ?>? THEN team2_id ELSE NULL END," +
            "status='Completed' WHERE match_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s1); ps.setInt(2, s2);
            ps.setInt(3, s1); ps.setInt(4, s2);
            ps.setInt(5, s2); ps.setInt(6, s1);
            ps.setInt(7, id);
            System.out.println(ps.executeUpdate() == 1 ? "Match result saved." : "Match ID not found.");
        } catch (SQLException e) { error(e); }
    }

    private static void viewMatches() {
        String sql = "SELECT m.match_id,t.tournament_name,a.team_name AS first_team," +
            "b.team_name AS second_team,m.match_date,m.score1,m.score2,m.status " +
            "FROM matches m JOIN tournaments t ON m.tournament_id=t.tournament_id " +
            "JOIN teams a ON m.team1_id=a.team_id JOIN teams b ON m.team2_id=b.team_id " +
            "ORDER BY m.match_date,m.match_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nMATCH LIST");
            while (rs.next()) {
                String score = rs.getObject("score1") == null ? "Not played" :
                    rs.getInt("score1") + " - " + rs.getInt("score2");
                System.out.printf("%d | %s | %s vs %s | %s | %s | %s%n",
                    rs.getInt("match_id"), rs.getString("tournament_name"),
                    rs.getString("first_team"), rs.getString("second_team"),
                    rs.getDate("match_date"), score, rs.getString("status"));
            }
        } catch (SQLException e) { error(e); }
    }

    private static void showStandings() {
        int tournament = number("Tournament ID: ");
        String sql = "SELECT t.team_name,COUNT(m.match_id) AS played," +
            "SUM(CASE WHEN (m.team1_id=t.team_id AND m.score1>m.score2) OR " +
            "(m.team2_id=t.team_id AND m.score2>m.score1) THEN 1 ELSE 0 END) AS wins," +
            "SUM(CASE WHEN m.score1=m.score2 THEN 1 ELSE 0 END) AS draws," +
            "SUM(CASE WHEN (m.team1_id=t.team_id AND m.score1<m.score2) OR " +
            "(m.team2_id=t.team_id AND m.score2<m.score1) THEN 1 ELSE 0 END) AS losses," +
            "SUM(CASE WHEN (m.team1_id=t.team_id AND m.score1>m.score2) OR " +
            "(m.team2_id=t.team_id AND m.score2>m.score1) THEN 3 " +
            "WHEN m.score1=m.score2 THEN 1 ELSE 0 END) AS points " +
            "FROM registrations r JOIN teams t ON r.team_id=t.team_id " +
            "LEFT JOIN matches m ON m.tournament_id=r.tournament_id AND " +
            "(m.team1_id=t.team_id OR m.team2_id=t.team_id) AND m.status='Completed' " +
            "WHERE r.tournament_id=? GROUP BY t.team_id,t.team_name " +
            "ORDER BY points DESC,wins DESC,t.team_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tournament);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\nTEAM\t\tPLAYED\tWINS\tDRAWS\tLOSSES\tPOINTS");
                while (rs.next())
                    System.out.printf("%-16s\t%d\t%d\t%d\t%d\t%d%n",
                        rs.getString("team_name"), rs.getInt("played"), rs.getInt("wins"),
                        rs.getInt("draws"), rs.getInt("losses"), rs.getInt("points"));
            }
        } catch (SQLException e) { error(e); }
    }

    private static void error(SQLException e) {
        System.out.println("Database operation failed: " + e.getMessage());
    }
}
