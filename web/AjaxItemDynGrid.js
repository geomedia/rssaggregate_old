/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//Lors du click sur un bouton de la pagination il faut modifier le paramettre du champ caché en fonction du bouton cliqué, ensuite, on lance la requete
//function paginsubmit(bt) {
//
//    // On supprimer la liste 1 des journaux
//    $('#firstResult').val(bt.value);
//
//}



/***
 *  Utilisé par JQgrid pour formater le champ titre de l'item
 * @param {type} cellvalue
 * @param {type} options
 * @param {type} rowObjcet
 * @param {type} l4
 * @param {type} l5
 * @returns {String}
 */
function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
    return '<a href = "/RSSAgregate/item/read?id=' + rowObjcet[0] + '">' + rowObjcet[1] + '</a>';
}


/***
 * Utilisé par JQgrid pour formater le champ flux de l'item
 * @param {type} cellvalue
 * @param {type} options
 * @param {type} rowObjcet
 * @param {type} l4
 * @returns {String}
 */
function  fluxFormatter(cellvalue, options, rowObjcet, l4) {
    pp = "";
    for (i = 0; i < cellvalue.length; i++) {
        pp += "<div class=\"boxelement\">" + cellvalue[i]['val'] + "</div>  ";

    }
    return pp;
}



/***
 * Supprimer les balise html coté utilisateur
 * @param {type} cellvalue
 * @param {type} options
 * @param {type} rowObjcet
 * @param {type} l4
 * @returns {@exp;@call;$@call;text}
 */
function descFormatter(cellvalue, options, rowObjcet, l4) {
    var d = document;
    var odv = d.createElement("div");
    $(odv).append(cellvalue);
    return $(odv).text();
}

function  dateFormatter(cellvalue, options, rowObjcet, l4) {

    var datePub = $.datepicker.formatDate('yy-mm-dd', new Date(cellvalue));
    return datePub;
}



;


