/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//Lors du click sur un bouton de la pagination il faut modifier le paramettre du champ caché en fonction du bouton cliqué, ensuite, on lance la requete
function paginsubmit(bt) {

    // On supprimer la liste 1 des journaux
    $('#firstResult').val(bt.value);
    $('#afin').click();
}


$(document).ready(function() {
    var i;
    var $btAfin = $('#afin');
    var $resudiv = $('#resudiv');

// Lors d'un click sur le boutton affin
    $btAfin.on('click', function truc2() {

        $resudiv.empty(); // on vide la liste des départements

        //Récupération des paramettres dans le formulaire, il seront utilisé plus bas dans la requête ajax
        $itPrPage = $('#itPrPage');
        $firstResult = $('#firstResult');
        $typeincident=$('input:checked[name="type"]');
        // Gestion de la selection pour clos ou non clos;
        $clos = $('#clos').prop('checked');
        alert($typeincident.val());

//-----------------------------------------------------------------
//                  ENVOIE DE LA REQUETE EN AJAX
//-----------------------------------------------------------------
        $.ajax({
            url: 'incidents/list?vue=jsondesc',
            data: '&firstResult=' + $firstResult.val() + '&itPrPage=' + $itPrPage.val() + '&clos=' + $clos+'&type='+$typeincident.val(), // on envoie $_GET['id_region']
            dataType: 'json',
            success: function(jsonentre) {

                var json = jsonentre['items'];
                var i;
                var j;
                // réécriture du 

                for (i = 0; i < json.length; i++) {
//                        <input name="id" type="checkbox" value="${flux.ID}"/>
                    $resudiv.append('<li>' +
                            '<a href="incidents/read?id=' + json[i]['id'] + '&type='+$typeincident.val()+'">' +
                            json[i]['flux'] + '</a>' +
                            '<p>' + json[i]['messageEreur'] + '</p>' +
                            '<p>Date début : ' + json[i]['dateDebut'] + ' - Date fin : ' + json[i]['dateFin'] + '</p>'+
                            '</li>');
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
});