/**
 * Tic Tac Toe — Game Logic & UI Controller
 * Direct port of GameLogic.java to JavaScript
 * Implements Minimax with Alpha-Beta Pruning
 *
 * @author Prashant
 */

// ═══════════════════════════════════════════════════════════
//  Game Logic (ported from GameLogic.java)
// ═══════════════════════════════════════════════════════════

const WIN_LINES = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8], // rows
    [0, 3, 6], [1, 4, 7], [2, 5, 8], // columns
    [0, 4, 8], [2, 4, 6]              // diagonals
];

const EMPTY = 0, X_MARK = 1, O_MARK = 2;
const IN_PROGRESS = 0, X_WINS = 1, O_WINS = 2, DRAW = 3;

let board = new Array(9).fill(EMPTY);
let playerTurn = true;
let gameOver = false;
let difficulty = 'easy';
let winningLine = null;

let scores = { player: 0, ai: 0, draws: 0 };

// ─── Core Game Functions ──────────────────────────────────

function makePlayerMove(index) {
    if (index < 0 || index > 8 || board[index] !== EMPTY || !playerTurn) {
        return false;
    }
    board[index] = X_MARK;
    playerTurn = false;
    return true;
}

function makeAIMove() {
    if (playerTurn || getGameResult() !== IN_PROGRESS) return -1;

    let move;
    switch (difficulty) {
        case 'easy': move = easyMove(); break;
        case 'medium': move = mediumMove(); break;
        case 'hard': move = hardMove(); break;
        default: move = easyMove();
    }

    if (move !== -1) {
        board[move] = O_MARK;
        playerTurn = true;
    }
    return move;
}

function getGameResult() {
    for (const line of WIN_LINES) {
        if (board[line[0]] !== EMPTY &&
            board[line[0]] === board[line[1]] &&
            board[line[1]] === board[line[2]]) {
            winningLine = line;
            return board[line[0]] === X_MARK ? X_WINS : O_WINS;
        }
    }
    if (board.every(cell => cell !== EMPTY)) return DRAW;
    return IN_PROGRESS;
}

function resetBoard() {
    board.fill(EMPTY);
    playerTurn = true;
    gameOver = false;
    winningLine = null;
}

// ─── AI Strategies ────────────────────────────────────────

/** Easy: tries to win or block, otherwise random. */
function easyMove() {
    let move = findWinningMove(O_MARK);
    if (move !== -1) return move;
    move = findWinningMove(X_MARK);
    if (move !== -1) return move;
    return randomMove();
}

/** Medium: 60% chance of playing optimal (minimax), 40% random. */
function mediumMove() {
    if (Math.random() < 0.6) return hardMove();
    return easyMove();
}

/** Hard: full Minimax with alpha-beta pruning — unbeatable. */
function hardMove() {
    let bestScore = -Infinity;
    let bestMove = -1;

    for (let i = 0; i < 9; i++) {
        if (board[i] === EMPTY) {
            board[i] = O_MARK;
            const score = minimax(false, -Infinity, Infinity);
            board[i] = EMPTY;
            if (score > bestScore) {
                bestScore = score;
                bestMove = i;
            }
        }
    }
    return bestMove;
}

/**
 * Minimax algorithm with alpha-beta pruning.
 * @param {boolean} isMaximizing - true if AI's turn
 * @param {number} alpha - best score the maximizer can guarantee
 * @param {number} beta - best score the minimizer can guarantee
 * @returns {number} evaluated score
 */
