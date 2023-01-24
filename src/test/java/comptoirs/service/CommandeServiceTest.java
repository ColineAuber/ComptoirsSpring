package comptoirs.service;

import comptoirs.dao.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
        // Ce test est basé sur le jeu de données dans "test_data.sql"
class CommandeServiceTest {
    private static final String ID_PETIT_CLIENT = "0COM";
    private static final String ID_GROS_CLIENT = "2COM";
    private static final String VILLE_PETIT_CLIENT = "Berlin";
    private static final BigDecimal REMISE_POUR_GROS_CLIENT = new BigDecimal("0.15");

    @Autowired
    private CommandeService service;

    @Autowired
    private ProduitRepository produitDao;
    @Test
    void testCreerCommandePourGrosClient() {
        var commande = service.creerCommande(ID_GROS_CLIENT);
        assertNotNull(commande.getNumero(), "On doit avoir la clé de la commande");
        assertEquals(REMISE_POUR_GROS_CLIENT, commande.getRemise(),
                "Une remise de 15% doit être appliquée pour les gros clients");
    }

    @Test
    void testCreerCommandePourPetitClient() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertNotNull(commande.getNumero());
        assertEquals(BigDecimal.ZERO, commande.getRemise(),
                "Aucune remise ne doit être appliquée pour les petits clients");
    }

    @Test
    void testCreerCommandeInitialiseAdresseLivraison() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertEquals(VILLE_PETIT_CLIENT, commande.getAdresseLivraison().getVille(),
                "On doit recopier l'adresse du client dans l'adresse de livraison");
    }

    @Test
    void testCommandeDejaEnvoyee(){
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistreExpédition(99999));
    }

    @Test
    void testCommandeDateMiseAJour(){
        var commande = service.enregistreExpédition(99998);
        assertEquals(commande.getSaisiele(), java.time.LocalDate.now());
    }

    @Test
    void testCommandeQuantiteDecrementee(){
        // On regarde combien il y a d'unité en stock avant l'envoi de la commande
        var produit = produitDao.findById(98).orElseThrow();
        int stockAvant = produit.getUnitesEnStock();
        // On enregistre l'expédition de la commande
        service.enregistreExpédition(99998);
        // On regarde combien il y a d'unité en stock après
        var produit2 = produitDao.findById(98).orElseThrow();
        assertEquals(stockAvant - 20, produit2.getUnitesEnStock());
    }

}
