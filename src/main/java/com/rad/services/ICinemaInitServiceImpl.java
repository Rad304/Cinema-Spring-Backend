package com.rad.services;

import com.rad.entities.*;
import com.rad.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("ICinemaInitService")
@Transactional
public class ICinemaInitServiceImpl implements ICinemaInitService {

    @Autowired private UserRepository userRepository;
    @Autowired private VilleRepository villeRepository;
    @Autowired private CinemaRepository cinemaRepository;
    @Autowired private SalleRepository salleRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private SeanceRepository seanceRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private FilmRepository filmRepository;
    @Autowired private ProjectionRepository projectionRepository;
    @Autowired private TicketRepository ticketRepository;

    @Override
    public void initUsers() {
        List<User> users = Stream.of(
                new User((long) 1, "user1", "password", "user1@gmail.com", "user"),
                new User((long) 2, "user2", "123", "user2@gmail.com", "user"),
                new User((long) 3, "yasser", "password", "yasser@gmail.com", "admin")
        ).collect(Collectors.toList());
        userRepository.saveAll(users);
    }

    @Override
    public void initVilles() {
        Stream.of("Casablanca", "Marrakech", "Rabat", "Tanger").forEach(nomVille -> {
            Ville ville = new Ville();
            ville.setName(nomVille);
            villeRepository.save(ville);
        });
    }

    @Override
    public void initCinemas() {
        villeRepository.findAll().forEach(ville -> {
            Stream.of("MegaRama", "IMAX", "FOUNOUN", "RIALTO", "LYNX").forEach(nomCinema -> {
                Cinema cinema = new Cinema();
                cinema.setName(nomCinema);
                cinema.setNombreSalles(3 + (int)(Math.random()*7));
                cinema.setVille(ville);
                cinemaRepository.save(cinema);
            });
        });
    }

    @Override
    public void initSalles() {
        cinemaRepository.findAll().forEach(cinema -> {
            for(int i = 0; i < cinema.getNombreSalles(); i++){
                Salle salle = new Salle();
                salle.setName("Salle" + (i+1));
                salle.setCinema(cinema);
                salle.setNombrePlaces(15 + (int)(Math.random()*20));
                salleRepository.save(salle);
            }
        });
    }

    @Override
    public void initPlaces() {
        salleRepository.findAll().forEach(salle -> {
            for(int i = 0; i < salle.getNombrePlaces(); i++){
                Place place = new Place();
                place.setNumero(i+1);
                place.setSalle(salle);
                placeRepository.save(place);
            }
        });
    }

    @Override
    public void initSeances() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Stream.of("12:00", "15:00", "17:00", "19:00", "21:00").forEach(s ->{
            Seance seance = new Seance();
            try{
                seance.setHeureDebut(dateFormat.parse(s));
                seanceRepository.save(seance);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initCategories() {
        Stream.of("Histoire", "Actions", "Fiction", "Drama").forEach(cat -> {
            Categorie categorie = new Categorie();
            categorie.setName(cat);
            categoryRepository.save(categorie);
        });
    }

    @Override
    public void initFilms() {
        double[] durees = new double[]{1,1.5,2,2.5,3};
        List<Categorie> categories = categoryRepository.findAll();
        Stream.of("Interstellar", "Jumanji", "Django Unchained", "Inception", "The Godfather", "The Matrix", "The Dark Knight", "The Good The Bad The Ugly", "Psycho", "Joker").forEach(titreFilm -> {
            Film film = new Film();
            film.setTitre(titreFilm);
            film.setDuree(durees[new Random().nextInt(durees.length)]);
            film.setPhoto(titreFilm.replaceAll(" ", "") + ".jpg");
            film.setCategorie(categories.get(new Random().nextInt(categories.size())));
            filmRepository.save(film);
        });
    }

    @Override
    public void initProjections() {
        double[] prices = new double[]{30,50,60,70,90,100};
        List<Film> films = filmRepository.findAll();
        villeRepository.findAll().forEach(ville -> {
            ville.getCinemas().forEach(cinema -> {
                cinema.getSalles().forEach(salle -> {
                    int index = new Random().nextInt(films.size());
                    Film film = films.get(index);
                    seanceRepository.findAll().forEach(seance -> {
                        Projection projection = new Projection();
                        projection.setDateProjection(new Date());
                        projection.setFilm(film);
                        projection.setPrix(prices[new Random().nextInt(prices.length)]);
                        projection.setSalle(salle);
                        projection.setSeance(seance);
                        projectionRepository.save(projection);
                    });
                });
            });
        });
    }

    @Override
    public void initTickets() {
        projectionRepository.findAll().forEach(projection -> {
            projection.getSalle().getPlaces().forEach(place -> {
                Ticket ticket = new Ticket();
                ticket.setPlace(place);
                ticket.setPrix(projection.getPrix());
                ticket.setProjection(projection);
                ticket.setCodePayment((int)Math.random()*9999);
                ticket.setReserve(false);
                ticketRepository.save(ticket);
            });
        });
    }
}
