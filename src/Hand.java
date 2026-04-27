import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hand {
    private ArrayList<Card> contents;

    public Hand() {
        this.contents = new ArrayList<>();
    }

    public Hand(Card[] cards) {
        this.contents = new ArrayList<>(Arrays.asList(cards));
    }

    public void addCard(Card card) {
        contents.add(card);
    }

    public ArrayList<Card> getCards() {
        return contents;
    }

    public Card[] hasCards(Card[] cards) {
        List<Card> matched = new ArrayList<>();
        for (Card c : cards) {
            if (contents.contains(c)) {
                matched.add(c);
            }
        }
        return matched.toArray(new Card[0]);
    }

    public boolean hasAllCards(Card[] cards) {
        for (Card c : cards) {
            if (!contents.contains(c)) {
                return false;
            }
        }
        return true;
    }
}
