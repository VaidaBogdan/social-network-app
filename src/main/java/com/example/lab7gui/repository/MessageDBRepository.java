package com.example.lab7gui.repository;

import com.example.lab7gui.domain.Message;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageDBRepository extends InMemoryRepository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, Utilizator> userRepo;
    private final Validator<Message> val;

    public MessageDBRepository(String url, String username, String password, Repository<Long, Utilizator> userRepo, Validator<Message> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
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
             PreparedStatement statement = connection.prepareStatement("select * from messages");
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long idMesaj = resultSet.getLong("id_mesaj"); // ID MESAJ
                Long idFrom = resultSet.getLong("id_from");
                Utilizator from = userRepo.findOne(idFrom).get(); // FROM
                String mesaj = resultSet.getString("mesaj"); // MESAJUL
                String idsTo = resultSet.getString("ids_to");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime(); // DATA
                List<Utilizator> to = new ArrayList<>(); // TO
                List<Long> iduri= idStringToIdList(idsTo);
                iduri.forEach(id -> to.add(userRepo.findOne(id).get()));
                Message m = new Message(from, to, mesaj, data);
                m.setId(idMesaj);
                super.save(m);
            }
            resultSet.close();
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long idMesaj = resultSet.getLong("id_mesaj"); // ID MESAJ
                Long idOriginal = resultSet.getLong("id_original"); // ID ORIGINAL
                if(idOriginal != 0){
                    Message m = findOne(idMesaj).get();
                    super.delete(m.getId());
                    m.setOriginal(findOne(idOriginal).get());
                    super.save(m);
                }

            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Message> save(Message entity){
        val.validate(entity);
        String insertStatement = "insert into messages(id_from, mesaj, ids_to, data, id_original) values(?,?,?,?,?)";
        Optional<Message> r= Optional.empty();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)){
            statement.setLong(1, entity.getFrom().getId());
            statement.setString(2, entity.getMesaj());
            statement.setString(3, idListToIdString(entity.getToIds()));
            statement.setTimestamp(4, Timestamp.valueOf(entity.getData()));
            if(entity.getOriginal() != null){
                statement.setLong(5, entity.getOriginal().getId());
            }
            else {
                statement.setNull(5, Types.BIGINT);
            }

            if(statement.executeUpdate() > 0){
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id_mesaj");
                entity.setId(id);
                super.save(entity);
                r = Optional.of(entity);
                return r;
            }
            else System.out.println("fail add mesaj...");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return r;
    }
    @Override
    public Optional<Message> delete(Long id){
        String deleteStatement = "delete from messages where id_mesaj=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, id);
            var toBeDeleted = super.findOne(id);
            if(toBeDeleted.isPresent()){
                var message = toBeDeleted.get();
                if(statement.executeUpdate()>0)
                {   super.delete(id);
                    message.setId(-1L);
                    return Optional.of(message);
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

    public Optional<Message> update(Message entity){
        String updateStatement = "update messages set id_original=? where id_mesaj=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(updateStatement)) {
            statement.setLong(2, entity.getId());
            statement.setNull(1, Types.BIGINT);
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
