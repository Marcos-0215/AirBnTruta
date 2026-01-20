package com.devcaotics.airBnTruta.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import com.devcaotics.airBnTruta.model.entities.Fugitivo;
import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/fugitivo")
public class FugitivoController {

    @Autowired
    private Facade facade;

    @Autowired
    private HttpSession session;

    private String msg = null;

    
    @GetMapping({"/", ""})
    public String init(
        @RequestParam(required = false) String local,
        @RequestParam(required = false) Double precoMax,
        Model m
    ) {

        if (session.getAttribute("fugitivoLogado") != null) {
            List<Hospedagem> hospedagens = new ArrayList<>();
            try {

                //hospedagens = facade.filterHospedagemByAvailable();

                // sem filtro
                if (
                    (local == null || local.isBlank())
                    && precoMax == null
                ) {
                    hospedagens = facade.filterHospedagemByAvailable();
                }
                // Com filtro
                else {
                    hospedagens = facade.filterHospedagens(local, precoMax);
                }


                m.addAttribute("hospedagens", hospedagens);



            } catch (SQLException e) {
                e.printStackTrace();
                m.addAttribute("msg", "Não foi possível carregar as hospedagens disponíveis!");
            }

            return "fugitivo/index";
        }

        m.addAttribute("fugitivo", new Fugitivo());
        m.addAttribute("msg", this.msg);
        this.msg = null;

        return "fugitivo/login";
    }


    // Tela de INTERESSES
    @GetMapping("/interesses")
    public String meusInteresses(Model m) {

        Fugitivo fugitivo =
            (Fugitivo) session.getAttribute("fugitivoLogado");

        if (fugitivo == null) {
            return "redirect:/fugitivo";
        }

        try {
            List<Hospedagem> hospedagens =
                facade.filterInteressesDisponiveis(
                    fugitivo.getCodigo()
                );

            m.addAttribute("hospedagens", hospedagens);

        } catch (SQLException e) {
            e.printStackTrace();
            m.addAttribute(
                "msg",
                "Erro ao carregar seus interesses"
            );
        }

        return "fugitivo/interesses";
    }


    // CADASTRO
    @PostMapping("/save")
    public String newFugitivo(Model m, Fugitivo f) {

        try {
            facade.create(f);
            this.msg = "Cadastro realizado com sucesso! Agora faça o login.";
        } catch (SQLException e) {
            e.printStackTrace();
            this.msg = "Erro ao realizar cadastro!";
        }

        return "redirect:/fugitivo";
    }

    //LOGIN
    @PostMapping("/login")
    public String login(
            Model m,
            @RequestParam String vulgo,
            @RequestParam String senha
    ) {

        try {
            Fugitivo logado = facade.loginFugitivo(vulgo, senha);

            if (logado == null) {
                this.msg = "Vulgo ou senha inválidos!";
                return "redirect:/fugitivo";
            }

            session.setAttribute("fugitivoLogado", logado);
            return "redirect:/fugitivo";

        } catch (SQLException e) {
            e.printStackTrace();
            this.msg = "Erro ao realizar login!";
            return "redirect:/fugitivo";
        }
    }

    //LOGOUT
    @GetMapping("/logout")
    public String logout(Model m) {

        session.removeAttribute("fugitivoLogado");
        return "redirect:/fugitivo";
    }
}
