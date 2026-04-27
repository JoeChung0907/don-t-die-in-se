import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomBrain implements Brain {
    private final Random rng;
    private final Card[] suspects;
    private final Card[] weapons;
    private final Card[] roomCards;
    private final Room[] rooms;

    public RandomBrain(Card[] suspects, Card[] weapons, Card[] roomCards, Room[] rooms) {
        this.suspects = suspects;
        this.weapons = weapons;
        this.roomCards = roomCards;
        this.rooms = rooms;
        this.rng = new Random();
    }

    @Override
    public void playerMove(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved) {}

    @Override
    public void playerMoveInRoom(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved) {}

    @Override
    public void playerMoveOutRoom(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved) {}

    @Override
    public void playerSuggestion(Player[] players, Tile[][] tiles, int playerId, int playerIdSuggested, int playerIdShown, Card[] suggestion) {}

    @Override
    public void playerShows(Player[] players, Tile[][] tiles, int playerId, int playerIdShown, Card card) {}

    @Override
    public void playerShown(Player[] players, Tile[][] tiles, int playerId, int playerIdShows, Card card) {}

    @Override
    public void playerAccusationFail(Player[] players, Tile[][] tiles, int playerId, int playerIdSuggested, Card[] accusation) {}

    @Override
    public boolean makeSuggestion(Player[] players, Tile[][] tiles, int playerId) {
        return rng.nextDouble() < 0.7;
    }

    @Override
    public boolean makeAccusation(Player[] players, Tile[][] tiles, int playerId) {
        return rng.nextDouble() < 0.05;
    }

    @Override
    public Card[] cardsSuggestion(Player[] players, Tile[][] tiles, int playerId) {
        Player p = players[playerId];
        Card roomCard = (p.inRoom >= 0 && p.inRoom < roomCards.length)
                ? roomCards[p.inRoom]
                : roomCards[rng.nextInt(roomCards.length)];
        return new Card[] {
                suspects[rng.nextInt(suspects.length)],
                weapons[rng.nextInt(weapons.length)],
                roomCard
        };
    }

    @Override
    public Card[] cardsAccusation(Player[] players, Tile[][] tiles, int playerId) {
        return new Card[] {
                suspects[rng.nextInt(suspects.length)],
                weapons[rng.nextInt(weapons.length)],
                roomCards[rng.nextInt(roomCards.length)]
        };
    }

    @Override
    public int[] moveTo(Player[] players, Tile[][] tiles, int playerId, int roll) {
        Player p = players[playerId];

        // in a room 
        if (p.inRoom != -1) {
            // 40% chance to stay in room
            if (rng.nextDouble() < 0.4) {
                return new int[] {p.posX, p.posY};
            }
            // find free doors to exit
            int[] xs = rooms[p.inRoom].getEntrancesX();
            int[] ys = rooms[p.inRoom].getEntrancesY();
            List<int[]> freeEntrances = new ArrayList<>();
            for (int i = 0; i < xs.length; i++) {
                int ex = xs[i], ey = ys[i];
                if (ex >= 0 && ey >= 0 && ex < tiles.length && ey < tiles[0].length
                        && tiles[ex][ey].isEnterable && !tiles[ex][ey].isOccupied) {
                    freeEntrances.add(new int[] {ex, ey});
                }
            }
            if (freeEntrances.isEmpty()) return new int[] {p.posX, p.posY}; // no exit, stay
            return freeEntrances.get(rng.nextInt(freeEntrances.size())); // pick random door
        }

        //BFS to find all reachable tiles within dice roll
        int W = tiles.length;
        int H = tiles[0].length;
        int[][] dist = new int[W][H];
        for (int[] row : dist) Arrays.fill(row, -1);
        dist[p.posX][p.posY] = 0;

        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {p.posX, p.posY});

        List<int[]> reachable = new ArrayList<>();
        reachable.add(new int[] {p.posX, p.posY}); // staying is also an option

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int d = dist[cur[0]][cur[1]];
            if (d >= roll) continue; // can't go further than dice roll
            for (int i = 0; i < 4; i++) {
                int nx = cur[0] + dx[i];
                int ny = cur[1] + dy[i];
                if (nx < 0 || ny < 0 || nx >= W || ny >= H) continue; // out of bounds
                if (dist[nx][ny] != -1) continue; // already visited
                Tile t = tiles[nx][ny];
                if (!t.isEnterable || t.isOccupied) continue; // wall or blocked
                dist[nx][ny] = d + 1;
                if (t.doorTo != -1) {
                    reachable.add(new int[] {-1, t.doorTo}); // door: can enter room
                } else {
                    reachable.add(new int[] {nx, ny}); // normal tile
                    queue.add(new int[] {nx, ny}); // keep searching from here
                }
            }
        }

        return reachable.get(rng.nextInt(reachable.size())); // pick random destination
    }

    @Override
    public boolean useWarp(Player[] players, Tile[][] tiles, int playerId) {
        return rng.nextDouble() < 0.3;
    }

    @Override
    public int selectCard(Player[] players, Tile[][] tiles, int playerId, Card[] cards) {
        return rng.nextInt(cards.length);
    }
}
