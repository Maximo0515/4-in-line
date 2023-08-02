import java.util.Scanner;


public class cuatroenlinea {
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final char EMPTY = 'O';
    private static final char PLAYER = 'J';
    private static final char COMPUTER = 'C';

    private static char[][] board;

    

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            board = new char[ROWS][COLS];
            initializeBoard();

            printBoard();

            while (true) {
                // Turno del jugador
                int playerCol = getPlayerMove(scanner);
                int playerRow = getNextRow(playerCol);
                board[playerRow][playerCol] = PLAYER;
                printBoard();

                if (isWinner(PLAYER)) {
                    System.out.println("¡Felicidades! ¡Has ganado!");
                    break;
                }

                // Turno de la computadora
                int computerCol = getComputerMove();
                int computerRow = getNextRow(computerCol);
                board[computerRow][computerCol] = COMPUTER;
                printBoard();

                if (isWinner(COMPUTER)) {
                    System.out.println("Lo siento, la computadora ha ganado.");
                    break;
                }

                // Verificar si el tablero está lleno (empate)
                if (isBoardFull()) {
                    System.out.println("Empate. El tablero está lleno.");
                    break;
                }
            }
        }
    }

    private static void initializeBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    private static void printBoard() {
        // Imprimir números de columnas
        for (int i = 1; i <= COLS; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // Imprimir tablero con borde superior
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static int getPlayerMove(Scanner scanner) {
        int playerCol;

        while (true) {
            System.out.print("Elige una columna (1-7): ");

            // Verificar si hay un entero disponible para leer
            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida. Debes ingresar un número entre 1 y 7.");
                System.out.print("Elige una columna (1-7): ");
                scanner.next(); // Consumir el valor no válido
            }

            playerCol = scanner.nextInt() - 1;

            if (playerCol >= 0 && playerCol < COLS && isColumnAvailable(playerCol)) {
                break;
            }

            System.out.println("Movimiento inválido. Intenta de nuevo.");
        }

        return playerCol;
    }

    private static boolean isColumnAvailable(int col) {
        return board[0][col] == EMPTY;
    }

    private static int getNextRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                return row;
            }
        }
        return -1; // Columna llena, esto no debería suceder si se verifica antes de llamar a esta función.
    }

    private static boolean isWinner(char symbol) {
        // Verificar filas
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == symbol &&
                    board[row][col + 1] == symbol &&
                    board[row][col + 2] == symbol &&
                    board[row][col + 3] == symbol) {
                    return true;
                }
            }
        }

        // Verificar columnas
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS - 3; row++) {
                if (board[row][col] == symbol &&
                    board[row + 1][col] == symbol &&
                    board[row + 2][col] == symbol &&
                    board[row + 3][col] == symbol) {
                    return true;
                }
            }
        }

        // Verificar diagonales descendentes
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == symbol &&
                    board[row + 1][col + 1] == symbol &&
                    board[row + 2][col + 2] == symbol &&
                    board[row + 3][col + 3] == symbol) {
                    return true;
                }
            }
        }

        // Verificar diagonales ascendentes
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == symbol &&
                    board[row - 1][col + 1] == symbol &&
                    board[row - 2][col + 2] == symbol &&
                    board[row - 3][col + 3] == symbol) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (isColumnAvailable(col)) {
                return false;
            }
        }
        return true;
    }

    private static int getComputerMove() {
        int[] scores = new int[COLS];
        int maxScore = 0;
        int bestCol = COLS / 2; // Iniciar en la columna central

        // Primero, intentar bloquear al jugador si este tiene una oportunidad de ganar
        for (int col = 0; col < COLS; col++) {
            if (isColumnAvailable(col)) {
                int row = getNextRow(col);
                board[row][col] = PLAYER;
                if (isWinner(PLAYER)) {
                    board[row][col] = EMPTY;
                    return col;
                }
                board[row][col] = EMPTY;
            }
        }

        // Si no hay oportunidad de bloquear al jugador, evaluar cada columna para determinar el puntaje
        for (int col = 0; col < COLS; col++) {
            if (isColumnAvailable(col)) {
                int row = getNextRow(col);

                // Evaluar puntaje si la computadora coloca su ficha en esta columna
                board[row][col] = COMPUTER;
                scores[col] = evaluateMove(COMPUTER);
                board[row][col] = EMPTY;

                // Actualizar mejor columna si se encuentra una con puntaje más alto
                if (scores[col] >= maxScore) {
                    maxScore = scores[col];
                    bestCol = col;
                }
            }
        }

        return bestCol;
    }

    private static int evaluateMove(char symbol) {
        int score = 0;
        int[] consecutive = new int[4];

        // Evaluar filas
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                updateConsecutive(board[row][col], consecutive);
                if (board[row][col] == EMPTY) {
                    score += getScore(consecutive, symbol);
                }
            }
            resetConsecutive(consecutive);
        }

        // Evaluar columnas
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                updateConsecutive(board[row][col], consecutive);
                if (board[row][col] == EMPTY) {
                    score += getScore(consecutive, symbol);
                }
            }
            resetConsecutive(consecutive);
        }

        // Evaluar diagonales descendentes
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int startRow = Math.min(row + col, ROWS - 1);
                int startCol = Math.max(col - row, 0);
                for (int i = 0; i < ROWS && startRow - i >= 0 && startCol + i < COLS; i++) {
                    updateConsecutive(board[startRow - i][startCol + i], consecutive);
                }
                if (startRow - ROWS + 1 >= 0) {
                    score += getScore(consecutive, symbol);
                }
                resetConsecutive(consecutive);
            }
        }

        // Evaluar diagonales ascendentes
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int startRow = Math.max(row - COLS + col + 1, 0);
                int startCol = Math.max(col - row, 0);
                for (int i = 0; i < ROWS && startRow + i < ROWS && startCol + i < COLS; i++) {
                    updateConsecutive(board[startRow + i][startCol + i], consecutive);
                }
                if (startRow + 3 < ROWS) {
                    score += getScore(consecutive, symbol);
                }
                resetConsecutive(consecutive);
            }
        }

        return score;
    }

    private static void updateConsecutive(char cell, int[] consecutive) {
        if (cell == COMPUTER) {
            consecutive[0]++;
            consecutive[1] = 0;
        } else if (cell == PLAYER) {
            consecutive[1]++;
            consecutive[0] = 0;
        } else {
            consecutive[0] = 0;
            consecutive[1] = 0;
        }
    }

    private static void resetConsecutive(int[] consecutive) {
        consecutive[0] = 0;
        consecutive[1] = 0;
    }

    private static int getScore(int[] consecutive, char symbol) {
        if (consecutive[0] == 3 && symbol == COMPUTER) {
            return 100;
        } else if (consecutive[0] == 3 && symbol == PLAYER) {
            return 50;
        } else if (consecutive[1] == 3 && symbol == COMPUTER) {
            return 80;
        } else if (consecutive[1] == 3 && symbol == PLAYER) {
            return 30;
        } else if (consecutive[0] == 2 && symbol == COMPUTER) {
            return 10;
        } else if (consecutive[0] == 2 && symbol == PLAYER) {
            return 5;
        } else if (consecutive[1] == 2 && symbol == COMPUTER) {
            return 7;
        } else if (consecutive[1] == 2 && symbol == PLAYER) {
            return 3;
        } else {
            return 0;
        }
    }
}
