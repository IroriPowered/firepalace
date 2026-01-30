package cc.irori.firepalace.common.redis.protocol.impl.downstream;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.common.status.GameStatus;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public record DownstreamStatusPacket(List<GameStatus> statusList) implements DownstreamPacket {

  public static final String ID = "status";

  public DownstreamStatusPacket(BsonDocument root) {
    BsonArray array = root.getArray("status_list");
    List<GameStatus> list = new ArrayList<>();
    for (BsonValue value : array) {
      list.add(GameStatus.deserialize(value.asDocument()));
    }
    this(List.copyOf(list));
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void handle(DownstreamPacketHandler handler) {
    handler.handleStatus(this);
  }

  @Override
  public void serialize(BsonDocument root) {
    BsonArray array = new BsonArray();
    for (GameStatus status : statusList) {
      array.add(status.serialize(new BsonDocument()));
    }
    root.append("status_list", array);
  }
}
