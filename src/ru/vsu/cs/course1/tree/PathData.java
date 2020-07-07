package ru.vsu.cs.course1.tree;

import java.util.ArrayList;
import java.util.Stack;

public class PathData {


    public Stack<String> path= new Stack<String>();
    public int sum = 0;

    public void addSum(int sum){
        this.sum = sum;
    }


    public PathData(int sum, Stack<String> path){
        this.sum = sum;
        this.path = path;
    }

    public void setPath(Stack<String> stack){
        path = stack;
    }


}
