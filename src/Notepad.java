public class Notepad {
    private String generalNotes;
    private String[][] columnNotes;

    public Notepad() {
        this.generalNotes = "";
        this.columnNotes = new String[3][50];
    }

    public String getGeneralNotes() {
        return generalNotes;
    }

    public String getColumnNotes(int type, int id) {
        return columnNotes[type][id];
    }

    public void setGeneralNotes(String str) {
        this.generalNotes = str;
    }

    public void setColumnNotes(int type, int id, String str) {
        columnNotes[type][id] = str;
    }
}
