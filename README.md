# 🎮 Tic Tac Toe — With Unbeatable AI

A modern, polished Tic Tac Toe game featuring an **unbeatable AI** powered by the **Minimax algorithm with Alpha-Beta Pruning**. Available as a **Java desktop app** and a **web version** playable in any browser.

[![Play Online](https://img.shields.io/badge/▶_Play_Online-Click_Here-00d4ff?style=for-the-badge&logo=googlechrome&logoColor=white)](https://its-prashanttechie.github.io/TicTacToe-With-Unbeatable-AI/)

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Build](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ Features

- **🤖 Three Difficulty Levels:**
  - **Easy** — AI tries to win/block but otherwise plays randomly
  - **Medium** — Mix of optimal and random play (60/40)
  - **Hard** — Unbeatable Minimax AI with Alpha-Beta Pruning

- **🎨 Modern Dark Theme UI** — Custom-painted components with:
  - Neon cyan (X) and magenta (O) marks
  - Hover effects on cells
  - Winning line glow animation
  - Rounded, pill-shaped buttons

- **📊 Live Score Tracking** — Tracks Wins, Losses, and Draws across rounds

- **🏗️ Clean Architecture** — Game logic fully separated from UI (MVC pattern)

- **🌐 Web Version** — Play directly in your browser via GitHub Pages, no install needed

---

## 🌐 Play Online

**No installation required!** Play the game right in your browser:

👉 **[https://its-prashanttechie.github.io/TicTacToe-With-Unbeatable-AI/](https://its-prashanttechie.github.io/TicTacToe-With-Unbeatable-AI/)**

---

## 🚀 How to Run (Desktop Version)

### Prerequisites
- **Java 17+** installed ([Download](https://adoptium.net/))
- **Maven 3.6+** installed ([Download](https://maven.apache.org/download.cgi))

### Build & Run

```bash
# Clone the repository
git clone https://github.com/Its-PrashantTechie/TicTacToe-With-Unbeatable-AI.git
cd TicTacToe-With-Unbeatable-AI

# Compile and run
mvn clean compile exec:java

# Or build a runnable JAR
mvn clean package
java -jar target/tictactoe-ai-1.0.0.jar
```

### Run in IDE
Just open the project in **IntelliJ IDEA**, **Eclipse**, or **VS Code** — Maven auto-configures everything. Click ▶️ Run on `Main.java`.

---

## 🧠 How the AI Works

The Hard mode AI uses the **Minimax algorithm** — a decision-making algorithm for two-player zero-sum games.

**How it works:**
1. For every possible move, the AI simulates all future game states recursively
2. It assumes the opponent also plays optimally (minimizes AI's score)
3. The AI picks the move that maximizes its guaranteed outcome

**Alpha-Beta Pruning** optimization trims branches that can't possibly affect the final decision, making the search faster.

```
Result: The Hard AI is mathematically unbeatable — it will always win or draw.
```

---

## 📁 Project Structure

```
├── src/main/java/com/tictactoe/   # Java Desktop App
│   ├── Main.java                  # Application entry point
│   ├── GameFrame.java             # Window frame setup
│   ├── GameLogic.java             # Core game logic & AI
│   └── GamePanel.java             # Dark-themed UI components
│
├── docs/                          # Web Version (GitHub Pages)
│   ├── index.html                 # Game page structure
│   ├── style.css                  # Dark theme styling
│   └── game.js                    # Game logic & AI (JS port)
│
└── pom.xml                        # Maven build config
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17    | Desktop app core language |
| Swing      | Desktop GUI framework |
| HTML/CSS/JS | Web version |
| Minimax    | AI decision-making |
| Alpha-Beta Pruning | AI optimization |
| Maven      | Build & dependency management |
| GitHub Pages | Web hosting |

---

## 📝 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

