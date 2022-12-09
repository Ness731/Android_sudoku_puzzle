package com.example.sudoku;

import java.util.*;

public class SudokuRule {
    int[][] board;
    int[][] groups = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};

    public SudokuRule(int[][] board) {
        this.board = board;
    }

    public boolean checkAll() {
        for (int row = 0; row < board.length; row++) { // 행
            for (int col = 0; col < board.length; col++) { // 열
                int code = isValid(row, col);
                if (code != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    //좌표 한 개를 입력받아 해당 숫자의 유효성을 검사하는 메소드
    public boolean check(int row, int col) {
        int code = isValid(row, col);
        if (code == -1) {
            return true;
        }
        return false;
    }

    public int isValid(int row, int col) {
        // 수평, 수직, 3x3 영역 모두 겹치지 않아야 함
        // 세 메소드 모두 겹치면 false를 반환
        if (!horizontalCheck(row, col))
            return 1;
        if (!verticalCheck(row, col))
            return 2;
        if (!checkSubBoard(row, col))
            return 3;
        return -1;
    }

    public boolean checkSubBoard(int row, int col) {
        int[] rowField = setField(row);
        int[] colField = setField(col);

        for (int n = 0; n < rowField.length; n++) {
            for (int m = 0; m < colField.length; m++) {
                if (board[row][col] != 0) {
                    if (!(rowField[n] == row && colField[m] == col) &&
                            board[rowField[n]][colField[m]] == board[row][col]) { //입력받은 좌표가 아니면서 숫자가 겹친다면
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean horizontalCheck(int row, int col) {
        for (int i = 0; i < board.length; i++) {
            if (board[row][col] > 0) {
                if (i != col && board[row][i] == board[row][col]) //입력받은 좌표가 아니면서 숫자가 겹친다면
                    return false;
            }
        }
        return true;
    }

    public boolean verticalCheck(int row, int col) {
        for (int i = 0; i < board.length; i++) {
            if (board[row][col] > 0) {
                if (i != row && board[i][col] == board[row][col]) //입력받은 좌표가 아니면서 숫자가 겹친다면
                    return false;
            }
        }
        return true;
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

    public ArrayList<int[]> setAllEmptyButton() {
        ArrayList<int[]> emptyButtons = new ArrayList<>();
        for (int[] rows : groups) {
            for (int[] cols : groups) {
                emptyButtons.addAll(setEmptyButton(rows, cols)); // 전체 보드에 대한 빈 블록 리스트를 구성
            }
        }
        return emptyButtons;
    }

    private ArrayList<int[]> setEmptyButton(int[] rows, int[] cols) {
        ArrayList<int[]> elements = new ArrayList<>();
        ArrayList<int[]> emptyButtons = new ArrayList<>();
        Random rand = new Random();
        int randomCount = rand.nextInt(5) + 2; // 빈 블록 개수는 2~6개

        for (int i = 0; i < rows.length; i++) { // 3x3 영역의 좌표를 hashmap 형태로 변환
            for (int j = 0; j < cols.length; j++) {
                elements.add(new int[]{rows[i], cols[j]});
            }
        }
        for (int i = 0; i < randomCount; i++) {
            int idx = rand.nextInt(elements.size());
            emptyButtons.add(elements.get(idx));
            elements.remove(idx);
        }
        return emptyButtons;
    }
}