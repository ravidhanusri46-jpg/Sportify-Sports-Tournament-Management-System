import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.net.InetSocketAddress;
import java.util.*;

public class Backend {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/api/teams", exchange -> {
            try {
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Origin", "*"
                );
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
                );
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Headers", "Content-Type"
                );

                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if (exchange.getRequestMethod().equals("GET")) {
                    getTeams(exchange);
                } else if (exchange.getRequestMethod().equals("POST")) {
                    addTeam(exchange);
                } else {
                    send(exchange, 405, "Method not allowed");
                }

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500, "Server error: " + e.getMessage());
            }
        });

        server.createContext("/api/players", exchange -> {
            try {
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Origin", "*"
                );
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
                );
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Headers", "Content-Type"
                );

                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if (exchange.getRequestMethod().equals("GET")) {
                    getPlayers(exchange);
                } else if (exchange.getRequestMethod().equals("POST")) {
                    addPlayer(exchange);
                } else {
                    send(exchange, 405, "Method not allowed");
                }

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500, "Server error: " + e.getMessage());
            }
        });
    server.createContext("/api/tournaments", exchange -> {
    try {
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
        );
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );

        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (exchange.getRequestMethod().equals("GET")) {
            getTournaments(exchange);
        } else if (exchange.getRequestMethod().equals("POST")) {
            addTournament(exchange);
        } else {
            send(exchange, 405, "Method not allowed");
        }

    } catch (Exception e) {
        e.printStackTrace();
        send(exchange, 500, "Server error: " + e.getMessage());
    }
});
server.createContext("/api/matches", exchange -> {

    try {
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET, POST,PUT, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );

        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (exchange.getRequestMethod().equals("GET")) {
            getMatches(exchange);
        }  else if (exchange.getRequestMethod().equals("POST")) {
                 addMatch(exchange);
        } else if (exchange.getRequestMethod().equals("PUT")) {
                updateMatchScore(exchange);
        }else {
            send(exchange, 405, "Method not allowed");
        }

    } catch (Exception e) {
        e.printStackTrace();
        send(exchange, 500, "Server error: " + e.getMessage());
    }
});

        server.createContext("/api/standings", exchange -> {

            try {
                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Origin", "*"
                );

                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Methods", "GET, OPTIONS"
                );

                exchange.getResponseHeaders().set(
                    "Access-Control-Allow-Headers", "Content-Type"
                );

                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if (exchange.getRequestMethod().equals("GET")) {
                    getStandings(exchange);
                } else {
                    send(exchange, 405, "Method not allowed");
                }

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500, "Server error: " + e.getMessage());
            }
        });

server.createContext("/api/dashboard/stats", exchange -> {

    try {
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        if (exchange.getRequestMethod().equals("GET")) {
            getDashboardStats(exchange);
        } else {
            send(exchange, 405, "Method not allowed");
        }

    } catch (Exception e) {
        e.printStackTrace();
        send(exchange, 500, "Server error: " + e.getMessage());
    }
});

