package sample;
import java.util.*;

public class Graph {

    private HashMap<Integer, HashSet<Integer>> vertexMap = new HashMap<>();
    public HashMap<Integer, Integer> pathMap = new HashMap<>();
    public HashSet <Integer> visited = new HashSet<>();

    public void addVertex(Integer vertexName) {
        if (!hasVertex(vertexName)) {
            vertexMap.put(vertexName, new HashSet<Integer>());
        }
    }

    public boolean hasVertex(Integer vertexName) {
        return vertexMap.containsKey(vertexName);
    }

    public void addEdge(Integer vertexName1, Integer vertexName2) {
        if (!hasVertex(vertexName1)) addVertex(vertexName1);
        if (!hasVertex(vertexName2)) addVertex(vertexName2);
        HashSet<Integer> edges1 = vertexMap.get(vertexName1);
        HashSet<Integer> edges2 = vertexMap.get(vertexName2);
        edges1.add(vertexName2);
        edges2.add(vertexName1);
    }

    public Map<Integer, HashSet<Integer>> getVertexMap() {
        return vertexMap;
    }

    public int size () {
        return vertexMap.size();
    }

    public void printPath(Integer vertexOne, Integer vertexTwo){
        BFS(vertexOne, vertexTwo);
    }

    private void BFS(Integer start, Integer end){
        Integer tmp;
        ArrayList<Integer> queue = new ArrayList<>();
        queue.add(start);
        visited.clear();
        pathMap.clear();
        visited.add(start);
        while (!queue.isEmpty()){

            tmp = queue.get(0);
            queue.remove(0);
            if (tmp.intValue() == end.intValue()){
                Integer pass = pathMap.get(tmp);
                Main.result.appendText(end + "\n");
                while (true){
                    Main.result.appendText(pass + "\n");
                    if (pathMap.containsKey(pass)){
                        pass = pathMap.get(pass);
                    } else {
                        break;
                    }
                }
                break;
            }
            else {
                // Если ещё не нашли цель
                HashSet<Integer> lst = vertexMap.get(tmp);
                for (Integer i: lst){

                    if (!visited.contains(i)) {
                        queue.add(queue.size(), i);
                        //System.out.println("NOT VISITED " + i);
                        visited.add(i);
                        pathMap.put(i, tmp);
                    } else {
                        //System.out.print("VISUTED");
                    }
                }
            }
        }
        System.out.print(visited.size());

    }
}