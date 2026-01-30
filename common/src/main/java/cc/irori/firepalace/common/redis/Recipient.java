package cc.irori.firepalace.common.redis;

public enum Recipient {

  UPSTREAM("upstream"),
  DOWNSTREAM("downstream"),
  ;

  private final String id;

  Recipient(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
