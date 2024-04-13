package com.example.lab7gui.repository;

import com.example.lab7gui.domain.Conversation;
import com.example.lab7gui.domain.Message;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConversationDBRepository extends InMemoryRepository<Long, Conversation>{

    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, Utilizator> userRepo;

    private final Repository<Long, Message> messageRepo;
    private final Validator<Conversation> val;

    public ConversationDBRepository(String url, String username, String password, Repository<Long, Utilizator> userRepo,
                                    Repository<Long, Message> messageRepo, Validator<Conversation> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
        this.messageRepo = messageRepo;
        this.val = validator;
        loadData();
    }

    private List<Long> idStringToIdList(String s){
        return Arrays.stream(s.split(","))
                .map(Long::valueOf)
                .toList();
    }

    private String idListToIdString(List<Long> iduri){
        return iduri.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private void loadData(){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from conversations")
        ){
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("id_conversation");
                String iduriParticipanti = resultSet.getString("ids_participants");
                String iduriMesaje = resultSet.getString("ids_messages");
                List<Utilizator> participanti = new ArrayList<>();
                List<Message> mesaje = new ArrayList<>();
                idStringToIdList(iduriParticipanti).forEach(i -> participanti.add(userRepo.findOne(i).get()));
                if(!iduriMesaje.isEmpty())
                    idStringToIdList(iduriMesaje).forEach(i -> mesaje.add(messageRepo.findOne(i).get()));
                Conversation c = new Conversation(participanti, mesaje);
                c.setId(id);
                super.save(c);

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Conversation> save(Conversation entity){
        val.validate(entity);
        String insertStatement = "insert into conversations(ids_participants, ids_messages) values(?,?)";
        Optional<Conversation> r= Optional.empty();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)){
            String idParticipanti = idListToIdString(entity.getParticipantsIds());
            String idMesage = idListToIdString(entity.getMessagesIds());
            statement.setString(1, idParticipanti);
            statement.setString(2, idMesage);
            if(statement.executeUpdate() > 0){
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id_conversation");
                entity.setId(id);
                super.save(entity);
                r = Optional.of(entity);
                return r;
            }
            else System.out.println("fail add convo...");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return r;
    }
    @Override
    public Optional<Conversation> delete(Long id){
        String deleteStatement = "delete from conversations where id_conversation=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, id);
            var toBeDeleted = super.findOne(id);
            if(toBeDeleted.isPresent()){
                var convo = toBeDeleted.get();
                if(statement.executeUpdate()>0)
                {
                    //stergi conversatia, stergi toate mesajele din conversatie din repo
                    List<Long> l = convo.getMessagesIds();
                    if(!l.isEmpty())
                        l.forEach(messageRepo::delete);
                    super.delete(id);
                    convo.setId(-1L);
                    return Optional.of(convo);
                }
                else
                {
                    System.out.println("Delete failed");
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    @Override
    public Optional<Conversation> update(Conversation entity){
        String updateStatement = "update conversations set ids_messages=? where id_conversation=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(updateStatement)){
            statement.setString(1, idListToIdString(entity.getMessagesIds()));
            statement.setLong(2, entity.getId());
            var toBeUpdated = super.findOne(entity.getId());
            if(toBeUpdated.isPresent()){
                if(statement.executeUpdate() > 0){
                    super.update(entity);
                    return super.findOne(entity.getId());
                }
                else{
                    System.out.println("eroare modif convo ripbozo");
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

}
