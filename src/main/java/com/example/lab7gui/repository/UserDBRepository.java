package com.example.lab7gui.repository;

import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.domain.validators.Validator;
import com.example.lab7gui.utils.PasswordUtils;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.Math.toIntExact;

public class UserDBRepository extends InMemoryRepository<Long, Utilizator> {

    private final String url;
    private final String username;
    private final String password;

    private final Validator<Utilizator> val;


    public UserDBRepository(String url, String username, String password, Validator<Utilizator> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        loadData();
        this.val = validator;

    }

    private void loadData(){
        //this.findAll().forEach(super::save);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users")
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id_user");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                Utilizator user=new Utilizator(firstName,lastName);
                user.setId(id);
                super.save(user);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    @Override
//    public Optional<Utilizator> findOne(Long longID) {
//        try(Connection connection = DriverManager.getConnection(url, username, password);
//            PreparedStatement statement = connection.prepareStatement("select * from users " +
//                    "where id_user = ?");
//
//        ) {
//            statement.setInt(1, Math.toIntExact(longID));
//            ResultSet resultSet = statement.executeQuery();
//            if(resultSet.next()) {
//                String firstName = resultSet.getString("first_name");
//                String lastName = resultSet.getString("last_name");
//                Utilizator u = new Utilizator(firstName,lastName);
//                u.setId(longID);
//                return Optional.of(u);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return Optional.empty();
//    }

//    @Override
//    public Iterable<Utilizator> findAll() {
//        Set<Utilizator> users = new HashSet<>();
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("select * from users");
//             ResultSet resultSet = statement.executeQuery()
//        ) {
//
//            while (resultSet.next())
//            {
//                Long id= resultSet.getLong("id_user");
//                String firstName=resultSet.getString("first_name");
//                String lastName=resultSet.getString("last_name");
//                Utilizator user=new Utilizator(firstName,lastName);
//
//                user.setId(id);
//                users.add(user);
//
//            }
//            return users;
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        val.validate(entity);
        String insertStatement = "insert into users(first_name, last_name) values(?,?)";
        Optional<Utilizator> r = Optional.empty();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            if(statement.executeUpdate() > 0){
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id_user");
                entity.setId(id);
                super.save(entity);
                r =Optional.of(entity);
                return r;

            }
            else System.out.println("fail la add ...");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return r;
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        String deleteStatement = "delete from users where id_user=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, aLong);
            var toBeDeleted = super.findOne(aLong);
            if(toBeDeleted.isPresent()){
                var user = toBeDeleted.get();
                if(statement.executeUpdate()>0)
                {
                    super.delete(aLong);
                    return Optional.of(user);
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
    public Optional<Utilizator> update(Utilizator entity) {
        val.validate(entity);
        String updateStatement = "update users set first_name = ?, last_name = ? where id_user = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(updateStatement)){
            statement.setLong(3, entity.getId());
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
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
    @Override
    public Optional<Utilizator> savePasswd(Utilizator entity, String parola) {
        val.validate(entity);
        String insertStatement = "insert into users(first_name, last_name, password_hash) values(?,?,?)";
        Optional<Utilizator> r = Optional.empty();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, PasswordUtils.hashPassword(parola));
            if(statement.executeUpdate() > 0){
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id_user");
                entity.setId(id);
                super.save(entity);
                r =Optional.of(entity);
                return r;

            }
            else System.out.println("fail la add parola ...");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return r;
    }

    @Override
    public Optional<Utilizator> findOnePasswd(Long longID, String parola) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id_user = ? and password_hash = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            statement.setString(2, PasswordUtils.hashPassword(parola));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator u = new Utilizator(firstName,lastName);
                u.setId(longID);
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