$(document).ready(function() {

    // Au démarrage on définit date 1 et 2 
    $('#date1').val("2000-01-01");

    var datePub = $.datepicker.formatDate('yy-mm-dd', new Date(new Date())); // Il s'agit de la date du jour
    $('#date2').val(datePub);



    /***
     * Gestion du click sur le boutton reset pour réinitialiser la selection de l'utilisateur
     */
    $('#reset').on('click', function() {
        $('#fluxSelection2').empty();
        // Au démarrage on définit date 1 et 2 
        $('#date1').val("2000-01-01");

        var datePub = $.datepicker.formatDate('yy-mm-dd', new Date(new Date())); // Il s'agit de la date du jour
        $('#date2').val(datePub);

        $('#afin').click()
    });



    /***
     * Affichage des données brutes d'une items et mise a jour des éléments html. La requête est soumise avec ajax
     */
    $('#donneeBrutes').on('change', function truc3() {

        // récupération de l'id

        id = $('#donneeBrutes').val();

        $.ajax({
            url: "/RSSAgregate/item/donneesbrutes", // le nom du fichier indiqué dans le formulaire
            type: "POST", // la méthode indiquée dans le formulaire (get ou post)
            data: 'id=' + id, // je sérialise les données (voir plus loin), ici les $_POST
            dataType: 'json',
            success: function(html) { // je récupère la réponse du fichier PHP
                $('#divDonneeBrutes').empty();

                var datePub = $.datepicker.formatDate('MM dd, yy', new Date(html['datePub'] * 1000));
                var dateReq = $.datepicker.formatDate('MM dd, yy', new Date(html['dateRecup'] * 1000));

                $('#divDonneeBrutes').append("<p>titre :  " + html['titre'] + "</p>")
                $('#divDonneeBrutes').append("<p id=\"datePub\">Date publication :  " + datePub + "</p>")
                $('#divDonneeBrutes').append("<p>Date récupération :  " + dateReq + "</p>")
                $('#divDonneeBrutes').append("<p>guid :  " + html['guid'] + "</p>")
                $('#divDonneeBrutes').append("<p>Lien :  " + html['link'] + "</p>")

                $('#QComment').text(html['description']);
                $('#divDonneeBrutes').append($('<code id=\"co\"> </code>'));
                $('#co').text(html['description']);


            }
        });

    });



    $("#list").jqGrid({
        loadonce: false,
        url: rootpath + "item/list?vue=grid",
        datatype: "json",
        mtype: "GET",
        colNames: ["ID", "titre", "description", "flux", "date"],
        colModel: [
            {name: "ID", width: 55, hidden: true},
            {name: "titre", width: 90, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
            {name: "description", index: 'description', key: true, formatter: descFormatter, search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
            {name: "flux", index: 'flux', key: true, search: true, width: 80, formatter: fluxFormatter, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
            {name: "date répub", formatter: dateFormatter}



            //                            {name: "pays", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
            //                            {name: "typeJournal", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
            //                            {name: "urlAccueil", width: 150, sortable: true, searchoptions: {sopt: ['cn', 'eq']}}
        ],
        pager: '#pager',
        rowNum: 50,
        rowList: [50, 100, 200],
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
        ident: "\t",
        height: 500,
        search: true,
        //                        search: {
        //                            modal: true,
        //                            Find: 'txt recherche',
        //                            multipleSearch: true,
        //                            sFilter: 'lalalalaa'
        //                        },
        multipleSearch: true,
        postData: {
            filters: {groupOp: "AND", rules: [/*{field: "titre", op: "gt", data: "truc"}, {field: "nom", op: "lt", data: "ss"}*/]}
        }

    });

    $("#list").jqGrid('navGrid', '#pager', {add: false, edit: false, del: false, search: true, refresh: true},
    {}, {}, {}, {multipleSearch: true, multipleGroup: true, showQuery: true});

    optionsSearch = {
        multipleSearch: true, multipleGroup: true, showQuery: true
    };


    jQuery("#list").navGrid('#pager', {add: false, edit: false, del: false, search: true, refresh: true}, {}, {}, {}, optionsSearch).navButtonAdd('#pager',
            {
                caption: "'<strong style=\"font-weight: bolder;color: red;\">Export CSV</strong>",
                buttonicon: "ui-icon-add",
                onClickButton: function() {

//                    rafine= confirm("Item Rafinée ?");

                    $('#dia').remove();
                    $('nav').append('<div id="dia" title="Information sur le traitement"><p><label>Supprimer HTML</label> <input type="checkbox" id="suppHtml"/><br />\n\
<label>Utiliser le \\ comme caractère d\'échapement</label><input type="checkbox" id="escape"/>\n\
<p>L\'export des données peut prendre plusieurs minutes. Si cette export dure plus de 20 secondes vous serez automatiquement redigé vers le répertoire contenant vos fichiers résultats bien que la tache d\'export ne soit pas nécessairement terminée. Recharger cette page par la suite pour voir tous les fichiers</p>\n\
</p></div>');
                    $('#dia').dialog({minHeight: 300, minWidth: 400, closeText: "hide", show: "fade", dialogClass: "alert"});
                    $('#dia').dialog({
                        modal: true,
                        buttons: {
                            Ok: function() {

                                urlReq = rootpath + 'item/list?vue=csv';
                                if ($('#escape').is(':checked')) {
                                    urlReq += '&escape=true';
                                }
                                if ($('#suppHtml').is(':checked')) {
                                    urlReq += '&html=true';
                                }

                                $("#list").jqGrid('excelExport', {tag: 'csv', url: urlReq});

                                $(this).dialog("close");
                            },
                            Cancel: function() {
                                $(this).dialog("close");
                            }

                        }
                    });



                    opt = {exptype: "jsonstring"};

                },
                position: "last"
            });

    jQuery("#list").navGrid('#pager', {add: false, edit: false, del: false, search: true, refresh: true}, {}, {}, {}, optionsSearch).navButtonAdd('#pager',
            {
                caption: "'<strong style=\"font-weight: bolder;color: red;\">Export XLS</strong>",
                buttonicon: "ui-icon-add",
                onClickButton: function() {
                    opt = {exptype: "jsonstring"};
                    $("#list").jqGrid('excelExport', {tag: 'csv', url: rootpath + 'item/list?vue=xls'});
                },
                position: "last"
            });




    /***
     * Lors du click sur le bouton permettant d'affiner la recherche
     */
    $('#afin').on('click', function rechercheItem() {
        // Récupération des paramettre de la requete
//        var $fluxSelection2 = $('#fluxSelection2');
        options = $('#fluxSelection2 li');

        //on force la sélection dans la liste 2 afin de pouvoir utiliser la fonction val() sur ce composant html

        idFlux = {
            field: "listFlux",
            op: "in",
            data: []
        };

        strId = "";
        for (i = 0; i < options.length; i++) {
//            strId += strId+", "
            idFlux['data'].push(options[i]['value']);
        }
        if (strId.length > 0) {
            strId = strId.substr(0, strId.length - 2);
        }
//        idFlux['data'].push(strId);

        $('#resudiv').empty(); // on vide la liste des départements

        //Récupération des paramettres dans le formulaire, il seront utilisé plus bas dans la requête ajax
        $itPrPage = $('#itPrPage');
        $firstResult = $('#firstResult');
        $order = $('#order');

        // Récupération des flux sélectionne
        champSpe = []; // Variable permettant de récupérer les champs spéciaux
        if (idFlux['data'].length > 0) {
            champSpe.push(idFlux);
        }


        //Gestion des date
        $date1 = $('#date1').val() + " 00:00:00";
        d1 = {
            field: "dateRecup",
            op: "gt",
            data: $date1
        };

        $date2 = $('#date2').val() + ' 23:59:59';
        d2 = {
            field: "dateRecup",
            op: "lt",
            data: $date2
        };
        champSpe.push(d2);
        champSpe.push(d1);

        //-----------------------------------------------------------------
//              CONSTRUCTION DU FILTRE POUR Recharger la GRID
//-----------------------------------------------------------------
        filters =
                {
                    "caption": "truc modif",
                    "groupOp": "AND",
                    "rules": [],
                    spefield: champSpe
                };

        $("#list").jqGrid('setGridParam', {data: [], postData: {filters: JSON.stringify(filters)}});
        $("#list").jqGrid().trigger("reloadGrid");

    });
});