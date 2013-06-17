/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.RecapActivite;

/**
 *
 * @author clem
 */
public class RecapActiviteForm extends AbstrForm {

    public RecapActiviteForm() {
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {


        RecapActivite recap = (RecapActivite) objEntre;


        // Capture des deux date
        Date date1 = null;
        Date date2 = null;
        try {
            String d1 = request.getParameter("date1");
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime = fmt.parseDateTime(d1);
            date1 = dateTime.toDate();
            recap.setDate1(date1);

            System.out.println("J'ai bien une date 1 : " + date1.toString());

            String d2 = request.getParameter("date2");
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime2 = fmt2.parseDateTime(d2);
            date2 = dateTime2.toDate();
            System.out.println("Deuxieme date ; " + date2.toString());

            recap.setDate2(date2);
            System.out.println(dateTime2.toString());
        } catch (Exception e) {
            System.out.println("Impossible de parser la date");

        }


        //récupération des flux
        String[] tabflux = request.getParameterValues("flux");

        int i ;
        if (tabflux != null) {
            for (i = 0; i < tabflux.length; i++) {
                Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabflux[i]));
                recap.getListFlux().add(fl);
//                fl.setID(new Long(tabflux[i]));

                // On récupère les item entre les deux date
//                DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//                daoItem.setDate1(date1);
//                daoItem.setDate2(date2);
//
//                List<Item> retour = daoItem.findCretaria();
//                System.out.println("size : " + retour.size());

            }
        }

        return recap;
    }
}
