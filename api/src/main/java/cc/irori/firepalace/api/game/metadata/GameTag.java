package cc.irori.firepalace.api.game.metadata;

public enum GameTag {

  SOLO("solo"),
  TWO_OR_MORE("two_or_more"),
  ;

  private final String id;

  GameTag(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
