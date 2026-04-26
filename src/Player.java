public class Player {
    public int posX;
    public int prevPosX;
    public int posY;
    public int prevPosY;
    public int inRoom;
    public int prevInRoom;
    public String name;
    public Hand hand;
    public Notepad notes;
    public boolean hasGuessed;

    public Player(int posX, int posY, String name) {
        this.posX = posX;
        this.prevPosX = posX;
        this.posY = posY;
        this.prevPosY = posY;
        this.inRoom = -1;
        this.prevInRoom = -1;
        this.name = name;
        this.hand = new Hand();
        this.notes = new Notepad();
        this.hasGuessed = false;
    }

    public String getName() {
        return name;
    }
}
