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
    
    
    
    
    
    
    
    /***
     * 
     */
     $('#selectionPeriod').on('change', function truc2() {
         
         
         id = $('#selectionPeriod').val();
         
         
         // Requête en ajax
         
         $.ajax({
            url: '/RSSAgregate/flux/statcaptation?&id='+id,
            data: '', // on envoie $_GET['id_region']
            dataType: 'json',
            success: function(jsonentre) {
                $('#affichageCaptation').empty();
                   $('#affichageCaptation').append("<p><strong>Nombre d\'item capturée ensemble de la période : </strong>"+jsonentre['statSommeItemCapture']+"</p>");
                $('#affichageCaptation').append("<p><strong>Moyenne jour : </strong>"+jsonentre['statMoyenne']+"</p>");
                $('#affichageCaptation').append("<p><strong>Minimum jour : </strong>"+jsonentre['statMin']+"</p>");
                $('#affichageCaptation').append("<p><strong>Premier quartile jour : </strong>"+jsonentre['statQuartilePremier']+"</p>");
                $('#affichageCaptation').append("<p><strong>Troisiéme quartile jour : </strong>"+jsonentre['statQuartileTrois']+"</p>");
                
                $('#affichageCaptation').append("<p><strong>Maximum jour : </strong>"+jsonentre['statMax']+"</p>");
                $('#affichageCaptation').append("<p><strong>Ecart type jour : </strong>"+jsonentre['statEcartType']+"</p>");
             
                $('#affichageCaptation').append("<p><h2>Regroupement par jour de la semaine</h2>");
                
                
                $('#affichageCaptation').append("<table border=\"1\">\n\
<tr> <td>       </td><td>Lundi</td><td>Mardi</td><td>Mercredi</td><td>Jeudi</td><td>Vendredi</td><td>Samedi</td><td>Dimanche</td></tr>\n\
<tr> <td>Moyenne</td><td>"+jsonentre['statMoyLundi']+"</td><td>"+jsonentre['statMoyMardi']+"</td><td>"+jsonentre['statMoyMercredi']+"</td><td>"+jsonentre['statMoyJeudi']+"</td><td>"+jsonentre['statMoyVendredi']+"</td><td>"+jsonentre['statMoySamedi']+"</td><td>"+jsonentre['statMoyDimanche']+"</td></tr>\n\
<tr> <td>Mediane</td><td>"+jsonentre['statMedLundi']+"</td><td>"+jsonentre['statMedMardi']+"</td><td>"+jsonentre['statMedMercredi']+"</td><td>"+jsonentre['statMedJeudi']+"</td><td>"+jsonentre['statMedVendredi']+"</td><td>"+jsonentre['statMedSamedi']+"</td><td>"+jsonentre['statMedDimanche']+"</td></tr>\n\
<tr> <td>Ecart type </td><td>"+jsonentre['statEcartTypeLundi']+"</td><td>"+jsonentre['statEcartTypeMardi']+"</td><td>"+jsonentre['statEcartTypeMercredi']+"</td><td>"+jsonentre['statEcartTypeJeudi']+"</td><td>"+jsonentre['statEcartTypeVendredi']+"</td><td>"+jsonentre['statEcartTypeSamedi']+"</td><td>"+jsonentre['statEcartTypeDimanche']+"</td></tr>\n\
</table>\n\
");
                
                
                // On dessine la plat
                $('#container').highcharts({
                                        chart: {
                                            type: 'boxplot'
                                        },
                                        title: {
                                            text: 'BoxPloat nombre d\'item jour sur la période'
                                        },
                                        legend: {
                                            enabled: false
                                        },
                                        xAxis: {
                                            categories: ['1', '2', '3', '4', '5'],
                                            title: {
                                                text: 'Flux'
                                            }
                                        },
                                        yAxis: {
                                            title: {
                                                text: 'Nombre d\'item par jour'
                                            },
                                            plotLines: [{
                                                    value: 932,
                                                    color: 'red',
                                                    width: 1,
                                                    label: {
                                                        text: 'Theoretical mean: 932',
                                                        align: 'center',
                                                        style: {
                                                            color: 'gray'
                                                        }
                                                    }
                                                }]
                                        },
                                        series: [{
                                                name: 'Observations',
                                                data: [
                                                    [jsonentre['statMin'], jsonentre['statQuartilePremier'], jsonentre['statMedian'], jsonentre['statQuartileTrois'],  jsonentre['statMax']]
                                                ],
                                                tooltip: {
                                                    headerFormat: '<em>Experiment No {point.key}</em><br/>'
                                                }
                                            }
//                                            , {
//                                                name: 'Outlier',
//                                                color: Highcharts.getOptions().colors[0],
//                                                type: 'scatter',
//                                                data: [// x, y positions where 0 is the first category
//                                                    [0, 644],
//                                                    [4, 718],
//                                                    [4, 951],
//                                                    [4, 969]
//                                                ],
//                                                marker: {
//                                                    fillColor: 'white',
//                                                    lineWidth: 1,
//                                                    lineColor: Highcharts.getOptions().colors[0]
//                                                },
//                                                tooltip: {
//                                                    pointFormat: 'Observation: {point.y}'
//                                                }
//                                            }
                                        ]
                                    });
                
                
//                $('#affichageCaptation').append("");
//                $('#affichageCaptation').append("<tr><td>Moyenne</td><td>LUN</td><td>Mardi</td><td>Merc</td><td>Jeud</td><td>Ven</td><td>Sam</td><td>DIM</td></tr>");
                
                
//                $('#affichageCaptation').append("</table>");
                

                
//                alert(jsonentre);
            }
         }
     );
         
         
         
         
     });
     
     
     
     
     /***
      * Gestion du graphique de récap des captures jours en fonction des paramettres de saisies.
      */
      $('#form').on('submit', function truc2() {

        // Si il sélectionner toutes les items de la liste 2

//                    var $fluxSelection2 = $('#fluxSelection2');
        options = $('#fluxSelection2 li');

        //on force la sélection dans la liste 2 afin de pouvoir utiliser la fonction val() sur ce composant html
//            for (i = 0; i < options.length; i++) {
//                options[i].setAttribute('selected', 'true');
//            }

        //===========================================
        //          Récupération des paramettres sélectionné par l'utilisateur
        //===========================================

        //Flux
        options = $('#fluxSelection2 li');
        //on force la sélection dans la liste 2 afin de pouvoir utiliser la fonction val() sur ce composant html
        idFlux = {
            field: "listFlux",
            op: "in",
            data: []
        };

        for (i = 0; i < options.length; i++) {
            idFlux['data'].push(options[i]['value']);
        }


        // Récupération des flux sélectionne
        champSpe = []; // Variable permettant de récupérer les champs spéciaux
        champSpe.push(idFlux);


        //Date
        $date1 = $('#date1').val()+" 00:00:00";
        $date2 = $('#date2').val()+" 23:59:59";


        d1 = {
            field: "dateRecup",
            op: "gt",
            data: $date1
        };

        d2 = {
            field: "dateRecup",
            op: "lt",
            data: $date2
        };
        champSpe.push(d1);
        champSpe.push(d2);

//            param = param.substr(1, param.length);



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

        filterJson = JSON.stringify(filters);



        $.ajax({
            url: $(this).attr('action'), // le nom du fichier indiqué dans le formulaire
            type: $(this).attr('method'), // la méthode indiquée dans le formulaire (get ou post)
//            data: $(this).serialize(), // je sérialise les données (voir plus loin), ici les $_POST
            data: "filters=" + filterJson, // je sérialise les données (voir plus loin), ici les $_POST
            dataType: 'json',
            success: function(html) {
                $('#container').highcharts(html);
//                alert(html);
//                alert('succes');
            }

        });
        return false;
    });

});