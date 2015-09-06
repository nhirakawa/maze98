package com.maze.maze;

import java.util.*;

/**
 * Created by nick on 8/23/15.
 */
public class Maze {

    private int width;
    private int height;
    private List<List<Cell>> maze;
    private List<Wall> walls;
    private Cell start;
    private Cell end;
    private Random random;

    public Maze(int width, int height){
        this.width = width;
        this.height = height;
        this.maze = new ArrayList<List<Cell>>();
        this.walls = new LinkedList<Wall>();
        this.start = null;
        this.end = null;
        this.random = new Random();
        init();
    }

    /**
     * Set up empty maze cells
     */
    private void init(){
        for(int i = 0; i < this.getWidth(); i++){
            List<Cell> row = new ArrayList<Cell>();
            for(int j = 0; j < this.getHeight(); j++){
                row.add(new Cell(i, j));
            }
            this.maze.add(row);
        }
        for(int i = 0; i < this.getWidth(); i++){
            for(int j = 0; j < this.getHeight(); j++){
                Cell cell = this.maze.get(i).get(j);
                if(cell.getX() > 0){
                    Cell below = getCell(i-1, j);
                    cell.addNeighbor(below);
                    addWallToList(new Wall(cell, below));
                }
                if(cell.getX() < this.getWidth() - 1){
                    Cell above = getCell(i+1, j);
                    cell.addNeighbor(above);
                    addWallToList(new Wall(cell, above));
                }
                if(cell.getY() > 0){
                    Cell left = getCell(i, j-1);
                    cell.addNeighbor(left);
                    addWallToList(new Wall(cell, left));
                }
                if(cell.getY() < this.getHeight() - 1){
                    Cell right = getCell(i, j+1);
                    cell.addNeighbor(right);
                    addWallToList(new Wall(cell, right));
                }
            }
        }
    }

    private void addWallToList(Wall wall){
        if(!this.walls.contains(wall)){
            this.walls.add(wall);
        }
    }

    public List<List<Cell>> getCells(){
        return this.maze;
    }

    public List<Wall> getWalls(){
        return this.walls;
    }

    private Cell getCell(int x, int y){
        return this.maze.get(x).get(y);
    }

    private Cell getRandomCell(){
        int x = this.random.nextInt(this.getWidth());
        int y = this.random.nextInt(this.getHeight());
        return getCell(x, y);
    }

    private Cell getRandomUnvisitedCell(){
        Cell cell = getRandomCell();
        while(cell.isVisited()){
            cell = getRandomCell();
        }
        return cell;
    }

    private boolean hasUnvisitedCells(){
        for(int i = 0; i < this.getWidth(); i++){
            for(int j = 0; j < this.getHeight(); j++){
                if(!getCell(i, j).isVisited()){
                    return true;
                }
            }
        }
        return false;
    }

    public void generate(){
        Cell current = getCell(0, 0);
        this.start = current;
        current.setVisited(true);
        List<Cell> unvisited = new LinkedList<Cell>();
        for(int i = 0; i < this.getWidth(); i++){
            for(int j = 0; j < this.getHeight(); j++){
                Cell cell = getCell(i, j);
                if(!cell.isVisited()){
                    unvisited.add(cell);
                }
            }
        }
        Stack<Cell> stack = new Stack<>();
        while(hasUnvisitedCells()){
            LinkedList<Cell> unvisitedNeighbors = new LinkedList<Cell>();
            for(Cell c : current.getNeighbors()){
                if(!c.isVisited()){
                    unvisitedNeighbors.add(c);
                }
            }
            if(unvisitedNeighbors.size() > 0){
                Cell randomNeighbor = getRandomNeighbor(unvisitedNeighbors);
                stack.push(current);
                Wall wall = new Wall(current, randomNeighbor);
                if(this.walls.contains(wall)){
                    this.walls.remove(wall);
                }
                current = randomNeighbor;
                current.setVisited(true);
            } else if(!stack.isEmpty()){
                current = stack.pop();
            } else {
                current = getRandomUnvisitedCell();
                current.setVisited(true);
            }
        }
        this.end = getCell(this.getWidth() -1, this.getHeight() -1);
        for(Wall wall : walls){
            Cell cell1 = wall.getCell1();
            Cell cell2 = wall.getCell2();
            cell1.removeNeighbor(cell2);
            cell2.removeNeighbor(cell1);
        }
    }

    public List<Cell> solve(){
        for(List<Cell> row : getCells()){
            for(Cell cell : row){
                cell.setVisited(false);
            }
        }
        Cell current = start;
        return rSolve(current);
    }

    private List<Cell> rSolve(Cell cell){
        cell.setVisited(true);
        if(cell.equals(end)){
            List<Cell> path = new LinkedList<>();
            path.add(end);
            return path;
        }else{
            if(cell.hasUnvisitedNeighbors()){
                List<Cell> unvisitedNeighbors = cell.getUnvisitedNeighbors();
                for(Cell neighbor : unvisitedNeighbors){
                    LinkedList<Cell> path = (LinkedList) rSolve(neighbor);
                    neighbor.setVisited(true);
                    if(path != null){
                        path.addFirst(cell);
                        return path;
                    }
                }
                return null;
            }else{
                return null;
            }
        }
    }

    private Cell getRandomNeighbor(List<Cell> neighbors){
        return neighbors.get(this.random.nextInt(neighbors.size()));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getStart() {
        return start;
    }

    public Cell getEnd() {
        return end;
    }
}
