/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

/**
 *  Un critère de recherche a envoyer aux dao pour faire fonctionner la recherche par critéria. Les paramettres doivent être complété par les servlet
 * @author clem
 */
public class SearchFilter {
    String field;
//    Boolean mappedFiels; // Indique si il s'agit d'une entité ou d'un champs classique
    
    String op; // Opérateur issues de jqgrid ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']  voir doc jqgrid variable sopt : http://www.trirand.com/jqgridwiki/doku.php?id=wiki:search_config
    Object data;
    Class type;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
