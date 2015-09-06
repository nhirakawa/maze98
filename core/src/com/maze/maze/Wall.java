package com.maze.maze;

/**
 * Created by nick on 8/23/15.
 */
public class Wall {

    private Cell cell1;
    private Cell cell2;

    public Wall(Cell cell1, Cell cell2){
        this.cell1 = cell1;
        this.cell2 = cell2;
    }

    public Cell getCell1() {
        return cell1;
    }

    public Cell getCell2() {
        return cell2;
    }

    public Cell getOther(Cell cell){
        if(this.cell1.equals(cell)){
            return this.cell2;
        }else{
            return this.cell1;
        }
    }

    public boolean contains(Cell cell){
        return this.cell1.equals(cell) || this.cell2.equals(cell);
    }

    @Override
    public int hashCode(){
        int result = 23;
        result = result * 37 + this.cell1.hashCode();
        result = result * 37 + this.cell2.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other){
        Wall wall = (Wall) other;
        return (this.getCell1().equals(wall.getCell1()) && this.getCell2().equals(wall.getCell2()))
                || (this.getCell1().equals(wall.getCell2()) && this.getCell2().equals(wall.getCell1()));
    }

    @Override
    public String toString(){
        return this.cell1.toString() + " | " + this.cell2.toString();
    }
}
