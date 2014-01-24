/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#afin').on('click', function truc() {
        rechercherGrid();
    });
    function rechercherGrid() {
        //On détruit la grille précédente

        $('#list').jqGrid('GridUnload');

        // On récupère le type

        type = $('#type').val();
        type = $('input:checked[name="type"]').val();
        clos = $('input:checked[name="clos"]').val();
        champSpe = [];
        if (clos === 'true') {
//            alert("clos");
            dateFin = {
                field: "dateFin",
                op: "inn",
                data: []
            };
            champSpe.push(dateFin);
        }
        else {
//            alert("non clos");
            dateFin = {
                field: "dateFin",
                op: "isn",
                data: []
            };
            champSpe.push(dateFin);
        }


        filters = {
            "caption": "truc modif",
            "groupOp": "AND",
            "rules": [],
            spefield: champSpe
        };
//        alert(JSON.stringify(filters));


//        $("#list").jqGrid('setGridParam', {url: rootpath + 'incidents/list?type=' + type + "&vue=gridd"+"&filters="+ JSON.stringify(filters), data: [], postData: {filters: JSON.stringify(filters)}});
        $("#list").jqGrid({
            loadonce: true,
            rowTotal: 9999,
            url: rootpath + "incidents/list?vue=grid&type=" + type+"&filters="+ JSON.stringify(filters),
//                                            url: "${rootpath}item/list?vue=grid",
            datatype: "json",
//            datatype: 'local',
            mtype: "GET",
            colNames: ["ID", "intitulé", "type", "messageEreur", "dateDebut", "dateFin"],
            colModel: [
                {name: "ID", width: 55, key: true, hidden: true, searchoptions: {sopt: ['cn', 'eq']}},
                {name: "intitulé", classtype: 'clem', sorttype: 'float', width: 90, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
                {name: "type", index: 'type', key: false, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                {name: "messageEreur", index: 'langue', key: false, search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                {name: "dateDebut", sorttype:'text', datefmt:'Y-mm-dd',   width: 80, align: "right", searchoptions: {sopt: ['eq', 'ge', 'le']}},
                {name: "dateFin", sorttype:'text', datefmt:'Y-mm-dd', width: 80, align: "right", /*stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}},*/ searchoptions: {sopt: [ 'eq', 'ge', 'le']}},
            ],
            pager: "#pager",
            rowNum: 30,
            rowList: [10, 20, 30, 50, 100, 200, 500, 1000],
            sortname: "invid",
            sortorder: "desc",
            viewrecords: true,
            gridview: true,
            autoencode: true,
            caption: "Recherche parmis les journaux",
            sortable: true,
            sorttype: 'text',
            autowidth: true,
            exptype: "csvstring",
            root: "grid",
            height: 500,
            multiselect: true,
            ident: "\t"
//                                    filterToolbar: {searchOperators: true},
//                                    search: {
//                                        caption: "Search...",
//                                        Find: "Find",
//                                        Reset: "Reset",
//                                        odata: ['equal', 'not equal', 'less', 'less or equal', 'greater', 'greater or equal', 'begins with', 'does not begin with', 'is in', 'is not in', 'ends with', 'does not end with', 'contains', 'does not contain'],
//                                        groupOps: [{op: "AND", text: "all"}, {op: "OR", text: "any"}],
//                                        matchText: " match",
//                                        rulesText: " rules"
//                                    }
        }
        );



        optionsearch = {searchOperators: true, stringResult: true};
//                                jQuery("#list").jqGrid('filterToolbar', {searchOperators: true});
        jQuery("#list").filterToolbar(optionsearch);
        jQuery("#list").navGrid('#pager', {edit: false, add: false, del: false, search: false})
                .navButtonAdd('#pager', {
            caption: "'Export To CSV",
            buttonicon: "ui-icon-add",
            onClickButton: function() {

                opt = {exptype: "jsonstring"};
                $("#list").jqGrid('excelExport', {tag: 'csv', url: '${rootpath}journaux/list?vue=csv'});
            },
            position: "last"
        })
                .navButtonAdd('#pager', {
            caption: "'Clore ",
            onClickButton: function() {
                reponse = confirm('Vous vous apprétez à Clore des incidents manuellement. Confirmez vous votre choix ?');
                if (reponse) {
                    selRowId = $('#list').jqGrid('getGridParam', 'selarrrow');
                    alert('' + selRowId);
                    //chaine = "";
                    //for (i = 0; i < selRowId.length; i++) {
                    //    chaine += 'id=' + selRowId[i] + ',';
                    //}
                    //if (chaine.length > 2) {
                    //    chaine = chaine.substr(0, chaine.length - 1);
                    //}
                    //url = ${rootpath} + 'flux/rem?' + chaine;
                    url = rootpath + 'incidents/close?id=' + selRowId;
                    location.href = url;
                }
            }
        }




        ).navButtonAdd('#pager', {
            caption: "'Supprimer ",
            onClickButton: function() {
                reponse = confirm('Vous vous apprétez à supprimer un flux. Toutes les items associées seront supprimée. Cette manipulation est irréverssible. Confirmez vous votre choix ?');
                if (reponse) {
                    selRowId = $('#list').jqGrid('getGridParam', 'selarrrow');
                    alert('' + selRowId);
                    //chaine = "";
                    //for (i = 0; i < selRowId.length; i++) {
                    //    chaine += 'id=' + selRowId[i] + ',';
                    //}
                    //if (chaine.length > 2) {
                    //    chaine = chaine.substr(0, chaine.length - 1);
                    //}
                    //url = ${rootpath} + 'flux/rem?' + chaine;
                    url = rootpath + 'incidents/rem?id=' + selRowId;
                    location.href = url;
                }
            }
        });
//          $('#gs_dateDebut').datepicker();
          $('#gs_dateDebut').datepicker({dateFormat: "yy-mm-dd"});
          $('#gs_dateFin').datepicker({dateFormat: "yy-mm-dd"});
    }
    ;
    
    
    
//    $('#gs_dateDebut').datepicker();
    
//       $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
    
});

/***
 *  Formal la première cellule de la grid
 * @param {type} cellvalue
 * @param {type} options
 * @param {type} rowObjcet
 * @param {type} l4
 * @param {type} l5
 * @returns {String}
 */
function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
    id = rowObjcet[0];
    
    if(id===undefined){ // Bug étrange, parfois il faut aller chercher la colonne 0 parfois c'est un tableau avec clé, notamment après un trie.
        id = rowObjcet['ID'];
    }
    texteLien = cellvalue;
//    typeIncid = rowObjcet[2];
    return '<a href = "/RSSAgregate/incidents/read?id=' + id + "&type=" + type + "\">" + texteLien + '</a>';
}

