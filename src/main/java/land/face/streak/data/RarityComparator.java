package land.face.streak.data;

import java.util.Comparator;

public class RarityComparator implements Comparator<Reward> {

  public int compare(Reward r1, Reward r2) {
    return Integer.compare(r2.getRarity().ordinal(), r1.getRarity().ordinal());
  }
}
