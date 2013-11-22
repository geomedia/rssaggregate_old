/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.List;
import rssagregator.beans.form.AbstrForm;

/**
 * Un critère de recherche a envoyer aux dao pour faire fonctionner la recherche par critéria. Les paramettres doivent
 * être complété par les servlet ou les class héritant de {@link AbstrForm}
 *
 * @author clem
 */
public class SearchFilter {

    /**
     * *
     * Le champs sur lequel porte le filtre.
     */
    String field;
    /**
     * *
     * L'opérateur. Pour l'instant sont implémenté dans le recherche criteria: <ul>
     * <li>eq : pour equal</li>
     * <li>cn : pour like</li>
     * <li>in : le champs doit être dans une liste de valeur opérateur in</li>
     * <li>gt : greater than</li>
     * <li>lt : less than</li>
     * <li>inn : not null --> valeur ajouté par rapport a la grid</li>
     * <li>isn : is null --> valeur ajouté par rapport a la grid</li>
     * <li><li>
     * Ces opérateur sont inspiré des opérateur provenant de filter de jqgrid voir
     * http://www.trirand.com/jqgridwiki/doku.php?id=wiki:search_config à savoir
     * ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']. En cas d'implémentation de nouveau
     * opérateur, il faut modifier les recherche criteria dans les dao {@link AbstrDao#gestionCriteria(java.lang.Boolean) ()
     * }
     * <
     * /ul>
     */
    String op; // Opérateur issues de jqgrid voir doc jqgrid variable sopt : 
    /**
     * *
     * Le filtre doit comparer à la data envoyé. C'est un objet pourvant contenir ce qu'on veut. Une
     *
     * @{@link List} dans le cas d'une recherche utilisant l'opérateur in, un {@link Long} une {@link  String}...
     */
    Object data;
    /**
     * *
     * Le type du champs sur lequel porte la requête. La dao ne va pas chercher à découvrir le type du champs sur lequel
     * porte la requête mais se fier a ce qui est spécifié ici.
     */
    Class type;

    public String getField() {
        return field;
    }

    /**
     * *
     * setter de {@linkplain #field}
     *
     * @param field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * *
     * getter de {@link #op}
     *
     * @return
     */
    public String getOp() {
        return op;
    }

    /**
     * *
     * setter de {@link #op}
     *
     * @return
     */
    public void setOp(String op) {
        this.op = op;
    }

    /***
     * getter for {@link #data}
     * @return 
     */
    public Object getData() {
        return data;
    }

    /***
     * setter for {@linkplain #data}
     * @param data 
     */
    public void setData(Object data) {
        this.data = data;
    }

    
    /***
     * getter for {@link #type}
     * @return 
     */
    public Class getType() {
        return type;
    }

    /***
     * setter for {@link #type}
     * @param type 
     */
    public void setType(Class type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

//        return true;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchFilter other = (SearchFilter) obj;
        if ((this.field == null) ? (other.field != null) : !this.field.equals(other.field)) {
            return false;
        }
        if ((this.op == null) ? (other.op != null) : !this.op.equals(other.op)) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            System.out.println("---> FALSE pour : " + this.data.getClass());
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }
}