server.createContext("/api/analytics/refresh", exchange -> {

    try {
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );

        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (exchange.getRequestMethod().equals("POST")) {
            refreshAnalytics(exchange);
        } else {
            send(exchange, 405, "Method not allowed");
        }

    } catch (Exception e) {
        e.printStackTrace();
        send(exchange, 500, "Analytics refresh failed: " + e.getMessage());
    }
});

        server.start();

        System.out.println("Sportify Backend started!");
        System.out.println("Server running at http://localhost:8080");
    }

    private static void getTeams(HttpExchange exchange)
            throws Exception {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        String sql = "SELECT * FROM teams ORDER BY team_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"team_id\":").append(rs.getInt("team_id")).append(",")
                    .append("\"team_name\":\"").append(escape(rs.getString("team_name"))).append("\",")
                    .append("\"department\":\"").append(escape(rs.getString("department"))).append("\",")
                    .append("\"sport\":\"").append(escape(rs.getString("sport"))).append("\",")
                    .append("\"coach_name\":\"").append(escape(rs.getString("coach_name"))).append("\",")
                    .append("\"city\":\"").append(escape(rs.getString("city"))).append("\"")
                    .append("}");
            }
        }

        json.append("]");
        sendJson(exchange, 200, json.toString());
    }

    private static void addTeam(HttpExchange exchange)
            throws Exception {

        String body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                data.put(
                    URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                );
            }
        }

        String name = data.get("team_name");
        String department = data.get("department");
        String sport = data.get("sport");
        String coach = data.get("coach_name");
        String city = data.get("city");

        if (name == null || department == null || sport == null
                || coach == null || city == null
                || name.isBlank() || department.isBlank()
                || sport.isBlank() || coach.isBlank() || city.isBlank()) {
            send(exchange, 400, "Please fill all team details");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int nextId = 1;

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT COALESCE(MAX(team_id), 0) + 1 FROM teams"
                     )) {
                    if (rs.next()) nextId = rs.getInt(1);
                }

                String sql = "INSERT INTO teams "
                    + "(team_id, team_name, department, sport, coach_name, city) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, nextId);
                    ps.setString(2, name);
                    ps.setString(3, department);
                    ps.setString(4, sport);
                    ps.setString(5, coach);
                    ps.setString(6, city);

                    ps.executeUpdate();
                }

                con.commit();
                send(exchange, 201, "Team registered successfully!");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    private static void getPlayers(HttpExchange exchange)
            throws Exception {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        String sql = "SELECT p.player_id, p.player_name, "
                + "p.team_id, t.team_name, p.participation "
                + "FROM players p "
                + "JOIN teams t ON p.team_id = t.team_id "
                + "ORDER BY p.player_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"player_id\":").append(rs.getInt("player_id")).append(",")
                    .append("\"player_name\":\"").append(escape(rs.getString("player_name"))).append("\",")
                    .append("\"team_id\":").append(rs.getInt("team_id")).append(",")
                    .append("\"team_name\":\"").append(escape(rs.getString("team_name"))).append("\",")
                    .append("\"participation\":\"").append(escape(rs.getString("participation"))).append("\"")
                    .append("}");
            }
        }

        json.append("]");
        sendJson(exchange, 200, json.toString());
    }

    private static void addPlayer(HttpExchange exchange)
            throws Exception {

        String body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                data.put(
                    URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                );
            }
        }

        String name = data.get("player_name");
        String teamValue = data.get("team_id");
        String participation = data.get("participation");

        if (name == null || teamValue == null || participation == null
                || name.isBlank() || teamValue.isBlank()
                || participation.isBlank()) {
            send(exchange, 400, "Please fill all player details");
            return;
        }

        int teamId;

        try {
            teamId = Integer.parseInt(teamValue);
        } catch (NumberFormatException e) {
            send(exchange, 400, "Invalid team ID");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int nextId = 1;

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT COALESCE(MAX(player_id), 0) + 1 FROM players"
                     )) {
                    if (rs.next()) nextId = rs.getInt(1);
                }

                String sql = "INSERT INTO players "
                    + "(player_id, player_name, team_id, participation) "
                    + "VALUES (?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, nextId);
                    ps.setString(2, name);
                    ps.setInt(3, teamId);
                    ps.setString(4, participation);

                    ps.executeUpdate();
                }

                con.commit();
                send(exchange, 201, "Player registered successfully!");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

private static void getTournaments(HttpExchange exchange) throws Exception {
    String sql = "SELECT tournament_id, tournament_name, sport, start_date, end_date FROM tournaments ORDER BY tournament_id";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        while (rs.next()) {
            if (!first) json.append(",");
            first = false;

            json.append("{")
                .append("\"tournament_id\":").append(rs.getInt("tournament_id")).append(",")
                .append("\"tournament_name\":\"").append(escape(rs.getString("tournament_name"))).append("\",")
                .append("\"sport\":\"").append(escape(rs.getString("sport"))).append("\",")
                .append("\"start_date\":\"").append(rs.getDate("start_date")).append("\",")
                .append("\"end_date\":\"").append(rs.getDate("end_date")).append("\"")
                .append("}");
        }

        json.append("]");
        sendRaw(exchange, 200, json.toString());
    }
}

