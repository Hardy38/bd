package contraintes;

import connexion.Connexion_class;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

/**
 *
 * @author hardy
 */
public class PrixApayer {

    private final Connexion_class conn;
    private final Connection getconn;

    public PrixApayer() throws ParseException {
        conn = new Connexion_class();
        getconn = conn.getConnection();
    }

    public int genererPrix(int numProg) throws SQLException {
        ResultSet rs = null;
        String str = "SELECT numProgrammation FROM Groupe WHERE numProgrammation  = " + numProg;
        Statement st = getconn.createStatement();
        rs = st.executeQuery(str);

        boolean trouve = false;
        while (rs.next()) {
            trouve = true;
        }

        int tarifDeBase = trouvePrix(numProg);
        if (trouve) {
            tarifDeBase = tarifDeBase - ((tarifDeBase * 20) / 100);
        } else if (isIndependant(numProg) && accompagner(numProg)){
            tarifDeBase = tarifDeBase + ((tarifDeBase * 20) / 100); 
        }

        return tarifDeBase;
    }

    /**
     *
     * @param numProg le prix d'une voyage
     * @return
     * @throws SQLException
     */
    public int trouvePrix(int numProg) throws SQLException {
        ResultSet rs = null;
        String str = "SELECT tarif FROM Programmation WHERE numProgrammation = " + numProg;
        Statement st = getconn.createStatement();
        rs = st.executeQuery(str);
        rs.next();
        return rs.getInt(1);
    }

    /**
     *
     * Retourne vrai si le voyage est accompagné
     *
     * @param numProg
     * @return
     * @throws SQLException
     */
    public boolean accompagner(int numProg) throws SQLException {
        //Verifie si le voyage est accompagné
        boolean trouve = false;
        ResultSet rsAc = null;
        String st = "SELECT numResponsable FROM Independant WHERE numProgrammation  = " + numProg;
        Statement stAc = getconn.createStatement();
        rsAc = stAc.executeQuery(st);
        int num = 1;
        while (rsAc.next()) {
            trouve = true;
            num = rsAc.getInt(1);
        }
        //Le numero du responsable est 0 si le voyage n'est pas accompagné
        boolean accompagner = false;
        if (trouve && num == 0) {
            accompagner = true;
        }
        return accompagner;
    }

    public boolean isIndependant(int numProg) throws SQLException {
        ResultSet rsInd = null;
        String strInd = "SELECT numProgrammation FROM Independant WHERE numProgrammation  = " + numProg;
        Statement stI = getconn.createStatement();
        rsInd = stI.executeQuery(strInd);
        boolean trouveInd = false;
        while (rsInd.next()) {
            trouveInd = true;
        }
        return trouveInd;
    }
}
