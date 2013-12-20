/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#afin').on('click', function truc() {
        rechercherGrid();
    });
    function rechercherGrid() {
        alert('grid')
        // On récupère le type

        type = $('#type').val();
        type = $('input:checked[name="type"]').val();
        clos = $('input:checked[name="clos"]').val();
        champSpe = [];
        if (clos === 'true') {
            dateFin = {
                field: "dateFin",
                op: "inn",
                data: []
            };
            champSpe.push(dateFin);
        }
        else {
            dateFin = {
                field: "dateFin",
                op: "isn",
                data: []
            };
            champSpe.push(dateFin);
        }


        filters =
                {
                    "caption": "truc modif",
                    "groupOp": "AND",
                    "rules": [],
                    spefield: champSpe
                };
        $("#list").jqGrid('setGridParam', {url: rootpath + 'incidents/list?type=' + type + "&vue=grid", data: [], postData: {filters: JSON.stringify(filters)}});
//        $("#list").jqGrid().trigger("reloadGrid");
        gogrid();
        alert('f');
    }
    ;
});
function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
    // Lors du classement après recherche sur le client side, le rowObjet ne peut être lu de la même manière. La ligne suivant permet de pallier à ce problème
    id = rowObjcet[0];
    texteLien = rowObjcet[1];
    if (rowObjcet[0] === undefined) {
        id = rowObjcet['ID'];
        texteLien = rowObjcet['nom'];
    }
    return '<a href = "/RSSAgregate/flux/read?id=' + id + '">' + texteLien + '</a>';
}


function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
//        alert("0" + rowObjcet[0]);
//        alert("1" + rowObjcet[1])
//        alert("2"+ rowObjcet[2])
    return '<a href = "/RSSAgregate/incidents/read?id=' + rowObjcet[0] + "&type=" + rowObjcet[2] + '">' + rowObjcet[1] + '</a>';
}

function gogrid() {
    alert("gogo")
    $("#list").jqGrid({
        url: "${rootpath}flux/list?vue=grid",
        loadonce: true,
        datatype: "json",
        mtype: "GET",
        colNames: ["ID", 'nom', "Journal", "Type", "active", "created"],
        colModel: [
            {name: "ID", key: true, width: 55, hidden: true},
            {name: "nom", width: 55, search: true, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
            {name: "journalLie", width: 90, searchoptions: {sopt: ['cn', 'eq']}},
            {name: "typeFlux", title: 'Type', search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
            {name: "active", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
            {name: "created", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
        ],
        pager: "#pager",
        rowNum: 10,
        rowList: [30, 50, 100, 150, 300, 500],
        sortname: "invid",
        sortorder: "desc",
        viewrecords: true,
        gridview: true,
        autoencode: true,
        caption: "Recherche parmis les flux",
        sortable: true,
        sorttype: 'text',
        autowidth: true,
        exptype: "csvstring",
        root: "grid",
        multiselect: true,
//                                            scrollrows : true,
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
            $("#list").jqGrid('excelExport', {tag: 'csv', url: '${rootpath}flux/list?vue=csv'});
        },
        position: "last"
    })
            .navButtonAdd('#pager',
            {
                caption: "Supprimer",
                buttonicon: "ui-icon-add",
                onClickButton: function() {
                    reponse = confirm('Vous vous apprétez à supprimer un flux. Toutes les items associées seront supprimée. Cette manipulation est irréverssible. Confirmez vous votre choix ?');
                    if (reponse) {
                        selRowId = $('#list').jqGrid('getGridParam', 'selarrrow');
                        //chaine = "";
                        //for (i = 0; i < selRowId.length; i++) {
                        //    chaine += 'id=' + selRowId[i] + ',';
                        //}
                        //if (chaine.length > 2) {
                        //    chaine = chaine.substr(0, chaine.length - 1);
                        //}
                        //url = ${rootpath} + 'flux/rem?' + chaine;
                        url = rootpath + 'flux/rem?id=' + selRowId;
                        location.href = url;
                    }

                },
            })
            .navButtonAdd('#pager', {
        caption: "Mise a jour",
        buttonicon: "ui-icon-add",
        onClickButton: function() {
            alert('maj' + formatIdParamFromSelectedRow());
            location.href = $
            rootpath + 'flux/maj?' + formatIdParamFromSelectedRow();
        }
    });
}
;