import java.io.BufferedReader;
import java.io.IOException;

public class Room {
    private int[] entrancesX;
    private int[] entrancesY;
    private int[] positionsX;
    private int[] positionsY;
    private boolean[] occupied;
    private int warpTo;

    public Room(int id) {
        this.entrancesX = new int[0];
        this.entrancesY = new int[0];
        this.positionsX = new int[0];
        this.positionsY = new int[0];
        this.occupied = new boolean[0];
        this.warpTo = -1;
    }

    public Room(int id, BufferedReader br) throws IOException {
        br.reset();
        br.readLine();
        int roomStart = Integer.parseInt(br.readLine());
        moveToLine(br, roomStart);

        int roomCount = Integer.parseInt(br.readLine());
        int[] pointers = new int[roomCount];
        for (int i = 0; i < roomCount; i++) {
            pointers[i] = Integer.parseInt(br.readLine());
        }

        moveToLine(br, pointers[id]);
        warpTo = Integer.parseInt(br.readLine());

        int entranceCount = Integer.parseInt(br.readLine());
        entrancesX = new int[entranceCount];
        entrancesY = new int[entranceCount];
        for (int i = 0; i < entranceCount; i++) {
            entrancesX[i] = Integer.parseInt(br.readLine());
            entrancesY[i] = Integer.parseInt(br.readLine());
        }

        int posCount = Integer.parseInt(br.readLine());
        positionsX = new int[posCount];
        positionsY = new int[posCount];
        occupied = new boolean[posCount];
        for (int i = 0; i < posCount; i++) {
            positionsX[i] = Integer.parseInt(br.readLine());
            positionsY[i] = Integer.parseInt(br.readLine());
        }
    }

    private void moveToLine(BufferedReader br, int target) throws IOException {
        br.reset();
        for (int i = 1; i < target; i++) {
            br.readLine();
        }
    }

    public int[] occupy() {
        for (int i = 0; i < occupied.length; i++) {
            if (!occupied[i]) {
                occupied[i] = true;
                return new int[]{positionsX[i], positionsY[i]};
            }
        }
        return null;
    }

    public void clear(int posX, int posY) {
        for (int i = 0; i < positionsX.length; i++) {
            if (positionsX[i] == posX && positionsY[i] == posY) {
                occupied[i] = false;
                return;
            }
        }
    }

    public int getWarp() {
        return warpTo;
    }

    public int[] getEntrancesX() {
        return entrancesX;
    }

    public int[] getEntrancesY() {
        return entrancesY;
    }
}
