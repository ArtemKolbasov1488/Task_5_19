package ru.vsu.cs;

import ru.vsu.cs.course1.tree.BinaryTree;
import ru.vsu.cs.course1.tree.DefaultBinaryTree;
import ru.vsu.cs.course1.tree.PathData;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Function;

public class intBinaryTree<Integer> extends BinaryTree<Integer> implements DefaultBinaryTree<Integer> {
    public intBinaryTree(Function<String, Integer> fromStrFunc, Function<Integer, String> toStrFunc) {
        super(fromStrFunc,toStrFunc);
    }

    public intBinaryTree(Function<String, Integer> fromStrFunc) {
        super(fromStrFunc,x->x.toString());
    }

    public intBinaryTree() {
        this(null);
    }

    public ArrayList<PathData> searchMaxTrees(){
        class Inner{
            int globalMax = 0;
            ArrayList<PathData> paths = new ArrayList<>();

            Stack<String> curStack = new Stack<String>();

            public int searchMax(SimpleTreeNode node, boolean isLeft, boolean isRight){
                if(node == null)
                    return 0;
                if(isLeft || isRight)
                {
                    if(isLeft)
                        curStack.push("L"+node.value);
                    else
                        curStack.push("R"+ node.value);
                }
                int leftSum =searchMax(node.left,true,false);
                int rightSum = searchMax(node.right,false,true) ;
                int sum = leftSum + rightSum;

                sum = sum + java.lang.Integer.parseInt(node.value.toString());

                if(sum == globalMax && node!=root)
                {
                    paths.add(new PathData(sum,(Stack<String>) curStack.clone()));
                }
                if(sum>globalMax && node!=root) {

                    globalMax = sum;
                    if(paths.size()!=0)
                        paths.removeAll(paths);
                    paths.add(new PathData(sum,(Stack<String>) curStack.clone()));
                }
                if(!curStack.empty())
                curStack.pop();
                return sum;

            }
        }
        Inner inner= new Inner();
        inner.searchMax(super.root,false,false);
        return inner.paths;
    }




}
