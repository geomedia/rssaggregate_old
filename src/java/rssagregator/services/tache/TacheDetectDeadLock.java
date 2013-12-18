/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Cette méthode a pour objectif de détecter des DeadLock. Elle écrit un log d'erreur si c'est le cas. Lo4g se charge de
 * transmettre. Il s'agit plus d'une tache de débug le but n'est pas de clore les taches..
 *
 * @author clem
 */
public class TacheDetectDeadLock extends TacheImpl<TacheDetectDeadLock> {

//    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheDetectDeadLock.class);

    @Override
    protected void callCorps() throws Exception {

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] idThread = threadMXBean.findDeadlockedThreads();


        if (idThread != null) {
            for (int i = 0; i < idThread.length; i++) {
                long l = idThread[i];
                logger.error("Il semble qu'il y ait un deadLock Thread ID : " + l);

            }
        }
    }
}
