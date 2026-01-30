package cc.irori.firepalace.api.game.metadata;

public enum GameTag {

  // Height must be 24 pixels
  SOLO("solo", 80),
  TWO_OR_MORE("two_or_more", 80),
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
