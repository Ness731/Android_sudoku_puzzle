package com.example.sudoku;

import java.util.*;

public class Sudoku {
    int[][] board;
    int[][] groups = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};

    public Sudoku(int[][] board) {
        this.board = board;
    }

    public boolean check() {
        String status = "";
        for (int row = 0; row < board.length; row++) { // 행
            for (int col = 0; col < board.length; col++) { // 열
                int code = isValid(row, col);
                if (code != -1) {
                    switch (code) {
                        case 1:
                            status = "수평 중복";
                            break;
                        case 2:
                            status = "수직 중복";
                            break;
                        case 3:
                            status = "3x3 중복";
                            break;
                    }
                    System.out.println("유효하지 않은 보드 : " + status);
                    System.out.println("좌표: " + row + ", " + col + " | 값 : " + board[row][col]);
                    return false;
                }
            }
        }
        System.out.println("유효한 보드");
        return true;
    }

    public int isValid(int row, int col) {
        // 수평, 수직, 3x3 영역 모두 겹치지 않아야 함
        // 세 메소드 모두 겹치면 false를 반환
        if (!horizontalCheck(row, col))
            return 1;
        if (!verticalCheck(col, col))
            return 2;
        if (!checkSubBoard(row, col))
            return 3;
        return -1;
    }

    public boolean checkSubBoard(int row, int col) {
        int[] rowField = setField(row);
        int[] colField = setField(col);

        System.out.println("입력 좌표: (" + row + ", " + col + ")");
        System.out.print("row 그룹:");
        print1dArr(rowField);
        System.out.print("col 그룹:");
        print1dArr(colField);

        for (int n = 0; n < rowField.length; n++) {
            for (int m = 0; m < colField.length; m++) {
                if (!(rowField[n] == row && colField[m] == col) &&
                        board[rowField[n]][colField[m]] == board[row][col]){ //입력받은 좌표가 아니면서 숫자가 겹친다면
                    System.out.println();
                    return false;
                }
            }
        }

        return true;
    }

    public boolean horizontalCheck(int row, int col) {
        for (int i = 0; i < board.length - 1; i++) {
            if (i != col && board[row][i] == board[row][col]) //입력받은 좌표가 아니면서 숫자가 겹친다면
                return false;
        }
        return true;
    }

    public boolean verticalCheck(int row, int col) {
        for (int i = 0; i < board.length - 1; i++) {
            if (i != row && board[i][col] == board[row][col]) //입력받은 좌표가 아니면서 숫자가 겹친다면
                return false;
        }
        return true;
    }

    public void print1dArr(int[] arr) {
        System.out.print("( ");
        for (int i = 0; i < arr.length; i++)
            System.out.print(arr[i] + " ");
        System.out.println(")");
    }

    private int[] setField(int n) {
        for (int[] g : groups) {
            for (int i = 0; i < g.length; i++) {
                if (g[i] == n)
                    return g;
            }
        }
        return null;
    }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) { // 행
            for (int j = 0; j < board.length; j++) { // 열
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        BoardGenerator bg = new BoardGenerator();
        int[][] board = bg.get();
        Sudoku test = new Sudoku(board);
        test.printBoard();
        test.check();

        int[][] board2 = bg.get();
        board2[0][2] = 5;
        board2[1][2] = 5;
        board2[2][2] = 5;
        Sudoku test2 = new Sudoku(board2);
        test2.printBoard();
        test2.check();
    }
}