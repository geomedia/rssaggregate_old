/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.traitement.AbstrRaffineur;
import rssagregator.beans.traitement.RaffineurSimpleImplementation;

/**
 *
 * @author clem
 */
public class ItemTest {

    public ItemTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//    /**
//     * Test of getTitre method, of class Item.
//     */
//    @Test
//    public void testGetTitre() {
//        System.out.println("getTitre");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getTitre();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTitre method, of class Item.
//     */
//    @Test
//    public void testSetTitre() {
//        System.out.println("setTitre");
//        String titre = "";
//        Item instance = new Item();
//        instance.setTitre(titre);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDescription method, of class Item.
//     */
//    @Test
//    public void testGetDescription() {
//        System.out.println("getDescription");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getDescription();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDescription method, of class Item.
//     */
//    @Test
//    public void testSetDescription() {
//        System.out.println("setDescription");
//        String description = "";
//        Item instance = new Item();
//        instance.setDescription(description);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDatePub method, of class Item.
//     */
//    @Test
//    public void testGetDatePub() {
//        System.out.println("getDatePub");
//        Item instance = new Item();
//        Date expResult = null;
//        Date result = instance.getDatePub();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDatePub method, of class Item.
//     */
//    @Test
//    public void testSetDatePub() {
//        System.out.println("setDatePub");
//        Date datePub = null;
//        Item instance = new Item();
//        instance.setDatePub(datePub);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDateRecup method, of class Item.
//     */
//    @Test
//    public void testGetDateRecup() {
//        System.out.println("getDateRecup");
//        Item instance = new Item();
//        Date expResult = null;
//        Date result = instance.getDateRecup();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDateRecup method, of class Item.
//     */
//    @Test
//    public void testSetDateRecup() {
//        System.out.println("setDateRecup");
//        Date dateRecup = null;
//        Item instance = new Item();
//        instance.setDateRecup(dateRecup);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGuid method, of class Item.
//     */
//    @Test
//    public void testGetGuid() {
//        System.out.println("getGuid");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getGuid();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGuid method, of class Item.
//     */
//    @Test
//    public void testSetGuid() {
//        System.out.println("setGuid");
//        String guid = "";
//        Item instance = new Item();
//        instance.setGuid(guid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCategorie method, of class Item.
//     */
//    @Test
//    public void testGetCategorie() {
//        System.out.println("getCategorie");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getCategorie();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCategorie method, of class Item.
//     */
//    @Test
//    public void testSetCategorie() {
//        System.out.println("setCategorie");
//        String categorie = "";
//        Item instance = new Item();
//        instance.setCategorie(categorie);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getHashContenu method, of class Item.
//     */
//    @Test
//    public void testGetHashContenu() {
//        System.out.println("getHashContenu");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getHashContenu();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setHashContenu method, of class Item.
//     */
//    @Test
//    public void testSetHashContenu() {
//        System.out.println("setHashContenu");
//        String hashContenu = "";
//        Item instance = new Item();
//        instance.setHashContenu(hashContenu);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSyncStatut method, of class Item.
//     */
//    @Test
//    public void testGetSyncStatut() {
//        System.out.println("getSyncStatut");
//        Item instance = new Item();
//        Byte expResult = null;
//        Byte result = instance.getSyncStatut();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSyncStatut method, of class Item.
//     */
//    @Test
//    public void testSetSyncStatut() {
//        System.out.println("setSyncStatut");
//        Byte syncStatut = null;
//        Item instance = new Item();
//        instance.setSyncStatut(syncStatut);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getListFlux method, of class Item.
//     */
//    @Test
//    public void testGetListFlux() {
//        System.out.println("getListFlux");
//        Item instance = new Item();
//        List expResult = null;
//        List result = instance.getListFlux();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setListFlux method, of class Item.
//     */
//    @Test
//    public void testSetListFlux() {
//        System.out.println("setListFlux");
//        List<Flux> listFlux = null;
//        Item instance = new Item();
//        instance.setListFlux(listFlux);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLink method, of class Item.
//     */
//    @Test
//    public void testGetLink() {
//        System.out.println("getLink");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getLink();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLink method, of class Item.
//     */
//    @Test
//    public void testSetLink() {
//        System.out.println("setLink");
//        String link = "";
//        Item instance = new Item();
//        instance.setLink(link);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getContenu method, of class Item.
//     */
//    @Test
//    public void testGetContenu() {
//        System.out.println("getContenu");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getContenu();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setContenu method, of class Item.
//     */
//    @Test
//    public void testSetContenu() {
//        System.out.println("setContenu");
//        String contenu = "";
//        Item instance = new Item();
//        instance.setContenu(contenu);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getID method, of class Item.
//     */
//    @Test
//    public void testGetID() {
//        System.out.println("getID");
//        Item instance = new Item();
//        Long expResult = null;
//        Long result = instance.getID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setID method, of class Item.
//     */
//    @Test
//    public void testSetID() {
//        System.out.println("setID");
//        Long ID = null;
//        Item instance = new Item();
//        instance.setID(ID);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDoublon method, of class Item.
//     */
//    @Test
//    public void testGetDoublon() {
//        System.out.println("getDoublon");
//        Item instance = new Item();
//        List expResult = null;
//        List result = instance.getDoublon();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDoublon method, of class Item.
//     */
//    @Test
//    public void testSetDoublon() {
//        System.out.println("setDoublon");
//        List<DoublonDe> doublon = null;
//        Item instance = new Item();
//        instance.setDoublon(doublon);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getModified method, of class Item.
//     */
//    @Test
//    public void testGetModified() {
//        System.out.println("getModified");
//        Item instance = new Item();
//        Timestamp expResult = null;
//        Timestamp result = instance.getModified();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setModified method, of class Item.
//     */
//    @Test
//    public void testSetModified() {
//        System.out.println("setModified");
//        Timestamp modified = null;
//        Item instance = new Item();
//        instance.setModified(modified);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of compareTo method, of class Item.
//     */
//    @Test
//    public void testCompareTo() {
//        System.out.println("compareTo");
//        Item o = null;
//        Item instance = new Item();
//        int expResult = 0;
//        int result = instance.compareTo(o);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addFlux method, of class Item.
//     */
//    @Test
//    public void testAddFlux() throws Exception {
//        System.out.println("addFlux");
//        Flux f = null;
//        Item instance = new Item();
//        instance.addFlux(f);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appartientAuFlux method, of class Item.
//     */
//    @Test
//    public void testAppartientAuFlux() throws Exception {
//        System.out.println("appartientAuFlux");
//        Flux f = null;
//        Item instance = new Item();
//        boolean expResult = false;
//        boolean result = instance.appartientAuFlux(f);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getItemRaffinee method, of class Item.
//     */
//    @Test
//    public void testGetItemRaffinee() {
//        System.out.println("getItemRaffinee");
//        Item instance = new Item();
//        ItemRaffinee expResult = null;
//        ItemRaffinee result = instance.getItemRaffinee();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setItemRaffinee method, of class Item.
//     */
//    @Test
//    public void testSetItemRaffinee() {
//        System.out.println("setItemRaffinee");
//        ItemRaffinee itemRaffinee = null;
//        Item instance = new Item();
//        instance.setItemRaffinee(itemRaffinee);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getReadURL method, of class Item.
//     */
//    @Test
//    public void testGetReadURL() {
//        System.out.println("getReadURL");
//        Item instance = new Item();
//        String expResult = "";
//        String result = instance.getReadURL();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of returnDoublonforRaffineur method, of class Item.
     */
    @Test
    public void testReturnDoublonforRaffineur() {


        AbstrRaffineur raffineur = new RaffineurSimpleImplementation();
        raffineur.setID(new Long(11));

        Item item1 = new Item();
        item1.setID(new Long(1));

        DoublonDe resu = item1.returnDoublonforRaffineur(raffineur);
        if (resu != null) {
            fail("Devait être null");
        }



        DoublonDe doublonDe1 = new DoublonDe();
        doublonDe1.setID(new Long(4));
        doublonDe1.setItemRef(item1);
        doublonDe1.setItemDoublon(item1);
        doublonDe1.setRaffineurEmploye(raffineur);
        item1.getDoublon().add(doublonDe1);
        
        resu = item1.returnDoublonforRaffineur(raffineur);
        if(!resu.getItemRef().getID().equals(item1.getID()) || !resu.getItemDoublon().getID().equals(item1.getID())){
            fail("devait retourné item1 ");
        }


    }

