/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.history.HistoryPolicy;

/**
 *
 * @author clem
 */
public class JournalEntityLisner implements DescriptorCustomizer{

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
         HistoryPolicy policy = new HistoryPolicy();
        policy.addHistoryTableName("EMPLOYEE_HIST");
        policy.addStartFieldName("START_DATE");
        policy.addEndFieldName("END_DATE");
        descriptor.setHistoryPolicy(policy);
    }
    
}
