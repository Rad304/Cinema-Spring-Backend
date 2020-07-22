package com.rad;


import com.rad.entities.*;
import com.rad.services.ICinemaInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;


@SpringBootApplication
public class CinemasApplication implements CommandLineRunner {

    @Autowired private ICinemaInitService cinemaInitService;
    @Autowired private RepositoryRestConfiguration restConfiguration;


    public static void main(String[] args) {
        SpringApplication.run(CinemasApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        restConfiguration.exposeIdsFor(Film.class, Salle.class, Ticket.class, Cinema.class, Ville.class, Categorie.class);
        cinemaInitService.initUsers();
        cinemaInitService.initVilles();
        cinemaInitService.initCinemas();
        cinemaInitService.initSalles();
        cinemaInitService.initPlaces();
        cinemaInitService.initSeances();
        cinemaInitService.initCategories();
        cinemaInitService.initFilms();
        cinemaInitService.initProjections();
        cinemaInitService.initTickets();
    }


}



