package com.example.lab7gui.repository;

import com.example.lab7gui.domain.Prietenie;
import com.example.lab7gui.domain.PrietenieStatus;
import com.example.lab7gui.domain.Tuple;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieDBRepository extends InMemoryRepository<Tuple<Long,Long>,Prietenie> {

    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, Utilizator> userRepo;

    private final Validator<Prietenie> val;
    public PrietenieDBRepository(String url, String username, String password, Repository<Long, Utilizator> userRepo,Validator<Prietenie> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
        loadData();
        this.val = validator;

    }

    private void loadData(){
        //this.findAll().forEach(super::save);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from prietenii");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id1 = resultSet.getLong("id_user1");
                Long id2 = resultSet.getLong("id_user2");
                String data = resultSet.getString("data");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate data2 = LocalDate.parse(data, formatter);
                LocalDateTime data3 = data2.atStartOfDay();
                PrietenieStatus status = PrietenieStatus.valueOf(resultSet.getString("status"));
                var u1 = userRepo.findOne(id1);
                var u2 = userRepo.findOne(id2);
                Prietenie p = new Prietenie(u1.get(),u2.get(), status);
                p.setData(data3);

                super.save(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    @Override
//    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
//        return Optional.empty();
//    }
//    @Override
//    public Iterable<Prietenie> findAll() {
//        Set<Prietenie> prietenii = new HashSet<>();
//        return prietenii;
//    }
    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        val.validate(entity);
        String insertStatement = "insert into prietenii(id_user1, id_user2, data, status) values(?,?,?,?)";
        Optional<Prietenie> r = Optional.empty();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertStatement)){
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dataCaString = entity.getData().format(formatter);
            statement.setString(3, dataCaString);
            statement.setString(4, entity.getStatus().toString());
            if(statement.executeUpdate() > 0)
            {

                super.save(entity);
//                var u1 = userRepo.findOne(entity.getId().getLeft());
//                var u2 = userRepo.findOne(entity.getId().getRight());
//                u1.get().addFriend(u2.get());
//                u2.get().addFriend(u1.get());
                r = Optional.of(entity);
            }
            else
                System.out.println("acesti useri sunt deja prieteni!");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return r;
    }
    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> ID) {
        String deleteStatement = "delete from prietenii where id_user1=? and id_user2=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, ID.getLeft());
            statement.setLong(2, ID.getRight());
            var toBeDeleted = super.findOne(ID);
            if(toBeDeleted.isPresent()){
                var prietenie = toBeDeleted.get();
                if(statement.executeUpdate() > 0){

//                    var u1 = userRepo.findOne(ID.getLeft());
//                    var u2 = userRepo.findOne(ID.getRight());
//                    u1.get().delFriend(u2.get());
//                    u2.get().delFriend(u1.get());
                    super.delete(ID);
                    return Optional.of(prietenie);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity){
        String updateStatement = "update prietenii set status=? where id_user1=? and id_user2=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(updateStatement)){
            statement.setString(1, entity.getStatus().toString());
            statement.setLong(2, entity.getId().getLeft());
            statement.setLong(3, entity.getId().getRight());
            var toBeUpdated = super.findOne(entity.getId());
            if(toBeUpdated.isPresent()){
                if(statement.executeUpdate() > 0){
                    super.update(entity);
                    return super.findOne(entity.getId());
                }
                else
                {
                    System.out.println("Update failed");
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

}
