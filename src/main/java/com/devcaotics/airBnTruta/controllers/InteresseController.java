package com.devcaotics.airBnTruta.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devcaotics.airBnTruta.model.entities.Fugitivo;
import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.entities.Interesse;
import com.devcaotics.airBnTruta.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/interesse")
public class InteresseController {

    @Autowired
    private Facade facade;

    @Autowired
    private HttpSession session;

    @PostMapping("/registrar")
    public String registrarInteresse(
        @RequestParam int hospedagemId
    ) {

        Fugitivo fugitivo =
            (Fugitivo) session.getAttribute("fugitivoLogado");

        if (fugitivo == null) {
            return "redirect:/fugitivo";
        }

        try {
            // evita interesse duplicado (segurança extra)
            if (facade.existeInteresse(
                    fugitivo.getCodigo(),
                    hospedagemId
                )) {
                return "redirect:/hospedagem/view/" + hospedagemId;
            }

            Hospedagem h = facade.readHospedagem(hospedagemId);

            Interesse i = new Interesse();
            i.setInteressado(fugitivo);
            i.setInteresse(h);

            facade.create(i);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/hospedagem/view/" + hospedagemId;
    }
}
