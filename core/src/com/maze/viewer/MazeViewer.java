package com.maze.viewer;

import com.maze.maze.Maze;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nick Hirakawa
 */
public class MazeViewer extends JFrame {

    private JPanel panel;

    public MazeViewer(Maze maze){
        this(maze, 20);
    }

    public MazeViewer(Maze maze, int cellSize){
        this(maze, cellSize, cellSize / 4);
    }

    public MazeViewer(Maze maze, int cellSize, int gapSize){
        setTitle("jMaze");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel = new MazePanel(maze, cellSize, gapSize);
        setContentPane(this.panel);
        int width = (maze.getWidth() + 1) * (cellSize + gapSize) + gapSize;
        int height = (maze.getHeight() + 2) * (cellSize + gapSize) + gapSize;
        this.setPreferredSize(new Dimension(width, height));
        this.pack();
        this.setVisible(true);
    }
}
