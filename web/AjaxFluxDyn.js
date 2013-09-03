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
        $journalid = $('#journalid');



//-----------------------------------------------------------------
//                  ENVOIE DE LA REQUETE EN AJAX
//-----------------------------------------------------------------
        $.ajax({
            url: '/RSSAgregate/flux/list?&vue=jsondesc',
            data: 'journalid=' + $journalid.val() + '&firstResult=' + $firstResult.val() + '&itPrPage=' + $itPrPage.val(), // on envoie $_GET['id_region']
            dataType: 'json',
            success: function(jsonentre) {

                var json = jsonentre['items'];
                var i;
                var j;
                // réécriture du 
                for (i = 0; i < json.length; i++) {

//                        <input name="id" type="checkbox" value="${flux.ID}"/>
                    $resudiv.append('<li>' +
                            '<input name="id" type="checkbox" value="' + json[i]['id'] + '">' +
                            '</input>' +
                            '<a href="/RSSAgregate/flux/read?id=' + json[i]['id'] + '">' + json[i]['flux'].trim() + '</a>');
                    $resudiv.append('</li>');
                }

                // Il faut redessiner les boutton 
                $('#btPaginDiv').empty();
                var $nbitem = jsonentre['nbitem'];

//                    var $itPrPage = jsonentre['itPrPage'];
                var $itPrPage = parseInt($('#itPrPage').val());
//                    alert($itPrPage);
//                    jsonentre['itPrPage'];
                var $firsResult = jsonentre['firstResult'];

                var i;
                var $debut;
                if (($firsResult - 5 * $itPrPage) < 0) {
                    $debut = 0;
                }
                else {
                    $debut = $firsResult - 5 * $itPrPage;
                }

                var fin;
                if (($firsResult + 10 * $itPrPage) > $nbitem) {
                    fin = $nbitem;
                }
                else {
                    fin = $firsResult + 10 * $itPrPage;
                }

                for (i = $debut; i < fin; i = i + $itPrPage) {
                    $('#btPaginDiv').append('<button class="btPagin" type="button" name="btPagin" value="' + i + '" onclick="paginsubmit(this)">' + i + ' - ' + (i + $itPrPage) + '</button>')
                }


                $('#btPaginDiv').append('Nombre de résultat au total : ' + $nbitem)

                //On colorise le button courant
                var $bts = $('button.btPagin').each(function truc(bt) {
                    if (this.value == $firsResult) {
                        this.setAttribute('class', 'btPaginOn');
                    }
                });
            }
        });
    });

//    }
//    )
});