private static void addTournament(HttpExchange exchange) throws Exception {
    String body = new String(exchange.getRequestBody().readAllBytes());
    Map<String, String> data = parseForm(body);

    String name = data.get("tournament_name");
    String sport = data.get("sport");
    String startDate = data.get("start_date");
    String endDate = data.get("end_date");

    if (name == null || sport == null || startDate == null || endDate == null ||
        name.isBlank() || sport.isBlank() || startDate.isBlank() || endDate.isBlank()) {
        send(exchange, 400, "{\"message\":\"Please fill all tournament details\"}");
        return;
    }

    String sql = "INSERT INTO tournaments (tournament_id, tournament_name, sport, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        int nextId = 1;

String idSql = "SELECT COALESCE(MAX(tournament_id), 0) + 1 FROM tournaments";

try (PreparedStatement idPs = con.prepareStatement(idSql);
     ResultSet idRs = idPs.executeQuery()) {

    if (idRs.next()) {
        nextId = idRs.getInt(1);
    }
}

ps.setInt(1, nextId);
ps.setString(2, name);
ps.setString(3, sport);
ps.setDate(4, java.sql.Date.valueOf(startDate));
ps.setDate(5, java.sql.Date.valueOf(endDate));
        ps.executeUpdate();
        send(exchange, 200, "{\"message\":\"Tournament created successfully!\"}");
    }
}
    private static Map<String, String> parseForm(String body) {
    Map<String, String> data = new java.util.HashMap<>();

    for (String pair : body.split("&")) {
        String[] parts = pair.split("=", 2);

        if (parts.length == 2) {
            String key = java.net.URLDecoder.decode(
                parts[0], java.nio.charset.StandardCharsets.UTF_8
            );

            String value = java.net.URLDecoder.decode(
                parts[1], java.nio.charset.StandardCharsets.UTF_8
            );

            data.put(key, value);
        }
    }

    return data;
}
    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    private static void send(
            HttpExchange exchange, int status, String message)
            throws IOException {

        String json = "{\"message\":\"" + escape(message) + "\"}";
        sendJson(exchange, status, json);
    }
    private static void sendRaw(
        HttpExchange exchange, int status, String json)
        throws IOException {

    sendJson(exchange, status, json);
}

    private static void sendJson(
            HttpExchange exchange, int status, String json)
            throws IOException {

        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
 }
        private static void getMatches(HttpExchange exchange) throws Exception {

    String sql = """
        SELECT
            m.match_id,
            t.tournament_name,
            a.team_name AS team1_name,
            b.team_name AS team2_name,
            m.match_date,
            m.score1,
            m.score2,
            w.team_name AS winner_name,
            m.status
        FROM matches m
        JOIN tournaments t
            ON m.tournament_id = t.tournament_id
        JOIN teams a
            ON m.team1_id = a.team_id
        JOIN teams b
            ON m.team2_id = b.team_id
        LEFT JOIN teams w
            ON m.winner_id = w.team_id
        ORDER BY m.match_id
    """;

    try (
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        while (rs.next()) {

            if (!first) {
                json.append(",");
            }

            first = false;

            json.append("{")
                .append("\"match_id\":").append(rs.getInt("match_id")).append(",")
                .append("\"tournament_name\":\"").append(escape(rs.getString("tournament_name"))).append("\",")
                .append("\"team1_name\":\"").append(escape(rs.getString("team1_name"))).append("\",")
                .append("\"team2_name\":\"").append(escape(rs.getString("team2_name"))).append("\",")
                .append("\"match_date\":\"").append(rs.getString("match_date")).append("\",")
                .append("\"score1\":").append(rs.getObject("score1") == null ? "null" : rs.getInt("score1")).append(",")
                .append("\"score2\":").append(rs.getObject("score2") == null ? "null" : rs.getInt("score2")).append(",")
                .append("\"winner_name\":")
                .append(rs.getString("winner_name") == null ? "null" : "\"" + escape(rs.getString("winner_name")) + "\"")
                .append(",")
                .append("\"status\":\"").append(escape(rs.getString("status"))).append("\"")
                .append("}");
        }

        json.append("]");

        sendRaw(exchange, 200, json.toString());
    }
}

