package com.example.lab7gui.service;

import com.example.lab7gui.domain.*;
import com.example.lab7gui.repository.Repository;
import com.example.lab7gui.repository.paging.Page;
import com.example.lab7gui.repository.paging.Pageable;
import com.example.lab7gui.repository.paging.PagingRepository;
import com.example.lab7gui.utils.Observable;
import com.example.lab7gui.utils.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Service implements ServiceInterface, Observable {
    private List<Observer> observers = new ArrayList<>();

    private final Repository<Long, Utilizator> userRepo;
    private final Repository<Tuple<Long,Long>,Prietenie> prietenieRepo;
    private final Repository<Long, Message> messageRepo;

    private final Repository<Long, Conversation> conversationRepo;

    private final PagingRepository<Long, Utilizator> userPagingRepo;

    public Service(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>,
            Prietenie> prietenieRepo, Repository<Long, Message> messageRepo, Repository<Long, Conversation> convoRepo,
                   PagingRepository<Long, Utilizator> userPagingRepo) {
        this.userRepo = userRepo;
        this.prietenieRepo = prietenieRepo;
        this.messageRepo = messageRepo;
        this.conversationRepo = convoRepo;
        this.userPagingRepo = userPagingRepo;
    }

    @Override
    public Utilizator addUser(Utilizator user) {
        Optional<Utilizator> a;
        try{
            a=userRepo.save(user);
            if(a.isEmpty()) {
                System.err.println("Eroare la add, idk");
                return null;
            }
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Utilizator addUserPasswd(Utilizator user, String passwd) {
        Optional<Utilizator> a;
        try{
            a=userRepo.savePasswd(user, passwd);
            if(a.isEmpty()) {
                System.err.println("Eroare la add, idk");
                return null;
            }
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public Utilizator removeUser(Long id) {
        try{
            // pun in alta lista toate prieteniile care trb sterse, evit concurrentmodificationexception
            List<Prietenie> friendshipsToDelete = new ArrayList<>();
            Iterable<Prietenie> allFriendships = prietenieRepo.findAll();

            allFriendships.forEach(friendship -> {if (friendship.getId().getLeft().equals(id) || friendship.getId().getRight().equals(id))
                friendshipsToDelete.add(friendship);});

            // le sterg
            friendshipsToDelete.forEach(friendship -> {prietenieRepo.delete(friendship.getId());});
            //modific listele de prieteni la toti userii, la fel pun in alta lista toti prietenii
            Iterable<Utilizator> allUsers = userRepo.findAll();

            allUsers.forEach(user -> {
                //copie lista prieteni
                ArrayList<Utilizator> friendsCopy = new ArrayList<>(user.getFriends());
                //sterg din copie
                friendsCopy.removeIf(friend -> friend.getId().equals(id));
                //inlocuiesc cu copia
                user.setFriends(friendsCopy);});
            // daca sterg un user, sterg toate conversatiile care il contin!

            Iterable<Conversation> convos = conversationRepo.findAll();
            List<Conversation> toDelete = new ArrayList<>();
            convos.forEach(c ->{
                if(c.getParticipantsIds().contains(id))
                    toDelete.add(c);
            });
            toDelete.forEach(convo -> conversationRepo.delete(convo.getId()));

            // sterge user
            Optional<Utilizator> u = userRepo.delete(id);
            if(u.isEmpty()) {
                System.err.println("Nu exista user cu acest id!");
                return null;
            }

            notifyObs();
            return u.get();
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }

    }

    @Override
    public boolean createFriendship(Tuple<Long,Long> id) {
        Optional<Utilizator> friend1 = userRepo.findOne(id.getLeft());
        Optional<Utilizator> friend2 = userRepo.findOne(id.getRight());
        Optional<Prietenie> f;
        try {
            if (friend1.isEmpty() || friend2.isEmpty()) {
                throw new IllegalArgumentException("Nu exista 2 useri pentru idurile acestea!!");
            }

            Optional<Prietenie> exista = prietenieRepo.findOne(new Tuple<>(id.getRight(), id.getLeft()));
            if (exista.isPresent()) {
                System.err.println("Acesti utilizatori sunt deja prieteni!!");
                return false;
            }
            Optional<Prietenie> exista2 = prietenieRepo.findOne(id);
            if (exista2.isPresent()) {
                System.err.println("Exista deja o cerere/prietenie intre acesti useri!!!");
                return false;
            }
            Prietenie pr = new Prietenie(friend1.get(), friend2.get(), PrietenieStatus.PENDING);
            prietenieRepo.save(pr);
            notifyObs();
        }

        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Prietenie deleteFriendship(Tuple<Long, Long> id) {
        Optional<Utilizator> friend1 = userRepo.findOne(id.getLeft());
        Optional<Utilizator> friend2 = userRepo.findOne(id.getRight());
        Optional<Prietenie> f = Optional.empty();
        try {
            if (friend1.isEmpty() || friend2.isEmpty() || friend1.equals(friend2)) {
                throw new IllegalArgumentException("Nu exista 2 useri pentru idurile acestea!!!");
            }
            Iterable<Prietenie> prieteni = prietenieRepo.findAll();
            for(Prietenie pri: prieteni){
                if(
                        (Objects.equals(pri.getId().getLeft(), friend1.get().getId()) && Objects.equals(pri.getId().getRight(), friend2.get().getId())) ||
                        (Objects.equals(pri.getId().getLeft(), friend2.get().getId()) && Objects.equals(pri.getId().getRight(), friend1.get().getId()))
                ){
                    f=prietenieRepo.delete(pri.getId());
                    break;
                }

            }

        }

        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }

        if(f.isEmpty()) {
            System.err.println("Acesti utilizatori nu sunt prieteni!");
            return null;
        }
        if(f.get().getStatus() == PrietenieStatus.ACCEPTED) {
            friend1.get().delFriend(friend2.get());
            friend2.get().delFriend(friend1.get());
        }
        else{
            friend1.get().delFriendPending(friend2.get());
            friend2.get().delFriendPending(friend1.get());
        }
        notifyObs();
        return f.get();
    }

    @Override
    public Iterable<Utilizator> getUtilizatori() {
        return userRepo.findAll();
    }

    @Override
    public Iterable<Prietenie> getPrietenii() {
        return prietenieRepo.findAll();
    }

    @Override
    public Utilizator getUser(Long id) {
        return userRepo.findOne(id).orElse(null);
    }

    public Utilizator getUserPasswd(Long id, String passwd) {
        return userRepo.findOnePasswd(id, passwd).orElse(null);
    }

    @Override
    public int nrComunitati() {
        int nr = 0;
        Set<Long> vizitat = new HashSet<>();

        for(Utilizator u: userRepo.findAll()){
            if(!vizitat.contains(u.getId())){
                dfs(u,vizitat);
                nr++;
            }
        }
        return nr;
    }

    /**
     * simple dfs algorithm
     * @param u - the source where we start from
     * @param vizitat - set of id's that were visited before
     * @return - list of users that represent a conex component of the social network graph
     */
    private List<Utilizator> dfs(Utilizator u, Set<Long> vizitat){
        ArrayList<Utilizator> l = new ArrayList<>();
        l.add(u);
        vizitat.add(u.getId());
        for (Utilizator pr: u.getFriends()){
            if(!vizitat.contains(pr.getId())) {
                List<Utilizator> list = dfs(pr, vizitat);
                l.addAll(list);
            }
        }
        return l;

    }

    /**
     * gets the longest path starting from this user
     * @param sursa - the source from where we start the DFS
     * @param vizitat - set of id's that were visited
     * @return - the value of the longest path starting from this user
     */
    private int dfs2(Utilizator sursa, Set<Long> vizitat){
        int max = -1;
        for(Utilizator u: sursa.getFriends()){
            if(!vizitat.contains(u.getId())){
                vizitat.add(u.getId());
                int lungime = dfs2(u, vizitat);
                if(lungime > max)
                    max = lungime;
                vizitat.remove(u.getId());

            }
        }
        return max+1;
    }

    /**
     * gets the longest path from a conex component
     * @param users - list of users from the conex component
     * @return - the value of the longest path in the conex component of users
     */
    private int celMaiMareDrum(List<Utilizator> users){
        int max=0;
        for(Utilizator u: users){
            Set<Long> vizitat = new HashSet<>();
            int lungime = dfs2(u, vizitat);
            if(lungime > max)
                max = lungime;

        }
        return max;
    }

    public List<Iterable<Utilizator>> ceaMaiSociabilaComunitate() {
        List<Iterable<Utilizator>> list = new ArrayList<>();
        Iterable<Utilizator> users = userRepo.findAll();
        Set<Long> vizitat = new HashSet<>();
        int max = -1;
        for(Utilizator u: users){
            if(!vizitat.contains(u.getId())){
                List<Utilizator> useri2 = dfs(u, vizitat);
                int lungime = celMaiMareDrum(useri2);
                if(lungime > max){
                    list = new ArrayList<>(); // resetam lista, apoi inlocuim cu lista noua care e mai lunga
                    list.add(useri2);
                    max = lungime;
                }
                else if(max == lungime)
                    list.add(useri2);
            }
        }
        return list;
    }

    @Override
    public Utilizator updateUser(Utilizator u) {

        Optional<Utilizator> a;
        try{
            a=userRepo.update(u);
            if(a.isEmpty()) {
                System.err.println("Nu exista user cu acest id!");
                return null;
            }
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Map<Utilizator, LocalDateTime> filtrarePrieteniiLuna(Long id, Integer luna) {
        // Găsește utilizatorul cu id-ul dat
        Optional<Utilizator> utilizator = userRepo.findOne(id);

        // Verifică dacă utilizatorul există
        if (utilizator.isEmpty()) {
            throw new IllegalArgumentException("Utilizatorul cu id-ul " + id + " nu există.");
        }
        Iterable<Prietenie> toatePrieteniile = prietenieRepo.findAll();

        // Convertim Iterable în Stream
        Stream<Prietenie> streamPrieteniile = StreamSupport.stream(toatePrieteniile.spliterator(), false);

        // Filtrăm prieteniile care aparțin utilizatorului și care au fost create în luna dată
        List<Prietenie> prieteniiFiltrate = streamPrieteniile
                .filter(p -> (p.getId().getLeft().equals(id) || p.getId().getRight().equals(id)) && p.getData().getMonthValue() == luna)
                .toList();

        // Convertim lista de prietenii într-un map de utilizatori și date

        return prieteniiFiltrate
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getId().getLeft().equals(id) ? userRepo.findOne(p.getId().getRight()).get() : userRepo.findOne(p.getId().getLeft()).get(),
                        Prietenie::getData));
    }

    public Prietenie updatePrietenie(Prietenie p){
        Optional<Prietenie> a;
        try{
            a=prietenieRepo.update(p);
            if(a.isEmpty()) {
                System.err.println("Nu exista prietenie cu acest id!");
                return null;
            }
            Utilizator u1 = p.getU1();
            Utilizator u2 = p.getU2();
            u1.delFriendPending(u2);
            u2.delFriendPending(u1);
            u1.addFriend(u2, PrietenieStatus.ACCEPTED);
            u2.addFriend(u1, PrietenieStatus.ACCEPTED);
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Prietenie findPrietenie(Tuple<Long,Long> id){
        return prietenieRepo.findOne(id).orElse(null);
    }

    public Iterable<Conversation> getConversatii(){
        return conversationRepo.findAll();
    }

    public Conversation addConversation(Conversation c){
        Optional<Conversation> a;
        try{
            a=conversationRepo.save(c);
            if(a.isEmpty()) {
                System.err.println("Eroare la add, idk");
                return null;
            }
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Message sendMessage(Conversation c, Message m){
        Optional<Message> mes;
        try{
            mes = messageRepo.save(m);
            if(mes.isEmpty()) {
                System.err.println("Eroare la add, idk");
                return null;
            }
            c.addMsg(m);
            notifyObs();
            return mes.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Conversation updateConversation(Conversation c) {

        Optional<Conversation> a;
        try{
            a=conversationRepo.update(c);
            if(a.isEmpty()) {
                System.err.println("Nu exista conversatie cu acest id!");
                return null;
            }
            notifyObs();
            return a.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Message deleteMessage(Conversation c, Long id){
        Optional<Message> m = messageRepo.findOne(id);
        if(m.isEmpty()){
            System.out.println("nu exista mesaj cu acest id");
            return null;
        }
        try {
            Message m2 = m.get();
            var l = c.getMessages();
            l.forEach(mes->{
                if (mes.getOriginal() == m2)
                    messageRepo.update(mes);
            });

            c.delMsg(m2);
            List<Message> mesajeActualizate = new ArrayList<>();
            l.forEach(mesaj-> {
                mesajeActualizate.add(messageRepo.findOne(mesaj.getId()).get());
            });
            Optional<Message> m3 = messageRepo.delete(id);
            if(m3.isEmpty()){
                System.out.println("eroare delete mesaj idk");
                return null;
            }
            c.setMessages(mesajeActualizate);
            notifyObs();
            return m3.get();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public Conversation deleteConversation(Long id){
        Optional<Conversation> con = conversationRepo.findOne(id);
        if(con.isEmpty()){
            System.out.println("nu exista conversatie cu acest id");
            return null;
        }
        else{
            try {
                Optional<Conversation> co = conversationRepo.delete(id);
                if (co.isEmpty()) {
                    System.out.println("eroare delete conversatie idk");
                    return null;
                }
                notifyObs();
                return co.get();
            }
            catch (Exception ex){
                System.err.println(ex.getMessage());
                return null;
            }
        }
    }

    public Page<Utilizator> getUsersOnPage(Pageable pageable){
        return userPagingRepo.findAll(pageable);
    }


    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObs() {
        observers.forEach(Observer::update);
    }
}

