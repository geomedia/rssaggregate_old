/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.RecapActivite;

/**
 *
 * @author clem
 */
public class RecapActiviteForm extends AbstrForm {

        protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DaoItem.class);
    
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

            String d2 = request.getParameter("date2");
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime2 = fmt2.parseDateTime(d2);
            date2 = dateTime2.toDate();

            recap.setDate2(date2);
        } catch (Exception e) {
            logger.debug("Impossible de parser la date");

        }

        //récupération des flux
        String[] tabflux = request.getParameterValues("fluxSelection2");

        int i ;
        if (tabflux != null) {
            for (i = 0; i < tabflux.length; i++) {
                Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabflux[i]));
                recap.getListFlux().add(fl);
            }
        }
        return recap;
    }
}
