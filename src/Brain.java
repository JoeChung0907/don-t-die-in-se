public interface Brain {
    void playerMove(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved);
    void playerMoveInRoom(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved);
    void playerMoveOutRoom(Player[] players, Tile[][] tiles, int playerId, int playerIdMoved);
    void playerSuggestion(Player[] players, Tile[][] tiles, int playerId, int playerIdSuggested, int playerIdShown, Card[] suggestion);
    void playerShows(Player[] players, Tile[][] tiles, int playerId, int playerIdShown, Card card);
    void playerShown(Player[] players, Tile[][] tiles, int playerId, int playerIdShows, Card card);
    void playerAccusationFail(Player[] players, Tile[][] tiles, int playerId, int playerIdSuggested, Card[] accusation);

    boolean makeSuggestion(Player[] players, Tile[][] tiles, int playerId);
    boolean makeAccusation(Player[] players, Tile[][] tiles, int playerId);
    Card[] cardsSuggestion(Player[] players, Tile[][] tiles, int playerId);
    Card[] cardsAccusation(Player[] players, Tile[][] tiles, int playerId);
    int[] moveTo(Player[] players, Tile[][] tiles, int playerId, int roll);
    boolean useWarp(Player[] players, Tile[][] tiles, int playerId);
    int selectCard(Player[] players, Tile[][] tiles, int playerId, Card[] cards);
}