function dateFormatter(cellvalue, options, rowObjcet, l4, l5) {
//    alert(cellvalue);
   date = new Date(cellvalue);
    var datePub = $.datepicker.formatDate('yy-mm-dd', new Date(cellvalue));
//   var datePub = $.datepicker.formatDate('MM dd, yy', new Date(date));
    return datePub;
}


//function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
//    // Lors du classement après recherche sur le client side, le rowObjet ne peut être lu de la même manière. La ligne suivant permet de pallier à ce problème
//    id = rowObjcet[0];
//    texteLien = rowObjcet[1];
//    if (rowObjcet[0] === undefined) {
//        id = rowObjcet['ID'];
//        texteLien = rowObjcet['nom'];
//    }
//    return '<a href = "/RSSAgregate/flux/read?id=' + id + '">' + texteLien + '</a>';
//}


//function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
////        alert("0" + rowObjcet[0]);
////        alert("1" + rowObjcet[1])
////        alert("2"+ rowObjcet[2])
//
//    return '<a href = "/RSSAgregate/incidents/read?id=' + rowObjcet[0] + "&type=" + rowObjcet[2] + '">' + rowObjcet[1] + '</a>';
//}


/***
 * Déclanche le rafraichissement de la grid
 * @returns {undefined}
 */
function gogrid() {
//    $("#list").empty();
    /***
     *  Utilisé par JQgrid pour formater le champ journal en un lien
     * @param {type} cellvalue
     * @param {type} options
     * @param {type} rowObjcet
     * @param {type} l4
     * @param {type} l5
     * @returns {String}
     */

//  $("#list").empty();
//    $(function() {



//    });
//$("#list").navGrid("#pager",{refreshstate:'current'});
//  $("#list").jqGrid().trigger("reloadGrid");
//  alert("dd")

}
;