    /**
     * Test of remonterRecursivementDoublon method, of class Item.
     */
    @Test
    public void testRemonterRecursivementDoublon() {


        AbstrRaffineur raffineur = new RaffineurSimpleImplementation();
        raffineur.setID(new Long(11));

        Item item1 = new Item();
        item1.setID(new Long(1));
        Item item2 = new Item();
        item2.setID(new Long(2));
        Item item3 = new Item();
        item3.setID(new Long(3));

        DoublonDe doublonDe1 = new DoublonDe();
        doublonDe1.setID(new Long(4));
        doublonDe1.setItemRef(item1);
        doublonDe1.setItemDoublon(item1);
        doublonDe1.setRaffineurEmploye(raffineur);
        item1.getDoublon().add(doublonDe1);



        DoublonDe doublonDe2 = new DoublonDe();
        doublonDe2.setID(new Long(5));
        doublonDe2.setItemRef(item1);
        doublonDe2.setItemDoublon(item2);
        doublonDe2.setRaffineurEmploye(raffineur);
        item2.getDoublon().add(doublonDe2);


        DoublonDe doublonDe3 = new DoublonDe();
        doublonDe3.setID(new Long(6));
        doublonDe3.setItemRef(item2);
        doublonDe3.setItemDoublon(item3);
        doublonDe3.setRaffineurEmploye(raffineur);
        item3.getDoublon().add(doublonDe3);


        //On tente de remonter le 1 
//        item1.r
        Item resu = item1.remonterRecursivementDoublon(raffineur);
        if (!resu.getID().equals(item1.getID())) {
            fail("1 on voulait item 1 on a " + resu.getID());
        }


        resu = item2.remonterRecursivementDoublon(raffineur);
        if (!resu.getID().equals(item1.getID())) {
            fail("2 on voulait item 1 ");
        }


        resu = item3.remonterRecursivementDoublon(raffineur);
        if (!resu.getID().equals(item1.getID())) {
            fail("3 on voulait item 1 ");
        }

    }