private static void getStandings(HttpExchange exchange) throws Exception {

    String query = exchange.getRequestURI().getRawQuery();

    if (query == null || !query.startsWith("tournament_id=")) {
        send(exchange, 400, "Please select a tournament");
        return;
    }

    Map<String, String> params = parseForm(query);
    int tournamentId;

    try {
        tournamentId = Integer.parseInt(params.get("tournament_id"));
    } catch (Exception e) {
        send(exchange, 400, "Invalid tournament ID");
        return;
    }

    String sql = """
        SELECT
            t.team_id,
            t.team_name,
            COUNT(m.match_id) AS played,
            COALESCE(SUM(
                CASE WHEN m.winner_id = t.team_id THEN 1 ELSE 0 END
            ), 0) AS won,
            COALESCE(SUM(
                CASE WHEN m.winner_id IS NULL
                AND m.match_id IS NOT NULL THEN 1 ELSE 0 END
            ), 0) AS draws,
            COALESCE(SUM(
                CASE WHEN m.winner_id IS NOT NULL
                AND m.winner_id != t.team_id THEN 1 ELSE 0 END
            ), 0) AS lost,
            (
                COALESCE(SUM(
                    CASE WHEN m.winner_id = t.team_id THEN 3 ELSE 0 END
                ), 0)
                +
                COALESCE(SUM(
                    CASE WHEN m.winner_id IS NULL
                    AND m.match_id IS NOT NULL THEN 1 ELSE 0 END
                ), 0)
            ) AS points
        FROM teams t
        JOIN tournaments tr ON tr.tournament_id = ?
            AND tr.sport = t.sport
        LEFT JOIN matches m
            ON m.tournament_id = tr.tournament_id
            AND m.status = 'Completed'
            AND (m.team1_id = t.team_id OR m.team2_id = t.team_id)
        GROUP BY t.team_id, t.team_name
        ORDER BY points DESC, won DESC, t.team_name
    """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, tournamentId);

        try (ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            int rank = 1;

            while (rs.next()) {

                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"rank\":").append(rank++).append(",")
                    .append("\"team_name\":\"")
                    .append(escape(rs.getString("team_name"))).append("\",")
                    .append("\"played\":").append(rs.getInt("played")).append(",")
                    .append("\"won\":").append(rs.getInt("won")).append(",")
                    .append("\"draws\":").append(rs.getInt("draws")).append(",")
                    .append("\"lost\":").append(rs.getInt("lost")).append(",")
                    .append("\"points\":").append(rs.getInt("points"))
                    .append("}");
            }

            json.append("]");
            sendRaw(exchange, 200, json.toString());
        }
    }
}

private static void getDashboardStats(
        HttpExchange exchange) throws Exception {

    String sql = """
        SELECT
            (SELECT COUNT(*) FROM teams) AS total_teams,
            (SELECT COUNT(*) FROM players) AS total_players,
            (SELECT COUNT(*) FROM tournaments) AS total_tournaments,
            (SELECT COUNT(*) FROM matches) AS total_matches
        """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            String json = "{"
                + "\"total_teams\":" + rs.getInt("total_teams") + ","
                + "\"total_players\":" + rs.getInt("total_players") + ","
                + "\"total_tournaments\":" + rs.getInt("total_tournaments") + ","
                + "\"total_matches\":" + rs.getInt("total_matches")
                + "}";

            sendRaw(exchange, 200, json);
        }
    }
}

