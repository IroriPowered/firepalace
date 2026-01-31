package cc.irori.firepalace.api.game.metadata;

public enum GameTag {

  // Height must be 24 pixels
  SOLO("solo", 80),
  TWO_OR_MORE("two_or_more", 64),
  THREE_OR_MORE("three_or_more", 64),
  FOUR_OR_MORE("four_or_more", 64),
  PVP("pvp", 40),
  ACTION("action", 80),
  COMING_SOON("coming_soon", 69)
  ;

  private final String id;
  private final int imageWidth;

  GameTag(String id, int imageWidth) {
    this.id = id;
    this.imageWidth = imageWidth;
  }

  public String getId() {
    return id;
  }

  public int getImageWidth() {
    return imageWidth;
  }
}
