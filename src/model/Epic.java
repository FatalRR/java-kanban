package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskList = new ArrayList<>();
    public Epic(String name, String description) {
    super(name, description);
    }

    public ArrayList<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void addSubtaskToEpic() {
        subTaskList.add(getId());
    }
    public void deleteSubtask () {
        subTaskList.remove(getId());
    }
}
