package com.devcaotics.airBnTruta.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.entities.Hospedeiro;
import com.devcaotics.airBnTruta.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/hospedeiro")
public class HospedeiroController {

    @Autowired
    private Facade facade;
    private String msg = null;
    @Autowired
    private HttpSession session;

    @GetMapping({"/",""})
    public String init(Model m) {

        if(session.getAttribute("hospedeiroLogado") != null){
            Hospedeiro logado = (Hospedeiro)this.session.getAttribute("hospedeiroLogado");
            try {

                Map<Integer, Boolean> mapaInteresses = new HashMap<>();
                List<Hospedagem> hospedagens = this.facade.filterHospedagemByHospedeiro(logado.getCodigo());

                for (Hospedagem h : hospedagens) {
                    boolean temInteresse = this.facade.hospedagemTemInteresse(h.getCodigo());
                    mapaInteresses.put(h.getCodigo(), temInteresse);
                }

                m.addAttribute("hospedagens", hospedagens);
                m.addAttribute("mapaInteresses", mapaInteresses);                
                
            } catch (SQLException e) {
                e.printStackTrace();
                m.addAttribute("msg", "não foi possível carregar suas hospedagens! Contate o desenvolvedor!");
            }

            return "hospedeiro/index";
        }

        m.addAttribute("hospedeiro", new Hospedeiro());
        m.addAttribute("msg", this.msg);
        this.msg=null;
        return "hospedeiro/login";
    }


    // TELA DE INTERESSES POR HOSPEDAGEM
    @GetMapping("/interesses/{id}")
    public String interessesHospedagem(
            @PathVariable int id,
            Model m
    ) {
        
        if (session.getAttribute("hospedeiroLogado") == null) {
            return "redirect:/hospedeiro";
        }

        try {
            Hospedagem h = facade.readHospedagem(id);

            // garante que a hospedagem pertence ao hospedeiro logado
            if (h == null ||
                h.getHospedeiro().getCodigo() !=
                ((Hospedeiro) session.getAttribute("hospedeiroLogado")).getCodigo()) {

                return "redirect:/hospedeiro";
            }

            m.addAttribute("hospedagem", h);
            m.addAttribute(
                "interesses",
                facade.listarInteressesPorHospedagem(id)
            );

            return "hospedeiro/interesses";

        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro";
        }
    }



    @PostMapping("/save")
    public String newHospedeiro(Model m, Hospedeiro h) {
        //TODO: process POST request
        
        try {
            facade.create(h);
            this.msg="Parabéns! Seu cadastro foi realizado com sucesso! Agora faça o login, por favor, meu querido hospedeiro de minha vida!";

        } catch (SQLException e) {
            this.msg="Chorou! Não foi possível criar seu cadastro. Rapa daqui, fi da peste!";
        }

        return "redirect:/hospedeiro";
    }

    @PostMapping("/login")
    public String login(Model m,@RequestParam String vulgo,
        @RequestParam String senha
    ) {
        //TODO: process POST request
        
        try {
            Hospedeiro logado = facade.loginHospedeiro(vulgo, senha);
            if(logado == null){
                this.msg = "Erro ao Logar";
                return "redirect:/hospedeiro";
            }
            session.setAttribute("hospedeiroLogado", logado);
            return "redirect:/hospedeiro";
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            this.msg = "Erro ao logar!";
            return "redirect:/hospedeiro";
        }

        
    }
    
    
    @GetMapping("/logout")
    public String logout(Model m) {

        session.removeAttribute("hospedeiroLogado");;

        return "redirect:/hospedeiro";
    }
    

}
