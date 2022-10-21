package model;

public class Subtask extends Task {
    private Epic epic;
    public int epicId = 0;
    public Subtask(String name,String description) {
        super(name, description);
        this.id=epicId;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

}
