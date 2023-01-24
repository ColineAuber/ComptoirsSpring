package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

import java.util.NoSuchElementException;

@SpringBootTest
        // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;

    @Autowired
    LigneService service;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(),
                "La ligne doit être enregistrée, sa clé générée");
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class,
                () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
                "La quantite d'une ligne doit être positive");
    }

    @Test
    void testCommandeNExistePas(){

        assertThrows(Exception.class,
                () -> service.ajouterLigne(99, 98, 20));
    }

    @Test
    void testProduitNExistePas(){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(99999, 4, 20));
    }

    @Test
    void testCommandeDejaEnvoyee(){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(99999, 98, 15));
    }

    @Test
    void testQuantitéStockSuffisante(){
        assertThrows(IllegalArgumentException.class,
                () -> service.ajouterLigne(99998, 98, 150));
    }

    @Test
    void testQuantiteCommandeeIncrementee(){

    }

}
