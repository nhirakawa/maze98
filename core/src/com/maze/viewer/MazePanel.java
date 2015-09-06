package com.maze.viewer;

import com.maze.maze.Cell;
import com.maze.maze.Maze;
import com.maze.maze.Wall;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * @author Nick Hirakawa
 */
public class MazePanel extends JPanel {

    private Maze maze;
    private int cellSize;
    private int gapSize;

    public MazePanel(Maze maze, int cellSize, int gapSize){
        this.maze = maze;
        this.cellSize = cellSize;
        this.gapSize = gapSize;
        this.setBackground(Color.WHITE);
        int width = (maze.getWidth() + 1) * (cellSize + gapSize) + gapSize;
        int height = (maze.getHeight() + 1) * (cellSize + gapSize) + gapSize;
        this.setPreferredSize(new Dimension(width, height));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        drawBorders(this.cellSize, this.gapSize, g);

        for(List<Cell> row : this.maze.getCells()){
            for(Cell cell : row){
                drawCell(cell, this.cellSize, this.gapSize, g);
            }
        }

        for(Wall wall : this.maze.getWalls()){
            drawWall(wall, this.cellSize, this.gapSize, g);
        }
    }

    private void drawCell(Cell cell, int cellSize, int gapSize, Graphics g){
        int x1 = cell.getX() * (cellSize + gapSize) + gapSize;
        int y1 = cell.getY() * (cellSize + gapSize) + gapSize;
        int width = cellSize;
        int height = cellSize;
        g.setColor(Color.WHITE);
        if(cell.equals(this.maze.getStart())){
            g.setColor(Color.GREEN);
        }
        if(cell.equals(this.maze.getEnd())){
            g.setColor(Color.RED);
        }
        g.fillRect(x1, y1, width, height);
    }

    private void drawWall(Wall wall, int cellSize, int gapSize, Graphics g){
        Cell cell1 = wall.getCell1();
        Cell cell2 = wall.getCell2();
        if(cell1.getX() == cell2.getX()){
            if(cell1.getY() > cell2.getY()){
                drawBottomWall(cell1.getX(), cell1.getY(), cellSize, gapSize, g);
            } else {
                drawTopWall(cell1.getX(), cell1.getY(), cellSize, gapSize, g);
            }
        } else {
            if(cell1.getX() < cell2.getX()){
                drawRightWall(cell1.getX(), cell1.getY(), cellSize, gapSize, g);
            }else{
                drawLeftWall(cell1.getX(), cell1.getY(), cellSize, gapSize, g);
            }
        }
    }

    private void drawTopWall(int x, int y, int cellSize, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        int x1 = x * (cellSize + gapSize) + gapSize;
        int y1 = y * (cellSize + gapSize) + gapSize + cellSize;
        int width = cellSize;
        int height = gapSize;
        g.fillRect(x1, y1, width, height);
        drawBorderConnectors(x1 - gapSize, y1, gapSize, g);
    }

    private void drawBottomWall(int x, int y, int cellSize, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        int x1 = x * (cellSize + gapSize) + gapSize;
        int y1 = y * (cellSize + gapSize);
        int width = cellSize;
        int height = gapSize;
        g.fillRect(x1, y1, width, height);
        drawBorderConnectors(x1+gapSize, y1, gapSize, g);
    }

    private void drawLeftWall(int x, int y, int cellSize, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        int x1 = x * (cellSize + gapSize);
        int y1 = y * (cellSize + gapSize) + gapSize;
        int width = gapSize;
        int height = cellSize;
        g.fillRect(x1, y1, width, height);
        drawBorderConnectors(x1, y1+gapSize, gapSize, g);
    }

    private void drawRightWall(int x, int y, int cellSize, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        int x1 = x * (cellSize + gapSize) + gapSize + cellSize;
        int y1 = y * (cellSize + gapSize) + gapSize;
        int width = gapSize;
        int height = cellSize;
        g.fillRect(x1, y1, width, height);
        drawBorderConnectors(x1, y1 + cellSize, gapSize, g);
        drawBorderConnectors(x1, y1-gapSize, gapSize, g);
    }

    private void drawBorderConnectors(int x1, int y1, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(x1, y1, gapSize, gapSize);
    }

    private void drawBorders(int cellSize, int gapSize, Graphics g){
        g.setColor(Color.BLACK);
        int x1 = 0;
        int y1 = 0;
        int width = maze.getWidth() * (gapSize + cellSize) + gapSize;
        int height = gapSize;
        g.fillRect(x1, y1, width, height);

        width = gapSize;
        height = maze.getHeight() * (gapSize + cellSize) + gapSize;
        g.fillRect(x1, y1, width, height);

        x1 = 0;
        y1 = maze.getHeight() * (gapSize + cellSize);
        width = maze.getWidth() * (cellSize + gapSize) + gapSize;
        height = gapSize;
        g.fillRect(x1, y1, width, height);

        x1 = maze.getWidth() * (gapSize + cellSize);
        y1 = 0;
        width = gapSize;
        height = maze.getHeight() * (cellSize + gapSize) + gapSize;
        g.fillRect(x1, y1, width, height);
    }
}
