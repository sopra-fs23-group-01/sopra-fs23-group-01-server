package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);
  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "registerDate", target = "registerDate")
  @Mapping(source = "token", target = "token")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "registerDate", target = "registerDate")
  @Mapping(source = "rateDe", target = "rateDe")
  @Mapping(source = "rateUn", target = "rateUn")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "intro", target = "intro")
  @Mapping(source = "gender", target = "gender")
  @Mapping(source = "avatarUrl", target = "avatarUrl")
  @Mapping(source = "role", target = "role")
  @Mapping(source = "card", target = "card")
  UserGetDTO convertEntityToUserGetDTO(User user);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(source = "rateDe", target = "rateDe")
  @Mapping(source = "rateUn", target = "rateUn")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "intro", target = "intro")
  @Mapping(source = "gender", target = "gender")
  @Mapping(source = "avatarUrl", target = "avatarUrl")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

  @Mapping(source = "roomId", target = "roomId")
  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "roomProperty", target = "roomProperty")
  @Mapping(source = "roomOwnerId", target = "roomOwnerId")
  //@Mapping(source = "roomPlayers", target = "roomPlayers")
  //@Mapping(source = "token", target = "token")
  Room convertRoomPostDTOtoEntity(RoomPostDTO roomPostDTO);

  @Mapping(source = "roomId", target = "roomId")
  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "roomProperty", target = "roomProperty")
  @Mapping(source = "roomOwnerId", target = "roomOwnerId")
  @Mapping(source = "roomPlayers", target = "roomPlayers")
  @Mapping(source = "roomPlayersList", target = "roomPlayersList")
  //@Mapping(source = "token", target = "token")
  RoomGetDTO convertEntityToRoomGetDTO(Room room);

  @Mapping(source = "roomId", target = "roomId")
  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "roomProperty", target = "roomProperty")
  @Mapping(source = "roomOwnerId", target = "roomOwnerId")
  @Mapping(source = "roomPlayers", target = "roomPlayers")
      //@Mapping(source = "token", target = "token")
  Room convertRoomPutDTOtoEntity(RoomPutDTO roomPutDTO);
}
