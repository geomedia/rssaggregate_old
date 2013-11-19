/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#form').on('submit', function truc2() {
        alert("subb");

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
            alert('put' + options[i]['value']);
        }


        // Récupération des flux sélectionne
        champSpe = []; // Variable permettant de récupérer les champs spéciaux
        champSpe.push(idFlux);


        //Date

        $date2 = $('#date2');
        $date1 = $('#date1');

        alert('D1 val : ' + $('#date1').val());

        d1 = {
            field: "dateRecup",
            op: "gt",
            data: $date1.val()
        };

        d2 = {
            field: "dateRecup",
            op: "lt",
            data: $date2.val()
        };
        champSpe.push(d1);
        champSpe.push(d2);
        alert('date1 : ' + $date1.val());
        alert('date2 '+$date2.val());

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