function minimax(isMaximizing, alpha, beta) {
    const result = evaluateBoard();
    if (result !== IN_PROGRESS) {
        if (result === O_WINS) return 10;
        if (result === X_WINS) return -10;
        return 0; // DRAW
    }

    if (isMaximizing) {
        let maxScore = -Infinity;
        for (let i = 0; i < 9; i++) {
            if (board[i] === EMPTY) {
                board[i] = O_MARK;
                const score = minimax(false, alpha, beta);
                board[i] = EMPTY;
                maxScore = Math.max(score, maxScore);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
        }
        return maxScore;
    } else {
        let minScore = Infinity;
        for (let i = 0; i < 9; i++) {
            if (board[i] === EMPTY) {
                board[i] = X_MARK;
                const score = minimax(true, alpha, beta);
                board[i] = EMPTY;
                minScore = Math.min(score, minScore);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
        }
        return minScore;
    }
}

/** Evaluates board state without side effects. */
function evaluateBoard() {
    for (const line of WIN_LINES) {
        if (board[line[0]] !== EMPTY &&
            board[line[0]] === board[line[1]] &&
            board[line[1]] === board[line[2]]) {
            return board[line[0]] === X_MARK ? X_WINS : O_WINS;
        }
    }
    if (board.every(cell => cell !== EMPTY)) return DRAW;
    return IN_PROGRESS;
}

/** Finds a move that immediately wins for the given mark. */
function findWinningMove(mark) {
    for (const line of WIN_LINES) {
        const [a, b, c] = [board[line[0]], board[line[1]], board[line[2]]];
        if (a === mark && b === mark && c === EMPTY) return line[2];
        if (a === mark && c === mark && b === EMPTY) return line[1];
        if (b === mark && c === mark && a === EMPTY) return line[0];
    }
    return -1;
}

/** Picks a random empty cell. */
function randomMove() {
    const empty = [];
    for (let i = 0; i < 9; i++) {
        if (board[i] === EMPTY) empty.push(i);
    }
    return empty.length === 0 ? -1 : empty[Math.floor(Math.random() * empty.length)];
}

// ═══════════════════════════════════════════════════════════
//  UI Controller
// ═══════════════════════════════════════════════════════════

const cells = document.querySelectorAll('.cell');
const statusEl = document.getElementById('status');
const scorePlayerEl = document.getElementById('score-player');
const scoreAIEl = document.getElementById('score-ai');
const scoreDrawEl = document.getElementById('score-draw');
const modalOverlay = document.getElementById('modal-overlay');
const modalMessage = document.getElementById('modal-message');
const diffButtons = document.querySelectorAll('.pill-btn[data-diff]');

// ─── Cell Click Handler ───────────────────────────────────

cells.forEach(cell => {
    cell.addEventListener('click', () => {
        if (gameOver || !playerTurn) return;

        const index = parseInt(cell.dataset.index);
        if (makePlayerMove(index)) {
            renderCell(index, 'X');

            const result = getGameResult();
            if (result !== IN_PROGRESS) {
                endGame(result);
                return;
            }

            statusEl.textContent = 'AI is thinking...';
            statusEl.className = 'status';

            // Slight delay for AI move to feel natural
            setTimeout(() => {
                const aiMove = makeAIMove();
                if (aiMove !== -1) {
                    renderCell(aiMove, 'O');
                }

                const afterAI = getGameResult();
                if (afterAI !== IN_PROGRESS) {
                    endGame(afterAI);
                } else {
                    statusEl.textContent = 'Your turn — place X';
                    statusEl.className = 'status';
                }
            }, 300);
        }
    });
});

// ─── Render a Mark on a Cell ──────────────────────────────

function renderCell(index, mark) {
    const cell = cells[index];
    cell.textContent = mark;
    cell.classList.add(mark.toLowerCase(), 'taken', 'animate-mark');
}

// ─── End Game ─────────────────────────────────────────────

function endGame(result) {
    gameOver = true;

    // Update scores
    if (result === X_WINS) scores.player++;
    else if (result === O_WINS) scores.ai++;
    else if (result === DRAW) scores.draws++;
    updateScoreDisplay();

    // Highlight winning cells
    if (winningLine) {
        winningLine.forEach(idx => cells[idx].classList.add('winning'));
    }

    // Mark all cells as game-over to disable hover
    cells.forEach(c => c.classList.add('game-over'));

    // Status message
    let message;
    if (result === X_WINS) {
        message = '🎉 You win!';
        statusEl.className = 'status win';
    } else if (result === O_WINS) {
        message = 'AI wins — try again!';
        statusEl.className = 'status lose';
    } else {
        message = "It's a draw!";
        statusEl.className = 'status';
    }
    statusEl.textContent = message;

    // Show modal after a moment
    setTimeout(() => {
        modalMessage.textContent = message + '\nPlay again?';
        modalOverlay.classList.add('show');
    }, 1200);
}

// ─── Reset Game ───────────────────────────────────────────

function resetGame() {
    resetBoard();
    cells.forEach(cell => {
        cell.textContent = '';
        cell.className = 'cell';
    });
    statusEl.textContent = 'Your turn — place X';
    statusEl.className = 'status';
    modalOverlay.classList.remove('show');
}

// ─── Score Display ────────────────────────────────────────

function updateScoreDisplay() {
    scorePlayerEl.textContent = scores.player;
    scoreAIEl.textContent = scores.ai;
    scoreDrawEl.textContent = scores.draws;
}

// ─── Difficulty Buttons ───────────────────────────────────

diffButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        difficulty = btn.dataset.diff;
        diffButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    });
});

// ─── New Game & Modal Buttons ─────────────────────────────

document.getElementById('btn-new-game').addEventListener('click', resetGame);
document.getElementById('btn-rematch').addEventListener('click', resetGame);
document.getElementById('btn-close').addEventListener('click', () => {
    modalOverlay.classList.remove('show');
});
