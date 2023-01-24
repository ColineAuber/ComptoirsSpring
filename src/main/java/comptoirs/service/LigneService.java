package comptoirs.service;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.LigneRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Ligne;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import comptoirs.entity.Commande;
import comptoirs.entity.Produit;

@Service
@Validated // Les contraintes de validatipn des méthodes sont vérifiées
public class LigneService {
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    private final CommandeRepository commandeDao;
    private final LigneRepository ligneDao;
    private final ProduitRepository produitDao;

    private Commande c;

    private Produit p;

    // @Autowired
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    public LigneService(CommandeRepository commandeDao, LigneRepository ligneDao, ProduitRepository produitDao) {
        this.commandeDao = commandeDao;
        this.ligneDao = ligneDao;
        this.produitDao = produitDao;
    }

    /**
     * <pre>
     * Service métier :
     *     Enregistre une nouvelle ligne de commande pour une commande connue par sa clé,
     *     Incrémente la quantité totale commandée (Produit.unitesCommandees) avec la quantite à commander
     * Règles métier :
     *     - le produit référencé doit exister
     *     - la commande doit exister
     *     - la commande ne doit pas être déjà envoyée (le champ 'envoyeele' doit être null)
     *     - la quantité doit être positive
     *     - On doit avoir une quantite en stock du produit suffisante
     * <pre>
     *
     *  @param commandeNum la clé de la commande
     *  @param produitRef la clé du produit
     *  @param quantite la quantité commandée (positive)
     *  @return la ligne de commande créée
     */
    @Transactional
    Ligne ajouterLigne(Integer commandeNum, Integer produitRef, @Positive int quantite) {

        // On vérifie que le produit référencé existe
        var produit = produitDao.findById(produitRef).orElseThrow();

        // On vérifie que la commande existe
        var commande = commandeDao.findById(commandeNum).orElseThrow();

        // On vérifie que la commande n'a pas été déjà envoyée
        if (commande.getEnvoyeele() != null) {
            throw new IllegalArgumentException("La commande a déjà été envoyée.");
        }

        // On vérifie que la quantitée commandée est positive
        if(quantite < 0){
            throw new IllegalArgumentException("La quantitée commandée est négative.");
        }

        // On vérifie que la quantite en stock du produit est suffisante
        if(produit.getUnitesEnStock() < quantite){
            throw new IllegalArgumentException("Il n'y a pas assez de stock.");
        }

        // On enregistre une nouvelle ligne de commande pour une commande connue par sa clé
        Commande c = commandeDao.getReferenceById(commandeNum);

        Produit p = produitDao.getReferenceById(produitRef);

        Ligne ligne = new Ligne(c, p, quantite);

        // On incrémente la quantité totale commandée avec la quantite à commander
        ligne.getProduit().setUnitesCommandees(quantite + p.getUnitesCommandees());

        ligneDao.save(ligne);

        return ligne;

    }
}