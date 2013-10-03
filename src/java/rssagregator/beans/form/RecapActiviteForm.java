/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.RecapActivite;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

/**
 * L'affichage du récapitulatif de l'activité est a refaire. Cette class va surement subir des modifications
 * @author clem
 */
public class RecapActiviteForm extends AbstrForm {
//TODO : Refaire la visualisation de l'activité. Le formulaire sera surement à modifier.
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DaoItem.class);
    private Date date1;
    private Date date2;
    private List<Flux> listFlux;

    public RecapActiviteForm() {
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {


        RecapActivite recap = (RecapActivite) objEntre;

        if (valide) {
            recap.setDate1(date1);
            recap.setDate2(date2);
            recap.setListFlux(listFlux);
        }
        return recap;





        // Capture des deux date
//        try {
//            String d1 = request.getParameter("date1");
//            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//            DateTime dateTime = fmt.parseDateTime(d1);
//            date1 = dateTime.toDate();
//            recap.setDate1(date1);
//
//            String d2 = request.getParameter("date2");
//            DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
//            DateTime dateTime2 = fmt2.parseDateTime(d2);
//            date2 = dateTime2.toDate();
//
//            recap.setDate2(date2);
//        } catch (Exception e) {
//            logger.debug("Impossible de parser la date");
//        }

        //récupération des flux
//        String[] tabflux = request.getParameterValues("fluxSelection2");
//
//        int i;
//        if (tabflux != null) {
//            for (i = 0; i < tabflux.length; i++) {
//                Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabflux[i]));
//                recap.getListFlux().add(fl);
//            }
//        }
//        return recap;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {

        String s;
        this.erreurs = new HashMap<String, String[]>();

        s = request.getParameter("date1");
        if (s != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime = fmt.parseDateTime(s);
            this.date1 = dateTime.toDate();
        }

        s = request.getParameter("date2");
        if (s != null) {
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime2 = fmt2.parseDateTime(s);
            this.date2 = dateTime2.toDate();
        }

        String[] tabflux = request.getParameterValues("fluxSelection2");
        this.listFlux = new ArrayList<Flux>();
        if (tabflux != null) {
            for (int i = 0; i < tabflux.length; i++) {
                Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabflux[i]));
                this.listFlux.add(fl);
            }

        }

        if (this.erreurs.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
