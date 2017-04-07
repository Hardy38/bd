package contraintes;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import connexion.Connexion_class;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import modele.programmation.Prog_Alerte;

/**
 *
 * @author tsialouh
 */
public class Alerte {

    private final Connexion_class conn;
    private final Connection getconn;
    private final List<Prog_Alerte> listAlertes;

    public Alerte() throws ParseException {
        this.conn = new Connexion_class();
        this.getconn = conn.getConnection();
        listAlertes = new ArrayList<>();
    }

    /**
     *
     * Affiche les voyages insuffisament remplis à deux semaines de la date départ
     *
     * @return liste des programmations en alerte
     * @throws SQLException
     */
    public List<Prog_Alerte> mes_aletes() throws SQLException {
        final int nbJoursLimites = 14;
        ResultSet rs;
        String str = "SELECT P.numProgrammation, P.nbPlacesDispos, P.dateDepart, V.numVoyage, V.nbPlacesMin, V.nbPlacesMax  FROM Programmation P JOIN Voyage V on (P.numVoyageP = V.numVoyage)";
        PreparedStatement st = getconn.prepareStatement(str);
        rs = st.executeQuery();
        while (rs.next()) {
            // Si à moins de 2 semaines (14 jours) de la date de départ du voyage, le nombre minimum n'est pas atteint, le voyage est annulé
            if (nbJoursRestants(rs.getInt(1)) < nbJoursLimites && nbPlacesOccupees(rs.getInt("numProgrammation")) < rs.getInt(5)) {
                //Ajoute la programmation en cours dans la liste des alertes
                int nbOcc = nbPlacesOccupees(rs.getInt("numProgrammation"));
                listAlertes.add(new Prog_Alerte(rs.getInt(1), rs.getInt(2), date_ToStr(rs.getDate(3)), rs.getInt(4), rs.getInt(5), rs.getInt(6), nbOcc));
            }
        }
        System.out.println("Exécution effectuée avec succès!");
        rs.close();
        return listAlertes;
    }

    /**
     *
     * Converti la date en string
     *
     * @param indate
     * @return
     */
    private String date_ToStr(Date indate) {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yy");
        try {
            dateString = sdfr.format(indate);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dateString;
    }

    /**
     *
     * Retourne le nombre de jours restants entre la date départ d'un voyage et
     * la date du jour
     *
     * @param dateDepart
     * @return
     */
    public long nbJoursRestants(int numProgrammation) throws SQLException {
        
        ResultSet rs;
        String str = "SELECT dateDepart FROM Programmation WHERE numProgrammation = "+numProgrammation;
        PreparedStatement st = getconn.prepareStatement(str);
        
        Date dateDepart = null;
        rs = st.executeQuery();
        rs.next();
        dateDepart = rs.getDate(1);
        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MMM-yy");
        java.util.Date date_du_jour = new java.util.Date();
        final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
        long difference_entre_deux_dates = dateDepart.getTime() - date_du_jour.getTime();
        long nbjours_restants = difference_entre_deux_dates / (MILLISECONDS_PER_DAY);
        return nbjours_restants;
    }

    /**
     *
     * Calcule le nombre de places occupées pour une programmation donnée
     *
     * @param numProgrammation
     * @return
     * @throws java.sql.SQLException
     */
    private int nbPlacesOccupees(int numProgrammation) throws SQLException {
        int nbPlacesMax = 0;
        int nbPlacesRestants = 0;

        ResultSet rs;
        String str = "SELECT  P.nbPlacesDispos, V.nbPlacesMax  FROM Programmation P JOIN Voyage V on (P.numVoyageP = V.numVoyage) WHERE P.numProgrammation  = " + numProgrammation;
        PreparedStatement st = getconn.prepareStatement(str);
        rs = st.executeQuery();
        while (rs.next()) {
            nbPlacesMax = rs.getInt("nbPlacesMax");
            nbPlacesRestants = rs.getInt("nbPlacesDispos");
        }
        return nbPlacesMax - nbPlacesRestants;
    }
    
    /**
     * 
     * Retourne la date du jour
     * @return
     */
    public Date dateDuJour(){
        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MMM-yy");
        java.util.Date date_du_jour = new java.util.Date();
        return (Date) date_du_jour;
    }

}
