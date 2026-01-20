package com.devcaotics.airBnTruta.model.repositories;

import com.devcaotics.airBnTruta.model.entities.Fugitivo;
import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.entities.Hospedeiro;
import com.devcaotics.airBnTruta.model.entities.Interesse;
import com.devcaotics.airBnTruta.model.entities.Servico;
import java.sql.SQLException;
import java.util.List;


@org.springframework.stereotype.Repository
public class Facade {

    private static Facade myself = null;

    private Repository<Servico,Integer> rServico;
    private Repository<Fugitivo, Integer> rFugitivo;
    private Repository<Hospedeiro, Integer> rHospedeiro;
    private Repository<Hospedagem, Integer> rHospedagem;

    public Facade(){
        rServico = new ServicoRepository();
        this.rFugitivo = new FugitivoRepository();
        this.rHospedeiro = new HospedeiroRepository();
        this.rHospedagem = new HospedagemRepository();
    }

    public static Facade getCurrentInstance(){

        if(myself == null)
            myself = new Facade();

        return myself;
    }

    public void create(Servico s) throws SQLException{
        this.rServico.create(s);
    }

    public void update(Servico s) throws SQLException{
        this.rServico.update(s);
    }

    public Servico readServico(int codigo) throws SQLException{
        return this.rServico.read(codigo);
    }

    public void deleteServico(int codigo) throws SQLException{
        this.rServico.delete(codigo);
    }

    public List<Servico> readAllServico() throws SQLException{
        return this.rServico.readAll();
    }

    public void create(Fugitivo f) throws SQLException{
        this.rFugitivo.create(f);
    }

    public void update(Fugitivo f) throws SQLException{
        this.rFugitivo.update(f);
    }

    public Fugitivo readFugitivo(int codigo) throws SQLException{
        return this.rFugitivo.read(codigo);
    }

    public Fugitivo loginFugitivo(String vulgo, String senha) throws SQLException{
        return ((FugitivoRepository)this.rFugitivo).login(vulgo,senha);
    }

    public void create(Hospedeiro h) throws SQLException{
        this.rHospedeiro.create(h);
    }

    public Hospedeiro loginHospedeiro(String vulgo, String senha) throws SQLException{
        return ((HospedeiroRepository)this.rHospedeiro).login(vulgo, senha);

    }

    public void create(Hospedagem h) throws SQLException{
        this.rHospedagem.create(h);
    }

    public Hospedagem readHospedagem(int codigo) throws SQLException{
        return this.rHospedagem.read(codigo);
    }

    public List<Hospedagem> filterHospedagemByAvailable() throws SQLException{
        return ((HospedagemRepository)this.rHospedagem).filterByAvailable();
    }

    public List<Hospedagem> filterHospedagens(
        String local,
        Double precoMax
    ) throws SQLException {
        return ((HospedagemRepository)this.rHospedagem).filterByAvailable(local, precoMax);
    }

    public List<Hospedagem> filterHospedagemByHospedeiro(int codigoHospedeiro) throws SQLException{
        return ((HospedagemRepository)this.rHospedagem).filterByHospedeiro(codigoHospedeiro);
    }

    public boolean existeInteresse(int fugitivoId, int hospedagemId)
        throws SQLException {

        return new InteresseRepository()
            .exists(fugitivoId, hospedagemId);
    }

    public void create(Interesse i) throws SQLException {
        new InteresseRepository().create(i);
    }

    public List<Hospedagem> filterInteressesDisponiveis(int fugitivoId)
        throws SQLException {

        return new InteresseRepository()
            .filterByFugitivoDisponiveis(fugitivoId);
    }

    public void removerInteresse(int fugitivoId, int hospedagemId)
        throws SQLException {

        new InteresseRepository()
            .delete(fugitivoId, hospedagemId);
    }

    public boolean hospedagemTemInteresse(int hospedagemId) throws SQLException {
        return new InteresseRepository().existsByHospedagem(hospedagemId);
    }

    public List<Interesse> listarInteressesPorHospedagem(int hospedagemId)
        throws SQLException {

        return new InteresseRepository().filterByHospedagem(hospedagemId);
    }

    public void aceitarInteresse(int interesseId)
            throws SQLException {

        Interesse i = new InteresseRepository().read(interesseId);

        if (i == null) return;

        ((HospedagemRepository)this.rHospedagem).aceitarFugitivo(
            i.getInteresse().getCodigo(),
            i.getInteressado().getCodigo()
        );
    }




}
