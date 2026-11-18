import pandas as pd
import matplotlib.pyplot as plt
import mysql.connector
from pathlib import Path
from db_config import DB_CONFIG

OUTPUT_DIR = Path("visualizations")
OUTPUT_DIR.mkdir(exist_ok=True)

def main():
    try:
        with mysql.connector.connect(**DB_CONFIG) as connection:
            teams = pd.read_sql_query(
                "SELECT team_id, team_name FROM teams ORDER BY team_id", connection
            )
            matches = pd.read_sql_query(
                "SELECT tournament_id, team1_id, team2_id, score1, score2 "
                "FROM matches WHERE status='Completed' "
                "AND score1 IS NOT NULL AND score2 IS NOT NULL", connection
            )
            players = pd.read_sql_query(
                "SELECT t.team_name, p.participation FROM players p "
                "JOIN teams t ON p.team_id=t.team_id", connection
            )

        if teams.empty:
            print("No teams found. Add teams and try again.")
            return

        # Preprocess: remove incomplete rows and convert scores to integers.
        matches = matches.dropna(subset=["score1", "score2"]).copy()
        if not matches.empty:
            matches["score1"] = matches["score1"].astype(int)
            matches["score2"] = matches["score2"].astype(int)

        results = []
        for _, team in teams.iterrows():
            tid = int(team["team_id"])
            played = wins = draws = losses = points = 0
            goals_for = goals_against = 0

            for _, match in matches.iterrows():
                if int(match["team1_id"]) == tid:
                    own, opponent = int(match["score1"]), int(match["score2"])
                elif int(match["team2_id"]) == tid:
                    own, opponent = int(match["score2"]), int(match["score1"])
                else:
                    continue

                played += 1
                goals_for += own
                goals_against += opponent
                if own > opponent:
                    wins += 1
                    points += 3
                elif own == opponent:
                    draws += 1
                    points += 1
                else:
                    losses += 1

            results.append({
                "Team": team["team_name"], "Played": played,
                "Wins": wins, "Draws": draws, "Losses": losses,
                "Goals For": goals_for, "Goals Against": goals_against,
                "Points": points
            })

        table = pd.DataFrame(results).sort_values(
            ["Points", "Wins", "Goals For"], ascending=[False, False, False]
        ).reset_index(drop=True)

        print("\nTOURNAMENT PERFORMANCE")
        print(table.to_string(index=False))

        # 1. Points comparison
        plt.figure(figsize=(9, 5))
        plt.bar(table["Team"], table["Points"])
        plt.title("Team Points Comparison")
        plt.xlabel("Team")
        plt.ylabel("Points")
        plt.xticks(rotation=20)
        plt.tight_layout()
        plt.savefig(OUTPUT_DIR / "team_points.png", dpi=150, bbox_inches="tight")
        plt.close()

        # 2. Wins, draws and losses
        x = range(len(table))
        width = 0.25
        plt.figure(figsize=(9, 5))
        plt.bar([i-width for i in x], table["Wins"], width, label="Wins")
        plt.bar(list(x), table["Draws"], width, label="Draws")
        plt.bar([i+width for i in x], table["Losses"], width, label="Losses")
        plt.xticks(list(x), table["Team"], rotation=20)
        plt.title("Match Performance")
        plt.xlabel("Team")
        plt.ylabel("Number of Matches")
        plt.legend()
        plt.tight_layout()
        plt.savefig(OUTPUT_DIR / "match_performance.png", dpi=150, bbox_inches="tight")
        plt.close()

        # 3. Points distribution
        positive = table[table["Points"] > 0]
        if not positive.empty:
            plt.figure(figsize=(7, 7))
            plt.pie(positive["Points"], labels=positive["Team"],
                    autopct="%1.1f%%", startangle=90)
            plt.title("Tournament Points Distribution")
            plt.tight_layout()
            plt.savefig(OUTPUT_DIR / "points_distribution.png", dpi=150, bbox_inches="tight")
            plt.close()

        # 4. Goals for and against
        plt.figure(figsize=(9, 5))
        plt.plot(table["Team"], table["Goals For"], marker="o", label="Goals For")
        plt.plot(table["Team"], table["Goals Against"], marker="o", label="Goals Against")
        plt.title("Team Scoring Performance")
        plt.xlabel("Team")
        plt.ylabel("Goals")
        plt.xticks(rotation=20)
        plt.legend()
        plt.tight_layout()
        plt.savefig(OUTPUT_DIR / "goals_comparison.png", dpi=150, bbox_inches="tight")
        plt.close()

        # 5. Player participation
        if not players.empty:
            counts = players["participation"].value_counts()
            plt.figure(figsize=(8, 5))
            plt.bar(counts.index, counts.values)
            plt.title("Player Participation by Position")
            plt.xlabel("Position")
            plt.ylabel("Number of Players")
            plt.xticks(rotation=20)
            plt.tight_layout()
            plt.savefig(OUTPUT_DIR / "player_participation.png", dpi=150, bbox_inches="tight")
            plt.close()

        print("\nCharts saved in:", OUTPUT_DIR.resolve())

    except mysql.connector.Error as error:
        print("Database connection/query error:", error)
    except Exception as error:
        print("Unexpected error:", error)

if __name__ == "__main__":
    main()