private static void refreshAnalytics(HttpExchange exchange)
        throws Exception {

    ProcessBuilder pb = new ProcessBuilder(
        "py", "-3.12", "analysis.py"
    );

    pb.directory(new File("../Python"));

    pb.redirectErrorStream(true);

    Process process = pb.start();

    String output;

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        output = result.toString();
    }

    int exitCode = process.waitFor();

    
if (exitCode == 0) {

    String[] charts = {
        "team_points.png",
        "match_performance.png",
        "points_distribution.png",
        "goals_comparison.png",
        "player_participation.png"
    };

    for (String chart : charts) {

        File source = new File(
            "../Python/visualizations/" + chart
        );

        File destination = new File(
            "../Frontend/visualizations/" + chart
        );

        try (
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(destination)
        ) {
            in.transferTo(out);
        }
    }

    send(exchange, 200,
        "Analytics charts refreshed successfully!");

} else {
    send(exchange, 500, "Python error: " + output);
}

}


    private static void addMatch(HttpExchange exchange) throws Exception {

    String body = new String(
        exchange.getRequestBody().readAllBytes(),
        java.nio.charset.StandardCharsets.UTF_8
    );

    Map<String, String> data = parseForm(body);

    int tournamentId = Integer.parseInt(data.get("tournament_id"));
    int team1Id = Integer.parseInt(data.get("team1_id"));
    int team2Id = Integer.parseInt(data.get("team2_id"));
    String matchDate = data.get("match_date");

    if (team1Id == team2Id) {
        send(exchange, 400, "Please select two different teams");
        return;
    }

    String sql = """
        INSERT INTO matches
        (match_id, tournament_id, team1_id, team2_id, match_date, status)
        VALUES (?, ?, ?, ?, ?, 'Scheduled')
    """;

    try (Connection con = DBConnection.getConnection()) {

        int nextId = 1;

        String idSql = "SELECT COALESCE(MAX(match_id), 0) + 1 FROM matches";

        try (PreparedStatement idPs = con.prepareStatement(idSql);
             ResultSet idRs = idPs.executeQuery()) {

            if (idRs.next()) {
                nextId = idRs.getInt(1);
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nextId);
            ps.setInt(2, tournamentId);
            ps.setInt(3, team1Id);
            ps.setInt(4, team2Id);
            ps.setDate(5, java.sql.Date.valueOf(matchDate));

            ps.executeUpdate();

            send(exchange, 200, "Match scheduled successfully!");
         }
      }
} 

private static void updateMatchScore(HttpExchange exchange) throws Exception {

    Map<String, String> data = parseForm(
        new String(exchange.getRequestBody().readAllBytes())
    );

    int matchId = Integer.parseInt(data.get("match_id"));
    int score1 = Integer.parseInt(data.get("score1"));
    int score2 = Integer.parseInt(data.get("score2"));

    if (matchId <= 0 || score1 < 0 || score2 < 0) {
        send(exchange, 400, "Invalid match ID or score");
        return;
    }

    try (Connection con = DBConnection.getConnection()) {

        int team1Id;
        int team2Id;

        String selectSql =
            "SELECT team1_id, team2_id FROM matches WHERE match_id = ?";

        try (PreparedStatement ps = con.prepareStatement(selectSql)) {

            ps.setInt(1, matchId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    send(exchange, 404, "Match not found");
                    return;
                }

                team1Id = rs.getInt("team1_id");
                team2Id = rs.getInt("team2_id");
            }
        }

        String updateSql =
            "UPDATE matches SET score1 = ?, score2 = ?, winner_id = ?, status = 'Completed' WHERE match_id = ?";

        try (PreparedStatement ps = con.prepareStatement(updateSql)) {

            ps.setInt(1, score1);
            ps.setInt(2, score2);

            if (score1 > score2) {
                ps.setInt(3, team1Id);
            } else if (score2 > score1) {
                ps.setInt(3, team2Id);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setInt(4, matchId);
            ps.executeUpdate();
        }

        send(exchange, 200, "Match result updated successfully!");
    }
}

}