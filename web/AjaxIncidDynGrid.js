/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#afin').on('click', function truc() {
        rechercherGrid();
    });



    function rechercherGrid() {
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
        else{
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

        $("#list").jqGrid('setGridParam', {url: rootpath+'incidents/list?type=' + type + "&vue=grid", data: [], postData: {filters: JSON.stringify(filters)}});
        $("#list").jqGrid().trigger("reloadGrid");



    }
    ;


});