/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//Lors du click sur un bouton de la pagination il faut modifier le paramettre du champ caché en fonction du bouton cliqué, ensuite, on lance la requete
function paginsubmit(bt) {

    // On supprimer la liste 1 des journaux
    $('#firstResult').val(bt.value);


    $('#afin').click();
//            $('#pagina').submit();
}



$(document).ready(function() {
    var i;
    var $btAfin = $('#afin');
    var $resudiv = $('#resudiv');

// Lors d'un click sur le boutton affin
    $btAfin.on('click', function truc2() {

        // Récupération des paramettre de la requete
        var $fluxSelection2 = $('#fluxSelection2');
        options = $('#fluxSelection2 option');

        //on force la sélection dans la liste 2 afin de pouvoir utiliser la fonction val() sur ce composant html
        for (i = 0; i < options.length; i++) {
            options[i].setAttribute('selected', 'true');
        }

        $resudiv.empty(); // on vide la liste des départements

        //Récupération des paramettres dans le formulaire, il seront utilisé plus bas dans la requête ajax
        $itPrPage = $('#itPrPage');
        $firstResult = $('#firstResult');
        $order = $('#order');
        $date2 = $('#date2');
        $date1 = $('#date1');




        // Récupération des flux sélectionne
        champSpe = [];
        
        idFlux = {
                field: "idFlux",
                op: "in",
                data: []
            };
            
        for (i = 0; i < $fluxSelection2.val().length; i++) {
            alert($fluxSelection2.val()[i]);
            idFlux['data'].push($fluxSelection2.val()[i]);
//            idFlux = {
//                field: "idFlux",
//                op: "in",
//                data: $fluxSelection2.val()[i]
//            };
            
        }
        champSpe.push(idFlux);


        //Gestion des date
        $date1 = $('#date1');
        d1 = {
            field: "date1",
            op: "gt",
            data: $date1.val()
        };

        d2 = {
            field: "date2",
            op: "lt",
            data: $date2.val()
        };
        champSpe.push(d2);
        alert($date1);
        champSpe.push(d1);
//            param = param.substr(1, param.length);

        //-----------------------------------------------------------------
//              CONSTRUCTION DU FILTRE POUR Recharger la GRID
//-----------------------------------------------------------------
        filters =
                {
                    "caption": "truc modif",
                    "groupOp": "AND",
                    "rules": [
//                        {"spefield": "idFlux", "op": "in", "data": "13940"},
//                        {"field": "invdate", "op": "le", "data": "2007-10-20"},
//                        {"field": "name", "op": "bw", "data": "Client 3"}
                    ],
                    spefield: champSpe
                };

//    alert($('#list').getGridParam()['caption']);
//    jQuery('#list').jqGrid('clearGridData');
//        jQuery("#list").searchGrid({multipleSearch:true, sFilter: 'zouzouzou'});
        $("#list").jqGrid('setGridParam', {data: [], postData: {filters: JSON.stringify(filters)}});
//        jQuery("#list").searchGrid(optionsSearch);
//        jQuery('#list').setPostData({filters: filters});
        $("#list").jqGrid().trigger("reloadGrid");
//         alert($('#list').getGridParam()['caption']);


//    $('#list').jqGrid().setGridParam(
//            {postData: filters}
//            ).trigger.('reloadGrid');
//                alert('exe');






//-----------------------------------------------------------------
//                  ENVOIE DE LA REQUETE EN AJAX
//-----------------------------------------------------------------
//        $.ajax({
//            url: 'item/list?vue=jsondesc',
//            data: param + '&firstResult=' + $firstResult.val() + '&itPrPage=' + $itPrPage.val() + '&order=' + $order.val() + "&date1=" + $date1.val() + '&date2=' + $date2.val(), // on envoie $_GET['id_region']
//            dataType: 'json',
//            success: function(jsonentre) {
//
//                var json = jsonentre['items'];
//                var i;
//                var j;
//                // réécriture du 
//                for (i = 0; i < json.length; i++) {
//                    var $str = "";
//                    $str += '<li class="item"><a href="item/read?id=' + json[i]['id'] + '">' + json[i]['titre'].trim() + '</a>';
////                        $resudiv.append('<li class="item"><a href="item/read?id=' + json[i]['id'] + '">' + json[i]['titre'].trim() + '</a>');
//                    var $tabflux = json[i]['flux'];
//                    for (j = 0; j < $tabflux.length; j++) {
//                        $str += $tabflux[j] + ' ; ';
////                            $resudiv.append($tabflux[j] + ' ; ');
//                    }
//
////                        $cont = json[i]['desc'];
////                        
////                        alert(json[i]['desc']);
//
//                    var p = '<p>' + json[i]['desc'] + '<p>';
//                    var $p = $(p).text();
//                    $str += '<p>' + $p + '</p>';
//
//                    $str += '</li>';
////                        alert((json[i]['desc']));
////                        $resudiv.append('<p>' + json[i]['desc'] + '</p>');
////                        $resudiv.append('</li>');
//
//                    $resudiv.append($str);
//
//                }
//                $('p.descP').each(function(r) {
////                        $(('p.descP')[r]).replaceWith("d");
////                        alert($($('p.descP')[r]).text());
//                });
//
//                // Il faut redessiner les boutton 
//                $('#btPaginDiv').empty();
//                var $nbitem = jsonentre['nbitem'];
//                var $itPrPage = parseInt($('#itPrPage').val())
//                var $firsResult = jsonentre['firsResult'];
//
//                var i;
//                var $debut;
//                if (($firsResult - 5 * $itPrPage) < 0) {
//                    $debut = 0;
//                }
//                else {
//                    $debut = $firsResult - 5 * $itPrPage;
//                }
//
//                var fin;
//                if (($firsResult + 10 * $itPrPage) > $nbitem) {
//                    fin = $nbitem;
//                }
//                else {
//                    fin = $firsResult + 10 * $itPrPage;
//                }
//
//                for (i = $debut; i < fin; i = i + $itPrPage) {
//                    $('#btPaginDiv').append('<button class="btPagin" type="button" name="btPagin" value="' + i + '" onclick="paginsubmit(this)">' + i + ' - ' + (i + $itPrPage) + '</button>')
//                }
//                $('#btPaginDiv').append('Nombre de résultat au total : ' + $nbitem)
//
//
//                //On colorise le button courant
//                var $bts = $('button.btPagin').each(function truc(bt) {
//                    if (this.value == $firsResult) {
//                        this.setAttribute('class', 'btPaginOn');
//
//                    }
//                });
//
//            }
//        });
    });

//    }
//    )
});