    /**
     * Test of addDoublon method, of class Item.
     */
    @Test
    public void testAddDoublon() {


        AbstrRaffineur raffineur = new RaffineurSimpleImplementation();
        raffineur.setID(new Long(11));

        Item item1 = new Item();
        item1.setID(new Long(1));
        Item item2 = new Item();
        item2.setID(new Long(2));
        Item item3 = new Item();
        item3.setID(new Long(3));

        DoublonDe doublonDe1 = new DoublonDe();
        doublonDe1.setID(new Long(4));
        doublonDe1.setItemRef(item1);
        doublonDe1.setItemDoublon(item1);
        doublonDe1.setRaffineurEmploye(raffineur);
        
        item1.getDoublon().add(doublonDe1);
             
        
        item2.addDoublon(item1, raffineur);
        if(item2.getDoublon().isEmpty()){
            fail("l'item 2 doit maintenant posséder des doublons");
        }
        if(item2.getDoublon().size()!=1){
            fail("il devait y avoir un seul entité doublon dans l'item 2");
        }
        
       DoublonDe doublon2 =  item2.getDoublon().get(0);
       if(!doublon2.getItemRef().equals(item1)){
           fail("devait pointer vers item1 mais pointe vers " + doublon2.getItemRef().getID());
       }
       if(!doublon2.getItemDoublon().equals(item2)){
           fail("L'item doublon devait être la 2 c'est la " + doublon2.getItemDoublon().getID());
       }
        

        item3.addDoublon(item2, raffineur);
        if(item3.getDoublon().size()!=1){
            fail("on vevait avoir une entité doublon pour l'item 3, on en a " + item3.getDoublon().size());
        }
        
        
        DoublonDe doublon3 = item3.getDoublon().get(0);
        if(!doublon3.getItemRef().equals(item1)){
            fail("devait pointer sur item 1 pour référence");
        }
        if(!doublon3.getItemDoublon().equals(item3)){
            fail("L'item doublon devait être la 3. C'est la " + doublon3.getItemDoublon().getID());
        }
        

    }
}